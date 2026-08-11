package com.ruoyi.biz.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.config.properties.WxMiniappProperties;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.user.facade.IWechatIdentityFacade;
import com.ruoyi.user.model.bo.WechatIdentityBO;
import com.ruoyi.user.model.param.WechatIdentityParam;
import com.ruoyi.web.vo.miniapp.MiniappLoginVO;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 小程序认证应用编排。
 */
@Component
@RequiredArgsConstructor
public class MiniappAuthBizService {
    private final WxMaService wxMaService;
    private final WxMiniappProperties properties;
    private final IWechatIdentityFacade identityFacade;
    private final TokenService tokenService;

    /**
     * 使用微信 code 完成静默登录。
     *
     * @param code 微信临时 code
     * @return 登录结果
     */
    public MiniappLoginVO silentLogin(final String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);
            WechatIdentityBO identity = identityFacade.resolve(new WechatIdentityParam().setChannel("MINIAPP")
                    .setAppId(properties.getAppId()).setOpenId(session.getOpenid()).setUnionId(session.getUnionid()));
            SysUser user = new SysUser();
            user.setUserId(identity.getMemberId());
            user.setUserName("miniapp-" + identity.getMemberId());
            user.setNickName("微信用户");
            String token = tokenService.createToken(new LoginUser(identity.getMemberId(), 0L, user, Set.of("miniapp")));
            MiniappLoginVO result = new MiniappLoginVO();
            result.setToken(token);
            result.setIdentityStatus(identity.getIdentityStatus());
            result.setPurchaseAllowed(identity.getPurchaseAllowed());
            return result;
        } catch (WxErrorException exception) {
            throw new ServiceException("微信登录失败，请稍后重试");
        }
    }
}
