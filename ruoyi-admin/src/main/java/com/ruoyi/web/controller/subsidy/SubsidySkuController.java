package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidySkuBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.subsidy.SubsidySkuForm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 国补后台 SKU 接口。 */
@RestController
@RequestMapping("/subsidy/skus")
public class SubsidySkuController {
    private final SubsidySkuBizService skuBizService;
    public SubsidySkuController(final SubsidySkuBizService skuBizService) { this.skuBizService = skuBizService; }
    /** 查询商品 SKU 列表。 */
    @GetMapping public AjaxResult list(@RequestParam final Long productId) { return AjaxResult.success(skuBizService.list(productId)); }
    /** 新增 SKU。 */
    @PostMapping public AjaxResult save(@RequestBody final SubsidySkuForm form) { skuBizService.save(form); return AjaxResult.success(); }
    /** 更新 SKU。 */
    @PutMapping("/{skuId}") public AjaxResult update(@PathVariable final Long skuId, @RequestBody final SubsidySkuForm form) { skuBizService.update(skuId, form); return AjaxResult.success(); }
}
