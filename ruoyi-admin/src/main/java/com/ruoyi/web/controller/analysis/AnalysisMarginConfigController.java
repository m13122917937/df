package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisMarginConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisMarginConfigQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisMarginConfigSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 保证金配置接口。
 */
@RestController
@RequestMapping("/analysis/margin")
public class AnalysisMarginConfigController extends BaseController {
    @Autowired
    private AnalysisMarginConfigBizService marginConfigBizService;

    /**
     * 查询保证金配置列表。
     *
     * @param request 查询条件
     * @return 保证金配置列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public AjaxResult list(AnalysisMarginConfigQueryRequest request) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toMarginConfigVOList(
                marginConfigBizService.list(AnalysisWebConvert.INSTANCE.toMarginConfigQuery(request))));
    }

    /**
     * 新增或修改保证金配置。
     *
     * @param request 保存请求
     * @return 配置主键
     */
    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult save(@Validated @RequestBody AnalysisMarginConfigSaveRequest request) {
        return AjaxResult.success(marginConfigBizService.save(
                AnalysisWebConvert.INSTANCE.toMarginConfigParam(request), getUserId()));
    }

    /**
     * 删除保证金配置。
     *
     * @param id 配置主键
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult delete(@PathVariable Long id) {
        marginConfigBizService.delete(id);
        return AjaxResult.success();
    }
}
