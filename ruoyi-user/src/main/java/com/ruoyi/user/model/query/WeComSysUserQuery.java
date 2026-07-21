package com.ruoyi.user.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/** 企业微信用户同步使用的系统用户查询条件。 */
@Data
@Accessors(chain = true)
public class WeComSysUserQuery {
    private String phonenumber;

    @QueryField(operator = DynamicCondition.Operator.NOT_NULL, field = "phonenumber")
    private Boolean phonenumberPresent;
}
