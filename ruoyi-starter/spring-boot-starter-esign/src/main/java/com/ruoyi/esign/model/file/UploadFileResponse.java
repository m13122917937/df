package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 上传文件响应结果
 *
 * @author ruoyi
 */
@Data
public class UploadFileResponse {

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
     * 文件ID
     * 由e签宝系统生成，后续操作文件使用该ID
     */
    private String fileId;

    /**
     * 文件存储地址
     */
    private String fileUrl;
}
