package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("所有订单申请")
public class AllOrderForm {

    @ApiModelProperty("订单编码集合")
    private List<String> orderCodeList;

    @ApiModelProperty("商家单号集合")
    private List<String> originalOrderIdList;

    @ApiModelProperty("旺店通单号集合")
    private List<String> erpOrderIdList;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("创建时间-开始")
    private Date createStartTime;

    @ApiModelProperty("创建时间-结束")
    private Date createEndTime;

    @ApiModelProperty("创建时间-开始（兼容字段）")
    private Date createTimeStart;

    @ApiModelProperty("创建时间-结束（兼容字段）")
    private Date createTimeEnd;

    @ApiModelProperty("发货时间-开始")
    private Date sendStartTime;

    @ApiModelProperty("发货时间-结束")
    private Date sendNedTime;

    @ApiModelProperty("收货时间-开始")
    private Date signedStartTime;

    @ApiModelProperty("收货时间-结束")
    private Date signedEndTime;

    @ApiModelProperty(value = "公司ID", hidden = true)
    private Long companyId;

    @ApiModelProperty("订单状态， ")
    private List<Integer> statusList;

    @ApiModelProperty("采购类型：1入仓、2代发")
    private Integer orderType;
}
