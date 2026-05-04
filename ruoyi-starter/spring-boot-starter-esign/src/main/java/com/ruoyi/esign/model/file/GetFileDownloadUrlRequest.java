package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 获取文件下载链接请求参数
 * 文档：https://open.esign.cn/doc/opendoc/file-and-template/fmpsge
 *
 * @author ruoyi
 */
@Data
public class GetFileDownloadUrlRequest {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 链接有效期，单位秒
     * 默认为300秒（5分钟），最大不超过3600秒（1小时）
     */
    private Integer expiresIn = 300;
}
