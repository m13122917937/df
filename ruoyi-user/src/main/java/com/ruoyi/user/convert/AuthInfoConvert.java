package com.ruoyi.user.convert;

import com.ruoyi.user.domain.AuthInfo;
import com.ruoyi.user.model.bo.AuthInfoBO;
import com.ruoyi.user.model.param.AuthInfoParam;
import com.ruoyi.user.model.query.AuthInfoQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 授权信息转换器
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Mapper
public interface AuthInfoConvert {

    AuthInfoConvert INSTANCE = Mappers.getMapper(AuthInfoConvert.class);

    /**
     * 将domain列表转换为BO列表
     *
     * @param list domain列表
     * @return BO列表
     */
    List<AuthInfoBO> domainToBoList(List<AuthInfo> list);

    /**
     * 将查询条件转换为domain
     *
     * @param authInfoQuery 查询条件
     * @return domain对象
     */
    AuthInfo queryToDomain(AuthInfoQuery authInfoQuery);

    /**
     * 将domain转换为BO
     *
     * @param authInfo domain对象
     * @return BO对象
     */
    AuthInfoBO domainToBo(AuthInfo authInfo);

    /**
     * 将参数转换为domain
     *
     * @param authInfoParam 参数对象
     * @return domain对象
     */
    AuthInfo paramToDomain(AuthInfoParam authInfoParam);

}
