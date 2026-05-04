package com.ruoyi.express.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.express.mapper.RouteSubscribeMapper;
import com.ruoyi.express.domain.RouteSubscribe;

/**
 * 快递信息订阅Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Service
public class RouteSubscribeService  extends ServiceImpl<RouteSubscribeMapper, RouteSubscribe> {
    @Autowired
    private RouteSubscribeMapper routeSubscribeMapper;

}
