package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderNewForm {

    @ApiModelProperty("省id")
    private Long province;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("商家单号")
    private String originalOrderId;

    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty("内部单号集合")
    private List<String> orderCodeList;

    @ApiModelProperty("商家单号集合")
    private List<String> originalOrderIdList;

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("店铺名称")
    private String shopName;

    @ApiModelProperty("商品名称")
    private String productNameLike;

    @ApiModelProperty("规格名称")
    private String skuNameLike;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("最晚发货时间开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastShippingTimeStart;

    /**
     * 最晚发货时间
     */
    @ApiModelProperty("最晚发货时间结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastShippingTimeEnd;

    @ApiModelProperty(hidden = false)
    private Integer orderType;
//
//    @ApiModelProperty("商品编码")
//    private String skuCode;
//
//    @ApiModelProperty(hidden = true)
//    private List<Integer> statusList;

//    /**
//     * NOT_SUPPLEMENTED(1, "未补"),
//     * <p>
//     * SUCCESS(2, "已成功补地址");
//     */
//    @ApiModelProperty(value = "地址状态：1, 未补； 2 地址已经完成", hidden = true)
//    private Integer addressStatus;

//
//    @ApiModelProperty("订单子状态，  不传 全部 ， 81、82 物流异常， 83、84、85 串码异常")
//    private List<Integer> subStatusList;
}
