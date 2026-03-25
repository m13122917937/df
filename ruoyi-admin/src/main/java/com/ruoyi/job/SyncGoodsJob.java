package com.ruoyi.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.wangdian.param.Pager;
import com.ruoyi.wangdian.param.goods.GoodsQueryParams;
import com.ruoyi.wangdian.rep.goods.GoodsQueryWithSpecRep;
import com.ruoyi.wangdian.utils.WdtClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component("syncGoodsJob")
public class SyncGoodsJob {

    @Autowired
    WdtClient wdtClient;

    @Autowired
    IProductSkuFacade productSkuFacade;

    /**
     * 同步旺店通商品SKU信息到p_product_sku
     * 根据specNo(商家编码)匹配skuCode，不存在则新增，存在则更新商品名称和规格名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute() throws IOException {
        log.info("开始同步旺店通商品SKU数据定时任务");

        int pageNo = 0;
        int pageSize = 200;
        int totalCount;
        int updateCount = 0;
        int insertCount = 0;

        GoodsQueryParams goodsQueryParams = new GoodsQueryParams();
        DateTime date = DateUtil.date();
        goodsQueryParams.setStartTime(DateUtil.format(DateUtil.offsetMinute(date, -5), DatePattern.NORM_DATETIME_PATTERN));
        goodsQueryParams.setEndTime(DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN));

        do {
            Pager pager = new Pager(pageSize, pageNo, 1);
            GoodsQueryWithSpecRep result = wdtClient.queryGoodsWithSpec(goodsQueryParams, pager);

            if (Objects.isNull(result) || Objects.isNull(result.getData())) {
                log.warn("旺店通返回数据为空，结束同步");
                break;
            }

            var goodsList = result.getData().getGoodsList();
            totalCount = result.getData().getTotalCount();

            if (goodsList == null || goodsList.isEmpty()) {
                log.info("当前页[{}]无商品数据，继续下一页", pageNo);
                pageNo++;
                continue;
            }

            log.info("正在同步第{}页，本页{}条，共{}条", pageNo, goodsList.size(), totalCount);

            for (var goods : goodsList) {
                var specList = goods.getSpecList();
                if (specList == null || specList.isEmpty()) {
                    continue;
                }
                for (var spec : specList) {
                    String skuCode = spec.getSpecNo();
                    if (skuCode == null || skuCode.isEmpty()) {
                        continue;
                    }
                    // 查询数据库中是否存在该SKU
                    ProductSkuBO productSku = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(skuCode));

                    // 构建保存/更新参数
                    ProductSkuParam param = new ProductSkuParam().setSkuCode(skuCode).setSpuCode(goods.getGoodsNo()).setProductName(goods.getGoodsName().replace(spec.getSpecName(), ""))
                            .setBrand(goods.getBrandName()).setCategory(goods.getClassName()).setSpecName(spec.getSpecName()).setSortOrder(Long.valueOf(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN)));
                    if (spec.getSnType() != null) {
                        param.setSnType(spec.getSnType().longValue());
                    }

                    if (productSku == null) {
                        // 不存在，新增
                        productSkuFacade.save(param);
                        insertCount++;
                        log.info("新增SKU: {}", skuCode);
                    } else {
                        // 存在，更新
                        param.setId(productSku.getId());
                        productSkuFacade.update(param, new ProductSkuQuery().setSkuCode(skuCode));
                        updateCount++;
                    }
                }
            }

            pageNo++;
        } while (pageNo * pageSize < totalCount);

        log.info("同步旺店通商品SKU数据完成，新增{}条，更新{}条", insertCount, updateCount);
    }

}
