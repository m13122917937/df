package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class ImeiVO {

    /**
     * 商品串码
     */

    private String sn;


    private String imel;
    /**
     * 激活状态
     */

    private Integer activated;
    /**
     * 激活状态
     */


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activatedTime;
    /**
     * 平台效验状态
     */

    private Integer platformImei;
    /**
     * 平台效验时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date platformTime;


}
