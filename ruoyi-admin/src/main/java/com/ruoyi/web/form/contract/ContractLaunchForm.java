package com.ruoyi.web.form.contract;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 发起签署表单
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractLaunchForm {

    /**
     * 合同主键
     */
    @NotNull(message = "合同id不能为空")
    private Long id;

    /**
     * e签宝文件id
     */
    @NotNull(message = "文件id不能为空")
    private String esignFileId;

    /**
     * 我方签署人姓名
     */
    private String ourSignerName;

    /**
     * 我方经办人手机号
     */
    private String ourOperatorMobile;

    /**
     * 我方经办人证件号
     */
    private String ourOperatorIdNumber;

    /**
     * 我方印章ID
     */
    private String ourSealId;

    /**
     * 供应商签署经办人姓名
     */
    private String vendorSignerName;

    /**
     * 供应商签署经办人手机号
     */
    private String vendorMobile;

    /**
     * 供应商签署经办人证件号
     */
    private String vendorIdNumber;
}
