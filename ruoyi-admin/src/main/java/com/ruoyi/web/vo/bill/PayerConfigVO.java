package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class PayerConfigVO {


    /**
     * $column.columnComment
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * 关键字
     */
    @ApiModelProperty("店铺名称")
    private String keyWord;
    /**
     * 关键字Id
     */
    private String platform;
    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体ID")
    private Long payerId;

    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体name")
    private String payerName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private Long createBy;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createByName;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改人
     */
    private String updateByName;

}
