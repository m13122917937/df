package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisRebateActivity;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import com.ruoyi.analysis.mapper.AnalysisRebateActivityMapper;
import com.ruoyi.analysis.mapper.AnalysisRebateDetailMapper;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 返利价保活动服务，仅访问 ana_rebate_* 新表。
 */
@Service
public class AnalysisRebateService extends ServiceImpl<AnalysisRebateActivityMapper, AnalysisRebateActivity> {
    @Autowired
    private AnalysisRebateDetailMapper detailMapper;

    /**
     * 查询返利价保活动及明细。
     */
    public List<AnalysisRebateBO> listActivities() {
        List<AnalysisRebateActivity> activities = baseMapper.selectAllOrdered();
        List<AnalysisRebateBO> result = new ArrayList<>();
        for (AnalysisRebateActivity activity : activities) {
            List<AnalysisRebateDetail> details = detailMapper.selectByActivityId(activity.getId());
            result.add(AnalysisConvert.INSTANCE.toBO(activity, details));
        }
        return result;
    }

    /**
     * 保存返利价保活动及明细。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveActivity(AnalysisRebateParam param) {
        validate(param);
        AnalysisRebateActivity activity = AnalysisConvert.INSTANCE.toDomain(param);
        LocalDateTime now = LocalDateTime.now();
        activity.setStatus(param.getStatus() == null ? AnalysisConstants.STATUS_PENDING : param.getStatus());
        if (activity.getId() == null) {
            activity.setCreatedBy(param.getOperatorId());
            activity.setCreatedTime(now);
            activity.setUpdatedTime(now);
            save(activity);
        } else {
            activity.setUpdatedBy(param.getOperatorId());
            activity.setUpdatedTime(now);
            updateById(activity);
            deleteDetails(activity.getId());
        }
        saveDetails(activity.getId(), param.getDetails());
        return activity.getId();
    }

    /**
     * 删除返利价保活动及明细。
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        deleteDetails(id);
        removeById(id);
    }

    private void saveDetails(Long activityId, List<AnalysisRebateParam.DetailParam> params) {
        if (params == null) {
            return;
        }
        for (AnalysisRebateDetail detail : AnalysisConvert.INSTANCE.toRebateDetailList(params)) {
            detail.setId(null);
            detail.setActivityId(activityId);
            detailMapper.insert(detail);
        }
    }

    private void deleteDetails(Long activityId) {
        detailMapper.deleteByActivityId(activityId);
    }

    private void validate(AnalysisRebateParam param) {
        if (!param.getStartTime().isBefore(param.getEndTime())) {
            throw new ServiceException("返利价保活动开始时间必须早于结束时间");
        }
    }
}
