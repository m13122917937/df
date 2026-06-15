package com.ruoyi.web.form.bill;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款账号维护对象 f_payer
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
public class PayerForm {
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 吉客云编号
     */
    @NotBlank(groups = {AddGroup.class}, message = "吉客云编号不能为空")
    private String outCode;

    /**
     * $column.columnComment
     */
    @NotNull(groups = {UpdateGroup.class}, message = "id不能为空")

    private Long id;
    /**
     * 付款户名
     */

    @NotBlank(groups = {AddGroup.class}, message = "付款户名不能为空")
    private String payName;
    /**
     * 银行信息
     */

    @NotBlank(groups = {AddGroup.class}, message = "银行信息不能为空")
    private String bankName;
    /**
     * 银行卡号
     */

    @NotBlank(groups = {AddGroup.class}, message = "银行卡号不能为空")
    private String payNo;



    /**
     * 是否激活， 0 激活， 1弃用
     */

    @NotNull(groups = {AddGroup.class}, message = "是否激活不能为空")
    private Integer actived;
    /**
     * 账户余额
     */

    @NotNull(groups = {AddGroup.class}, message = "账户余额不能为空")
    private BigDecimal balance;

    /**
     * 创建时间
     */

    private Date createTime;
    /**
     * 创建人
     */


    private Long createBy;
    /**
     * 修改时间
     */


    private Date updateTime;
    /**
     * 修改人
     */


    private Long updateBy;


}
