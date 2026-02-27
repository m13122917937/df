package com.ruoyi.config;

import com.fasc.open.api.config.HttpConfig;
import com.fasc.open.api.stratey.DefaultJsonStrategy;
import com.fasc.open.api.v5_1.client.CorpClient;
import com.fasc.open.api.v5_1.client.OpenApiClient;
import com.fasc.open.api.v5_1.client.ServiceClient;
import com.ruoyi.config.properties.FadadaProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(value = {FadadaProperties.class})
public class FadadaConfiguration {

    @Bean("openApiClient")
    public OpenApiClient openApiClient(final FadadaProperties fadadaProperties) {
        OpenApiClient openApiClient = new OpenApiClient(fadadaProperties.getAppId(), fadadaProperties.getAppSecret(), fadadaProperties.getServerUrl());
        HttpConfig httpConfig=new HttpConfig();
        httpConfig.setReadTimeout(fadadaProperties.getReadTimeout());
        httpConfig.setConnectTimeout(fadadaProperties.getConnectTimeout());
        openApiClient.setHttpConfig(httpConfig);
        openApiClient.setJsonStrategy(new DefaultJsonStrategy());
        return openApiClient;
    }

    @Bean()
    public ServiceClient serviceClient(final OpenApiClient openApiClient) {
        return new ServiceClient(openApiClient);
    }

    @Bean()
    public CorpClient corpClient(final OpenApiClient openApiClient) {
        return new CorpClient(openApiClient);
    }


}
