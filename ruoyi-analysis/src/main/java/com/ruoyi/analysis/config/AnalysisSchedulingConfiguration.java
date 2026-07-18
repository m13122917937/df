package com.ruoyi.analysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 经营分析代码调度配置，不依赖既有 sys_job 表。
 */
@Configuration
@EnableScheduling
public class AnalysisSchedulingConfiguration {
}
