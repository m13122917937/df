package com.ruoyi.biz.contract;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.bill.facade.IContractFacade;
import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.contract.ContractConvert;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 合同业务层（供应商端）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@Service
public class ContractBizService {

    private static final String SIGNER_VENDOR = "vendor";

    @Autowired
    private IContractFacade contractFacade;

    @Autowired
    private ICompanyFacade companyFacade;

    /**
     * 分页查询当前供应商的合同
     *
     * @param queryForm  查询表单
     * @param companyId  当前供应商id
     * @param pageParam  分页参数
     * @return 分页结果
     */
    public PageBO<ContractVO> pageList(ContractQueryForm queryForm, Long companyId, PageParamV2 pageParam) {
        Assert.notNull(companyId, "未识别供应商身份");
        ContractQuery query = ContractConvert.INSTANCE.queryFormToQuery(queryForm)
                .setVendorCompanyId(companyId);
        PageBO<ContractBO> page = contractFacade.listPage(query, pageParam);
        return new PageBO<>(enrich(page.getData()), page.getTotal());
    }

    /**
     * 查询合同详情（限制为当前供应商的合同）
     *
     * @param id        合同主键
     * @param companyId 当前供应商id
     * @return 详情
     */
    public ContractVO detail(Long id, Long companyId) {
        Assert.notNull(companyId, "未识别供应商身份");
        ContractBO bo = contractFacade.getOne(new ContractQuery().setId(id).setVendorCompanyId(companyId));
        Assert.notNull(bo, "合同不存在");
        return enrich(Collections.singletonList(bo)).get(0);
    }

    /**
     * 获取供应商签署URL
     *
     * @param id        合同主键
     * @param companyId 当前供应商id
     * @return 签署URL
     */
    public String signUrl(Long id, Long companyId) {
        Assert.notNull(companyId, "未识别供应商身份");
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(id).setVendorCompanyId(companyId));
        Assert.notNull(contract, "合同不存在");
        if (StrUtil.isBlank(contract.getEsignFlowId())) {
            throw new ServiceException("合同尚未发起签署");
        }
        throw new ServiceException("当前 e签宝 SignApi 已切换为 create-by-file，暂未接入获取签署URL接口");
    }

    private List<ContractVO> enrich(List<ContractBO> data) {
        if (CollUtil.isEmpty(data)) {
            return Collections.emptyList();
        }
        List<ContractVO> voList = ContractConvert.INSTANCE.toVOList(data);
        Set<Long> payerIds = data.stream().map(ContractBO::getOurPayerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> payerNameMap = payerIds.isEmpty() ? Collections.emptyMap() :
                payerIds.stream()
                        .map(id -> companyFacade.queryOne(new CompanyQuery().setId(id)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(CompanyBO::getId, CompanyBO::getCompanyName, (a, b) -> a));
        for (int i = 0; i < voList.size(); i++) {
            voList.get(i).setOurPayerName(payerNameMap.get(data.get(i).getOurPayerId()));
        }
        return voList;
    }
}
