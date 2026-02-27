package com.ruoyi.express.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

import java.util.Date;

/**
 * 【请填写功能名称】对象 o_express_contrast
 * 
 * @author ruoyi
 * @date 2025-10-11
 */
@Data
@Accessors(chain = true)
public class ExpressContrastBO {
    private static final long serialVersionUID = 1L;

            /** 主键 */
        private Long id;
            /** 平台 */
        private String platform;
            /** 类别 */
        private String category;
            /** 快递公司 */
        private String logisticsCode;
            /** 创建人 */
        private Long createBy;
            /** 创建时间 */
        private Date createTime;
            /** 修改人 */
        private Long updateBy;
            /** 修改时间 */
        private Date updateTime;
            /** 品牌 */
        private String brand;


}
