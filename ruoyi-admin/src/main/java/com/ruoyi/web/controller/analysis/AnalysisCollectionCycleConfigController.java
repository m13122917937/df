package com.ruoyi.web.controller.analysis;
import com.ruoyi.biz.analysis.AnalysisCollectionCycleConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisCollectionCycleConfigQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisCollectionCycleConfigSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/** 回款周期配置接口。 */
@RestController
@RequestMapping("/analysis/collection-cycle")
public class AnalysisCollectionCycleConfigController extends BaseController {
    @Autowired private AnalysisCollectionCycleConfigBizService collectionCycleConfigBizService;
    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public AjaxResult list(AnalysisCollectionCycleConfigQueryRequest request) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toCollectionCycleVOList(collectionCycleConfigBizService.list(AnalysisWebConvert.INSTANCE.toCollectionCycleQuery(request))));
    }
    @PostMapping("/save") @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult save(@RequestBody AnalysisCollectionCycleConfigSaveRequest request) {
        return AjaxResult.success(collectionCycleConfigBizService.save(AnalysisWebConvert.INSTANCE.toCollectionCycleParam(request), getUserId()));
    }
}
