package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("imei-vo")
@Data
public class ImeiVO {

    /**
     * 商品串码
     */
    @ApiModelProperty("sn")
    private String sn;

    @ApiModelProperty("imei码")
    private String imel;
    /**
     * 激活状态
     */
    @ApiModelProperty("激活状态,  0 待查寻 ； 1 已激活 2 未激活  3 型号不一致")
    private Integer activated;
    /**
     * 激活状态
     */

    @ApiModelProperty("激活查询时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activatedTime;
    /**
     * 平台效验状态
     */
    @ApiModelProperty("平台二销,  0 待查寻 ； 1 风险 2 正常")
    private Integer platformImei;
    /**
     * 平台效验时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date platformTime;


}
