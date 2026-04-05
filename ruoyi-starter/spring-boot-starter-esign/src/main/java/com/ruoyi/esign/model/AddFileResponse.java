package com.ruoyi.esign.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 添加签署文件响应结果
 *
 * @author ruoyi
 */
@Data
public class AddFileResponse {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 文件ID
     */
    @JSONField(name = "fileId")
    private String fileId;

    /**
     * 文件下载地址，有效期为永久
     */
    @JSONField(name = "fileUrl")
    private String fileUrl;
}
