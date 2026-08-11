package com.ruoyi.web.convert.miniapp;

import com.ruoyi.subsidy.model.bo.GbOrderBO;
import com.ruoyi.web.vo.miniapp.MiniappOrderVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 小程序订单 Web 转换器。
 */
@Mapper
public interface MiniappOrderWebConvert {
    MiniappOrderWebConvert INSTANCE = Mappers.getMapper(MiniappOrderWebConvert.class);

    MiniappOrderVO toVO(GbOrderBO source);

    /** 领域订单列表转小程序响应。 */
    List<MiniappOrderVO> toVOList(List<GbOrderBO> source);
}
