package com.ruoyi.user.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 企业微信用户关联查询条件。
 */
@Data
@Accessors(chain = true)
public class WeComUserRelationQuery {
    private Long userId;
    private String wecomUserId;
}
