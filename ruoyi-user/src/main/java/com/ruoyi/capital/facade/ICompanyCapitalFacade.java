package com.ruoyi.capital.facade;


import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;

import java.util.List;

public interface ICompanyCapitalFacade {

    /** @return 企业资金账户 */
    CompanyCapitalBO queryOne(CompanyCapitalQuery query);


    /**
     * 修改可用资金.
     *
     * @param logParam 资金流水参数
     */
    void changeAvailable(CompanyCapitalLogParam logParam);

    /**
     * 保存日志，并且修改保证金
     *
     * @param logParam 冻结流水参数
     */
    void freeze(CompanyCapitalLogParam logParam);


    /**
     * 保存日志，并且修改保证金，解冻保证金
     *
     * @param logParam 解冻流水参数
     */
    void unFreeze(CompanyCapitalLogParam logParam);

    /**
     * 获取保证金流水
     *
     * @param query 查询条件
     * @param createTimeDesc 分页参数
     * @return 资金流水分页数据
     */
    PageBO<CompanyCapitalLogBO> pageLog(CompanyCapitalLogQuery query, PageParamV2 createTimeDesc);

    /**
     * 获取保证金流水
     *
     * @param companyCapitalLogQuery 查询条件
     * @return 资金流水列表
     */
    List<CompanyCapitalLogBO> listLog(CompanyCapitalLogQuery companyCapitalLogQuery);
}
