package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 添加签署文件请求参数
 *
 * @author ruoyi
 */
@Data
public class AddFileRequest {

    /**
     * 签署流程ID
     */
    private String flowId;

    /**
     * 文件ID，用于唯一标识本次添加的文件
     * 由32位uuid组成，不重复即可
     */
    private String fileId;

    /**
     * 文件名称，需携带文件后缀，例如：合同.pdf
     */
    private String fileName;

    /**
     * 文件类型，支持pdf，不区分大小写，不传默认为pdf
     */
    private String fileType = "pdf";

    /**
     * 文件下载地址，需要GET请求可以直接下载文件
     * fileContent和url必须二选一传入
     */
    private String url;

    /**
     * 文件内容Base64编码
     * fileContent和url必须二选一传入
     */
    private String fileContent;

    /**
     * 是否需要加密存储，默认false
     * true - 需要加密存储；false - 不需要
     */
    private Boolean encrypted = false;
}
