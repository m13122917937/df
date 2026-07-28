package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisStoreOptionBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经营核算店铺选项接口。
 */
@RestController
@RequestMapping("/analysis/store-options")
@RequiredArgsConstructor
public class AnalysisStoreOptionController {

    private final AnalysisStoreOptionBizService analysisStoreOptionBizService;

    /**
     * 查询平台与店铺联动选项。
     *
     * @return 店铺选项集合
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('analysis:config:list')")
    public AjaxResult list() {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toStoreOptionVOList(
                analysisStoreOptionBizService.listStoreOptions()));
    }
}
