package com.ruoyi.kuaidi100.model.reponse;


import com.ruoyi.kuaidi100.model.SubscribeResp;

/**
 * 快递100返回参数.
 *
 * @author duk
 * @date 2024-04-30 下午3:08
 */
public class Kd100SubscribeResp {

    /**
     * 成功数据
     * @return 是否成功
     */
    public static SubscribeResp initSucceedKd(){
        SubscribeResp response = new SubscribeResp();
        response.setResult(Boolean.TRUE);
        response.setReturnCode("200");
        response.setMessage("成功");
        return response;
    }

    /**
     * 失败数据
     * @param msg 失败原因
     * @return 是否成功
     */
    public static SubscribeResp initFailKd(String msg){
        SubscribeResp response = new SubscribeResp();
        response.setResult(Boolean.FALSE);
        response.setReturnCode("500");
        response.setMessage(msg);
        return response;
    }
}
