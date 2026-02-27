package com.ruoyi.web.form.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资金流水明细对象 f_transactions
 *
 * @author ruoyi
 * @date 2025-12-01
 */
@Data
@Accessors(chain = true)
public class TransactionsForm {
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID（序号）
     */
    @ApiModelProperty("流水ID（序号）")
    @NotNull(groups = {UpdateGroup.class}, message = "流水不能为空")
    private Long id;

    /**
     * 所属账户ID，外键关联 accounts.account_id
     */
    @ApiModelProperty("所属账户ID")
    @NotNull(groups = {AddGroup.class}, message = "账户ID不能为空")
    private Long accountId;

    /**
     * 交易日期
     */
    @ApiModelProperty("交易日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionDate;

    /**
     * 类别，0收入，1支出
     */
    @ApiModelProperty("类别，0收入，1支出")
    @NotNull(groups = {AddGroup.class}, message = "交易类别不能为空")
    private Integer category;

    /**
     * 子类别，如工资、餐饮、交通、转账等
     */
    @ApiModelProperty("子类别，如工资、餐饮、交通、转账等")
    private String subCategory;

    /**
     * 交易金额（统一用正数）
     */
    @ApiModelProperty("交易金额（统一用正数）")
    private BigDecimal amount;

    /**
     * 具体支付方式（如刷卡、扫码、转账等）
     */
    @ApiModelProperty("具体支付方式（如刷卡、扫码、转账等）")
    private String paymentMethod;

    /**
     * 交易对方（如"星巴克"、"张三"、"工资入账"）
     */
    @ApiModelProperty("交易对方（如\"星巴克\"、\"张三\"、\"工资入账\"）")
    private String counterparty;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;

}