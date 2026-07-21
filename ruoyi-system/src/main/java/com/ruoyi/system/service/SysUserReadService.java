package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** 系统用户读取领域服务。 */
@Service
@RequiredArgsConstructor
public class SysUserReadService {
    private final SysUserMapper sysUserMapper;

    /**
     * 按条件查询单个系统用户。
     *
     * @param wrapper 查询条件
     * @return 系统用户，不存在时返回 null
     */
    public SysUser selectOne(Wrapper<SysUser> wrapper) {
        return sysUserMapper.selectOne(wrapper);
    }

    /**
     * 按条件查询系统用户列表。
     *
     * @param wrapper 查询条件
     * @return 系统用户列表
     */
    public List<SysUser> selectList(Wrapper<SysUser> wrapper) {
        return sysUserMapper.selectList(wrapper);
    }
}
