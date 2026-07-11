package com.ruoyi.biz.company;

import cn.hutool.core.convert.Convert;
import com.google.common.base.Splitter;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.bo.UserLoginBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.consts.UserRedisKey;
import com.ruoyi.user.model.param.MemberParam;
import com.ruoyi.user.model.param.UserParam;
import com.ruoyi.user.model.query.MemberQuery;
import com.ruoyi.user.model.query.UserQuery;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WxBizService {
    @Autowired
    IMemberFacade memberFacade;

    @Autowired
    RedisCache redisCache;


    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    public void pushUser(WxMpUser userWxInfo, String eventKey) {
        MemberBO memberBO = memberFacade.queryOne(new MemberQuery().setOpenId(userWxInfo.getOpenId()));

        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(eventKey);
        String type = map.get(UserApiConsts.QR_CODE_SCENE_TYPE);
        log.info("扫码参数:{}", map);
        if (Objects.equals(Convert.toInt(type), UserApiConsts.INVITATION_SUB_QR_CODE)) {
            if (Objects.isNull(memberBO)) {
                // 添加用户 并且添加 企业用户信息
                memberFacade.addMemberAndCompany(new MemberParam().setOpenId(userWxInfo.getOpenId()).setPhone(map.get(UserApiConsts.QR_CODE_SCENE_PHONE)).setNickName(map.get(UserApiConsts.QR_CODE_SCENE_USERNAME))
                        .setCompanyId(Convert.toLong(map.get(UserApiConsts.QR_CODE_SCENE_DEPTID))).setOwner(Convert.toInt(map.get(UserApiConsts.QR_CODE_SCENE_OWNER))));
            } else {
                // 添加企业用户信息
                String name = map.get(UserApiConsts.QR_CODE_SCENE_USERNAME);
                memberFacade.addMemberCompany(memberBO.getUserId(), Convert.toLong(map.get(UserApiConsts.QR_CODE_SCENE_DEPTID)), name,
                        Convert.toInt(map.get(UserApiConsts.QR_CODE_SCENE_OWNER)));
            }
        } else if (Objects.equals(Convert.toInt(type), UserApiConsts.LOGIN_QR_CODE_TYPE)) {
            String uuid = map.get(UserApiConsts.QR_CODE_SCENE_UUID);
            // 用户不存在,需要先添加用户
            UserLoginBO userLoginBO = new UserLoginBO();
            if (Objects.isNull(memberBO)) {
                userLoginBO.setNewUser(Boolean.TRUE);
                redisCache.setCacheObject(UserRedisKey.USER_LOGIN_QR_CODE + uuid, userLoginBO, 30, TimeUnit.MINUTES);
                return;
            }
            userLoginBO.setUserId(memberBO.getUserId()).setNewUser(Boolean.FALSE);
            // 登录成功设置key
            redisCache.setCacheObject(UserRedisKey.USER_LOGIN_QR_CODE + uuid, userLoginBO, 30, TimeUnit.MINUTES);
        }

    }


}
