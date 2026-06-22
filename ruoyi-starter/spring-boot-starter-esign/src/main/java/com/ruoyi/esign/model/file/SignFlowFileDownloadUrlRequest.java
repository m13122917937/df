package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 下载已签署文件及附属材料请求参数。
 *
 * @author ruoyi
 */
@Data
public class SignFlowFileDownloadUrlRequest {

    /**
     * 签署流程ID
     */
    private String signFlowId;

    /**
     * 下载链接有效期，单位秒，默认3600秒
     */
    private Integer urlAvailableDate;

    /**
     * 是否内网地址
     */
    private Boolean internalUrl;

    /**
     * 是否使用AES加密文件
     */
    private Boolean aesEncrypt;

    /**
     * 文件加密时使用的RSA公钥
     */
    private String rsaSecret;

    /**
     * RSA公钥版本标识
     */
    private String rsaSecretKey;
}
