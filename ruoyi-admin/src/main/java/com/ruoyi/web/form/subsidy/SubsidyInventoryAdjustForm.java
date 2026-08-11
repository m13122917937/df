package com.ruoyi.web.form.subsidy;

import lombok.Data;

/** 国补后台库存调整请求。 */
@Data
public class SubsidyInventoryAdjustForm {
    private Integer delta;
    private String remark;
}
