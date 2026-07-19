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
    private String goLiveDate = "2099-01-01";

    /**
     * 获取按 ISO 日期格式解析的模块上线日期。
     *
     * @return 上线日期
     */
    public LocalDate getGoLiveLocalDate() {
        return LocalDate.parse(goLiveDate);
    }

}
