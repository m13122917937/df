package com.ruoyi.esign.model.template;

import lombok.Data;

/**
 * 查询合同模板列表请求参数。
 *
 * @author ruoyi
 */
@Data
public class DocTemplatesRequest {

    /**
     * 查询页码，默认1
     */
    private Integer pageNum;

    /**
     * 每页数量，最大20
     */
    private Integer pageSize;
}
