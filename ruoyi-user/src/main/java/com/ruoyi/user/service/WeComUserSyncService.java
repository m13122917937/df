package com.ruoyi.user.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.user.convert.WeComUserConvert;
import com.ruoyi.user.model.bo.WeComUserProfileBO;
import com.ruoyi.user.model.query.WeComSysUserQuery;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 企业微信通讯录用户同步领域服务。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeComUserSyncService {
    private static final Long DEFAULT_ROLE_ID = 2L;
    private static final String DEFAULT_PASSWORD = "abc123456";

    private final WxCpService wxCpService;
    private final ISysUserFacade userFacade;

    /** 同步企业微信可见通讯录成员。 */
    public void syncUsers() {
        try {
            Map<String, WeComUserProfileBO> members = loadMembers();
            if (members.isEmpty()) {
                throw new IllegalStateException("企业微信未返回带手机号成员，取消停用操作");
            }
            SyncResult result = synchronizeMembers(members);
            result.disabled = disableAbsentUsers(members.keySet());
            log.info("企业微信用户同步完成：新增{}，更新{}，恢复{}，停用{}，跳过{}",
                result.created, result.updated, result.enabled, result.disabled, result.skipped);
        } catch (Exception exception) {
            log.error("企业微信用户同步失败，未执行或未完成停用操作", exception);
            throw new IllegalStateException("企业微信用户同步失败", exception);
        }
    }

    private Map<String, WeComUserProfileBO> loadMembers() throws Exception {
        Map<String, WeComUserProfileBO> members = new HashMap<>();
        List<WxCpDepart> departments = wxCpService.getDepartmentService().list(null);
        for (WxCpDepart department : departments) {
            appendDepartmentMembers(department.getId(), members);
        }
        return members;
    }

    private void appendDepartmentMembers(Long departmentId, Map<String, WeComUserProfileBO> members) throws Exception {
        List<WxCpUser> users = wxCpService.getUserService().listByDepartment(departmentId, false, 0);
        for (WxCpUser user : users) {
            WeComUserProfileBO profile = WeComUserConvert.INSTANCE.toProfile(user);
            if (StringUtils.isNotEmpty(profile.getMobile())) {
                profile.setMobile(profile.getMobile().trim());
                members.put(profile.getMobile(), profile);
            }
        }
    }

    private SyncResult synchronizeMembers(Map<String, WeComUserProfileBO> members) {
        SyncResult result = new SyncResult();
        for (WeComUserProfileBO profile : members.values()) {
            synchronizeMember(profile, result);
        }
        return result;
    }

    private void synchronizeMember(WeComUserProfileBO profile, SyncResult result) {
        SysUser user = findUserByMobile(profile.getMobile());
        if (user == null) {
            createUser(profile);
            result.created++;
        } else if ("admin".equals(user.getUserName())) {
            result.skipped++;
        } else {
            updateUser(user, profile, result);
        }
    }

    private SysUser findUserByMobile(String mobile) {
        return userFacade.selectOne(DynamicCondition.toWrapper(new WeComSysUserQuery().setPhonenumber(mobile)));
    }

    private void createUser(WeComUserProfileBO profile) {
        SysUser user = WeComUserConvert.INSTANCE.toSysUser(profile);
        user.setUserName(profile.getMobile());
        user.setStatus("0");
        user.setRoleIds(new Long[]{DEFAULT_ROLE_ID});
        user.setPassword(SecurityUtils.encryptPassword(DEFAULT_PASSWORD));
        userFacade.insertUser(user);
    }

    private void updateUser(SysUser user, WeComUserProfileBO profile, SyncResult result) {
        SysUser update = WeComUserConvert.INSTANCE.toSysUser(profile);
        update.setUserId(user.getUserId());
        userFacade.updateUserProfile(update);
        result.updated++;
        if (!"0".equals(user.getStatus())) {
            user.setStatus("0");
            userFacade.updateUserStatus(user);
            result.enabled++;
        }
    }

    private int disableAbsentUsers(Set<String> activePhones) {
        int disabled = 0;
        for (SysUser user : userFacade.selectList(DynamicCondition.toWrapper(
            new WeComSysUserQuery().setPhonenumberPresent(true)))) {
            if (shouldDisable(user, activePhones)) {
                user.setStatus("1");
                userFacade.updateUserStatus(user);
                disabled++;
            }
        }
        return disabled;
    }

    private boolean shouldDisable(SysUser user, Set<String> activePhones) {
        return !"admin".equals(user.getUserName()) && StringUtils.isNotEmpty(user.getPhonenumber())
            && !activePhones.contains(user.getPhonenumber().trim()) && !"1".equals(user.getStatus());
    }

    private static class SyncResult {
        private int created;
        private int updated;
        private int enabled;
        private int disabled;
        private int skipped;
    }
}
