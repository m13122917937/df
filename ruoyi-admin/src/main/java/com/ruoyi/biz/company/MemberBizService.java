package com.ruoyi.biz.company;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.user.facade.ICompanyFacade;
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

import java.util.Objects;
import java.lang.StringBuilder;

@Slf4j
@Service
public class MemberBizService {

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
        return wxMpService.getQrcodeService().qrCodePictureUrl(codeTicket.getTicket());
    }


    /**
     * 生成微信渠道字符串.
     *
     * @param companyId 企业id
     * @param type      业务类型
     * @param phone     手机号码
     * @param userName  用户名
     * @param owner     所有者类型
     * @param uuid      唯一标识
     * @return 拼接后的参数字符串
     */
    public String buildQrCodeParam(Long companyId, Integer type, String phone, String userName, Integer owner, String uuid) {
        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_TYPE, type);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_DEPTID, companyId);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_PHONE, phone);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_USERNAME, userName);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_OWNER, owner);
        appendIfNotNull(sb, UserApiConsts.QR_CODE_SCENE_UUID, uuid);
        return sb.length() > 0 && sb.charAt(0) == '&' ? sb.substring(1) : sb.toString();
    }

    /**
     * 如果值不为null则追加到StringBuilder.
     */
    private void appendIfNotNull(StringBuilder sb, String key, Object value) {
        if (value != null) {
            sb.append('&').append(key).append('=').append(value);
        }
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
