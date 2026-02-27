package com.ruoyi.biz.company;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.consts.ApiRedisKey;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.mapper.user.CompanyConvert;
import com.ruoyi.mapper.user.UserConvert;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.bo.UserLoginBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.consts.UserRedisKey;
import com.ruoyi.user.model.param.UserCompanyParam;
import com.ruoyi.user.model.query.UserCompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBizService {


    @Autowired
    WxMpService wxMpService;

    @Autowired
    RedisCache redisCache;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    TokenService tokenService;

    @Autowired
    IUserFacade userFacade;

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
        // 换取临时二维码ticket
        log.info("生成渠道二维码参数：{}", param);
        WxMpQrCodeTicket codeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(param,
                ObjectUtils.defaultIfNull(expireTime, UserApiConsts.QR_CODE_EXPIRE_SECONDS));

        if (Objects.isNull(codeTicket) || StrUtil.isBlank(codeTicket.getTicket())) {
            log.error("获取微信公众号ticket失败");
            throw new ServiceException("获取渠道二维码异常");
        }
        return wxMpService.getQrcodeService().qrCodePictureUrl(codeTicket.getTicket());
    }


    /**
     * 生成 微信渠道字符串.
     *
     * @param companyId 企业id
     * @param type      业务类型
     * @param phone     手机号码
     * @return
     */
    public String buildQrCodeParam(Long companyId, Integer type, String phone, String userName, Integer owner, String uuid) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserApiConsts.QR_CODE_SCENE_TYPE, type);
        map.put(UserApiConsts.QR_CODE_SCENE_DEPTID, companyId);
        map.put(UserApiConsts.QR_CODE_SCENE_PHONE, phone);
        map.put(UserApiConsts.QR_CODE_SCENE_USERNAME, userName);
        map.put(UserApiConsts.QR_CODE_SCENE_OWNER, owner);
        map.put(UserApiConsts.QR_CODE_SCENE_UUID, uuid);

        return map.entrySet().stream().filter(e -> Objects.nonNull(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
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
        UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(userLoginBO.getUserId()));
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

        UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(userLoginBO.getUserId()));
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
        UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(loginUser.getUserId()));
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

    public String loginTest() {

        UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(1L));
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
        loginUser.setDeptId(1L);
        // 生成token
        return tokenService.createToken(loginUser);


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
        PageBO<UserBO> userBOPageBO = userFacade.pageCompanyUser(new UserQuery().setNickNameLike(name).setCompanyId(companyId), pageParamV2);
        return new PageBO(UserConvert.INSTANCE.toVoList(userBOPageBO.getData()), userBOPageBO.getTotal());

    }


    /**
     * 删除用户
     *
     * @param companyId
     * @param userId
     */

    public void removeUserCompany(Long companyId, Long userId, Long currentUserId) {

        // 检查当前用户是否为该企业的管理员
        User masterUser = companyFacade.companyMasterUser(companyId);
        if (masterUser == null || !Objects.equals(masterUser.getUserId(), currentUserId)) {
            throw new ServiceException("只有企业管理员才能删除用户");
        }

        companyFacade.removeUserCompany(companyId, userId);
    }

    /**
     * 权限验证
     *
     * @param userId
     * @param deptId
     */
    public void checkUserMaster(Long userId, Long deptId) {

        User user = companyFacade.companyMasterUser(deptId);
        if (user == null || !Objects.equals(user.getUserId(), userId)) {
            throw new ServiceException("只有企业管理员才能操作");
        }
    }

    public void update(UserCompanyParam userCompanyParam, UserCompanyQuery userCompanyQuery) {

        companyFacade.update(userCompanyParam, userCompanyQuery);

    }
}
