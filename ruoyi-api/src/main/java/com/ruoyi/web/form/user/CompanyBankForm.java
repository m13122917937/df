package com.ruoyi.web.form.user;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class CompanyBankForm {


    @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 企业主键
     */

    private Long companyId;

    /**
     * 开户银行
     */

    @NotBlank(message = "开户银行不能为空", groups = {AddGroup.class})
    private String accountBank;
    /**
     * 银行账号
     */

    @NotBlank(message = "银行账号不能为空", groups = {AddGroup.class})
    private String bankAccount;
    /**
     * 注册省
     */

    @NotNull(message = "注册省ID不能为空", groups = {AddGroup.class})
    private Long province;
    /**
     * 注册市
     */

    @NotNull(message = "注册市ID不能为空", groups = {AddGroup.class})
    private Long city;
    /**
     * 是否默认 0 否 1 是
     */

//    @NotNull(message = "是否默认不能为空",groups = {AddGroup.class})
    private Integer defaulted;



}
