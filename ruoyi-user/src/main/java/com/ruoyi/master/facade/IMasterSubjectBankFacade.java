package com.ruoyi.master.facade;

import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.param.MasterSubjectBankParam;
import com.ruoyi.master.model.query.MasterSubjectBankQuery;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;

import java.util.List;

/**
 * 主体银行卡维护对外接口（原 IPayerFacade）。
 *
 * @author ruoyi
 * @date 2025-11-07
 */
public interface IMasterSubjectBankFacade {

    List<MasterSubjectBankBO> list(MasterSubjectBankQuery query, SortBy sort);

    PageBO<MasterSubjectBankBO> listPage(MasterSubjectBankQuery query, PageParamV2 pageParam);

    MasterSubjectBankBO getOne(MasterSubjectBankQuery query);

    boolean save(MasterSubjectBankParam param);

    boolean update(MasterSubjectBankParam param, MasterSubjectBankQuery query);

}
