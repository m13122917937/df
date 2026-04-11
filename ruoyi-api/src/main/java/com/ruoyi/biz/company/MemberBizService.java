package com.ruoyi.biz.company;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.mapper.member.MemberConvert;
import com.ruoyi.mapper.user.CompanyConvert;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.bo.UserLoginBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.consts.UserRedisKey;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import com.ruoyi.web.vo.user.CompanyVO;
import com.ruoyi.web.vo.user.LoginCompanyVO;
import com.ruoyi.web.vo.user.UserLoginVO;
import com.ruoyi.web.vo.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MemberBizService {


    @Autowired
    WxMpService wxMpService;

    @Autowired
    RedisCache redisCache;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    TokenService tokenService;

    @Autowired
    IMemberFacade memberFacade;

    @Resource
    private AuthenticationManager authenticationManager;


    /**
     * 生成渠道二维码.
     *
     * @param param      微信参数.
     * @param expireTime 过期时间.
     * @return 渠道二维码链接.
     */
    public String channelCode(final String param, final Integer expireTime) throws WxErrorException {
        WxMpQrCodeTicket codeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(param,
                ObjectUtils.defaultIfNull(expireTime, UserApiConsts.QR_CODE_EXPIRE_SECONDS));
        return wxMpService.getQrcodeService().qrCodePictureUrl(codeTicket.getTicket());
    }


    /**
     * 生成微信渠道字符串.
     *
     * @param companyId 企业id
     * @param type      业务类型
     * @param phone     手机号码
     * @param userName  用户名
     * @param owner     所有者类型
     * @param uuid      唯一标识
     * @return 拼接后的参数字符串
     */
    public String buildQrCodeParam(Long companyId, Integer type, String phone, String userName, Integer owner, String uuid) {
        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_TYPE, type);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_DEPTID, companyId);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_PHONE, phone);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_USERNAME, userName);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_OWNER, owner);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_UUID, uuid);
        return sb.length() > 0 && sb.charAt(0) == '&' ? sb.substring(1) : sb.toString();
    }

    /**
     * 如果值不为null则追加到StringBuilder.
     */
    private void appendIfNotNull(StringBuilder sb, String key, Object value) {
        if (value != null) {
            sb.append('&').append(key).append('=').append(value);
        }
    }

    /**
     * 登录二维码.
     *
     * @return
     */
    public UserLoginVO loginQrCode() throws WxErrorException {
        String uuid = IdUtil.fastSimpleUUID();
        String param = this.buildQrCodeParam(null, UserApiConsts.LOGIN_QR_CODE_TYPE, null, null, null, uuid);
        String url = this.channelCode(param, null);
        return new UserLoginVO(uuid, url);
    }

    /**
     * 查询登录状态
     *
     * @param uuid
     * @return
     */
    public LoginCompanyVO loginStatus(String uuid) {
        LoginCompanyVO loginCompanyVO = new LoginCompanyVO().setUuid(uuid);
        UserLoginBO userLoginBO = redisCache.getCacheObject(UserRedisKey.USER_KEY.USER_LOGIN_KEY.getKey() + uuid);
        if (Objects.isNull(userLoginBO)) {
            loginCompanyVO.setIsLogin(Boolean.FALSE);
            return loginCompanyVO;
        }
        loginCompanyVO.setNewUser(userLoginBO.getNewUser());
        if (userLoginBO.getNewUser()) {
            return loginCompanyVO;
        }
        MemberBO userBO = memberFacade.queryOne(new MemberQuery().setUserId(userLoginBO.getUserId()));
        Assert.notNull(userBO, "用户信息不存在");
        // 组装用户信息
        List<CompanyVO> companyVO = CompanyConvert.INSTANCE.toVoList(companyFacade.queryList(userLoginBO.getUserId()));
        return loginCompanyVO.setIsLogin(Boolean.TRUE).setCompanyVOList(companyVO).setUserName(userBO.getNickName()).setRoles("admin");

    }

    /**
     * 换取登录token
     *
     * @param uuid
     * @param companyId
     */
    public String login(String uuid, Long companyId) {
        UserLoginBO userLoginBO = redisCache.getCacheObject(UserRedisKey.USER_KEY.USER_LOGIN_KEY.getKey() + uuid);
        Assert.notNull(userLoginBO, "请重新登录");

        MemberBO userBO = memberFacade.queryOne(new MemberQuery().setUserId(userLoginBO.getUserId()));
        Assert.notNull(userBO, "用户信息不存在");

        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userBO.getOpenId(), UserDetailsServiceImpl.DEFAULT_PASSWORD);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw new UserPasswordNotMatchException();
            } else {
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        loginUser.setDeptId(companyId);
        // 生成token
        return tokenService.createToken(loginUser);
    }

    public String login(Long companyId, LoginUser loginUser) {
        MemberBO userBO = memberFacade.queryOne(new MemberQuery().setUserId(loginUser.getUserId()));
        Assert.notNull(userBO, "用户信息不存在");

        loginUser.setDeptId(companyId);
        // 生成token
        String token = tokenService.createToken(loginUser);
        return token;

    }

    /**
     * 获取当前登录信息
     *
     * @param loginUser
     * @return
     */
    public LoginCompanyVO loginInfo(LoginUser loginUser) {

        List<CompanyVO> companyVO = CompanyConvert.INSTANCE.toVoList(companyFacade.queryList(loginUser.getUserId()));
        for (CompanyVO vo : companyVO) {
            if (!Objects.equals(vo.getId(), loginUser.getDeptId())) {
                continue;
            }
            vo.setCurr(true);
        }
        LoginCompanyVO loginCompanyVO = new LoginCompanyVO().setUserName(loginUser.getUsername())
                .setCompanyVOList(companyVO).setRoles("admin");
        return loginCompanyVO;
    }

    /**
     * 查询企业下面用户列表
     *
     * @param companyId   企业id
     * @param name        姓名like
     * @param pageParamV2 分页参数
     * @return
     */
    public PageBO<UserVO> list(Long companyId, String name, PageParamV2 pageParamV2) {
        PageBO<MemberBO> memberBOPageBO = memberFacade.pageCompanyMember(new MemberQuery().setNickNameLike(name).setCompanyId(companyId), pageParamV2);
        return new PageBO<>(MemberConvert.INSTANCE.toVoList(memberBOPageBO.getData()), memberBOPageBO.getTotal());
    }

    /**
     * 删除用户
     *
     * @param companyId
     * @param userId
     */

    public void removeUserCompany(Long companyId, Long userId, Long currentUserId) {

        // 检查当前用户是否为该企业的管理员
      checkUserMaster(currentUserId, companyId);
        companyFacade.removeUserCompany(companyId, userId);
    }

    /**
     * 权限验证
     *
     * @param userId
     * @param deptId
     */
    public void checkUserMaster(Long userId, Long deptId) {

        if (!memberFacade.companyMasterUser(deptId, userId)) {
            throw new ServiceException("只有企业管理员才能操作");
        }
    }

    public void update(MemberCompanyParam memberCompanyParam, MemberCompanyQuery memberCompanyQuery) {

        companyFacade.update(memberCompanyParam, memberCompanyQuery);

    }

}
