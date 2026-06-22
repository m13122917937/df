package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 合同相关配置
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Data
@ConfigurationProperties(prefix = "contract")
public class ContractProperties {

    /**
     * 集团公司（我方框架协议主体）名称，匹配 u_company.company_name
     */
    private String groupPayerName = "四川畛域无界电子商务有限公司";
}
