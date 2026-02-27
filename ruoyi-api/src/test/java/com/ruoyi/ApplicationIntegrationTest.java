package com.ruoyi;

import cn.hutool.core.date.DateUtil;
import com.fasc.open.api.exception.ApiException;
import com.ruoyi.biz.fadada.FadadaBizService;
import com.ruoyi.biz.mq.MsgClient;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.system.service.ISysDictTypeService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

@ContextConfiguration
@SpringBootTest(classes = ApiApplication.class)
public class ApplicationIntegrationTest {


    @Autowired
    ExpressClient expressClient;
    @Autowired
    ISysDictTypeService dictTypeService;
    @Autowired
    private MsgClient msgClient;

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    RuoYiConfig ruoYiConfig;

    @Autowired
    FadadaBizService fadadaBizService;


//    @Test
//    public void throws ApiException {
//        fadadaBizService.xxx();
//    }

    @Test
    public void test() {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode("1bedb6539ed90000"));

        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo(routeSubscribeBO.getLogisticsNo())
                .setExpressCode(routeSubscribeBO.getLogisticsCode()).setOrderId(routeSubscribeBO.getOrderCode()).setCellphone(routeSubscribeBO.getPhone())
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, ruoYiConfig.getHost())));

        System.out.println(subscribeExpressCode);
    }


    @Test
    public void erwer() {

        // 订阅快递信息
        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo("SF3272987496015")
                .setExpressCode("sf").setOrderId("1ba824eb6f119000").setCellphone("18739934509")
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, "https://api.wujievip.cn")));
        System.out.println(subscribeExpressCode);
        //  扣保证金
//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setCompanyId(1L).setCreateTime(DateUtil.date()).setOutAmount(new BigDecimal(-50))
//                .setOrderNo("123123123").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));


//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setCompanyId(1L).setUpdateTime(DateUtil.date()).setAddAmount(new BigDecimal(50))
//                .setOrderNo("123123123").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));

    }


    @Test
    public void x() {
        SubscribeExpressParam subscribeExpressParam = new SubscribeExpressParam();
        subscribeExpressParam.setExpressCode("shunfeng");
        subscribeExpressParam.setExpressNo("SF0257574715016");
        subscribeExpressParam.setCellphone("13122917937");
        subscribeExpressParam.setExpressCallBackUrl("13122917937");
        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(subscribeExpressParam);
        System.out.println(subscribeExpressCode);

    }

}