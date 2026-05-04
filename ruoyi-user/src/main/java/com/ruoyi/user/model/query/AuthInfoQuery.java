package com.ruoyi.user.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 授权信息查询条件
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
public class AuthInfoQuery {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户名模糊查询
     */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "user_name")
    private String userNameLike;

    /**
     * 类型
     */
    private Integer type;

    /**
     * mac地址
     */
    private String macId;

}
