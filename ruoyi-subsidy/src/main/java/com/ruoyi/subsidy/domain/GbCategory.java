package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补商城分类。
 */
@Data
@Accessors(chain = true)
@TableName("gb_category")
public class GbCategory {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String categoryName;
    private String iconUrl;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private String saleProvinces;
    private Integer sortOrder;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
