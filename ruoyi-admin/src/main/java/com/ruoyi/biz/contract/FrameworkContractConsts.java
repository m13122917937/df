package com.ruoyi.biz.contract;

import com.ruoyi.bill.constant.ContractConsts;

/**
 * 框架合同静态常量。
 *
 * @author ruoyi
 */
public interface FrameworkContractConsts {

    /**
     * 合同类型：框架协议
     */
    Integer CONTRACT_TYPE = ContractConsts.Type.FRAMEWORK.getCode();

    /**
     * 合同名称后缀
     */
    String CONTRACT_NAME_SUFFIX = "框架协议";

    /**
     * 签署方：我方
     */
    String SIGNER_OUR = "our";

    /**
     * 签署方：供应商
     */
    String SIGNER_VENDOR = "vendor";

    /**
     * 批量初始化备注
     */
    String INIT_REMARK = "历史企业批量初始化框架协议";
}
