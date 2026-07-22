package com.ruoyi.user.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.system.facade.ISysRoleFacade;
import com.ruoyi.system.facade.ISysMenuFacade;
import com.ruoyi.user.convert.WeComUserConvert;
import com.ruoyi.user.domain.WeComUserRelation;
import com.ruoyi.user.facade.IWeComUserRelationFacade;
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

/**
 * 企业微信通讯录用户同步领域服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeComUserSyncService {
    private static final Long DEFAULT_ROLE_ID = 2L;
    private static final String DEFAULT_PASSWORD = "abc123456";

    private final WxCpService wxCpService;
    private final ISysUserFacade userFacade;
    private final ISysRoleFacade roleFacade;
    private final ISysMenuFacade menuFacade;
    private final IWeComUserRelationFacade relationFacade;

    /**
     * 同步企业微信可见通讯录成员。
     */
    public void syncUsers() {
        try {
            Map<String, WeComUserProfileBO> members = loadMembers();
            if (members.isEmpty()) {
                throw new IllegalStateException("企业微信未返回成员，取消停用操作");
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
            WeComUserProfileBO profile = loadProfile(user.getUserId());
            members.put(profile.getWecomUserId(), profile);
        }
    }

    private WeComUserProfileBO loadProfile(String wecomUserId) throws Exception {
        WxCpUser detail = wxCpService.getUserService().getById(wecomUserId);
        WeComUserProfileBO profile = WeComUserConvert.INSTANCE.toProfile(detail);
        if (StringUtils.isNotEmpty(profile.getMobile())) {
            profile.setMobile(profile.getMobile().trim());
        }
        return profile;
    }

    private SyncResult synchronizeMembers(Map<String, WeComUserProfileBO> members) {
        SyncResult result = new SyncResult();
        for (WeComUserProfileBO profile : members.values()) {
            synchronizeMember(profile, result);
        }
        return result;
    }

    private void synchronizeMember(WeComUserProfileBO profile, SyncResult result) {
        WeComUserRelation relation = relationFacade.findByWeComUserId(profile.getWecomUserId());
        SysUser user = findBoundOrMobileUser(relation, profile.getMobile());
        if (user == null) {
            createAndBindUser(profile);
            result.created++;
            return;
        }
        if ("admin".equals(user.getUserName())) {
            result.skipped++;
            return;
        }
        ensureDefaultRole(user);
        bindOrRefreshRelation(user, profile.getWecomUserId(), relation);
        updateUser(user, profile, result);
    }

    private SysUser findBoundOrMobileUser(WeComUserRelation relation, String mobile) {
        if (relation != null) {
            return userFacade.selectUserById(relation.getUserId());
        }
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        return userFacade.selectOne(DynamicCondition.toWrapper(new WeComSysUserQuery().setPhonenumber(mobile)));
    }

    private void createAndBindUser(WeComUserProfileBO profile) {
        SysUser user = WeComUserConvert.INSTANCE.toSysUser(profile);
        user.setUserName(resolveUserName(profile));
        user.setStatus("0");
        user.setRoleIds(new Long[]{DEFAULT_ROLE_ID});
        user.setPassword(SecurityUtils.encryptPassword(DEFAULT_PASSWORD));
        userFacade.insertUser(user);
        relationFacade.bind(user.getUserId(), profile.getWecomUserId());
    }

    private void ensureDefaultRole(SysUser user) {
        List<Long> roleIds = roleFacade.selectRoleListByUserId(user.getUserId());
        if (roleIds.isEmpty()) {
            userFacade.insertUserAuth(user.getUserId(), new Long[]{DEFAULT_ROLE_ID});
            log.info("企业微信同步用户未分配角色，已补充分配默认角色，userId={}", user.getUserId());
            return;
        }
        if (!roleIds.contains(DEFAULT_ROLE_ID) && menuFacade.selectMenuTreeByUserId(user.getUserId()).isEmpty()) {
            Long[] mergedRoleIds = new Long[roleIds.size() + 1];
            for (int index = 0; index < roleIds.size(); index++) {
                mergedRoleIds[index] = roleIds.get(index);
            }
            mergedRoleIds[roleIds.size()] = DEFAULT_ROLE_ID;
            userFacade.insertUserAuth(user.getUserId(), mergedRoleIds);
            log.warn("企业微信同步用户角色无可路由菜单，已追加默认角色，userId={}", user.getUserId());
        }
    }

    private String resolveUserName(WeComUserProfileBO profile) {
        return StringUtils.isNotEmpty(profile.getMobile()) ? profile.getMobile() : profile.getWecomUserId();
    }

    private void bindOrRefreshRelation(SysUser user, String wecomUserId, WeComUserRelation relation) {
        if (relation == null) {
            relationFacade.bind(user.getUserId(), wecomUserId);
            return;
        }
        relationFacade.touch(relation);
    }

    private void updateUser(SysUser user, WeComUserProfileBO profile, SyncResult result) {
        SysUser update = WeComUserConvert.INSTANCE.toSysUser(profile);
        update.setUserId(user.getUserId());
        if (StringUtils.isEmpty(profile.getMobile())) {
            update.setPhonenumber(user.getPhonenumber());
        }
        userFacade.updateUserProfile(update);
        result.updated++;
        if (!"0".equals(user.getStatus())) {
            user.setStatus("0");
            userFacade.updateUserStatus(user);
            result.enabled++;
        }
    }

    private int disableAbsentUsers(Set<String> activeWecomUserIds) {
        int disabled = 0;
        for (WeComUserRelation relation : relationFacade.listAll()) {
            SysUser user = userFacade.selectUserById(relation.getUserId());
            if (shouldDisable(user, relation.getWecomUserId(), activeWecomUserIds)) {
                user.setStatus("1");
                userFacade.updateUserStatus(user);
                disabled++;
            }
        }
        return disabled;
    }

    private boolean shouldDisable(SysUser user, String wecomUserId, Set<String> activeWecomUserIds) {
        return user != null && !"admin".equals(user.getUserName()) && !"1".equals(user.getStatus())
            && !activeWecomUserIds.contains(wecomUserId);
    }

    private static class SyncResult {
        private int created;
        private int updated;
        private int enabled;
        private int disabled;
        private int skipped;
    }
}
