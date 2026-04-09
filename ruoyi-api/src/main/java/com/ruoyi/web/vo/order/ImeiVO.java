package com.ruoyi.web.vo.order;

import lombok.Data;

import java.util.Date;

@Data

public class ImeiVO {
    /**
     * 商品串码
     */
    private String sn;


    /**
     * 商品串码
     */

    private String imel;

    /**
     * 激活状态
     */

    private Integer activated;

    /**
     * 激活状态
     */

    private Date activatedTime;

    /**
     * 平台效验状态
     */

    private Integer platformImei;
    /**
     * 平台效验时间
     */

    private Date platformTime;
    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 识别出来的商品名称（型号不一致时返回）
     */
    private String recognizedProductName;

    /**
     * 识别出来的型号规格（型号不一致时返回）
     */
    private String recognizedSkuName;

}
