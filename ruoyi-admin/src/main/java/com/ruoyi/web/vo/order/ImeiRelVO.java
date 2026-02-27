package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("串码接口列表")
@Data
public class ImeiRelVO {


    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 类别
     */
    @ApiModelProperty("类别")
    private String category;
    /**
     * 品牌
     */
    @ApiModelProperty("品牌")
    private String brand;
    /**
     * 状态 1待处理 2已对应 3已取消
     */
    @ApiModelProperty("状态 1待处理 2已对应 3已取消")
    private Integer status;
    /**
     * SKU编码
     */
    @ApiModelProperty("SKU编码")
    private String commonCode;
    /**
     * 型号
     */
    @ApiModelProperty("商品名")
    private String productName;
    /**
     * 型号
     */
    @ApiModelProperty("sku名")
    private String skuName;
    /**
     * 识别的skuId
     */
    private String snSkuId;
    /**
     * 识别型号
     */
    @ApiModelProperty("识别型号")
    private String snModel;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
