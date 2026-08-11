package com.ruoyi.user.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信身份解析参数。
 */
@Data
@Accessors(chain = true)
public class WechatIdentityParam {
    private String channel;
    private String appId;
    private String openId;
    private String unionId;
}
