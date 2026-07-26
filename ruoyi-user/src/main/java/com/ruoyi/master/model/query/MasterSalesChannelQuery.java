package com.ruoyi.master.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 销售渠道查询条件。
 */
@Data
@Accessors(chain = true)
public class MasterSalesChannelQuery {

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "channel_code")
    private String channelCodeLike;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "channel_name")
    private String channelNameLike;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "platform_name")
    private String platformNameLike;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "subject_name")
    private String subjectNameLike;
}
