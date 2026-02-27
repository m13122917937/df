package com.ruoyi.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = OssProperties.OSS_PREFIX)
public class OssProperties {

    public static final String OSS_PREFIX = "fy.oss";

    private String aliyunDomain;
    //阿里云路径前缀
    private String aliyunPrefix;
    //阿里云EndPoint
    private String aliyunEndPoint;
    //阿里云AccessKeyId
    private String aliyunAccessKeyId;
    //阿里云AccessKeySecret
    private String aliyunAccessKeySecret;
    //阿里云BucketName
    private String aliyunBucketName;
}
