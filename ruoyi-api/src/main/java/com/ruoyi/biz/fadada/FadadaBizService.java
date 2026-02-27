package com.ruoyi.biz.fadada;

import com.fasc.open.api.bean.base.BaseRes;
import com.fasc.open.api.enums.corp.CorpAuthScopeEnum;
import com.fasc.open.api.enums.corp.CorpIdentTypeEnum;
import com.fasc.open.api.exception.ApiException;
import com.fasc.open.api.v5_1.client.CorpClient;
import com.fasc.open.api.v5_1.client.ServiceClient;
import com.fasc.open.api.v5_1.req.corp.CorpUnbindReq;
import com.fasc.open.api.v5_1.req.corp.GetCorpAuthResourceUrlReq;
import com.fasc.open.api.v5_1.res.common.ECorpAuthUrlRes;
import com.ruoyi.config.properties.FadadaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class FadadaBizService {

    @Autowired
    ServiceClient serviceClient;

    @Autowired
    CorpClient corpClient;

    @Autowired
    FadadaProperties fadadaProperties;

    public void xxx() throws ApiException {
        // 获取企业用户授权链接
        GetCorpAuthResourceUrlReq req = new GetCorpAuthResourceUrlReq();
        req.setAccessToken(serviceClient.getAccessToken().getData().getAccessToken());
        //企业在应用中的唯一标识
        req.setClientCorpId("1");
        //（可选）法大大平台上企业主体的名称
        req.setCorpName("绵阳风扬通讯有限公司");
        //（可选）企业组织类型CorpIdentTypeEnum
        req.setCorpIdentType(CorpIdentTypeEnum.CORP.getCode());
        //（可选）企业统一社会信用代码或各种类型组织的唯一代码
        req.setCorpIdentNo("91510703MA6242CC17");
        //（可选）是否需要匹配企业身份信息一致，缺省为false。
        req.setCorpIdentInfoMatch(false);
        //（可选）企业授权范围列表CorpAuthScopeEnum
        req.setAuthScopes(Arrays.asList(new String[]{CorpAuthScopeEnum.IDENT_INFO.getCode(),
                CorpAuthScopeEnum.SIGN_TASK_INIT.getCode(),
                CorpAuthScopeEnum.SIGN_TASK_FILE.getCode(),
                CorpAuthScopeEnum.SEAL_INFO.getCode(),
                CorpAuthScopeEnum.TEMPLATE.getCode()
        }));
        //（可选）重定向地址。
        req.setRedirectUrl("Https://www.baidu.com");
        BaseRes<ECorpAuthUrlRes> res = corpClient.getCorpAuthUrl(req);
        System.out.println( res );
    }



    /**
     * 解除企业用户授权
     *
     * @throws ApiException
     */
    public  void corpUnbind() throws ApiException {
        CorpUnbindReq corpUnbindReq = new CorpUnbindReq();
        corpUnbindReq.setAccessToken(serviceClient.getAccessToken().getData().getAccessToken());
        //法大大平台为该企业在该应用appId范围内分配的唯一标识。
        corpUnbindReq.setOpenCorpId(fadadaProperties.getOpenCorpId());
        BaseRes<Void> res = corpClient.unbind(corpUnbindReq);
        System.out.println( res );
    }


}
