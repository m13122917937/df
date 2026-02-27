package com.ruoyi.oss;

import com.ruoyi.oss.cloud.AliyunCloudStorageService;
import com.ruoyi.oss.cloud.CloudStorageService;
import com.ruoyi.oss.properties.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Autowired
    OssProperties ossProperties;

    @Bean
    public CloudStorageService cloudStorageService() {
        CloudStorageService cloudStorageService = new AliyunCloudStorageService(ossProperties);
        return cloudStorageService;
    }

}
