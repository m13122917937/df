package com.ruoyi.biz.company;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.user.facade.IAuthInfoFacade;
import com.ruoyi.user.model.bo.AuthInfoBO;
import com.ruoyi.user.model.param.AuthInfoParam;
import com.ruoyi.user.model.query.AuthInfoQuery;
import com.ruoyi.web.form.company.AuthLoginInfoForm;
import com.ruoyi.web.form.company.AuthLoginSaveForm;
import com.ruoyi.web.form.company.AuthLoginUpForm;
import com.ruoyi.web.vo.company.AuthLoginInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 授权登录业务处理
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Service
public class AuthLoginBizService {

    private static final Date DEFAULT_EXPIRED = DateUtil.parseDateTime("2099-01-01 00:00:00");

    @Autowired
    private IAuthInfoFacade authInfoFacade;

    @Value("${crm.ali.sig:f40lzdiByTwuEQR0Y5pVS3Yt7OxwmheDbpnua}")
    private String sig;

    /**
     * 授权登录
     *
     * @param form 登录表单
     * @return 登录结果
     */
    public AuthLoginInfoVO login(AuthLoginInfoForm form) {
        AuthInfoBO authInfoBO = authInfoFacade.queryOne(new AuthInfoQuery().setUserName(form.getLoginName()));
        Assert.notNull(authInfoBO, "用户不存在");

        if (!authInfoBO.getPwd().equals(form.getPwd())) {
            throw new ServiceException("用户名或者密码不正确");
        }
        if (authInfoBO.getExpired().compareTo(new Date()) <= 0) {
            return new AuthLoginInfoVO().setType(1).setUserName(authInfoBO.getUserName()).setMacId(authInfoBO.getMacId());
        }
//        if (StrUtil.isNotEmpty(authInfoBO.getMacId()) && !authInfoBO.getMacId().equals(form.getMacId())) {
//            throw new ServiceException("登录设备异常");
//        }

        return new AuthLoginInfoVO()
                .setType(authInfoBO.getType())
                .setUserName(authInfoBO.getUserName())
                .setMacId(authInfoBO.getMacId())
                .setExpired(DateUtil.formatDate(authInfoBO.getExpired()));
    }

    /**
     * 注册授权账户
     *
     * @param form 注册表单
     * @return 注册结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginInfoVO reg(AuthLoginSaveForm form) {
        List<AuthInfoBO> userNameList = authInfoFacade.queryList(new AuthInfoQuery().setUserName(form.getLoginName()));
        if (CollUtil.isNotEmpty(userNameList)) {
            throw new ServiceException("用户已注册");
        }
        if (StrUtil.isNotEmpty(form.getMacId())) {
            List<AuthInfoBO> macList = authInfoFacade.queryList(new AuthInfoQuery().setMacId(form.getMacId()));
            if (CollUtil.isNotEmpty(macList)) {
                throw new ServiceException("设备已被绑定");
            }
        }

        AuthInfoParam param = new AuthInfoParam();
        param.setUserName(form.getLoginName());
        param.setPwd(form.getPwd());
        param.setMacId(form.getMacId());
        param.setExpired(form.getExpired() != null ? form.getExpired() : DEFAULT_EXPIRED);
        param.setType(form.getType() != null ? form.getType() : 1);
        param.setCreateBy(null);
        AuthInfoBO authInfoBO = authInfoFacade.insert(param);

        return new AuthLoginInfoVO()
                .setType(authInfoBO.getType())
                .setUserName(authInfoBO.getUserName())
                .setMacId(authInfoBO.getMacId())
                .setExpired(DateUtil.formatDate(authInfoBO.getExpired()));
    }

    /**
     * 更新授权信息
     *
     * @param form 更新表单
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginInfoVO up(AuthLoginUpForm form) {
        List<AuthInfoBO> authInfoList = authInfoFacade.queryList(new AuthInfoQuery().setUserName(form.getLoginName()));
        if (CollUtil.isEmpty(authInfoList)) {
            throw new ServiceException("用户名不存在");
        }
        if (authInfoList.size() > 1) {
            throw new ServiceException("数据异常");
        }
        if (StrUtil.isNotEmpty(sig) && !sig.equals(form.getSig())) {
            throw new ServiceException("请求不正确");
        }

        AuthInfoBO oldAuth = authInfoList.get(0);
        AuthInfoParam param = new AuthInfoParam();
        param.setId(oldAuth.getId());
        param.setUserName(form.getLoginName());
        param.setUserName(oldAuth.getUserName());
        param.setType(form.getType() != null ? form.getType() : oldAuth.getType());
        if (StrUtil.isNotEmpty(form.getPwd())) {
            param.setPwd(form.getPwd());
        } else {
            param.setPwd(oldAuth.getPwd());
        }
        param.setMacId(form.getMacId() != null ? form.getMacId() : oldAuth.getMacId());
        param.setExpired(form.getExpired() != null ? form.getExpired() : oldAuth.getExpired());
        param.setUpdateBy(null);

        authInfoFacade.update(param, new AuthInfoQuery().setId(oldAuth.getId()));

        AuthInfoBO authInfoBO = authInfoFacade.queryOne(new AuthInfoQuery().setId(oldAuth.getId()));
        return new AuthLoginInfoVO()
                .setType(authInfoBO.getType())
                .setUserName(authInfoBO.getUserName())
                .setMacId(authInfoBO.getMacId())
                .setExpired(DateUtil.formatDate(authInfoBO.getExpired()));
    }
}
