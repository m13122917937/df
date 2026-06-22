package com.ruoyi.biz.contract;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.bill.constant.ContractConsts;
import com.ruoyi.bill.facade.IContractFacade;
import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.param.ContractParam;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.config.properties.ContractProperties;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.model.sign.CreateByFileRequest;
import com.ruoyi.esign.model.sign.CreateByFileResponse;
import com.ruoyi.esign.properties.EsignProperties;
import com.ruoyi.mapper.contract.ContractConvert;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.contract.ContractForm;
import com.ruoyi.web.form.contract.ContractLaunchForm;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 合同业务层
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@Service
public class ContractBizService {

    private static final String SIGNER_OUR = "our";

    private static final String SIGNER_VENDOR = "vendor";

    @Autowired
    private IContractFacade contractFacade;

    @Autowired
    private ICompanyFacade companyFacade;

    @Autowired
    private ISysUserFacade sysUserFacade;

    @Autowired
    private ContractNoGenerator contractNoGenerator;

    @Autowired
    private EsignSignApi esignSignApi;

    @Autowired
    private EsignProperties esignProperties;

    @Autowired
    private ContractProperties contractProperties;

    /**
     * 新增 Company 时初始化一份框架协议草稿（我方主体取配置中的集团公司）
     *
     * @param vendorCompanyId 供应商 id
     * @param vendorName      供应商名称（用于合同名称）
     * @param createBy        创建人
     * @return 合同主键，未初始化或集团主体不存在时返回 null
     */
    @Transactional(rollbackFor = Exception.class)
    public Long initFrameworkContract(Long vendorCompanyId, String vendorName, Long createBy) {
        if (Objects.isNull(vendorCompanyId)) {
            return null;
        }
        String groupName = contractProperties.getGroupPayerName();
        if (StrUtil.isBlank(groupName)) {
            log.warn("未配置集团主体名称，跳过框架协议初始化，vendorCompanyId:{}", vendorCompanyId);
            return null;
        }
        CompanyBO group = companyFacade.queryOne(new CompanyQuery().setCompanyName(groupName));
        if (Objects.isNull(group)) {
            log.warn("集团主体「{}」未在 u_company 配置，跳过框架协议初始化，vendorCompanyId:{}", groupName, vendorCompanyId);
            return null;
        }
        ContractParam param = new ContractParam()
                .setContractNo(contractNoGenerator.next())
                .setContractName(StrUtil.format("{}-{}-框架协议", groupName, StrUtil.nullToEmpty(vendorName)))
                .setContractType(ContractConsts.Type.FRAMEWORK.getCode())
                .setOurPayerId(group.getId())
                .setVendorCompanyId(vendorCompanyId)
                .setStatus(ContractConsts.Status.DRAFT.getCode())
                .setCreateBy(createBy)
                .setCreateTime(DateUtil.date());
        return contractFacade.save(param);
    }

    /**
     * 分页查询合同列表
     *
     * @param queryForm 查询表单
     * @param pageParam 分页参数
     * @return 分页结果
     */
    public PageBO<ContractVO> pageList(ContractQueryForm queryForm, PageParamV2 pageParam) {
        ContractQuery query = ContractConvert.INSTANCE.queryFormToQuery(queryForm);
        PageBO<ContractBO> page = contractFacade.listPage(query, pageParam);
        List<ContractVO> voList = enrich(page.getData());
        return new PageBO<>(voList, page.getTotal());
    }

    /**
     * 查询合同详情
     *
     * @param id 合同主键
     * @return 合同详情
     */
    public ContractVO detail(Long id) {
        ContractBO bo = contractFacade.getOne(new ContractQuery().setId(id));
        Assert.notNull(bo, "合同不存在");
        List<ContractVO> voList = enrich(Collections.singletonList(bo));
        return voList.get(0);
    }

