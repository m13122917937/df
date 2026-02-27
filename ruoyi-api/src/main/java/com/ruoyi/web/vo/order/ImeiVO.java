package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("填写imei码")
public class ImeiVO {
    /**
     * 商品串码
     */
    private String sn;


    /**
     * 商品串码
     */
    @ApiModelProperty("商品串码")
    private String imel;

    /**
     * 激活状态
     */
    @ApiModelProperty("激活状态,  0 待查寻 ； 1 已激活 2 未激活  3 型号不一致， 4 串码不存在")
    private Integer activated;

    /**
     * 激活状态
     */
    @ApiModelProperty("激活查询时间")
    private Date activatedTime;

    /**
     * 平台效验状态
     */
    @ApiModelProperty("平台二销,  0 待查寻 ； 1 风险 2 正常")
    private Integer platformImei;
    /**
     * 平台效验时间
     */
    @ApiModelProperty("平台效验时间")
    private Date platformTime;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;



}
