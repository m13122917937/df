package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 获取文件下载链接响应结果
 *
 * @author ruoyi
 */
@Data
public class GetFileDownloadUrlResponse {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 文件下载链接
     * 该链接有效期为参数指定的时间，过期后需要重新获取
     */
    private String downloadUrl;
}
