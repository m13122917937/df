package com.ruoyi.user.model.query;

import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserQuery {

    private Long userId;
    @QueryField(operator = Operator.IN, field = "user_id")
    private List<Long> userIdSet;

    private String openId;

    private String unionId;

    private String username;

    private String nickName;

    @QueryField(operator = Operator.LIKE, field = "nick_name")
    private String nickNameLike;


    private String mobile;

    private String email;

    @QueryField(ignore = true)
    private Long companyId;
}
