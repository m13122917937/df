package com.ruoyi.biz.company;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.query.UserQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    public static String DEFAULT_PASSWORD = "admin123";

    @Autowired
    IUserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserBO userBO = userFacade.queryOne(new UserQuery().setOpenId(username));
        if (StringUtils.isNull(userBO)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户不存在/密码错误");
        }
        return createLoginUser(userBO);
    }

    public UserDetails createLoginUser(UserBO user) {
        SysUser sysUser = new SysUser();
        sysUser.setNickName(user.getNickName());
        sysUser.setUserName(user.getOpenId());
        sysUser.setPassword("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2");
        return new LoginUser(user.getUserId(), 0L,sysUser , Set.of("*.*.*"));
    }
}
