package com.ruoyi.biz.sys;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.system.facade.IDictDistrictFacade;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.model.consts.DictDistrictConsts;
import com.ruoyi.system.model.consts.SysRedisKey;
import com.ruoyi.system.model.query.DictDistrictQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IDictDistrictBizService {

    @Autowired
    IDictDistrictFacade dictDistrictFacade;

    @Autowired
    RedisCache redisCache;

    public List<DictDistrictBO> listProvince() {

        List<DictDistrictBO> list = redisCache.getCacheObject(RedisKeyUtil.generate(AdminRedisKey.ADMIN_BASE, SysRedisKey.ROOT_KEY, "province"));
        if (CollectionUtil.isEmpty(list)) {
            list = dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.PROVINCE.getValue()));
        }
        redisCache.setCacheObject(RedisKeyUtil.generate(AdminRedisKey.ADMIN_BASE, SysRedisKey.ROOT_KEY, "province"), list, 1, TimeUnit.DAYS);
        return list;
    }

    public List<DictDistrictBO> listCity(final Long province) {

        List<DictDistrictBO> list = redisCache.getCacheObject(RedisKeyUtil.generate(AdminRedisKey.ADMIN_BASE, SysRedisKey.ROOT_KEY, "city", String.valueOf(province)));
        if (CollectionUtil.isEmpty(list)) {
            list = dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.CITY.getValue()).setPid(province));
        }

        redisCache.setCacheObject(RedisKeyUtil.generate(AdminRedisKey.ADMIN_BASE, SysRedisKey.ROOT_KEY, "city", String.valueOf(province)), list, 1, TimeUnit.DAYS);
        return list;
    }
}
