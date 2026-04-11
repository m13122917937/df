package com.ruoyi.web.controller.user;

import com.ruoyi.biz.company.PayBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisKeyUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.user.model.consts.UserRedisKey;
import com.ruoyi.web.form.pay.PayForm;
import com.ruoyi.web.vo.user.PrePayVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 扫码支付V2 - H5微信支付.
 *
 * @date 2022/5/29
 */
@Slf4j
@RestController
@RequestMapping("pay")
public class PayController extends BaseController {

    @Autowired
    private PayBizService payBizService;

    @Autowired
    private RedisCache redisCache;

    /**
     * H5微信支付 - 创建支付预定单
     *
     * @param type 支付类型
     * @param form 支付表单
     * @return 支付预下单结果
     */
    @PostMapping("/{type}")
    public AjaxResult prePay(@PathVariable("type") final Integer type, @Validated @RequestBody final PayForm form) {
        Long userId = SecurityUtils.getUserId();
        Long companyId = SecurityUtils.getDeptId();
        form.setClientIp(IpUtils.getIpAddr());
        // 创建支付预定单并调用微信支付获取预下单信息
        PrePayVO result = payBizService.prePay(userId, companyId, form);
        return AjaxResult.success(result);
    }

    @PostMapping("/status/{tradeNo}")
    public AjaxResult payStatus(@PathVariable("tradeNo") final String tradeNo) {
        return AjaxResult.success(payBizService.payStatus(tradeNo));
    }

    @Anonymous
    @RequestMapping(value = "/notify/{tradeNo}", method = {RequestMethod.GET, RequestMethod.POST})
    public String payNotify(@PathVariable("tradeNo") final String tradeNo, @RequestBody(required = false) final String data) {
        log.info("支付回调通知数据[ tradeNo:{}]:{}", tradeNo, data);
        String processKey = RedisKeyUtil.generate(UserRedisKey.ROOT_KEY, UserRedisKey.Pay.ROOT_KEY, UserRedisKey.Pay.NOTIFY_PROCESS, tradeNo);
        if (!redisCache.setIfAbsent(processKey, "locked", 24L, TimeUnit.HOURS)) {
            return "处理中....";
        }

        // 判断是否已经处理过了
        log.info("支付结果[{}]:{}", tradeNo, true);
        try {
            return payBizService.payNotify(tradeNo, StringUtils.defaultString(data));
        } catch (Exception e) {
            redisCache.deleteObject(processKey);
        }
        return null;
    }

}
