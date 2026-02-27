package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("入仓订单参数")
public class WarehousingOrderParam {

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("商品名")
    private String productName;

    @ApiModelProperty("企业id")
    private Long companyId;

    @ApiModelProperty("状态: 5：当日发货，6：在途,10 确认收货,11 已经撤销")
    private List<Integer> statusList;


    @ApiModelProperty("拣货号码")
    private Long deliveryCode;

}
