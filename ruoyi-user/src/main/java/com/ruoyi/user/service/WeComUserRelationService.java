package com.ruoyi.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.WeComUserRelation;
import com.ruoyi.user.mapper.WeComUserRelationMapper;
import com.ruoyi.user.model.query.WeComUserRelationQuery;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 企业微信用户关联领域服务。
 */
@Service
public class WeComUserRelationService extends ServiceImpl<WeComUserRelationMapper, WeComUserRelation> {
    /**
     * 按企业微信成员标识查询关联记录。
     *
     * @param wecomUserId 企业微信成员标识
     * @return 关联记录
     */
    public WeComUserRelation findByWeComUserId(String wecomUserId) {
        return getOne(DynamicCondition.toWrapper(new WeComUserRelationQuery().setWecomUserId(wecomUserId)));
    }

    /**
     * 按系统用户标识查询关联记录。
     *
     * @param userId 系统用户标识
     * @return 关联记录
     */
    public WeComUserRelation findByUserId(Long userId) {
        return getOne(DynamicCondition.toWrapper(new WeComUserRelationQuery().setUserId(userId)));
    }

    /**
     * 查询全部企业微信关联记录。
     *
     * @return 关联记录列表
     */
    public List<WeComUserRelation> listAll() {
        return list(DynamicCondition.toWrapper(new WeComUserRelationQuery()));
    }

    /**
     * 保存或刷新成员关联关系。
     *
     * @param userId 系统用户标识
     * @param wecomUserId 企业微信成员标识
     * @return 关联记录
     */
    public WeComUserRelation bind(Long userId, String wecomUserId) {
        WeComUserRelation relation = findByWeComUserId(wecomUserId);
        if (relation == null) {
            relation = findByUserId(userId);
        }
        Date now = new Date();
        if (relation == null) {
            relation = new WeComUserRelation().setUserId(userId).setWecomUserId(wecomUserId)
                .setLastSyncTime(now).setCreateTime(now).setUpdateTime(now);
            save(relation);
        } else {
            relation.setUserId(userId).setLastSyncTime(now).setUpdateTime(now);
            updateById(relation);
        }
        return relation;
    }

    /**
     * 刷新成员最近同步时间。
     *
     * @param relation 关联记录
     */
    public void touch(WeComUserRelation relation) {
        relation.setLastSyncTime(new Date()).setUpdateTime(new Date());
        updateById(relation);
    }
}
