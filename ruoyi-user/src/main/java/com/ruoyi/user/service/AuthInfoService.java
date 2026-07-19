package com.ruoyi.user.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.AuthInfo;
import com.ruoyi.user.mapper.AuthInfoMapper;
import com.ruoyi.user.model.query.AuthInfoQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 授权信息Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Service
public class AuthInfoService extends ServiceImpl<AuthInfoMapper, AuthInfo> {

    /**
     * 新增授权信息。
     *
     * @param authInfo 授权信息
     * @return 保存后的授权信息
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthInfo insert(AuthInfo authInfo) {
        authInfo.setCreateTime(DateUtil.date());
        save(authInfo);
        return authInfo;
    }

    /**
     * 按条件更新授权信息。
     *
     * @param authInfo 授权信息
     * @param query 更新条件
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AuthInfo authInfo, AuthInfoQuery query) {
        authInfo.setUpdateTime(DateUtil.date());
        return update(authInfo, DynamicCondition.toWrapper(query));
    }

    /**
     * 删除授权信息。
     *
     * @param id 授权信息主键
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Integer id) {
        return removeById(id);
    }

}