    /**
     * 新增草稿合同
     *
     * @param form      表单
     * @param loginUser 登录用户
     * @return 主键
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(ContractForm form, LoginUser loginUser) {
        validateRefs(form);
        ContractParam param = ContractConvert.INSTANCE.formToParam(form);
        param.setId(null);
        param.setContractNo(contractNoGenerator.next());
        param.setStatus(ContractConsts.Status.DRAFT.getCode());
        param.setCreateBy(loginUser.getUserId());
        param.setCreateTime(DateUtil.date());
        return contractFacade.save(param);
    }

    /**
     * 修改草稿合同
     *
     * @param form      表单
     * @param loginUser 登录用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(ContractForm form, LoginUser loginUser) {
        ContractBO existing = contractFacade.getOne(new ContractQuery().setId(form.getId()));
        Assert.notNull(existing, "合同不存在");
        if (!Objects.equals(existing.getStatus(), ContractConsts.Status.DRAFT.getCode())) {
            throw new ServiceException("仅草稿状态合同可编辑");
        }
        validateRefs(form);
        ContractParam param = ContractConvert.INSTANCE.formToParam(form);
        param.setUpdateBy(loginUser.getUserId());
        param.setUpdateTime(DateUtil.date());
        contractFacade.update(param, new ContractQuery().setId(form.getId()));
    }

    /**
     * 删除草稿合同（仅 DRAFT）
     *
     * @param id 合同主键
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        ContractBO existing = contractFacade.getOne(new ContractQuery().setId(id));
        Assert.notNull(existing, "合同不存在");
        if (!Objects.equals(existing.getStatus(), ContractConsts.Status.DRAFT.getCode())) {
            throw new ServiceException("仅草稿状态合同可删除");
        }
        contractFacade.removeById(id);
    }

    /**
     * 发起签署：调用 e 签宝创建流程 + 添加签署人 + 启动流程
     *
     * @param form      发起签署表单
     * @param loginUser 登录用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void launch(ContractLaunchForm form, LoginUser loginUser) {
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(form.getId()));
        Assert.notNull(contract, "合同不存在");
        if (!Objects.equals(contract.getStatus(), ContractConsts.Status.DRAFT.getCode())) {
            throw new ServiceException("仅草稿状态合同可发起签署");
        }
        CompanyBO payer = companyFacade.queryOne(new CompanyQuery().setId(contract.getOurPayerId()));
        Assert.notNull(payer, "我方主体不存在");
        CompanyBO vendor = companyFacade.queryOne(new CompanyQuery().setId(contract.getVendorCompanyId()));
        Assert.notNull(vendor, "供应商不存在");

        String flowId = createByFile(contract, form, payer, vendor);

        contractFacade.update(new ContractParam()
                        .setEsignFlowId(flowId)
                        .setEsignFileId(form.getEsignFileId())
                        .setStatus(ContractConsts.Status.SIGNING.getCode())
                        .setUpdateBy(loginUser.getUserId())
                        .setUpdateTime(DateUtil.date()),
                new ContractQuery().setId(contract.getId()));
    }

    private String createByFile(ContractBO contract, ContractLaunchForm form, CompanyBO payer, CompanyBO vendor) {
        CreateByFileRequest request = new CreateByFileRequest();
        CreateByFileRequest.Doc doc = new CreateByFileRequest.Doc();
        doc.setFileId(form.getEsignFileId());
        doc.setFileName(contract.getContractName() + ".pdf");
        request.setDocs(Collections.singletonList(doc));

        CreateByFileRequest.SignFlowConfig config = new CreateByFileRequest.SignFlowConfig();
        config.setSignFlowTitle(contract.getContractName());
        config.setAutoFinish(true);
        config.setNotifyUrl(buildNotifyUrl(contract.getId()));
        if (Objects.nonNull(contract.getExpireTime())) {
            config.setSignFlowExpireTime(contract.getExpireTime().getTime());
        }
        request.setSignFlowConfig(config);
        request.setSigners(CollUtil.newArrayList(buildOurSigner(form, payer), buildVendorSigner(form, vendor)));

        CreateByFileResponse response = esignSignApi.createByFile(request);
        if (!Integer.valueOf(0).equals(response.getCode()) || response.getData() == null
                || StrUtil.isBlank(response.getData().getSignFlowId())) {
            throw new ServiceException("基于文件发起签署失败：" + response.getMessage());
        }
        return response.getData().getSignFlowId();
    }

    private CreateByFileRequest.Signer buildOurSigner(ContractLaunchForm form, CompanyBO payer) {
        CreateByFileRequest.Signer signer = new CreateByFileRequest.Signer();
        signer.setSignerType(1);
        signer.setSignConfig(buildSignConfig(1));
        CreateByFileRequest.OrgSignerInfo orgSignerInfo = new CreateByFileRequest.OrgSignerInfo();
        orgSignerInfo.setOrgName(payer.getCompanyName());
        if (StrUtil.isNotBlank(form.getOurOperatorMobile())) {
            orgSignerInfo.setTransactorInfo(buildTransactorInfo(form.getOurOperatorMobile(), form.getOurSignerName(), form.getOurOperatorIdNumber()));
        }
        signer.setOrgSignerInfo(orgSignerInfo);
        signer.setSignFields(Collections.singletonList(buildSignField(form.getEsignFileId(), form.getOurSealId(), 200F, 200F)));
        return signer;
    }

    private CreateByFileRequest.Signer buildVendorSigner(ContractLaunchForm form, CompanyBO vendor) {
        CreateByFileRequest.Signer signer = new CreateByFileRequest.Signer();
        signer.setSignerType(1);
        signer.setSignConfig(buildSignConfig(2));
        CreateByFileRequest.OrgSignerInfo orgSignerInfo = new CreateByFileRequest.OrgSignerInfo();
        orgSignerInfo.setOrgName(vendor.getCompanyName());
        if (StrUtil.isNotBlank(form.getVendorMobile())) {
            orgSignerInfo.setTransactorInfo(buildTransactorInfo(form.getVendorMobile(), form.getVendorSignerName(), form.getVendorIdNumber()));
        }
        signer.setOrgSignerInfo(orgSignerInfo);
        signer.setSignFields(Collections.singletonList(buildSignField(form.getEsignFileId(), null, 458F, 200F)));
        return signer;
    }

    private CreateByFileRequest.SignConfig buildSignConfig(Integer signOrder) {
        CreateByFileRequest.SignConfig signConfig = new CreateByFileRequest.SignConfig();
        signConfig.setSignOrder(signOrder);
        return signConfig;
    }

    private CreateByFileRequest.TransactorInfo buildTransactorInfo(String mobile, String name, String idNumber) {
        CreateByFileRequest.TransactorInfo transactorInfo = new CreateByFileRequest.TransactorInfo();
        transactorInfo.setPsnAccount(mobile);
        CreateByFileRequest.PsnInfo psnInfo = new CreateByFileRequest.PsnInfo();
        psnInfo.setPsnName(name);
        psnInfo.setPsnIDCardNum(idNumber);
        psnInfo.setPsnIDCardType("CRED_PSN_CH_IDCARD");
        transactorInfo.setPsnInfo(psnInfo);
        return transactorInfo;
    }

    private CreateByFileRequest.SignField buildSignField(String fileId, String sealId, Float positionX, Float positionY) {
        CreateByFileRequest.SignField signField = new CreateByFileRequest.SignField();
        signField.setFileId(fileId);
        CreateByFileRequest.NormalSignFieldConfig config = new CreateByFileRequest.NormalSignFieldConfig();
        config.setAssignedSealId(sealId);
        config.setSignFieldStyle(1);
        CreateByFileRequest.SignFieldPosition position = new CreateByFileRequest.SignFieldPosition();
        position.setPositionPage("1");
        position.setPositionX(positionX);
        position.setPositionY(positionY);
        config.setSignFieldPosition(position);
        signField.setNormalSignFieldConfig(config);
        return signField;
    }

    /**
     * 撤销签署中的合同
     *
     * @param id        合同主键
     * @param loginUser 登录用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id, LoginUser loginUser) {
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(id));
        Assert.notNull(contract, "合同不存在");
        if (!Objects.equals(contract.getStatus(), ContractConsts.Status.SIGNING.getCode())) {
            throw new ServiceException("仅签署中合同可撤销");
        }
        // e 签宝目前 starter 暂未提供撤销接口，先标记本地状态，由人工或后续接入撤销 API 处理
        contractFacade.update(new ContractParam()
                        .setStatus(ContractConsts.Status.CANCELED.getCode())
                        .setUpdateBy(loginUser.getUserId())
                        .setUpdateTime(DateUtil.date()),
                new ContractQuery().setId(id));
        log.info("合同{}撤销，flowId={}", id, contract.getEsignFlowId());
    }

    /**
     * 获取指定签署方的签署URL
     *
     * @param id          合同主键
     * @param signerType  签署人类型 our / vendor
     * @return 签署URL
     */
    public String signUrl(Long id, String signerType) {
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(id));
        Assert.notNull(contract, "合同不存在");
        if (StrUtil.isBlank(contract.getEsignFlowId())) {
            throw new ServiceException("合同尚未发起签署");
        }
        throw new ServiceException("当前 e签宝 SignApi 已切换为 create-by-file，暂未接入获取签署URL接口");
    }

    /**
     * 同步单个合同状态（兜底/回调内部统一入口）
     *
     * @param id 合同主键
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncStatus(Long id) {
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(id));
        if (Objects.isNull(contract) || StrUtil.isBlank(contract.getEsignFlowId())) {
            return;
        }
        log.warn("当前 e签宝 SignApi 已切换为 create-by-file，暂未接入查询签署流程详情接口，contractId:{}", id);
    }

    /**
     * 处理 e 签宝回调：transData 中带的是 contractId
     *
     * @param contractId   合同主键（透传）
     * @param flowId       e 签宝流程ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(Long contractId, String flowId) {
        ContractBO contract = contractFacade.getOne(new ContractQuery().setId(contractId));
        if (Objects.isNull(contract)) {
            log.warn("回调找不到合同，contractId:{}，flowId:{}", contractId, flowId);
            return;
        }
        if (StrUtil.isNotBlank(flowId) && !Objects.equals(flowId, contract.getEsignFlowId())) {
            log.warn("回调flowId与本地不一致，contractId:{}，local:{}，remote:{}", contractId, contract.getEsignFlowId(), flowId);
            return;
        }
        syncStatus(contractId);
    }

    /**
     * 兜底：扫描所有签署中合同，逐个查 e 签宝刷状态
     */
    public void syncAllSigning() {
        log.info("开始同步签署中合同状态");
        Integer page = 1;
        for (; ; page++) {
            PageBO<ContractBO> contractPage = contractFacade.listPage(
                    new ContractQuery().setStatus(ContractConsts.Status.SIGNING.getCode()),
                    new PageParamV2(page, 100));
            if (CollUtil.isEmpty(contractPage.getData())) {
                break;
            }
            for (ContractBO contract : contractPage.getData()) {
                syncStatus(contract.getId());
            }
        }
        log.info("结束同步签署中合同状态");
    }

    /**
     * 列出所有签署中合同（供 Job 兜底使用，不分页一次性拉，再由调用方控制并发）
     */
    public List<ContractBO> listSigning() {
        return contractFacade.list(new ContractQuery().setStatus(ContractConsts.Status.SIGNING.getCode()), null);
    }

    private void validateRefs(ContractForm form) {
        if (Objects.nonNull(form.getOurPayerId())) {
            CompanyBO payer = companyFacade.queryOne(new CompanyQuery().setId(form.getOurPayerId()));
            Assert.notNull(payer, "我方主体不存在");
        }
        if (Objects.nonNull(form.getVendorCompanyId())) {
            CompanyBO company = companyFacade.queryOne(new CompanyQuery().setId(form.getVendorCompanyId()));
            Assert.notNull(company, "供应商不存在");
        }
    }

    private List<ContractVO> enrich(List<ContractBO> data) {
        if (CollUtil.isEmpty(data)) {
            return Collections.emptyList();
        }
        List<ContractVO> voList = ContractConvert.INSTANCE.toVOList(data);
        Set<Long> payerIds = data.stream().map(ContractBO::getOurPayerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> companyIds = data.stream().map(ContractBO::getVendorCompanyId).filter(Objects::nonNull).collect(Collectors.toSet());
        companyIds.addAll(payerIds);
        Set<Long> userIds = new HashSet<>();
        userIds.addAll(data.stream().map(ContractBO::getCreateBy).filter(Objects::nonNull).collect(Collectors.toSet()));
        userIds.addAll(data.stream().map(ContractBO::getUpdateBy).filter(Objects::nonNull).collect(Collectors.toSet()));

        Map<Long, String> companyNameMap = companyIds.isEmpty() ? Collections.emptyMap() :
                companyIds.stream()
                        .map(id -> companyFacade.queryOne(new CompanyQuery().setId(id)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(CompanyBO::getId, CompanyBO::getCompanyName, (a, b) -> a));
        Map<Long, String> userNameMap = userIds.isEmpty() ? Collections.emptyMap() :
                sysUserFacade.selectUserByIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName, (a, b) -> a));

        for (ContractVO vo : voList) {
            vo.setOurPayerName(companyNameMap.get(vo.getOurPayerId()));
            vo.setVendorCompanyName(companyNameMap.get(vo.getVendorCompanyId()));
            vo.setCreateName(userNameMap.get(vo.getCreateBy()));
            vo.setUpdateName(userNameMap.get(vo.getUpdateBy()));
        }
        return voList;
    }

    private String buildNotifyUrl(Long contractId) {
        String notify = esignProperties.getNotify();
        if (StrUtil.isBlank(notify)) {
            return null;
        }
        // notify 可能是裸域名或带占位符的字符串，这里附加合同回调路径
        if (notify.contains("%s") || notify.contains("%d")) {
            return notify;
        }
        return StrUtil.removeSuffix(notify, "/") + "/esign/callback/contract/" + contractId;
    }
}
