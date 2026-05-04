package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 查询文件信息响应结果
 *
 * @author ruoyi
 */
@Data
public class GetFileInfoResponse {

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
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件存储地址
     */
    private String fileUrl;

    /**
     * 文件MD5值
     */
    private String fileMd5;
}
