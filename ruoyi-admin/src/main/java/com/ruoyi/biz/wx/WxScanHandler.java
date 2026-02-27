package com.ruoyi.biz.wx;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.company.UserBizService;
import com.ruoyi.biz.company.WxBizService;
import com.ruoyi.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 扫码关注事件 处理
 */
@Slf4j
@Component
public class WxScanHandler implements WxMpMessageHandler {

    @Autowired
    private WxBizService wxBizService;

    @Override
    public WxMpXmlOutMessage handle(final WxMpXmlMessage wxMessage, final Map<String, Object> map,
                                    final WxMpService weixinService, final WxSessionManager wxSessionManager) throws WxErrorException {

        //  判断是否通过渠道链接进来， 存储到 u_wechat_user
        if (StrUtil.isBlank(wxMessage.getEventKey())) {
            return null;
        }
        // 获取微信用户基本信息
        WxMpUser userWxInfo = weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);
        log.info("扫码用户信息:{}", JacksonUtil.toJson(userWxInfo));
        wxBizService.pushUser(userWxInfo, wxMessage.getEventKey());
        return WxMpXmlOutMessage.TEXT().content("登录成功，请在平台进行交易").fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
    }
}
