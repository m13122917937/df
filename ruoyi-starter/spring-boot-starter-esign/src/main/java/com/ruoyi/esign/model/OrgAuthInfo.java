package com.ruoyi.esign.model;

import com.ruoyi.esign.enums.AuthStatusEnum;
import lombok.Data;

/**
 * 机构认证信息响应
 *
 * @author ruoyi
 */
@Data
public class OrgAuthInfo {

    /**
     * 认证流程ID
     */
    private String authFlowId;

    /**
     * 认证状态
     * 1 - 认证中，2 - 认证成功，3 - 认证失败，4 - 认证取消
     */
    private Integer status;

    /**
     * 第三方平台用户唯一标识（办理人）
     */
    private String thirdPartyUserId;

    /**
     * e签宝机构ID
     */
    private String orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构证件类型
     * 1 - 营业执照
     * 2 - 组织机构代码证
     * 3 - 统一社会信用代码
     */
    private Integer orgCertType;

    /**
     * 机构证件号
     */
    private String orgCertNo;

    /**
     * 机构地址
     */
    private String orgAddress;

    /**
     * 法定代表人姓名
     */
    private String legalRepName;

    /**
     * 办理人姓名
     */
    private String agentName;

    /**
     * 办理人身份证号
     */
    private String agentIdCard;

    /**
     * 办理人手机号码
     */
    private String agentMobile;

    /**
     * e签宝办理人用户ID
     */
    private String agentUserId;

    /**
     * 是否完成法定代表人认证
     * true - 已完成，false - 未完成
     */
    private Boolean legalRepAuthed;

    /**
     * 认证失败原因
     */
    private String failReason;

    /**
     * 获取认证状态枚举
     *
     * @return 认证状态枚举
     */
    public AuthStatusEnum getStatusEnum() {
        return AuthStatusEnum.fromCode(this.status);
    }
}
