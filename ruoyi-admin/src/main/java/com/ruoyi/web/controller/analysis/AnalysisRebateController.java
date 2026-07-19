package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisRebateBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisRebateSaveRequest;
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
 * 返利价保活动接口。
 */
@RestController
@RequestMapping("/analysis/rebate")
public class AnalysisRebateController extends BaseController {
    @Autowired
    private AnalysisRebateBizService rebateBizService;

    /**
     * 查询活动列表。
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public AjaxResult list() {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toRebateVOList(rebateBizService.list()));
    }

    /**
     * 保存活动及明细。
     */
    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult save(@Validated @RequestBody AnalysisRebateSaveRequest request) {
        return AjaxResult.success(rebateBizService.save(
                AnalysisWebConvert.INSTANCE.toRebateParam(request), getUserId()));
    }

    /**
     * 删除活动及明细。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult delete(@PathVariable Long id) {
        rebateBizService.delete(id);
        return AjaxResult.success();
    }
}
