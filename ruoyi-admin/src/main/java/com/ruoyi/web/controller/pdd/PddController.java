package com.ruoyi.web.controller.pdd;

import com.pdd.pop.sdk.http.PopAccessTokenClient;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import com.ruoyi.biz.pdd.PddBizService;
import com.ruoyi.config.properties.PddProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
@RequestMapping("pdd")
public class PddController {

    @Autowired
    PddBizService pddBizService;

    @GetMapping("account/callback")
    public String account(@RequestParam("code") String code, @RequestParam("state") Long state) throws Exception {

        pddBizService.generateToken(code, state);

        return "success";
    }

}
