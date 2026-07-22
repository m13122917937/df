package com.ruoyi.user.facade;

import com.ruoyi.user.domain.WeComUserRelation;

import java.util.List;

/**
 * 企业微信用户关联对外接口。
 */
public interface IWeComUserRelationFacade {
    /**
     * 查询企业微信成员关联。
     *
     * @param wecomUserId 企业微信成员标识
     * @return 关联记录
     */
    WeComUserRelation findByWeComUserId(String wecomUserId);

    /**
     * 查询系统用户的企业微信关联。
     *
     * @param userId 系统用户标识
     * @return 关联记录
     */
    WeComUserRelation findByUserId(Long userId);

    /**
     * 绑定系统用户与企业微信成员。
     *
     * @param userId 系统用户标识
     * @param wecomUserId 企业微信成员标识
     * @return 关联记录
     */
    WeComUserRelation bind(Long userId, String wecomUserId);

    /**
     * 查询全部关联记录。
     *
     * @return 关联记录列表
     */
    List<WeComUserRelation> listAll();

    /**
     * 刷新关联的同步时间。
     *
     * @param relation 关联记录
     */
    void touch(WeComUserRelation relation);
}
