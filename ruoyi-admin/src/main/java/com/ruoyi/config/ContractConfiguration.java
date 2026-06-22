package com.ruoyi.config;

import com.ruoyi.config.properties.ContractProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 合同模块配置
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Configuration
@EnableConfigurationProperties(ContractProperties.class)
public class ContractConfiguration {
}
