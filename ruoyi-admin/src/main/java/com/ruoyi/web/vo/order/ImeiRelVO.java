package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class ImeiRelVO {


    /**
     * 主键
     */

    private Long id;
    /**
     * 类别
     */

    private String category;
    /**
     * 品牌
     */

    private String brand;
    /**
     * 状态 1待处理 2已对应 3已取消
     */

    private Integer status;
    /**
     * SKU编码
     */

    private String commonCode;
    /**
     * 型号
     */

    private String productName;
    /**
     * 型号
     */

    private String skuName;
    /**
     * 识别的skuId
     */
    private String snSkuId;
    /**
     * 识别型号
     */

    private String snModel;

    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
