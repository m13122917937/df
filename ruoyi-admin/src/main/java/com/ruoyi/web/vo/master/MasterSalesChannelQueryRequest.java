package com.ruoyi.web.vo.master;

import lombok.Data;

/**
 * 销售渠道列表查询请求。
 */
@Data
public class MasterSalesChannelQueryRequest {

    private String channelCodeLike;
    private String channelNameLike;
    private String platformNameLike;
    private String subjectNameLike;
}
