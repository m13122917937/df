package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 获取签署页面URL请求参数
 *
 * @author ruoyi
 */
@Data
public class GetSignUrlRequest {

    /**
     * 签署流程ID
     */
    private String flowId;

    /**
     * 签署人ID
     */
    private String signerId;

    /**
     * 打开方式，默认 1 - 新窗口打开
     * 1 - 新窗口打开；2 - 当前窗口打开
     */
    private Integer openMode = 1;

    /**
     * 签署完成后跳转地址，优先级高于后台配置的redirectUrl
     */
    private String redirectUrl;
}
