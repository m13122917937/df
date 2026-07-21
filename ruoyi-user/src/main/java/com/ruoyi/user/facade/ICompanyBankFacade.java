package com.ruoyi.user.facade;

import java.util.List;
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

	/** @return 企业银行账户列表 */
	List<CompanyBankBO> list(CompanyBankQuery query, SortBy sort);

	/** @return 企业银行账户分页列表 */
	PageBO<CompanyBankBO> listPage(CompanyBankQuery query, PageParamV2 pageParam);

	/** @return 企业银行账户 */
	CompanyBankBO getOne(CompanyBankQuery query);

	/** @return 账户数量 */
	Long count(CompanyBankQuery query);

	/** @return 已保存的企业银行账户 */
	CompanyBankBO save(CompanyBankParam param);

	/** @return 是否更新成功 */
	boolean update(CompanyBankParam param,CompanyBankQuery query);

}
