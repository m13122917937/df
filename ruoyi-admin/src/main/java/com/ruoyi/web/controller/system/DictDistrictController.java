package com.ruoyi.web.controller.system;


import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.Ignore;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.mapper.sys.DictDistrictConvert;
import com.ruoyi.system.facade.IDictDistrictFacade;
import com.ruoyi.system.model.consts.DictDistrictConsts;
import com.ruoyi.system.model.consts.SysRedisKey;
import com.ruoyi.system.model.query.DictDistrictQuery;
import com.ruoyi.web.vo.DictDistrictVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Ignore
@RestController
@Api(tags = "省市")
@RequestMapping("/system")
public class DictDistrictController {

    @Autowired
    private IDictDistrictFacade dictDistrictFacade;

    @Autowired
    RedisCache redisCache;

    @ApiOperation("查询省列表")
    @GetMapping("/province/list")
    public AjaxResult list() {

        List<DictDistrictVO> list = redisCache.getCacheObject(RedisKeyUtil.generate(SysRedisKey.ROOT_KEY, "province"));
        if (CollectionUtil.isEmpty(list)) {
            list = DictDistrictConvert.INSTANCE.toVo(dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.PROVINCE.getValue())));
            redisCache.setCacheObject(RedisKeyUtil.generate(SysRedisKey.ROOT_KEY, "province"), list, 1, TimeUnit.DAYS);
        }
        return AjaxResult.success(list);
    }

    @ApiOperation("查询市列表")
    @GetMapping("/city/{province}")
    public AjaxResult list(@PathVariable("province") Long province) {

        List<DictDistrictVO> list = redisCache.getCacheObject(RedisKeyUtil.generate(SysRedisKey.ROOT_KEY, "city", String.valueOf(province)));
        if (CollectionUtil.isEmpty(list)) {
            list = DictDistrictConvert.INSTANCE.toVo(dictDistrictFacade.list(new DictDistrictQuery().setLevel(DictDistrictConsts.Level.CITY.getValue()).setPid(province)));
            redisCache.setCacheObject(RedisKeyUtil.generate(SysRedisKey.ROOT_KEY, "city", String.valueOf(province)), list, 1, TimeUnit.DAYS);
        }
        return AjaxResult.success(list);
    }


}
