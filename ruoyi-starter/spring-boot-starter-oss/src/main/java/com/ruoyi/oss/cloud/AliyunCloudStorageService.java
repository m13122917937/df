/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.ruoyi.oss.cloud;

import com.aliyun.oss.OSSClient;
import com.ruoyi.oss.exception.OSSServiceException;
import com.ruoyi.oss.properties.OssProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云存储
 *
 * @author Mark sunlightcs@gmail.com
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSSClient client;

    public AliyunCloudStorageService(OssProperties ossProperties) {
        this.properties = ossProperties;
        //初始化
        init();
    }

    private void init() {
        client = new OSSClient(properties.getAliyunEndPoint(), properties.getAliyunAccessKeyId(),
                properties.getAliyunAccessKeySecret());
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            client.putObject(properties.getAliyunBucketName(), path, inputStream);
        } catch (Exception e) {
            throw new OSSServiceException("上传文件失败，请检查配置信息");
        }

        return properties.getAliyunDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(properties.getAliyunPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(properties.getAliyunPrefix(), suffix));
    }
}
