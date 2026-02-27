package com.ruoyi.web.form.bill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class DeductionListForm {


    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    private Long companyId;

    /**
     * 订单编号
     *
     */
    @ApiModelProperty("订单编号")
    private List<String> orderCodeList;

    /**
     * 商家单号
     */
    @ApiModelProperty("商家单号")
    private List<String> originalOrderIdList;


}
