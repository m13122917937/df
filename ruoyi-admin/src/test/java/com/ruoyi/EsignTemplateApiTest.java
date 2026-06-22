package com.ruoyi;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignTemplateApi;
import com.ruoyi.esign.model.template.DocTemplateInfoRequest;
import com.ruoyi.esign.model.template.DocTemplateInfoResponse;
import com.ruoyi.esign.model.template.DocTemplatesRequest;
import com.ruoyi.esign.model.template.DocTemplatesResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * e签宝合同模板接口测试。
 *
 * @author ruoyi
 */
@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class EsignTemplateApiTest {

    @Autowired
    private EsignTemplateApi esignTemplateApi;

    /**
     * 查询合同模板列表。
     */
    @Test
    public void docTemplates() {
        DocTemplatesRequest request = new DocTemplatesRequest();
        request.setPageNum(1);
        request.setPageSize(20);

        DocTemplatesResponse response = esignTemplateApi.docTemplates(request);
        log.info("e签宝合同模板列表响应：{}", response);

        Assertions.assertNotNull(response, "e签宝合同模板列表响应为空");
        Assertions.assertEquals(0, response.getCode(), response.getMessage());
        Assertions.assertNotNull(response.getData(), "e签宝合同模板列表 data 为空");
        Assertions.assertNotNull(response.getData().getTotal(), "e签宝合同模板总数为空");
    }

    /**
     * 查询合同模板中控件详情。
     */
    @Test
    public void docTemplateInfo() {
        DocTemplatesRequest listRequest = new DocTemplatesRequest();
        listRequest.setPageNum(1);
        listRequest.setPageSize(20);
        DocTemplatesResponse listResponse = esignTemplateApi.docTemplates(listRequest);

        Assertions.assertNotNull(listResponse, "e签宝合同模板列表响应为空");
        Assertions.assertEquals(0, listResponse.getCode(), listResponse.getMessage());
        Assertions.assertNotNull(listResponse.getData(), "e签宝合同模板列表 data 为空");
        Assumptions.assumeTrue(CollectionUtil.isNotEmpty(listResponse.getData().getDocTemplates()), "未查询到合同模板，跳过详情测试");

        String docTemplateId = listResponse.getData().getDocTemplates().get(0).getDocTemplateId();
        DocTemplateInfoRequest request = new DocTemplateInfoRequest();
        request.setDocTemplateId(docTemplateId);

        DocTemplateInfoResponse response = esignTemplateApi.docTemplateInfo(request);
        log.info("e签宝合同模板控件详情响应：{}", JacksonUtil.toJson(response));

        Assertions.assertNotNull(response, "e签宝合同模板控件详情响应为空");
        Assertions.assertEquals(0, response.getCode(), response.getMessage());
        Assertions.assertNotNull(response.getData(), "e签宝合同模板控件详情 data 为空");
        Assertions.assertEquals(docTemplateId, response.getData().getDocTemplateId(), "返回模板ID与请求不一致");
    }
}
