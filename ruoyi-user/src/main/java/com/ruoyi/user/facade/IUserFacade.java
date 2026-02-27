package com.ruoyi.user.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.param.UserParam;
import com.ruoyi.user.model.query.UserQuery;

import java.util.List;

/**
 * 用户信息Service接口
 *
 * @author ruoyi
 * @date 2025-09-06
 */
public interface IUserFacade {

    List<UserBO> queryList(UserQuery userQuery);


    UserBO queryOne(UserQuery userQuery);


    UserBO addUserAndCompany(UserParam userParam);


    PageBO<UserBO> userList(UserQuery userQuery, PageParamV2 pageParamV2);


    void addUserCompany(Long userId, Long aLong, String name);

    PageBO<UserBO> pageCompanyUser(UserQuery userQuery, PageParamV2 pageParamV2);

}
