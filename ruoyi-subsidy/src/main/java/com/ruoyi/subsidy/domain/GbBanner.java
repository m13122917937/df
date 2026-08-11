package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 国补商城轮播图。
 */
@Data
@Accessors(chain = true)
@TableName("gb_banner")
public class GbBanner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String bannerName;
    private String imageUrl;
    private String targetType;
    private String targetValue;
    private Integer sortOrder;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
