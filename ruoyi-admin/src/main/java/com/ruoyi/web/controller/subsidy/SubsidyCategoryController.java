package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyCategoryBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.subsidy.SubsidyCategoryForm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 国补后台分类接口。 */
@RestController
@RequestMapping("/subsidy/categories")
public class SubsidyCategoryController {
    private final SubsidyCategoryBizService categoryBizService;

    public SubsidyCategoryController(final SubsidyCategoryBizService categoryBizService) {
        this.categoryBizService = categoryBizService;
    }

    /** 查询分类列表。 */
    @GetMapping
    public AjaxResult list() {
        return AjaxResult.success(categoryBizService.list());
    }

    /** 新增分类。 */
    @PostMapping
    public AjaxResult save(@RequestBody final SubsidyCategoryForm form) {
        categoryBizService.save(form);
        return AjaxResult.success();
    }

    /** 更新分类。 */
    @PutMapping("/{categoryId}")
    public AjaxResult update(@PathVariable final Long categoryId, @RequestBody final SubsidyCategoryForm form) {
        categoryBizService.update(categoryId, form);
        return AjaxResult.success();
    }
}
