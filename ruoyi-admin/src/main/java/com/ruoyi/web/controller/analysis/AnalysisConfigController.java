package com.ruoyi.web.controller.analysis;

import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.biz.analysis.AnalysisConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public AjaxResult list(AnalysisQuery query) {
        return AjaxResult.success(configBizService.list(query));
    }

    /**
     * 新增或更新配置。
     */
    @PostMapping("/save")
    public AjaxResult save(@Validated @RequestBody AnalysisCostConfigParam param) {
        return AjaxResult.success(configBizService.save(param, getLoginUser()));
    }

    /**
     * 删除配置。
     */
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        configBizService.delete(id);
        return AjaxResult.success();
    }
}
