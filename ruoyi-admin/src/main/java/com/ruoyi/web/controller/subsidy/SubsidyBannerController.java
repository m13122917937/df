package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyBannerBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.subsidy.SubsidyBannerForm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 国补后台轮播图接口。 */
@RestController
@RequestMapping("/subsidy/banners")
public class SubsidyBannerController {
    private final SubsidyBannerBizService bannerBizService;
    public SubsidyBannerController(final SubsidyBannerBizService bannerBizService) { this.bannerBizService = bannerBizService; }
    /** 查询轮播图列表。 */
    @GetMapping public AjaxResult list() { return AjaxResult.success(bannerBizService.list()); }
    /** 新增轮播图。 */
    @PostMapping public AjaxResult save(@RequestBody final SubsidyBannerForm form) { bannerBizService.save(form); return AjaxResult.success(); }
    /** 更新轮播图。 */
    @PutMapping("/{bannerId}") public AjaxResult update(@PathVariable final Long bannerId, @RequestBody final SubsidyBannerForm form) { bannerBizService.update(bannerId, form); return AjaxResult.success(); }
}
