package com.ruoyi.capital.facade;


import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;

import java.util.List;

public interface ICompanyCapitalFacade {


    CompanyCapitalBO queryOne(CompanyCapitalQuery query);


    /**
     * 修改可用资金.
     *
     * @param logParam
     */
    void changeAvailable(CompanyCapitalLogParam logParam);

    /**
     * 保存日志，并且修改保证金
     *
     * @param logParam
     * @return
     */
    void freeze(CompanyCapitalLogParam logParam);


    /**
     * 保存日志，并且修改保证金，解冻保证金
     *
     * @param logParam
     * @return
     */
    void unFreeze(CompanyCapitalLogParam logParam);

    /**
     * 获取保证金流水
     *
     * @param query
     * @param createTimeDesc
     * @return
     */
    PageBO<CompanyCapitalLogBO> pageLog(CompanyCapitalLogQuery query, PageParamV2 createTimeDesc);

    /**
     * 获取保证金流水
     *
     * @param companyCapitalLogQuery
     * @return
     */
    List<CompanyCapitalLogBO> listLog(CompanyCapitalLogQuery companyCapitalLogQuery);
}
