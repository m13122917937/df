package com.ruoyi.user.model.bo;

import lombok.Data;

/**
 * 微信身份解析结果。
 */
@Data
public class WechatIdentityBO {
    private Long memberId;
    private String identityStatus;
    private Boolean purchaseAllowed;
}
