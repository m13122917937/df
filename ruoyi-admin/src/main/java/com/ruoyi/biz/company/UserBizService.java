package com.ruoyi.biz.company;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.query.CompanyQuery;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBizService {

    @Autowired
    IUserFacade userFacade;

    @Autowired
    RedisCache redisCache;


    @Autowired
    WxMpService wxMpService;

    @Autowired
    ICompanyFacade companyFacade;


    /**
     * 生成渠道二维码.
     *
     * @param param      微信参数.
     * @param expireTime 过期时间.
     * @return 渠道二维码链接.
     */
    public String channelCode(final String param, final Integer expireTime) throws WxErrorException {
        // 换取临时二维码ticket
        log.info("生成渠道二维码参数：{}", param);
        WxMpQrCodeTicket codeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(param,
                ObjectUtils.defaultIfNull(expireTime, UserApiConsts.QR_CODE_EXPIRE_SECONDS));

        if (Objects.isNull(codeTicket) || StrUtil.isBlank(codeTicket.getTicket())) {
            log.error("获取微信公众号ticket失败");
            throw new ServiceException("获取渠道二维码异常");
        }
        return wxMpService.getQrcodeService().qrCodePictureUrl(codeTicket.getTicket());
    }


    /**
     * 生成 微信渠道字符串.
     *
     * @param companyId 企业id
     * @param type      业务类型
     * @param phone     手机号码
     * @return
     */
    public String buildQrCodeParam(Long companyId, Integer type, String phone, String userName, Integer owner) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserApiConsts.QR_CODE_SCENE_TYPE, type);
        map.put(UserApiConsts.QR_CODE_SCENE_DEPTID, companyId);
        map.put(UserApiConsts.QR_CODE_SCENE_PHONE, phone);
        map.put(UserApiConsts.QR_CODE_SCENE_USERNAME, userName);
        map.put(UserApiConsts.QR_CODE_SCENE_OWNER, owner);

        return map.entrySet().stream().filter(e -> Objects.nonNull(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
    }

    public void checkCompany(final Long companyId) {

        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        if (Objects.isNull(companyBO)) {
            throw new ServiceException("企业不存在");
        }
    }

    /**
     * 删除用户
     *
     * @param companyId
     * @param userId
     */

    public void removeUserCompany(Long companyId, Long userId) {

        companyFacade.removeUserCompany(companyId, userId);
    }
}
