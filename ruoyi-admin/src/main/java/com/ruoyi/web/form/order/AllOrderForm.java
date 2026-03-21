package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AllOrderForm {

    @ApiModelProperty(value = "内部订单订单号", example = "[\"0002\" ,\"0001\" ,  ]")
    private List<String> orderCodeList;

    @ApiModelProperty(value = "商家单号", example = "[\"0002\" ,\"0001\" ,  ]")
    private List<String> originalOrderIdList;

    @ApiModelProperty(value = "旺店通单号", example = "[\"0002\" ,\"0001\" ,  ]")
    private List<String> erpOrderIdList;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "开始创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createStartTime;

    @ApiModelProperty(value = "结束创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createEndTime;

    @ApiModelProperty(value = "开始创建时间（兼容字段）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeStart;

    @ApiModelProperty(value = "结束创建时间（兼容字段）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;


    @ApiModelProperty(value = "发货开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendStartTime;

    @ApiModelProperty(value = "发货结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendNedTime;

    @ApiModelProperty(value = "收货时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedStartTime;

    @ApiModelProperty(value = "收货时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedEndTime;

    @ApiModelProperty(value = "订单状态:")
    private List<Integer> statusList;

    @ApiModelProperty(value = "采购类型：1入仓、2代发")
    private Integer orderType;
}
