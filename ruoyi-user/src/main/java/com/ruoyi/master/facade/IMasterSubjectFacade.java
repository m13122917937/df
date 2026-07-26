package com.ruoyi.master.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.master.model.query.MasterSubjectQuery;

import java.util.List;

/**
 * 经营主体领域对外接口。
 */
public interface IMasterSubjectFacade {

    /**
     * 分页查询经营主体。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 经营主体分页数据
     */
    PageBO<MasterSubjectBO> page(MasterSubjectQuery query, PageParamV2 pageParam);

    /**
     * 查询单个经营主体。
     *
     * @param query 查询条件
     * @return 经营主体；不存在时返回 null
     */
    MasterSubjectBO getOne(MasterSubjectQuery query);

    /**
     * 同步吉客云经营主体数据。
     */
    void syncSubjects();

    /**
     * 查询尚未设置默认银行卡的经营主体。
     *
     * @return defaultPayerId 为空的经营主体集合
     */
    List<MasterSubjectBO> listWithoutDefaultPayer();

    /**
     * 更新经营主体默认银行卡。
     *
     * @param subjectId 经营主体ID
     * @param payerId 银行卡ID，关联 m_subject_bank.id
     */
    void updateDefaultPayerId(Long subjectId, Long payerId);
}
