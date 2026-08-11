package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyInventoryBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.subsidy.SubsidyInventoryAdjustForm;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 国补后台库存接口。 */
@RestController
@RequestMapping("/subsidy/skus")
public class SubsidyInventoryController {
    private final SubsidyInventoryBizService inventoryBizService;

    public SubsidyInventoryController(final SubsidyInventoryBizService inventoryBizService) {
        this.inventoryBizService = inventoryBizService;
    }

    /** 调整库存并保留审计流水。 */
    @PostMapping("/{skuId}/inventory-adjustments")
    public AjaxResult adjust(@PathVariable final Long skuId, @RequestBody final SubsidyInventoryAdjustForm form) {
        inventoryBizService.adjust(skuId, form);
        return AjaxResult.success();
    }
}
