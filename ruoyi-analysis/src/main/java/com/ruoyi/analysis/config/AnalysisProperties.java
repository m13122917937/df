package com.ruoyi.analysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 经营分析模块配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "analysis")
public class AnalysisProperties {

    /**
     * 模块上线日期；该日期之前的数据永不写入分析表。
     */
    private LocalDate goLiveDate = LocalDate.of(2099, 1, 1);

    /**
     * 每日同步任务表达式。
     */
    private String syncCron = "0 0 2 * * ?";
}
