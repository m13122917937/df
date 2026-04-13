package com.ruoyi.user.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserQuery {

    private Long userId;
    @QueryField(operator = DynamicCondition.Operator.IN, field = "user_id")
    private List<Long> userIdSet;

    private String openId;

    private String unionId;

    private String username;

    private String nickName;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "nick_name")
    private String nickNameLike;


    private String mobile;

    private String email;

    @QueryField(ignore = true)
    private Long companyId;
}
