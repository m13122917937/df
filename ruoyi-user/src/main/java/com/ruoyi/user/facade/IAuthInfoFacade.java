package com.ruoyi.user.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.user.model.bo.AuthInfoBO;
import com.ruoyi.user.model.param.AuthInfoParam;
import com.ruoyi.user.model.query.AuthInfoQuery;

import java.util.List;

/**
 * 授权信息Facade接口
 *
 * @author ruoyi
 * @date 2026-04-18
 */
public interface IAuthInfoFacade {

    /**
     * 查询授权信息列表
     *
     * @param authInfoQuery 查询条件
     * @return 授权信息列表
     */
    List<AuthInfoBO> queryList(AuthInfoQuery authInfoQuery);

    /**
     * 查询单个授权信息
     *
     * @param authInfoQuery 查询条件
     * @return 授权信息
     */
    AuthInfoBO queryOne(AuthInfoQuery authInfoQuery);

    /**
     * 分页查询授权信息
     *
     * @param authInfoQuery 查询条件
     * @param pageParamV2   分页参数
     * @return 分页结果
     */
    PageBO<AuthInfoBO> pageQuery(AuthInfoQuery authInfoQuery, PageParamV2 pageParamV2);

    /**
     * 新增授权信息
     *
     * @param authInfoParam 授权信息参数
     * @return 新增后的授权信息
     */
    AuthInfoBO insert(AuthInfoParam authInfoParam);

    /**
     * 更新授权信息
     *
     * @param authInfoParam 授权信息参数
     * @return 是否更新成功
     */
    boolean update(AuthInfoParam authInfoParam, AuthInfoQuery query);

    /**
     * 根据ID删除授权信息
     *
     * @param id 主键ID
     * @return 是否删除成功
     */
    boolean deleteById(Integer id);

}
