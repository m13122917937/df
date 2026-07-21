package com.ruoyi.user.convert;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.user.model.bo.WeComUserProfileBO;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** 企业微信成员对象转换器。 */
@Mapper
public interface WeComUserConvert {
    WeComUserConvert INSTANCE = Mappers.getMapper(WeComUserConvert.class);

    /**
     * 将企业微信成员转换为领域对象。
     *
     * @param source 企业微信成员
     * @return 企业微信成员资料
     */
    @Mapping(target = "mobile", source = "mobile")
    WeComUserProfileBO toProfile(WxCpUser source);

    /**
     * 将企业微信成员资料转换为系统用户基本资料。
     *
     * @param source 企业微信成员资料
     * @return 系统用户基本资料
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "nickName", source = "name")
    @Mapping(target = "phonenumber", source = "mobile")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roleIds", ignore = true)
    SysUser toSysUser(WeComUserProfileBO source);
}
