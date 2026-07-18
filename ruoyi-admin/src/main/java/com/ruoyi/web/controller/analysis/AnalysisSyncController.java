package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisSyncBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 经营分析同步与重算接口。
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisSyncController extends BaseController {
    @Autowired
    private AnalysisSyncBizService syncBizService;

    /**
     * 手动同步指定自然日。
     */
    @PostMapping("/sync/run")
    public AjaxResult sync(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return AjaxResult.success(syncBizService.sync(date));
    }

    /**
     * 重算指定自然日。
     */
    @PostMapping("/calculate/rebuild")
    public AjaxResult rebuild(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        syncBizService.rebuild(date);
        return AjaxResult.success();
    }

    /**
     * 查询同步日志。
     */
    @GetMapping("/sync/logs")
    public AjaxResult logs(@RequestParam(defaultValue = "50") int limit) {
        return AjaxResult.success(syncBizService.logs(limit));
    }
}
