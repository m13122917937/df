package com.ruoyi.user.service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.user.config.WechatWeComProperties;
import com.ruoyi.user.model.query.WeComSysUserQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/** 企业微信扫码登录领域服务。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeComLoginService {
    private static final String STATE_KEY_PREFIX = "wecom:login:state:";
    private static final String TICKET_KEY_PREFIX = "wecom:login:ticket:";
    private static final int STATE_EXPIRE_MINUTES = 5;
    private static final int TICKET_EXPIRE_MINUTES = 1;

    private final WechatWeComProperties properties;
    private final WxCpService wxCpService;
    private final RedisCache redisCache;
    private final ISysUserFacade userFacade;
    private final SysPermissionService permissionService;
    private final TokenService tokenService;

    /**
     * 创建企业微信扫码授权地址。
     *
     * @return 授权地址
     */
    public String createAuthorizationUrl() {
        validateLoginConfiguration();
        String state = IdUtils.fastUUID();
        redisCache.setCacheObject(STATE_KEY_PREFIX + state, state, STATE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=" + properties.getCorpId()
            + "&agentid=" + properties.getAgentId()
            + "&redirect_uri=" + UriUtils.encode(properties.getLoginRedirectUri(), StandardCharsets.UTF_8)
            + "&state=" + state;
    }

    /**
     * 校验回调并创建一次性登录票据。
     *
     * @param code 企业微信授权码
     * @param state 授权状态码
     * @return 登录票据
     */
    public String createLoginTicket(String code, String state) {
        validateCallback(code, state);
        String mobile = loadMobile(code);
        SysUser user = findActiveUser(mobile);
        String ticket = IdUtils.fastUUID();
        redisCache.setCacheObject(TICKET_KEY_PREFIX + ticket, createToken(user), TICKET_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return ticket;
    }

    /**
     * 兑换一次性登录票据。
     *
     * @param ticket 登录票据
     * @return JWT
     */
    public String exchangeTicket(String ticket) {
        if (StringUtils.isEmpty(ticket)) {
            throw new ServiceException("登录票据无效", HttpStatus.UNAUTHORIZED);
        }
        String token = redisCache.getAndDeleteCacheObject(TICKET_KEY_PREFIX + ticket);
        if (StringUtils.isEmpty(token)) {
            throw new ServiceException("登录票据已失效，请重新扫码", HttpStatus.UNAUTHORIZED);
        }
        return token;
    }

    /**
     * 构建前端登录页地址。
     *
     * @param message 登录提示
     * @return 前端登录页地址
     */
    public String buildLoginPageUrl(String message) {
        return properties.getLoginPageUri() + "?wecomError=" + UriUtils.encode(message, StandardCharsets.UTF_8);
    }

    private void validateLoginConfiguration() {
        if (StringUtils.isEmpty(properties.getCorpId()) || StringUtils.isEmpty(properties.getContactSecret())
            || properties.getAgentId() == null || StringUtils.isEmpty(properties.getLoginRedirectUri())
            || StringUtils.isEmpty(properties.getLoginPageUri())) {
            throw new ServiceException("企业微信扫码登录配置不完整");
        }
    }

    private void validateCallback(String code, String state) {
        validateLoginConfiguration();
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)
            || redisCache.getAndDeleteCacheObject(STATE_KEY_PREFIX + state) == null) {
            throw new ServiceException("企业微信登录请求已失效，请重新扫码", HttpStatus.UNAUTHORIZED);
        }
    }

    private String loadMobile(String code) {
        try {
            WxCpOauth2UserInfo oauthUser = wxCpService.getOauth2Service().getUserInfo(properties.getAgentId(), code);
            WxCpUser weComUser = wxCpService.getUserService().getById(oauthUser.getUserId());
            if (StringUtils.isEmpty(weComUser.getMobile())) {
                throw new ServiceException("企业微信成员未维护手机号", HttpStatus.UNAUTHORIZED);
            }
            return weComUser.getMobile().trim();
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("读取企业微信登录成员失败", exception);
            throw new ServiceException("企业微信授权校验失败", HttpStatus.UNAUTHORIZED);
        }
    }

    private SysUser findActiveUser(String mobile) {
        SysUser user = userFacade.selectOne(DynamicCondition.toWrapper(
            new WeComSysUserQuery().setPhonenumber(mobile)));
        if (user == null) {
            throw new ServiceException("该企业微信成员尚未同步到系统", HttpStatus.UNAUTHORIZED);
        }
        if (!"0".equals(user.getStatus())) {
            throw new ServiceException("当前系统用户已停用", HttpStatus.UNAUTHORIZED);
        }
        return user;
    }

    private String createToken(SysUser user) {
        return tokenService.createToken(new LoginUser(user, permissionService.getMenuPermission(user)));
    }
}
