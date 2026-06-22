package com.ruoyi.biz.contract;

import com.ruoyi.bill.constant.ContractConsts;

/**
 * 销售合同静态常量。
 *
 * @author ruoyi
 */
public interface SalesContractConsts {

    /**
     * 合同类型：销售合同
     */
    Integer CONTRACT_TYPE = ContractConsts.Type.PURCHASE.getCode();

    /**
     * 合同名称后缀
     */
    String CONTRACT_NAME_SUFFIX = "销售合同";

    /**
     * 签署方：我方
     */
    String SIGNER_OUR = "our";

    /**
     * 签署方：供应商
     */
    String SIGNER_VENDOR = "vendor";
}
