package com.ruoyi.kuaidi100;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.kuaidi100.exception.ErrorCode;
import com.ruoyi.kuaidi100.exception.ExpressException;
import com.ruoyi.kuaidi100.model.*;
import com.ruoyi.kuaidi100.properties.ExpressProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 快递信息查询.
 */
@Slf4j
public class ExpressClient {

    private static final String SUCCEED = "200";

    public static final String LOG = "【快递100】";

    protected final ExpressProperties expressProperties;

    public ExpressClient(final ExpressProperties expressProperties) {
        this.expressProperties = expressProperties;
    }

    /**
     * 快递100 查询快递信息接口
     *
     * @param expressCode 快递公司编码
     * @param expressNo 快递单号
     * @param cellphone 收件人手机号
     * @return 快递物流信息
     */
    public ExpressResult findExpress(final String expressCode, final String expressNo, final String cellphone) {
        String num = expressNo.replace(" ", "").replaceAll("[\\p{Cf}]", "");;
        Map<String, String> paramMap = MapUtil.newHashMap(8);
        // 结果排序
        paramMap.put("order", "asc");
        // 快递公司
        paramMap.put("com", expressCode);
        // 快递单号
        paramMap.put("num", num);
        // 手机号码
        paramMap.put("phone", cellphone);
        // 开通行政区域解析功能
        paramMap.put("resultv2", "4");
        String param = JacksonUtil.toJson(paramMap);
        Map<String, Object> requestBody = MapUtil.newHashMap(5);
        // 授权码，请申请企业版获取
        requestBody.put("customer", expressProperties.getCustomer());
        requestBody.put("sign", sign(param + expressProperties.getKey() + expressProperties.getCustomer()));
        requestBody.put("param", param);

        ExpressResult expressResult;
        try {
            log.info("快递100调用入参--{}", JacksonUtil.toJson(requestBody));
            String result = HttpUtil.post(ApiInfoConstant.QUERY_URL, requestBody);
            log.info("调用快递API返回单号[{}]的快递信息数据：{}", expressNo, result);
            if (StrUtil.isBlank(result)) {
                throw new ExpressException(ErrorCode.API_ERROR);
            }
            expressResult = JacksonUtil.parse(result, ExpressResult.class);
        } catch (HttpException e) {
            log.warn("调用快递API接口异常[{}]:{}", expressNo, e.getMessage());
            throw new ExpressException(ErrorCode.API_ERROR);
        } catch (ConvertException e) {
            log.warn("调用快递API单号[{}]响应的快递数据转换异常:{}", expressNo, e);
            throw new ExpressException(ErrorCode.API_ERROR);
        }
        //查询失败状态处理
        return expressResult;
    }


    /**
     * 快递100加密方式统一为MD5后转大写
     *
     * @param msg 参数
     * @return MD5
     */
    private static String sign(String msg) {
        return SecureUtil.md5(msg).toUpperCase();
    }

    /**
     * 订阅快递
     *
     * @param expressParam 参数列表
     * @return String 订阅状态
     */
    public SubscribeExpressCode subscribeExpress(SubscribeExpressParam expressParam) {
        //附加参数信息(回调地址，签名，是否开启行政区域解析等)
        SubscribeParameters parameter = infoSubscribeParameters(expressParam);
        // 设置参数(设置key)
        SubscribeParam param = infoSubscribeParam(parameter, expressParam);
        // 设置请求参数
        SubscribeReq request = infoSubscribeReq();
        // 设置请求接口
        try {
            request.setParam(JacksonUtil.toJson(param));
            // 订阅
            Map<String, Object> map = BeanUtil.beanToMap(request);

            String post = HttpUtil.post(ApiInfoConstant.SUBSCRIBE_URL, map);
            SubscribeResp httpResult = JacksonUtil.parse(post, SubscribeResp.class);

            SubscribeExpressCode expressCode = SubscribeExpressCode.fromCode(httpResult.getReturnCode());
            if (Objects.nonNull(expressCode)) {
                return expressCode;
            }
        } catch (Exception e) {
            log.info("{}订阅接口-->出现异常--{}", LOG, e);
            return SubscribeExpressCode.SUBSCRIPTION_FAIL;
        }
        return SubscribeExpressCode.SUBSCRIPTION_SUCCESS;
    }

    /**
     * 修改订阅状态接口
     *
     * @param param 入参
     * @return 是否修改成功
     */
    public Boolean subscribeOperate(SubscribeOperateParam param) {
        Map<String, Object> requestBody = MapUtil.newHashMap(5);
        // 授权码，请申请企业版获取
        requestBody.put("key", expressProperties.getKey());
        String jsonStr = JacksonUtil.toJson(param);
        requestBody.put("param", jsonStr);
        requestBody.put("sign", sign(jsonStr + expressProperties.getKey() + expressProperties.getSecret()));
        try {
            log.info("{}修改订阅状态接口-->入参--{}", LOG, jsonStr);
            String result = HttpUtil.post(expressProperties.getSubscribeUrl(), requestBody);
            log.info("{}修改订阅状态接口-->返回：{}", LOG, result);
            if (StrUtil.isBlank(result)) {
                throw new ServiceException("修改订阅状态接口失败");
            }
            Map<String, Object> jsonObject = JacksonUtil.parse(result, Map.class);
            String code = (String) jsonObject.get("code");
            if (!SUCCEED.equals(code)) {
                throw new ServiceException("修改订阅状态失败！");
            }
        } catch (Exception e) {
            log.info("{}修改订阅状态接口-->异常--{}", LOG, e.getMessage());
            return false;
        }
        return true;
    }

    private SubscribeParameters infoSubscribeParameters(SubscribeExpressParam expressParam) {
        SubscribeParameters parameter = new SubscribeParameters();
        parameter.setCallbackurl(expressParam.getExpressCallBackUrl());
        String sign = sign(expressProperties.getKey() + expressProperties.getCustomer());
        parameter.setSalt(sign);
        parameter.setPhone(expressParam.getCellphone());
        //开通行政区域解析功能以及物流轨迹增加物流状态值
        parameter.setResultv2("4");
        return parameter;
    }

    private SubscribeParam infoSubscribeParam(SubscribeParameters parameter, SubscribeExpressParam expressParam) {
        SubscribeParam param = new SubscribeParam();
        param.setKey(expressProperties.getKey());
        param.setParameters(parameter);
        param.setCompany(expressParam.getExpressCode());
        param.setNumber(expressParam.getExpressNo());
        param.setFrom(expressParam.getFrom());
        param.setTo(expressParam.getTo());
        return param;
    }

    private SubscribeReq infoSubscribeReq() {
        SubscribeReq request = new SubscribeReq();
        request.setSchema("json");
        return request;
    }


}
