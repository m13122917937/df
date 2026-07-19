package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisCostConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisQueryRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 经营核算配置接口。
 */
@RestController
@RequestMapping("/analysis/config")
public class AnalysisConfigController extends BaseController {
    @Autowired
    private AnalysisConfigBizService configBizService;

    /**
     * 查询配置列表。
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public AjaxResult list(AnalysisQueryRequest request) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toCostConfigVOList(
                configBizService.list(AnalysisWebConvert.INSTANCE.toQuery(request))));
    }

    /**
     * 新增或更新配置。
     */
    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult save(@Validated @RequestBody AnalysisCostConfigSaveRequest request) {
        return AjaxResult.success(configBizService.save(
                AnalysisWebConvert.INSTANCE.toCostConfigParam(request), getUserId()));
    }

    /**
     * 删除配置。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('analysis:config:edit')")
    public AjaxResult delete(@PathVariable Long id) {
        configBizService.delete(id);
        return AjaxResult.success();
    }

    /**
     * 下载导入模板。
     */
    @PostMapping("/template")
    @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public void template(HttpServletResponse response) throws IOException {
        configBizService.downloadTemplate(response);
    }

    /**
     * 导出核算配置。
     */
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermi('analysis:config:export')")
    public void export(AnalysisQueryRequest request, HttpServletResponse response) throws IOException {
        configBizService.export(AnalysisWebConvert.INSTANCE.toQuery(request), response);
    }

    /**
     * 导入核算配置。
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('analysis:config:import')")
    public AjaxResult importExcel(@RequestParam String configType,
                                  @RequestParam(defaultValue = "false") boolean overwrite,
                                  @RequestParam("file") MultipartFile file) {
        return AjaxResult.success(configBizService.importExcel(configType, overwrite, file, getUserId()));
    }
}
