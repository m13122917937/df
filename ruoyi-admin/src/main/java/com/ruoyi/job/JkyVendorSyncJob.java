package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.vendor.VendorListParam;
import com.ruoyi.jky.rep.vendor.VendorListRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.user.domain.Company;
import com.ruoyi.user.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("jkyVendorSyncJob")
public class JkyVendorSyncJob {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 同步吉客云供应商编码到企业外部编码。
     */
    public void execute() {
        redisCache.tryLockRun(AdminRedisKey.Jky.VENDOR_SYNC_LOCK, 30L, TimeUnit.MINUTES, "吉客云供应商同步", this::doSync);
    }

    private void doSync() {
        SyncCount count = new SyncCount();
        boolean success = true;
        success = syncVendors(count);
        log.info("结束同步吉客云供应商定时任务，供应商{}条，更新{}条，跳过{}条，成功状态{}", count.vendorCount, count.updateCount, count.skipCount, success);
    }

    private boolean syncVendors(SyncCount count) {
        for (int pageIndex = 0; ; pageIndex++) {
            JkyResponse<VendorListRep> response = jkyTemplate.listVendors(buildQueryParam(pageIndex));
            if (!JkyResponseUtil.isSuccess(response)) {
                log.warn("吉客云供应商同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                return false;
            }
            VendorListRep data = JkyResponseUtil.getData(response);
            List<VendorListRep.VendorInfoRep> vendors = data == null ? null : data.getVendInfo();
            if (CollectionUtil.isEmpty(vendors)) {
                return true;
            }
            for (VendorListRep.VendorInfoRep vendor : vendors) {
                syncVendor(vendor, count);
            }
            if (vendors.size() < PAGE_SIZE) {
                return true;
            }
        }
    }

    private VendorListParam buildQueryParam(int pageIndex) {
        return new VendorListParam()
                .setPageIndex(pageIndex)
                .setPageSize(PAGE_SIZE)
                .setNeedAllPayAccount(0)
                .setIncludeDeleteAndBlockup(0);
    }

    private void syncVendor(VendorListRep.VendorInfoRep vendor, SyncCount count) {
        count.vendorCount++;
        if (vendor == null || StrUtil.hasBlank(vendor.getName(), vendor.getCode())) {
            count.skipCount++;
            return;
        }
        Company company = companyService.lambdaQuery()
                .eq(Company::getCompanyName, vendor.getName())
                .last("limit 1")
                .one();
        if (company == null) {
            count.skipCount++;
            log.info("吉客云供应商未匹配企业，供应商名称：{}，编码：{}", vendor.getName(), vendor.getCode());
            return;
        }
        if (Objects.equals(company.getOutNo(), vendor.getCode())) {
            count.skipCount++;
            return;
        }
        boolean updated = companyService.lambdaUpdate()
                .eq(Company::getId, company.getId())
                .set(Company::getOutNo, vendor.getCode())
                .update();
        if (updated) {
            count.updateCount++;
            return;
        }
        count.skipCount++;
    }

    private static class SyncCount {
        private int vendorCount;
        private int updateCount;
        private int skipCount;
    }
}
