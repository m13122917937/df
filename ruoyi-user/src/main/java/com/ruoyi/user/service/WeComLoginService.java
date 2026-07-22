package com.ruoyi.user.service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.system.facade.ISysRoleFacade;
import com.ruoyi.system.facade.ISysMenuFacade;
import com.ruoyi.user.config.WechatWeComProperties;
import com.ruoyi.user.domain.WeComUserRelation;
import com.ruoyi.user.facade.IWeComUserRelationFacade;
import com.ruoyi.user.model.query.WeComSysUserQuery;
import com.ruoyi.user.model.bo.WeComPrivateAuthorizationBO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.WxCpUserDetail;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.List;

/** 企业微信扫码登录领域服务。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeComLoginService {
    private static final String STATE_KEY_PREFIX = "wecom:login:state:";
    private static final String TICKET_KEY_PREFIX = "wecom:login:ticket:";
    private static final String PRIVATE_AUTH_KEY_PREFIX = "wecom:login:private-auth:";
    private static final String PRIVATE_AUTH_TICKET_PREFIX = "BIND:";
    private static final int STATE_EXPIRE_MINUTES = 5;
    private static final int TICKET_EXPIRE_MINUTES = 5;
    private static final Long DEFAULT_ROLE_ID = 2L;
    private static final String DEFAULT_PASSWORD = "abc123456";

    private final WechatWeComProperties properties;
    private final WxCpService wxCpService;
    private final RedisCache redisCache;
    private final ISysUserFacade userFacade;
    private final ISysRoleFacade roleFacade;
    private final ISysMenuFacade menuFacade;
    private final IWeComUserRelationFacade relationFacade;
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
        WeComLoginIdentity identity = loadIdentity(code, false);
        if (requiresPrivateAuthorization(identity)) {
            return PRIVATE_AUTH_TICKET_PREFIX + createPrivateAuthorization(identity.wecomUserId);
        }
        SysUser user = findActiveUser(identity);
        String ticket = IdUtils.fastUUID();
        redisCache.setCacheObject(TICKET_KEY_PREFIX + ticket, createToken(user), TICKET_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("企业微信登录票据已创建，trace={}", ticketTrace(ticket));
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
            log.warn("企业微信登录票据不存在或已被兑换，trace={}", ticketTrace(ticket));
            throw new ServiceException("登录票据已失效，请重新扫码", HttpStatus.UNAUTHORIZED);
        }
        log.info("企业微信登录票据兑换成功，trace={}", ticketTrace(ticket));
        return token;
    }

    /**
     * 生成手机号二次授权二维码。
     *
     * @param sessionId 二次授权会话标识
     * @param outputStream 图片输出流
     * @throws IOException 图片写入失败
     */
    public void writePrivateAuthorizationQr(String sessionId, OutputStream outputStream) throws IOException {
        try {
            String authorizationUrl = createPrivateAuthorizationUrl(sessionId);
            BitMatrix matrix = new MultiFormatWriter().encode(authorizationUrl, BarcodeFormat.QR_CODE, 280, 280);
            BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < matrix.getWidth(); x++) {
                for (int y = 0; y < matrix.getHeight(); y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            javax.imageio.ImageIO.write(image, "PNG", outputStream);
        } catch (Exception exception) {
            throw new IOException("手机号授权二维码生成失败", exception);
        }
    }

    /**
     * 完成手机号二次授权。
     *
     * @param code 企业微信授权码
     * @param sessionId 二次授权会话标识
     */
    public void completePrivateAuthorization(String code, String sessionId) {
        WeComPrivateAuthorizationBO session = getPrivateAuthorization(sessionId);
        try {
            WeComLoginIdentity identity = loadIdentity(code, true);
            validatePrivateIdentity(session, identity);
            SysUser user = findUserByMobile(identity.mobile);
            if (user == null || "admin".equals(user.getUserName())) {
                throw new ServiceException("手机号未匹配到可登录的系统用户", HttpStatus.UNAUTHORIZED);
            }
            if (!"0".equals(user.getStatus())) {
                throw new ServiceException("当前系统用户已停用", HttpStatus.UNAUTHORIZED);
            }
            ensureDefaultRole(user);
            relationFacade.bind(user.getUserId(), session.getWecomUserId());
            String ticket = IdUtils.fastUUID();
            redisCache.setCacheObject(TICKET_KEY_PREFIX + ticket, createToken(user), TICKET_EXPIRE_MINUTES, TimeUnit.MINUTES);
            session.setTicket(ticket);
            session.setErrorMessage(null);
        } catch (Exception exception) {
            session.setErrorMessage(resolvePrivateAuthorizationError(exception));
            log.warn("企业微信手机号二次授权失败，userId={}", session.getWecomUserId(), exception);
        }
        redisCache.setCacheObject(PRIVATE_AUTH_KEY_PREFIX + sessionId, session, STATE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 查询手机号二次授权状态。
     *
     * @param sessionId 二次授权会话标识
     * @return 二次授权会话
     */
    public WeComPrivateAuthorizationBO getPrivateAuthorizationStatus(String sessionId) {
        return getPrivateAuthorization(sessionId);
    }

    /**
     * 判断回调状态是否属于手机号二次授权。
     *
     * @param state 回调状态标识
     * @return 是否属于二次授权
     */
    public boolean isPrivateAuthorizationState(String state) {
        return StringUtils.isNotEmpty(state) && redisCache.getCacheObject(PRIVATE_AUTH_KEY_PREFIX + state) != null;
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

    private boolean requiresPrivateAuthorization(WeComLoginIdentity identity) {
        return relationFacade.findByWeComUserId(identity.wecomUserId) == null && StringUtils.isEmpty(identity.mobile);
    }

    private String createPrivateAuthorization(String wecomUserId) {
        String sessionId = IdUtils.fastUUID();
        WeComPrivateAuthorizationBO session = new WeComPrivateAuthorizationBO();
        session.setWecomUserId(wecomUserId);
        redisCache.setCacheObject(PRIVATE_AUTH_KEY_PREFIX + sessionId, session, STATE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return sessionId;
    }

    private String createPrivateAuthorizationUrl(String sessionId) {
        getPrivateAuthorization(sessionId);
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + properties.getCorpId()
            + "&redirect_uri=" + UriUtils.encode(properties.getLoginRedirectUri(), StandardCharsets.UTF_8)
            + "&response_type=code&scope=snsapi_privateinfo&agentid=" + properties.getAgentId()
            + "&state=" + sessionId + "#wechat_redirect";
    }

    private WeComPrivateAuthorizationBO getPrivateAuthorization(String sessionId) {
        if (StringUtils.isEmpty(sessionId)) {
            throw new ServiceException("手机号授权请求已失效，请重新扫码", HttpStatus.UNAUTHORIZED);
        }
        WeComPrivateAuthorizationBO session = redisCache.getCacheObject(PRIVATE_AUTH_KEY_PREFIX + sessionId);
        if (session == null) {
            throw new ServiceException("手机号授权请求已失效，请重新扫码", HttpStatus.UNAUTHORIZED);
        }
        return session;
    }

    private void validatePrivateIdentity(WeComPrivateAuthorizationBO session, WeComLoginIdentity identity) {
        if (!session.getWecomUserId().equals(identity.wecomUserId) || StringUtils.isEmpty(identity.mobile)) {
            throw new ServiceException("企业微信未返回授权手机号", HttpStatus.UNAUTHORIZED);
        }
    }

    private String resolvePrivateAuthorizationError(Exception exception) {
        return exception instanceof ServiceException ? exception.getMessage() : "手机号授权失败，请重新扫码";
    }

    private WeComLoginIdentity loadIdentity(String code, boolean requireMobile) {
        try {
            WxCpOauth2UserInfo oauthUser = wxCpService.getOauth2Service().getUserInfo(properties.getAgentId(), code);
            if (StringUtils.isEmpty(oauthUser.getUserId())) {
                throw new ServiceException("企业微信授权未返回成员标识", HttpStatus.UNAUTHORIZED);
            }
            if (!requireMobile && hasBoundSystemUser(oauthUser.getUserId())) {
                return new WeComLoginIdentity(oauthUser.getUserId(), null);
            }
            if (StringUtils.isEmpty(oauthUser.getUserTicket())) {
                if (requireMobile) {
                    throw new ServiceException("企业微信二次授权未返回 user_ticket", HttpStatus.UNAUTHORIZED);
                }
                return new WeComLoginIdentity(oauthUser.getUserId(), null);
            }
            WxCpUserDetail userDetail = wxCpService.getOauth2Service().getUserDetail(oauthUser.getUserTicket());
            if (StringUtils.isEmpty(userDetail.getMobile())) {
                if (requireMobile) {
                    throw new ServiceException("企业微信二次授权未返回手机号", HttpStatus.UNAUTHORIZED);
                }
                return new WeComLoginIdentity(oauthUser.getUserId(), null);
            }
            return new WeComLoginIdentity(oauthUser.getUserId(), userDetail.getMobile().trim());
        } catch (ServiceException exception) {
            throw exception;
        } catch (WxErrorException exception) {
            log.error("读取企业微信登录成员失败", exception);
            throw new ServiceException(resolveWxErrorMessage(exception), HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {
            log.error("读取企业微信登录成员失败", exception);
            throw new ServiceException("企业微信授权校验失败", HttpStatus.UNAUTHORIZED);
        }
    }

    private String resolveWxErrorMessage(WxErrorException exception) {
        if (exception.getError() == null) {
            return "企业微信授权校验失败";
        }
        if (exception.getError().getErrorCode() == 60020) {
            return "企业微信拒绝访问：服务器出口IP未加入当前应用的企业可信IP";
        }
        return "企业微信授权校验失败，错误码：" + exception.getError().getErrorCode();
    }

    private SysUser findActiveUser(WeComLoginIdentity identity) {
        WeComUserRelation relation = relationFacade.findByWeComUserId(identity.wecomUserId);
        SysUser user = relation == null ? findUnboundUser(identity) : userFacade.selectUserById(relation.getUserId());
        if (user == null) {
            user = createUser(identity);
        }
        ensureDefaultRole(user);
        if (!"0".equals(user.getStatus())) {
            throw new ServiceException("当前系统用户已停用", HttpStatus.UNAUTHORIZED);
        }
        if (relation == null && !"admin".equals(user.getUserName())) {
            relationFacade.bind(user.getUserId(), identity.wecomUserId);
        }
        return user;
    }

    private void ensureDefaultRole(SysUser user) {
        List<Long> roleIds = roleFacade.selectRoleListByUserId(user.getUserId());
        if (roleIds.isEmpty()) {
            userFacade.insertUserAuth(user.getUserId(), new Long[]{DEFAULT_ROLE_ID});
            log.info("企业微信登录用户未分配角色，已补充分配默认角色，userId={}", user.getUserId());
            return;
        }
        if (!roleIds.contains(DEFAULT_ROLE_ID) && menuFacade.selectMenuTreeByUserId(user.getUserId()).isEmpty()) {
            Long[] mergedRoleIds = new Long[roleIds.size() + 1];
            for (int index = 0; index < roleIds.size(); index++) {
                mergedRoleIds[index] = roleIds.get(index);
            }
            mergedRoleIds[roleIds.size()] = DEFAULT_ROLE_ID;
            userFacade.insertUserAuth(user.getUserId(), mergedRoleIds);
            log.warn("企业微信登录用户角色无可路由菜单，已追加默认角色，userId={}", user.getUserId());
        }
    }

    private SysUser findUserByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        return userFacade.selectOne(DynamicCondition.toWrapper(
            new WeComSysUserQuery().setPhonenumber(mobile)));
    }

    private boolean hasBoundSystemUser(String wecomUserId) {
        return relationFacade.findByWeComUserId(wecomUserId) != null
            || userFacade.selectUserByUserName(wecomUserId) != null;
    }

    private SysUser findUnboundUser(WeComLoginIdentity identity) {
        SysUser user = findUserByMobile(identity.mobile);
        return user == null ? userFacade.selectUserByUserName(identity.wecomUserId) : user;
    }

    private SysUser createUser(WeComLoginIdentity identity) {
        SysUser user = new SysUser();
        user.setUserName(identity.wecomUserId);
        user.setNickName(identity.wecomUserId);
        user.setPhonenumber(identity.mobile);
        user.setPassword(SecurityUtils.encryptPassword(DEFAULT_PASSWORD));
        user.setRoleIds(new Long[]{DEFAULT_ROLE_ID});
        user.setStatus("0");
        userFacade.insertUser(user);
        return user;
    }

    private String createToken(SysUser user) {
        return tokenService.createToken(new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user)));
    }

    private String ticketTrace(String ticket) {
        int startIndex = Math.max(0, ticket.length() - 8);
        return ticket.substring(startIndex);
    }

    private static class WeComLoginIdentity {
        private final String wecomUserId;
        private final String mobile;

        private WeComLoginIdentity(String wecomUserId, String mobile) {
            this.wecomUserId = wecomUserId;
            this.mobile = mobile;
        }
    }
}
