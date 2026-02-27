package com.ruoyi.web.controller.express;


import com.ruoyi.biz.express.ExpressBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.kuaidi100.model.SubscribeResp;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "快递信息相关")
@RestController
@Anonymous
@RequestMapping("/express/notify")
public class ExpressCallbackController {

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    ExpressBizService expressBizService;

    @Anonymous
    @PostMapping("/info")
    public SubscribeResp notify(String param, String sign) {
        return expressBizService.kdCallback(param, sign);
    }


}
