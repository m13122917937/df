package com.ruoyi.biz.company;

import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class CompanyCapitalBizService {

    @Autowired
    private ICompanyCapitalFacade companyCapitalFacade;

    BigDecimal amount = new BigDecimal("20");

    BigDecimal rate = new BigDecimal("0.8");

    /**
     * 查询企业资金账户。
     *
     * @param query 查询条件
     * @return 企业资金账户
     */
    public CompanyCapitalBO queryOne(CompanyCapitalQuery query) {
        return companyCapitalFacade.queryOne(query);
    }

    /**
     * 分页查询资金流水。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 资金流水分页结果
     */
    public PageBO<CompanyCapitalLogBO> pageLog(CompanyCapitalLogQuery query, PageParamV2 pageParam) {
        return companyCapitalFacade.pageLog(query, pageParam);
    }

    /**
     * 查询资金流水。
     *
     * @param query 查询条件
     * @return 资金流水列表
     */
    public List<CompanyCapitalLogBO> listLog(CompanyCapitalLogQuery query) {
        return companyCapitalFacade.listLog(query);
    }



    public BigDecimal calAmount(Integer quantity) {
        return Arith.mul(amount, new BigDecimal(quantity));
    }


    public BigDecimal calAmountRate(Integer quantity) {
        return Arith.mul(amount, new BigDecimal(quantity),  rate);
    }



}
