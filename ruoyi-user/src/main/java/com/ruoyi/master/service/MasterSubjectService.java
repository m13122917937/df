package com.ruoyi.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.redis.RedisCache;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.company.CompanyQueryParam;
import com.ruoyi.jky.rep.company.CompanyQueryRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.master.convert.MasterSubjectConvert;
import com.ruoyi.master.domain.MasterSubject;
import com.ruoyi.master.mapper.MasterSubjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 经营主体主数据服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasterSubjectService extends ServiceImpl<MasterSubjectMapper, MasterSubject> {

    private static final String SUBJECT_SYNC_LOCK = "fy:master:subject:sync:lock";
    private static final int PAGE_SIZE = 100;

    private final JkyTemplate jkyTemplate;
    private final RedisCache redisCache;

    /**
     * 从吉客云全量同步经营主体。
     */
    public void syncSubjects() {
        redisCache.tryLockRun(SUBJECT_SYNC_LOCK, 30L, TimeUnit.MINUTES,
                "吉客云经营主体同步", this::doSyncSubjects);
    }

    private void doSyncSubjects() {
        int total = 0;
        for (int pageIndex = 0; ; pageIndex++) {
            List<CompanyQueryRep.CompanyInfoRep> subjects = queryPage(pageIndex);
            if (subjects == null) {
                return;
            }
            total += syncPage(subjects);
            if (subjects.size() < PAGE_SIZE) {
                log.info("吉客云经营主体同步完成，成功写入 {} 条", total);
                return;
            }
        }
    }

    private List<CompanyQueryRep.CompanyInfoRep> queryPage(final int pageIndex) {
        CompanyQueryParam param = new CompanyQueryParam().setPageIndex(pageIndex).setPageSize(PAGE_SIZE);
        JkyResponse<List<CompanyQueryRep.CompanyInfoRep>> response = jkyTemplate.queryCompanies(param);
        if (!JkyResponseUtil.isSuccess(response)) {
            log.error("吉客云经营主体同步失败，pageIndex={}, code={}, msg={}", pageIndex,
                    response == null ? null : response.getCode(), response == null ? null : response.getMsg());
            return null;
        }
        List<CompanyQueryRep.CompanyInfoRep> data = JkyResponseUtil.getData(response);
        return data == null ? List.of() : data;
    }

    private int syncPage(final List<CompanyQueryRep.CompanyInfoRep> subjects) {
        int count = 0;
        LocalDateTime now = LocalDateTime.now();
        for (CompanyQueryRep.CompanyInfoRep source : subjects) {
            if (source == null || source.getCompanyId() == null) {
                continue;
            }
            MasterSubject subject = MasterSubjectConvert.INSTANCE.toDomain(source);
            // 吉客云接口不返回 isDelete，默认 0（未删除）
            if (subject.getIsDelete() == null) {
                subject.setIsDelete(0);
            }
            // 简称为空时取全称前4字符
            if (StrUtil.isBlank(subject.getSubjectShortName()) && StrUtil.isNotBlank(subject.getSubjectName())) {
                subject.setSubjectShortName(StrUtil.sub(subject.getSubjectName(), 0, 4));
            }
            subject.setLastSyncTime(now);
            subject.setCreatedTime(now);
            subject.setUpdatedTime(now);
            baseMapper.upsertByJkySubjectId(subject);
            count++;
        }
        return count;
    }
}
