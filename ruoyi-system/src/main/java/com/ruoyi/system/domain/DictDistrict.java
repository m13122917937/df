package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 【请填写功能名称】对象 sys_dict_district
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Data
@Accessors(chain = true)
@TableName("sys_dict_district")
public class DictDistrict {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId
    private Long districtId;
    /**
     * $column.columnComment
     */
    private Long pid;
    /**
     * $column.columnComment
     */
    private String district;
    /**
     * $column.columnComment
     */
    private Integer level;


}
