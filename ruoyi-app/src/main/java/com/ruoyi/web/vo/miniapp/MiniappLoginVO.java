package com.ruoyi.web.vo.miniapp;

import lombok.Data;

/**
 * 小程序登录响应。
 */
@Data
public class MiniappLoginVO {
    private String token;
    private String identityStatus;
    private Boolean purchaseAllowed;
}
