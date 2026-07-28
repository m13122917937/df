package com.ruoyi.web.controller.analysis;

import com.ruoyi.analysis.model.param.AnalysisPlatformFeeRateParam;
import com.ruoyi.analysis.model.query.AnalysisPlatformFeeRateQuery;
import com.ruoyi.biz.analysis.AnalysisPlatformFeeRateBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisPlatformFeeRateSaveRequest;
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
 * 平台服务费率配置接口。
 */
@RestController
@RequestMapping("/analysis/platform-fee-rate")
public class AnalysisPlatformFeeRateController extends BaseController {
    @Autowired
    private AnalysisPlatformFeeRateBizService platformFeeRateBizService;

    /**
     * 查询平台服务费率列表。
     *
     * @param query 查询条件
     * @return 费率列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('analysis:platformFeeRate:list')")
    public AjaxResult list(AnalysisPlatformFeeRateQuery query) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toPlatformFeeRateVOList(
                platformFeeRateBizService.list(query)));
    }

    /**
     * 新增或修改平台服务费率。
     *
     * @param request 保存请求
     * @return 配置主键
     */
    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermi('analysis:platformFeeRate:edit')")
    public AjaxResult save(@Validated @RequestBody AnalysisPlatformFeeRateSaveRequest request) {
        AnalysisPlatformFeeRateParam param = AnalysisWebConvert.INSTANCE.toPlatformFeeRateParam(request);
        return AjaxResult.success(platformFeeRateBizService.save(param, getUserId()));
    }

    /**
     * 删除平台服务费率。
     *
     * @param id 配置主键
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('analysis:platformFeeRate:edit')")
    public AjaxResult delete(@PathVariable Long id) {
        platformFeeRateBizService.delete(id);
        return AjaxResult.success();
    }
}
