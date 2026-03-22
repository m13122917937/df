package com.ruoyi.web.controller.pay;

import cn.hutool.core.convert.Convert;
import com.ruoyi.biz.pay.ThirdPayService;
import com.ruoyi.biz.pay.model.PrePayResult;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.system.model.consts.SystemConsts;
import com.ruoyi.system.facade.ISysConfigFacade;
import com.ruoyi.user.model.consts.UserRedisKey;
import com.ruoyi.web.form.pay.PayForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 扫码支付.
 *
 * @date 2022/5/29
 */
@Slf4j

@RestController
@RequestMapping("pay")
public class PayController {

    @Autowired
    private ThirdPayService thirdPayService;

    @Autowired
    private ISysConfigFacade sysConfigService;

    @Autowired
    private RedisCache redisCache;




    @PostMapping("/{type}")
    public AjaxResult prePay(@PathVariable("type") final Integer type, @Validated @RequestBody final PayForm form) {
        PayTypes payTypes = PayTypes.fromCode(type);
        String config = sysConfigService.selectConfigByKey(SystemConsts.ConfigKey.SIMULATE_CONFIG.getCode());
        if (!Convert.toBool(config)) {
            if (Arith.lt(form.getAmount(), BigDecimal.valueOf(50))) {
                return AjaxResult.error("支付金额必须大于50元");
            }
            if (payTypes == PayTypes.SIMULATE) {
                return AjaxResult.error("暂不支持模拟支付");
            }
        }
        Long userId = SecurityUtils.getUserId();
        Long companyId = SecurityUtils.getDeptId();
        PrePayResult result = thirdPayService.prePay(userId, companyId, payTypes, form);
        //客户端信息缓存一天，用于推送支付结果
        return AjaxResult.success(result);
    }




    @PostMapping("/status/{tradeNo}")
    public AjaxResult payStatus(@PathVariable("tradeNo") final String tradeNo) {
        return AjaxResult.success(thirdPayService.payStatus(tradeNo));
    }

    @Anonymous

    @RequestMapping(value = "/notify/{type}/{tradeNo}", method = {RequestMethod.GET, RequestMethod.POST})
    public String payNotify(@PathVariable("type") final Integer type,
                            @PathVariable("tradeNo") final String tradeNo,
                            @RequestBody(required = false) final String notifyData) {
        log.info("支付回调通知数据[type:{}, tradeNo:{}]:{}", type, tradeNo, notifyData);
        String data = StringUtils.defaultString(notifyData);
        String processKey = RedisKeyUtil.generate(UserRedisKey.ROOT_KEY, UserRedisKey.Pay.ROOT_KEY, UserRedisKey.Pay.NOTIFY_PROCESS, String.valueOf(type), tradeNo);
        String process = redisCache.getCacheObject(processKey);
        // 防止hash冲突，校验通知结果和缓存中是否相同
        if (StringUtils.isNotBlank(process) && process.equals(data)) {
            return "正在处理......";
        }
        // 处理状态缓存10s
        redisCache.setCacheObject(processKey, data, 10, TimeUnit.SECONDS);
        // 判断是否已经处理过了
        String resultKey = RedisKeyUtil.generate(UserRedisKey.ROOT_KEY, UserRedisKey.Pay.ROOT_KEY, UserRedisKey.Pay.NOTIFY_RESULT, String.valueOf(type), tradeNo);
        String result = redisCache.getCacheObject(resultKey);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        try {
            PayTypes payType = PayTypes.fromCode(type);
            result = thirdPayService.payNotify(payType, tradeNo, data);
            redisCache.setCacheObject(resultKey, result, 24, TimeUnit.HOURS);
            log.info("支付结果[{}]:{}", tradeNo, true);
        } catch (Exception e) {
            log.error("处理支付通知回调出现异常[{}]:", type, e);
            throw new ServiceException("处理支付通知回调出现异常");
        }
        return result;
    }
}
