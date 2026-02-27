package com.ruoyi.user.facade;

import java.util.List;
import com.ruoyi.user.domain.CompanyBank;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.user.model.param.CompanyBankParam;
import com.ruoyi.user.model.query.CompanyBankQuery;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-09-28
 */
public interface ICompanyBankFacade {

	List<CompanyBankBO> list(CompanyBankQuery query, SortBy sort);

	PageBO<CompanyBankBO> listPage(CompanyBankQuery query, PageParamV2 pageParam);

	CompanyBankBO getOne(CompanyBankQuery query);

	Long count(CompanyBankQuery query);

	CompanyBankBO save(CompanyBankParam param);

	boolean update(CompanyBankParam param,CompanyBankQuery query);

}
