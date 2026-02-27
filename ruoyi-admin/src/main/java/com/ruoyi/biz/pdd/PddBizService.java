package com.ruoyi.biz.pdd;

import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopAccessTokenClient;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddOrderInformationGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddOrderInformationGetResponse;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.config.properties.PddProperties;
import com.ruoyi.mapper.bill.PayerConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class PddBizService {

    @Autowired
    PddProperties pddProperties;

    @Autowired
    PopClient popClient;

    @Autowired
    IPayerConfigFacade payerConfigFacade;

    public void generateToken(String code, Long state) throws Exception {
        PopAccessTokenClient client = new PopAccessTokenClient(pddProperties.getClientId(), pddProperties.getClientSecret());
        AccessTokenResponse accessTokenResponse = client.generate(code);

        PayerConfigParam param = new PayerConfigParam();
        param.setToken(accessTokenResponse.getAccessToken()).setExpireTime(new Date(accessTokenResponse.getExpiresIn()));
        payerConfigFacade.update(param, new PayerConfigQuery().setId(state));

    }


    public void vxx() throws Exception {

        PddOrderInformationGetRequest request = new PddOrderInformationGetRequest();
        request.setOrderSn("260203-126636573091977");
        PddOrderInformationGetResponse response = popClient.syncInvoke(request, "98926fd9cbb9435688f933e63d83a24f927b59e8");
        System.out.println(JsonUtil.transferToJson(response));
    }
}
