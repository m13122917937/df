package com.ruoyi.esign.model;

import lombok.Data;

import java.util.List;

/**
 * 创建签署流程请求参数
 *
 * @author ruoyi
 */
@Data
public class CreateFlowRequest {

    /**
     * 业务自定义流水号
     */
    private String businessId;

    /**
     * 签署流程名称，最大长度不超过50字符
     */
    private String flowName;

    /**
     * 回调通知地址，e签宝会通过该地址向您推送签署流程状态变更事件
     */
    private String notifyUrl;

    /**
     * 是否允许修改签署日期，默认false
     * true - 允许签署方修改签署日期；false - 不允许
     */
    private Boolean allowModifySignDate = false;

    /**
     * 流程到期时间，时间戳格式，毫秒，到期后流程会自动终止无法再签署
     */
    private Long expireTime;

    /**
     * 签署流程类型，默认 1 - 标准签署流程
     * 1 - 标准签署；2 - 签批签署
     */
    private Integer flowType = 1;

    /**
     * 是否开启扫码签，默认false
     * 开启后，所有签署人可以通过扫码方式进行签署
     */
    private Boolean enableScanCodeSign = false;

    /**
     * 重发通知间隔时间，单位秒，默认 86400 秒（1天）
     */
    private Integer resendInterval = 86400;

    /**
     * 最大重发通知次数，默认 0 次不限制
     */
    private Integer maxResendCount = 0;

    /**
     * 是否自动发起，默认 true
     * true - 添加完所有签署内容后自动发起；false - 需要手动调用发起接口
     */
    private Boolean autoStart = true;

    /**
     * 签署完成后是否自动归档，默认 true
     */
    private Boolean autoArchive = true;

    /**
     * 企业盖章方式，默认 1 - 自动盖章
     * 1 - 自动盖章；2 - 手动盖章
     */
    private Integer orgSealMode = 1;

    /**
     * 透传字段，回调时原样返回，最大长度不超过2048字符
     */
    private String transData;
}
