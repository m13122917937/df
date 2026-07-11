package com.ruoyi.web.controller.system;


import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.consts.ApiRedisKey;
import com.ruoyi.mapper.sys.DictDistrictConvert;
import com.ruoyi.system.facade.IDictDistrictFacade;
import com.ruoyi.system.model.consts.DictDistrictConsts;
import com.ruoyi.system.model.query.DictDistrictQuery;
import com.ruoyi.web.vo.system.DictDistrictVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController

@RequestMapping("/system")
public class DictDistrictController {

    @Autowired
    private IDictDistrictFacade dictDistrictFacade;

    @Autowired
    RedisCache redisCache;


    @GetMapping("/province/list")
    public AjaxResult list() {

        List<DictDistrictVO> list = redisCache.getCacheObject(RedisKeyUtil.generate(ApiRedisKey.SYSTEM_PROVINCE));
        if (CollectionUtil.isEmpty(list)) {
            list = DictDistrictConvert.INSTANCE.toVo(dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.PROVINCE.getValue())));
            redisCache.setCacheObject(ApiRedisKey.SYSTEM_PROVINCE, list, 1, TimeUnit.DAYS);
        }
        return AjaxResult.success(list);
    }


    @GetMapping("/city/{province}")
    public AjaxResult list(@PathVariable("province") Long province) {

        List<DictDistrictVO> list = redisCache.getCacheObject(RedisKeyUtil.generate(ApiRedisKey.SYSTEM_PROVINCE_CITY, String.valueOf(province)));
        if (CollectionUtil.isEmpty(list)) {
            list = DictDistrictConvert.INSTANCE.toVo(dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.CITY.getValue()).setPid(province)));
            redisCache.setCacheObject(RedisKeyUtil.generate(ApiRedisKey.SYSTEM_PROVINCE_CITY, String.valueOf(province)), list, 1, TimeUnit.DAYS);
        }
        return AjaxResult.success(list);
    }


}
