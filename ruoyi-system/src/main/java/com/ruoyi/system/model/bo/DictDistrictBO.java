package com.ruoyi.system.model.bo;

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
public class DictDistrictBO {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
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
