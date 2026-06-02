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

    private static final int PAGE_SIZE = 200;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private IProductSkuFacade productSkuFacade;

    @Autowired
    private RedisCache redisCache;

    /**
     * 同步吉客云最近更新的商品 SKU 信息到 p_product_sku。
     */
    public void execute() {
        Boolean locked = redisCache.setIfAbsent(AdminRedisKey.Jky.GOODS_SYNC_LOCK, DateUtil.now(), 30L, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(locked)) {
            log.info("吉客云商品同步任务正在执行，本次跳过");
            return;
        }
        DateTime endTime = DateUtil.date();
        DateTime startTime = getStartTime(endTime);
        log.info("开始同步吉客云商品定时任务，时间范围：{} - {}", DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        SyncCount count = new SyncCount();
        boolean success = true;
        try {
            success = syncGoods(startTime, endTime, count);
            if (success) {
                redisCache.setCacheObject(AdminRedisKey.Jky.GOODS_LAST_SYNC_TIME, DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
            }
        } finally {
            redisCache.deleteObject(AdminRedisKey.Jky.GOODS_SYNC_LOCK);
        }
        log.info("结束同步吉客云商品定时任务，商品SKU{}条，新增{}条，更新{}条，跳过{}条，成功状态{}", count.goodsCount, count.insertCount, count.updateCount, count.skipCount, success);
    }

    private boolean syncGoods(DateTime startTime, DateTime endTime, SyncCount count) {
        long maxSkuId = 0L;
        for (; ; ) {
            GoodsListParam param = buildQueryParam(startTime, endTime, maxSkuId);
            JkyResponse<List<GoodsListRep>> response = jkyTemplate.goodsList(param);
            if (response == null || !Objects.equals(response.getCode(), 200)) {
                log.warn("吉客云商品同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                return false;
            }
            List<GoodsListRep> data = response.getResult() == null ? null : response.getResult().getData();
            if (CollectionUtil.isEmpty(data)) {
                return true;
            }
            for (GoodsListRep goods : data) {
                syncGoods(goods, count);
            }
            if (data.size() < PAGE_SIZE) {
                return true;
            }
            Long nextMaxSkuId = data.get(data.size() - 1).getSkuId();
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
        param.setBrand(goods.getBrandName());
        param.setCategory(goods.getCateName());
        param.setSpecName(goods.getSkuName());
        param.setBarCode(goods.getSkuBarcode());
        param.setSortOrder(Long.valueOf(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN)));
        if (goods.getIsSerialManagement() != null) {
            param.setSnType(goods.getIsSerialManagement().longValue());
        }
        return param;
    }

    private DateTime getStartTime(DateTime endTime) {
        String lastSyncTime = redisCache.getCacheObject(AdminRedisKey.Jky.GOODS_LAST_SYNC_TIME);
        if (StrUtil.isBlank(lastSyncTime)) {
            return DateUtil.offsetMinute(endTime, -5);
        }
        try {
            return DateUtil.parse(lastSyncTime, DatePattern.NORM_DATETIME_PATTERN);
        } catch (Exception e) {
            log.warn("吉客云商品上次同步时间解析失败：{}", lastSyncTime);
            return DateUtil.offsetMinute(endTime, -5);
        }
    }

    private String removeSpecName(String goodsName, String skuName) {
        if (StrUtil.hasBlank(goodsName, skuName)) {
            return goodsName;
        }
        return StrUtil.removeSuffix(goodsName, skuName);
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
