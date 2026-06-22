package com.ruoyi.esign.api;

import com.ruoyi.esign.model.template.DocTemplateInfoRequest;
import com.ruoyi.esign.model.template.DocTemplateInfoResponse;
import com.ruoyi.esign.model.template.DocTemplatesRequest;
import com.ruoyi.esign.model.template.DocTemplatesResponse;

/**
 * e签宝合同模板API。
 *
 * @author ruoyi
 */
public interface EsignTemplateApi {

    /**
     * 查询合同模板列表。
     *
     * @param request 请求参数
     * @return 模板列表
     */
    DocTemplatesResponse docTemplates(DocTemplatesRequest request);

    /**
     * 查询合同模板中控件详情。
     *
     * @param request 请求参数
     * @return 模板控件详情
     */
    DocTemplateInfoResponse docTemplateInfo(DocTemplateInfoRequest request);
}
