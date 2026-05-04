package com.ruoyi.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.user.domain.AuthInfo;
import com.ruoyi.user.mapper.AuthInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 授权信息Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Service
public class AuthInfoService extends ServiceImpl<AuthInfoMapper, AuthInfo> {

    @Autowired
    private AuthInfoMapper authInfoMapper;

}
