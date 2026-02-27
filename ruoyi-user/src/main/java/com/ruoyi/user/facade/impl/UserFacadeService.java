package com.ruoyi.user.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.convert.UserCov;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.domain.UserCompany;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.manager.UserCompanyManager;
import com.ruoyi.user.manager.UserManager;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.consts.UserEnum;
import com.ruoyi.user.model.param.UserParam;
import com.ruoyi.user.model.query.UserCompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserFacadeService implements IUserFacade {


    @Autowired
    UserManager userManager;

    @Autowired
    UserCompanyManager userCompanyManager;


    @Override
    public List<UserBO> queryList(final UserQuery userQuery) {

        User user = UserCov.INSTANCE.queryToBo(userQuery);
        List<User> list = userManager.list(new QueryWrapper<>(user));
        return UserCov.INSTANCE.domainToBoList(list);
    }

    @Override
    public UserBO queryOne(final UserQuery userQuery) {
        return UserCov.INSTANCE.domainToBo(userManager.getOne(new QueryWrapper<>(UserCov.INSTANCE.queryToBo(userQuery))));
    }

    @Override
    @Transactional
    public UserBO addUserAndCompany(final UserParam userParam) {

        User user = UserCov.INSTANCE.paramToBo(userParam);
        user.setCreateTime(DateUtil.date()).setDisable(UserEnum.UserDisable.NORMAL.getValue()).setDeleted(UserEnum.UserDeleted.NORMAL.getValue());
        userManager.save(user);
        UserBO userBO = UserCov.INSTANCE.domainToBo(user);
        UserCompany userCompany = new UserCompany().setCompanyId(userParam.getCompanyId()).setOwner(userParam.getOwner())
                .setUserId(userBO.getUserId()).setOwner(userParam.getOwner()).setCreateTime(DateUtil.date());
        userCompanyManager.save(userCompany);
        return userBO;
    }

    @Override
    public PageBO<UserBO> userList(final UserQuery userQuery, final PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<CompanyUserDTO> companyUsers = userManager.companyUser(userQuery);
        return PageUtils.fromList(companyUsers, UserCov.INSTANCE::dtoToBO);


    }

    @Override
    public void addUserCompany(Long userId, Long companyId, String name) {
        long count = userCompanyManager.count(DynamicCondition.toWrapper(new UserCompanyQuery().setUserId(userId).setCompanyId(companyId)));
        if (count > 1) {
            return;
        }
        UserCompany userCompany = new UserCompany().setCompanyId(companyId).setUserId(userId).setOwner(UserEnum.UserOwner.PEOPLE.getValue()).setCreateTime(DateUtil.date());
        userCompanyManager.save(userCompany);

        userManager.update(new User().setNickName(name), DynamicCondition.toWrapper(new UserQuery().setUserId(userId)));
    }

    @Override
    public PageBO<UserBO> pageCompanyUser(UserQuery userQuery, PageParamV2 pageParamV2) {

        PageUtils.startPage(pageParamV2);
        List<UserCompany> userCompanyList = userCompanyManager.list(DynamicCondition.toWrapper(new UserCompanyQuery().setCompanyId(userQuery.getCompanyId())));
        if (CollectionUtil.isEmpty(userCompanyList)) {
            return PageBO.empty();
        }
        Set<Long> userIdSet = userCompanyList.stream().map(UserCompany::getUserId).collect(Collectors.toSet());
        List<User> userList = userManager.list(DynamicCondition.toWrapper(new UserQuery().setUserIdSet(new ArrayList<>(userIdSet))));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getUserId, u -> u));
        List<UserBO> userBOList = new ArrayList<>();
        for (UserCompany userCompany : userCompanyList) {
            UserBO userBO = UserCov.INSTANCE.domainToBo(userMap.get(userCompany.getUserId()));
            userBO.setOwner(userCompany.getOwner());
            userBOList.add(userBO);
        }
        return PageUtils.fromList(userCompanyList, userCompanies -> userBOList);
    }
}
