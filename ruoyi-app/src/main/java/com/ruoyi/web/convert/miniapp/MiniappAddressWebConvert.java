package com.ruoyi.web.convert.miniapp;

import com.ruoyi.subsidy.domain.GbMemberAddress;
import com.ruoyi.subsidy.model.param.GbMemberAddressParam;
import com.ruoyi.web.form.miniapp.MiniappAddressRequest;
import com.ruoyi.web.vo.miniapp.MiniappAddressVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/** 小程序地址 Web 转换器。 */
@Mapper
public interface MiniappAddressWebConvert {
    MiniappAddressWebConvert INSTANCE = Mappers.getMapper(MiniappAddressWebConvert.class);
    /** 请求转领域参数。 */
    GbMemberAddressParam toParam(MiniappAddressRequest request);
    /** 领域对象转响应。 */
    MiniappAddressVO toVO(GbMemberAddress address);
    /** 领域列表转响应列表。 */
    List<MiniappAddressVO> toVOList(List<GbMemberAddress> addresses);
}
