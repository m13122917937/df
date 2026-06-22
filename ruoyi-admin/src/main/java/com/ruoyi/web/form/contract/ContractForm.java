package com.ruoyi.web.form.contract;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 合同表单（新增/编辑）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@Accessors(chain = true)
public class ContractForm {

    @NotNull(groups = {UpdateGroup.class}, message = "id不能为空")
    private Long id;

    @NotBlank(groups = {AddGroup.class}, message = "合同名称不能为空")
    private String contractName;

    @NotNull(groups = {AddGroup.class}, message = "合同类型不能为空")
    private Integer contractType;

    @NotNull(groups = {AddGroup.class}, message = "我方主体不能为空")
    private Long ourPayerId;

    @NotNull(groups = {AddGroup.class}, message = "供应商不能为空")
    private Long vendorCompanyId;

    /**
     * 签署过期时间
     */
    private Date expireTime;

    /**
     * 备注
     */
    private String remark;
}
