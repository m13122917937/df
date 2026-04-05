package com.ruoyi.esign.model;

import com.ruoyi.esign.enums.AuthStatusEnum;
import com.ruoyi.esign.enums.GenderEnum;
import lombok.Data;

/**
 * 个人认证信息响应
 *
 * @author ruoyi
 */
@Data
public class PersonalAuthInfo {

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
     * 第三方平台用户唯一标识
     */
    private String thirdPartyUserId;

    /**
     * e签宝用户ID
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 性别 1男 2女
     */
    private Integer gender;

    /**
     * 出生日期，格式：yyyy-MM-dd
     */
    private String birthday;

    /**
     * 民族
     */
    private String nation;

    /**
     * 地址
     */
    private String address;

    /**
     * 发证机关
     */
    private String issueAuthority;

    /**
     * 身份证有效期开始，格式：yyyy-MM-dd
     */
    private String startDate;

    /**
     * 身份证有效期结束，格式：yyyy-MM-dd
     */
    private String endDate;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

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

    /**
     * 获取性别枚举
     *
     * @return 性别枚举
     */
    public GenderEnum getGenderEnum() {
        if (this.gender == null) {
            return null;
        }
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getCode().equals(this.gender)) {
                return gender;
            }
        }
        return null;
    }
}
