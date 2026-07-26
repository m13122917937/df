package com.ruoyi.master.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.master.convert.MasterSubjectConvert;
import com.ruoyi.master.domain.MasterSubject;
import com.ruoyi.master.facade.IMasterSubjectFacade;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.master.model.query.MasterSubjectQuery;
import com.ruoyi.master.service.MasterSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营主体领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class MasterSubjectFacade implements IMasterSubjectFacade {

    private final MasterSubjectService masterSubjectService;

    /** {@inheritDoc} */
    @Override
    public PageBO<MasterSubjectBO> page(final MasterSubjectQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<MasterSubject> subjects = masterSubjectService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-updated_time,-id")));
        return PageUtils.fromList(subjects, MasterSubjectConvert.INSTANCE::toBOList);
    }

    /** {@inheritDoc} */
    @Override
    public MasterSubjectBO getOne(final MasterSubjectQuery query) {
        MasterSubject subject = masterSubjectService.getOne(DynamicCondition.toWrapper(query));
        return MasterSubjectConvert.INSTANCE.toBO(subject);
    }

    /** {@inheritDoc} */
    @Override
    public void syncSubjects() {
        masterSubjectService.syncSubjects();
    }

    /** {@inheritDoc} */
    @Override
    public List<MasterSubjectBO> listWithoutDefaultPayer() {
        List<MasterSubject> subjects = masterSubjectService
                .lambdaQuery().isNull(MasterSubject::getDefaultPayerId).list();
        return MasterSubjectConvert.INSTANCE.toBOList(subjects);
    }

    /** {@inheritDoc} */
    @Override
    public void updateDefaultPayerId(final Long subjectId, final Long payerId) {
        MasterSubject subject = new MasterSubject();
        subject.setId(subjectId);
        subject.setDefaultPayerId(payerId);
        masterSubjectService.updateById(subject);
    }
}
