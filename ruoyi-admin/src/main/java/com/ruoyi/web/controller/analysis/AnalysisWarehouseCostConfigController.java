package com.ruoyi.web.controller.analysis;
import com.ruoyi.biz.analysis.AnalysisWarehouseCostConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisWarehouseCostConfigSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/analysis/warehouse-cost")
public class AnalysisWarehouseCostConfigController extends BaseController {
 @Autowired private AnalysisWarehouseCostConfigBizService warehouseCostConfigBizService;
 @GetMapping("/list") @PreAuthorize("@ss.hasPermi('analysis:config:list')") public AjaxResult list(String monthValue) { com.ruoyi.analysis.model.query.AnalysisWarehouseCostConfigQuery query=new com.ruoyi.analysis.model.query.AnalysisWarehouseCostConfigQuery(); query.setMonthValue(monthValue); return AjaxResult.success(AnalysisWebConvert.INSTANCE.toWarehouseCostVOList(warehouseCostConfigBizService.list(query))); }
 @PostMapping("/save") @PreAuthorize("@ss.hasPermi('analysis:config:edit')") public AjaxResult save(@RequestBody AnalysisWarehouseCostConfigSaveRequest request) { return AjaxResult.success(warehouseCostConfigBizService.save(AnalysisWebConvert.INSTANCE.toWarehouseCostParam(request),getUserId())); }
}
