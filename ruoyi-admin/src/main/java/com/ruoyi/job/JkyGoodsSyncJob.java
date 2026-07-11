package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.goods.GoodsListParam;
import com.ruoyi.jky.rep.goods.GoodsListRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.job.util.SyncTimeUtil;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.product.model.query.ProductSkuQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("jkyGoodsSyncJob")
public class JkyGoodsSyncJob {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private IProductSkuFacade productSkuFacade;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SyncTimeUtil syncTimeUtil;

    /**
     * 同步吉客云最近更新的商品 SKU 信息到 p_product_sku。
     */
    public void execute() {
        redisCache.tryLockRun(AdminRedisKey.Jky.GOODS_SYNC_LOCK, 30L, TimeUnit.MINUTES, "吉客云商品同步", this::doSync);
    }

    private void doSync() {
        DateTime endTime = DateUtil.date();
        DateTime startTime = syncTimeUtil.getStartTime(AdminRedisKey.Jky.GOODS_LAST_SYNC_TIME, 1, TimeUnit.DAYS, "吉客云商品");
        log.info("开始同步吉客云商品定时任务，时间范围：{} - {}", DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        SyncCount count = new SyncCount();
        boolean success = true;
        success = syncGoods(startTime, endTime, count);
        if (success) {
            syncTimeUtil.saveSyncTime(AdminRedisKey.Jky.GOODS_LAST_SYNC_TIME, endTime);
        }
        log.info("结束同步吉客云商品定时任务，商品SKU{}条，新增{}条，更新{}条，跳过{}条，成功状态{}", count.goodsCount, count.insertCount, count.updateCount, count.skipCount, success);
    }

    private boolean syncGoods(DateTime startTime, DateTime endTime, SyncCount count) {
        long maxSkuId = 0L;
        for (; ; ) {
            GoodsListParam param = buildQueryParam(startTime, endTime, maxSkuId);
            JkyResponse<GoodsListRep.GoodsListWrapper> response = jkyTemplate.goodsList(param);
            if (!JkyResponseUtil.isSuccess(response)) {
                log.warn("吉客云商品同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                return false;
            }
            GoodsListRep.GoodsListWrapper wrapper = JkyResponseUtil.getData(response);
            List<GoodsListRep> goodsList = wrapper == null ? null : wrapper.getGoods();
            if (CollectionUtil.isEmpty(goodsList)) {
                return true;
            }
            for (GoodsListRep goods : goodsList) {
                syncGoods(goods, count);
            }
            if (goodsList.size() < PAGE_SIZE) {
                return true;
            }
            Long nextMaxSkuId = goodsList.get(goodsList.size() - 1).getSkuId();
            if (nextMaxSkuId == null || nextMaxSkuId <= maxSkuId) {
                return true;
            }
            maxSkuId = nextMaxSkuId;
        }
    }

    private GoodsListParam buildQueryParam(DateTime startTime, DateTime endTime, long maxSkuId) {
        GoodsListParam param = new GoodsListParam();
        param.setPageIndex(0);
        param.setPageSize(PAGE_SIZE);
        param.setMaxSkuId(maxSkuId);
        param.setStartDateModifiedGoods(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setEndDateModifiedGoods(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setIsQueryDelete("0");
        return param;
    }

    private void syncGoods(GoodsListRep goods, SyncCount count) {
        count.goodsCount++;
        ProductSkuParam param = buildProductSkuParam(goods);
        if (param == null) {
            count.skipCount++;
            return;
        }
        try {
            ProductSkuBO productSku = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(param.getSkuCode()));
            if (productSku == null) {
                productSkuFacade.save(param);
                count.insertCount++;
                log.info("新增吉客云SKU: {}", param.getSkuCode());
                return;
            }
            param.setId(productSku.getId());

            productSkuFacade.update(param, new ProductSkuQuery().setSkuCode(param.getSkuCode()));
            count.updateCount++;
        } catch (Exception e) {
            count.skipCount++;
            log.error("吉客云商品同步异常，goodsNo={}，skuId={}，skuCode={}", goods.getGoodsNo(), goods.getSkuId(), param.getSkuCode(), e);
        }
    }

    private ProductSkuParam buildProductSkuParam(GoodsListRep goods) {
        if (goods == null) {
            return null;
        }
        String skuCode = firstNotBlank(goods.getSkuNo(), goods.getSkuBarcode(), goods.getGoodsNo());
        if (StrUtil.isBlank(skuCode)) {
            return null;
        }
        ProductSkuParam param = new ProductSkuParam();
        param.setSkuCode(skuCode);
        param.setSpuCode(goods.getGoodsNo());
        param.setProductName(removeSpecName(goods.getGoodsName(), goods.getSkuName()));
        param.setBrand(normalizeBrand(goods.getBrandName()));
        param.setCategory(goods.getCateName());
        param.setSpecName(goods.getSkuName());
        param.setBarCode(goods.getSkuBarcode());
        param.setSortOrder(Long.valueOf(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN)));
        if (goods.getIsSerialManagement() != null) {
            param.setSnType(goods.getIsSerialManagement().longValue());
        }
        return param;
    }

    private String removeSpecName(String goodsName, String skuName) {
        if (StrUtil.hasBlank(goodsName, skuName)) {
            return goodsName;
        }
        return StrUtil.removeSuffix(goodsName, skuName);
    }

    /**
     * 统一品牌名，将"红米"映射为"小米"
     */
    private String normalizeBrand(String brand) {
        if (StrUtil.isBlank(brand)) {
            return brand;
        }
        if (Objects.equals("红米", brand) || Objects.equals("Redmi", brand)) {
            return "小米";
        }
        return brand;
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static class SyncCount {
        private int goodsCount;
        private int insertCount;
        private int updateCount;
        private int skipCount;
    }
}
