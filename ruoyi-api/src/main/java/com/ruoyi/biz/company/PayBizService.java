package com.ruoyi.biz.company;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.facade.IRechargeFacade;
import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.consts.PaymentConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.config.properties.WxMpProperties;
import com.ruoyi.config.properties.WxPayProperties;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import com.ruoyi.web.form.pay.PayForm;
import com.ruoyi.web.vo.user.PrePayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

/**
 * 支付业务服务 - H5微信支付预订单创建
 * 使用 wxjava-pay 模块对接微信H5网页支付
 *
 * @author ruoyi
 * @date 2025
 */
@Slf4j
@Service
public class PayBizService {

    @Autowired
    private WxMpProperties wxMpProperties;

    @Autowired
    private WxPayProperties wxPayProperties;

    @Autowired
    private IMemberFacade memberFacade;

    @Autowired
    private IRechargeFacade rechargeFacade;

    @Autowired
    private ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    private ICompanyFacade companyFacade;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    /**
     * H5微信网页支付预下单 - 创建预订单并调用微信支付API
     *
     * @param memberId  用户ID
     * @param companyId 企业ID
     * @param payForm   支付表单
     * @return 预支付结果，包含H5支付链接
     */
    public PrePayVO prePay(Long memberId, Long companyId, PayForm payForm) {
        // 1. 校验企业信息
        CompanyBO company = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        Assert.notNull(company, "企业不存在");

        // 2. 校验支付参数
        verifyPayForm(payForm);

        // 3. 创建支付预订单并保存到数据库 u_recharge 表
        RechargeBO recharge = createRechargeOrder(memberId, companyId, payForm, IdUtils.fastSimpleUUID());

        // 4. 调用微信H5支付统一下单接口（使用wxjava-pay）
        Object o = wxPay(memberId, payForm, recharge.getTradeNo());

        // 5. 构造返回结果
        PrePayVO prePayResult = new PrePayVO();
        prePayResult.setTradeNo(recharge.getTradeNo());
        prePayResult.setData(o);
        return prePayResult;
    }

    public Object wxPay(Long userId, PayForm payForm, String tradeNo) {
        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        // 设置appid和mchid
        request.setAppid(wxMpProperties.getConfigs().get(0).getAppId());
        request.setMchid(wxPayProperties.getMchId());

        //body字段长度不能大于128
        request.setDescription("保证金充值");
        request.setOutTradeNo(tradeNo);
        //微信支付金额单位为分
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setCurrency("CNY");
        amount.setTotal(NumberUtil.mul(payForm.getAmount(), 100).intValue());
        request.setAmount(amount);
        request.setNotifyUrl(String.format(WebConstants.PAY_NOTIFY_URL, ruoYiConfig.getHost(), tradeNo));
        try {
            // JSAPI 微信内H5网页支付
            return wxPayService.createOrderV3(TradeTypeEnum.NATIVE, request);
        } catch (WxPayException e) {
            log.error("微信预支付失败:[{}]:", e.getMessage());
            e.printStackTrace();
            throw new ServiceException("微信预支付失败：" + e.getMessage());
        }
    }

    /**
     * 创建支付预订单并持久化到数据库 u_recharge 表
     *
     * @param userId    用户ID
     * @param companyId 企业ID
     * @param payForm   支付表单
     * @param tradeNo   交易单号
     * @return 保存后的支付订单
     */
    public RechargeBO createRechargeOrder(Long userId, Long companyId,
                                          PayForm payForm, String tradeNo) {
        // 构建支付参数
        RechargeParam rechargeParam = new RechargeParam();
        rechargeParam.setCreateBy(userId);
        rechargeParam.setCompanyId(companyId);
        rechargeParam.setAmount(payForm.getAmount());
        rechargeParam.setTradeNo(tradeNo);
        rechargeParam.setTradeStates(PaymentConsts.PAY_PRE);
        rechargeParam.setCreateTime(DateUtil.date());

        // 保存到数据库
        return rechargeFacade.save(rechargeParam);
    }

    /**
     * 查询支付状态
     *
     * @param tradeNo 交易单号
     * @return 是否已支付成功
     */
    public Boolean payStatus(String tradeNo) {
        RechargeBO recharge = rechargeFacade.queryOne(new RechargeQuery().setTradeNo(tradeNo));
        if (Objects.isNull(recharge)) {
            throw new ServiceException("充值订单不存在");
        }
        // 非预支付状态，直接返回结果
        if (!PaymentConsts.PAY_PRE.equals(recharge.getTradeStates())) {
            return PaymentConsts.PAY_SUCCESS.equals(recharge.getTradeStates());
        }
        // 预支付状态，仍然返回未支付
        return false;
    }

    public String payNotify(String tradeNo, String data, SignatureHeader signatureHeader) throws WxPayException {
        // 校验支付结果
        WxPayNotifyV3Result wxPayNotifyV3Result = wxPayService.parseOrderNotifyV3Result(data, signatureHeader);

        // 查询数据库订单
        RechargeBO recharge = rechargeFacade.queryOne(new RechargeQuery().setTradeNo(tradeNo));
        if (Objects.isNull(recharge)) {
            log.error("充值订单不存在，tradeNo:{}", tradeNo);
            throw new ServiceException("充值订单不存在");
        }

        // 校验支付金额
        BigDecimal notifyAmount = new BigDecimal(wxPayNotifyV3Result.getResult().getAmount().getTotal()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        if (recharge.getAmount().compareTo(notifyAmount) != 0) {
            log.error("支付金额不匹配，tradeNo:{}, expected:{}, actual:{}", tradeNo, recharge.getAmount(), notifyAmount);
            throw new ServiceException("支付金额不匹配");
        }

        // 更新订单状态为成功
        RechargeParam updateParam = new RechargeParam().setTradeStates(PaymentConsts.PAY_SUCCESS).setOutNo(wxPayNotifyV3Result.getResult().getTransactionId()).setNotifyTime(new Date());
        rechargeFacade.update(updateParam, new RechargeQuery().setId(recharge.getId()));

        // 增加企业可用资金
        CompanyCapitalLogParam capitalLog = new CompanyCapitalLogParam().setOutAmount(BigDecimal.ZERO).setOrderNo(recharge.getTradeNo())
                .setCompanyId(recharge.getCompanyId()).setAddAmount(recharge.getAmount()).setRemark("微信H5支付充值").setType(CompanyCapitalConsts.LogTypes.CHARGE.getCode());
        companyCapitalFacade.changeAvailable(capitalLog);

        log.info("微信H5支付成功，订单已完成，rechargeId:{}, tradeNo:{}, amount:{}", recharge.getId(), tradeNo, recharge.getAmount());

        // 返回成功给微信
        return "SUCCESS";
    }

    /**
     * 校验支付表单参数
     */
    private void verifyPayForm(PayForm payForm) {
        if (Objects.isNull(payForm.getAmount()) || payForm.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("支付金额必须大于0");
        }
        if (Objects.isNull(payForm.getClientIp())) {
            throw new ServiceException("客户端IP不能为空");
        }
//        if (Objects.isNull(payForm.getSubject()) || payForm.getSubject().isEmpty()) {
//            throw new ServiceException("订单标题不能为空");
//        }
    }
}
