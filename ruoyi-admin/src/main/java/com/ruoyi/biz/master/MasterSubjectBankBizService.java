package com.ruoyi.biz.master;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.master.facade.IMasterSubjectBankFacade;
import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.query.MasterSubjectBankQuery;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.master.facade.IMasterSubjectFacade;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.master.model.query.MasterSubjectQuery;
import com.ruoyi.mapper.bill.PayerConvert;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.web.vo.bill.PayerVO;
import com.ruoyi.web.vo.master.MasterSubjectBankListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 主体银行卡应用编排服务：基于 m_subject_bank.out_code = m_subject.subject_code 关联，
 * 提供按主体查卡、设置默认银行卡、同步后自动补设默认卡。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasterSubjectBankBizService {

    /** 银行卡按录入时间、主键升序，"第一张"即最早录入。 */
    private static final SortBy BANK_SORT = SortBy.of("+create_time,+id");

    private final IMasterSubjectFacade masterSubjectFacade;
    private final IMasterSubjectBankFacade payerFacade;
    private final ISysUserFacade sysUserFacade;

    /**
     * 查询主体下的银行卡列表及当前默认卡。
     *
     * @param subjectId 经营主体ID
     * @return 银行卡列表响应
     */
    public MasterSubjectBankListVO listBanks(final Long subjectId) {
        MasterSubjectBO subject = masterSubjectFacade.getOne(new MasterSubjectQuery().setId(subjectId));
        if (subject == null) {
            throw new ServiceException("经营主体不存在");
        }
        List<MasterSubjectBankBO> banks = payerFacade.list(
                new MasterSubjectBankQuery().setOutCode(subject.getSubjectCode()), BANK_SORT);
        List<PayerVO> voList = PayerConvert.INSTANCE.toVOList(banks);
        fillUserName(voList);
        MasterSubjectBankListVO result = new MasterSubjectBankListVO();
        result.setDefaultPayerId(subject.getDefaultPayerId());
        result.setList(voList);
        return result;
    }

    /**
     * 设置主体默认银行卡。
     *
     * @param subjectId 经营主体ID
     * @param payerId 银行卡ID
     */
    public void setDefaultBank(final Long subjectId, final Long payerId) {
        MasterSubjectBO subject = masterSubjectFacade.getOne(new MasterSubjectQuery().setId(subjectId));
        if (subject == null) {
            throw new ServiceException("经营主体不存在");
        }
        MasterSubjectBankBO payer = payerFacade.getOne(new MasterSubjectBankQuery().setId(payerId));
        if (payer == null) {
            throw new ServiceException("银行卡不存在");
        }
        if (!Objects.equals(payer.getOutCode(), subject.getSubjectCode())) {
            throw new ServiceException("银行卡不属于该主体");
        }
        masterSubjectFacade.updateDefaultPayerId(subjectId, payerId);
    }

    /**
     * 为尚未设置默认银行卡的主体自动补设第一张银行卡，供同步任务调用。
     */
    public void autoFillDefaultBank() {
        List<MasterSubjectBO> subjects = masterSubjectFacade.listWithoutDefaultPayer();
        if (CollectionUtil.isEmpty(subjects)) {
            return;
        }
        for (MasterSubjectBO subject : subjects) {
            try {
                fillOne(subject);
            } catch (Exception e) {
                log.warn("主体{}自动补设默认银行卡失败：{}", subject.getId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 为单个主体补设第一张银行卡。
     *
     * @param subject 经营主体
     */
    private void fillOne(final MasterSubjectBO subject) {
        List<MasterSubjectBankBO> banks = payerFacade.list(
                new MasterSubjectBankQuery().setOutCode(subject.getSubjectCode()), BANK_SORT);
        if (CollectionUtil.isEmpty(banks)) {
            return;
        }
        masterSubjectFacade.updateDefaultPayerId(subject.getId(), banks.get(0).getId());
    }

    /**
     * 回填银行卡创建人、修改人姓名。
     *
     * @param voList 银行卡视图集合
     */
    private void fillUserName(final List<PayerVO> voList) {
        if (CollectionUtil.isEmpty(voList)) {
            return;
        }
        Set<Long> userIds = new HashSet<>();
        for (PayerVO vo : voList) {
            if (vo.getCreateBy() != null) {
                userIds.add(vo.getCreateBy());
            }
            if (vo.getUpdateBy() != null) {
                userIds.add(vo.getUpdateBy());
            }
        }
        if (userIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = sysUserFacade.selectUserByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
        for (PayerVO vo : voList) {
            vo.setCreateName(nameMap.get(vo.getCreateBy()));
            vo.setUpdateName(nameMap.get(vo.getUpdateBy()));
        }
    }
}
