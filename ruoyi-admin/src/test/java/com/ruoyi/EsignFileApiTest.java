package com.ruoyi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignFileApi;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.api.EsignTemplateApi;
import com.ruoyi.esign.model.file.CreateByDocTemplateRequest;
import com.ruoyi.esign.model.file.CreateByDocTemplateResponse;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlResponse;
import com.ruoyi.esign.model.sign.CreateByFileRequest;
import com.ruoyi.esign.model.sign.CreateByFileResponse;
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

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * e签宝文件接口测试。
 *
 * @author ruoyi
 */
@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class EsignFileApiTest {


    private static final String PREVIEW_SIGN_FLOW_ID = "";

    @Autowired
    private EsignFileApi esignFileApi;

    @Autowired
    private EsignSignApi esignSignApi;

    @Autowired
    private EsignTemplateApi esignTemplateApi;

    /**
     * 填写模板生成文件。
     */
    @Test
    public void createByDocTemplate() {
        DocTemplateInfoResponse.DataDTO template = queryFirstTemplateInfo();
        Assumptions.assumeTrue(template != null, "未查询到合同模板，跳过生成文件测试");

        CreateByDocTemplateRequest request = new CreateByDocTemplateRequest();
        request.setDocTemplateId(template.getDocTemplateId());
        request.setFileName("模板生成文件测试-" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss"));
        request.setRequiredCheck(true);
        request.setComponents(buildComponents(template.getComponents()));

        CreateByDocTemplateResponse response = esignFileApi.createByDocTemplate(request);
        log.info("e签宝填写模板生成文件响应：{}", JacksonUtil.toJson(response));

        Assertions.assertNotNull(response, "e签宝填写模板生成文件响应为空");
        Assertions.assertEquals(0, response.getCode(), response.getMessage());
        Assertions.assertNotNull(response.getData(), "e签宝填写模板生成文件 data 为空");
        Assertions.assertTrue(StrUtil.isNotBlank(response.getData().getFileId()), "生成文件ID为空");
    }

    /**
     * 基于文件发起签署。
     */
    @Test
    public void createByFile() {
        CreateByFileRequest request = new CreateByFileRequest();
        CreateByFileRequest.Doc doc = new CreateByFileRequest.Doc();
        doc.setFileId("fef4a5bed31943c083b4e4d3f580da3c");
        doc.setFileName("测试签署文件.pdf");
        request.setDocs(Collections.singletonList(doc));

        CreateByFileRequest.SignFlowConfig signFlowConfig = new CreateByFileRequest.SignFlowConfig();
        signFlowConfig.setSignFlowTitle("基于文件发起签署测试-" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss"));
        signFlowConfig.setAutoFinish(true);
        request.setSignFlowConfig(signFlowConfig);

        CreateByFileRequest.Signer signer = new CreateByFileRequest.Signer();
        signer.setSignerType(0);
        signer.setSignConfig(buildSignConfig(1));
        CreateByFileRequest.PsnSignerInfo psnSignerInfo = new CreateByFileRequest.PsnSignerInfo();
        psnSignerInfo.setPsnAccount("16730277811");
        CreateByFileRequest.PsnInfo psnInfo = new CreateByFileRequest.PsnInfo();
        psnInfo.setPsnName("测试签署人");
        psnSignerInfo.setPsnInfo(psnInfo);
        signer.setPsnSignerInfo(psnSignerInfo);
        signer.setSignFields(Collections.singletonList(buildSignField("fef4a5bed31943c083b4e4d3f580da3c")));
        request.setSigners(Collections.singletonList(signer));

        CreateByFileResponse response = esignSignApi.createByFile(request);
        log.info("e签宝基于文件发起签署响应：{}", JacksonUtil.toJson(response));

        Assertions.assertNotNull(response, "e签宝基于文件发起签署响应为空");
        Assertions.assertEquals(0, response.getCode(), response.getMessage());
        Assertions.assertNotNull(response.getData(), "e签宝基于文件发起签署 data 为空");
        Assertions.assertTrue(StrUtil.isNotBlank(response.getData().getSignFlowId()), "签署流程ID为空");
    }

//    /**
//     * 获取签署中文件预览下载链接并下载文件。
//     */
//    @Test
//    public void previewFileDownloadUrl() {
//        Assumptions.assumeTrue(StrUtil.isNotBlank(PREVIEW_SIGN_FLOW_ID), "请填写签署流程ID PREVIEW_SIGN_FLOW_ID");
//
//        PreviewFileDownloadUrlRequest request = new PreviewFileDownloadUrlRequest();
//        request.setSignFlowId(PREVIEW_SIGN_FLOW_ID);
//        request.setDocFileId(PREVIEW_DOC_FILE_ID);
//        request.setUrlAvailableDate(3600);
//
//        PreviewFileDownloadUrlResponse response = esignFileApi.previewFileDownloadUrl(request);
//        log.info("e签宝签署中文件预览下载链接响应：{}", JacksonUtil.toJson(response));
//
//        Assertions.assertNotNull(response, "e签宝签署中文件预览下载链接响应为空");
//        Assertions.assertEquals(0, response.getCode(), response.getMessage());
//        Assertions.assertNotNull(response.getData(), "e签宝签署中文件预览下载链接 data 为空");
//        Assertions.assertTrue(StrUtil.isNotBlank(response.getData().getFileDownloadUrl()), "文件下载链接为空");
//
//        HttpResponse downloadResponse = HttpRequest.get(response.getData().getFileDownloadUrl()).execute();
//        Assertions.assertTrue(downloadResponse.isOk(), "下载文件失败：" + downloadResponse.body());
//        byte[] fileBytes = downloadResponse.bodyBytes();
//        Assertions.assertTrue(fileBytes.length > 0, "下载文件内容为空");
//
//        String fileName = StrUtil.blankToDefault(response.getData().getFileName(), PREVIEW_DOC_FILE_ID + ".pdf");
//        File downloadFile = FileUtil.file(FileUtil.getTmpDir(), fileName);
//        FileUtil.writeBytes(fileBytes, downloadFile);
//        log.info("e签宝签署中文件预览文件已下载：{}", downloadFile.getAbsolutePath());
//    }

    private CreateByFileRequest.SignConfig buildSignConfig(Integer signOrder) {
        CreateByFileRequest.SignConfig signConfig = new CreateByFileRequest.SignConfig();
        signConfig.setSignOrder(signOrder);
        return signConfig;
    }

    private CreateByFileRequest.SignField buildSignField(String fileId) {
        CreateByFileRequest.SignField signField = new CreateByFileRequest.SignField();
        signField.setFileId(fileId);
        CreateByFileRequest.NormalSignFieldConfig config = new CreateByFileRequest.NormalSignFieldConfig();
        config.setAutoSign(false);
        config.setSignFieldStyle(1);
        CreateByFileRequest.SignFieldPosition position = new CreateByFileRequest.SignFieldPosition();
        position.setPositionPage("1");
        position.setPositionX(200F);
        position.setPositionY(200F);
        config.setSignFieldPosition(position);
        signField.setNormalSignFieldConfig(config);
        return signField;
    }

    private DocTemplateInfoResponse.DataDTO queryFirstTemplateInfo() {
        DocTemplatesRequest listRequest = new DocTemplatesRequest();
        listRequest.setPageNum(1);
        listRequest.setPageSize(20);
        DocTemplatesResponse listResponse = esignTemplateApi.docTemplates(listRequest);
        Assertions.assertNotNull(listResponse, "e签宝合同模板列表响应为空");
        Assertions.assertEquals(0, listResponse.getCode(), listResponse.getMessage());
        Assertions.assertNotNull(listResponse.getData(), "e签宝合同模板列表 data 为空");
        if (CollectionUtil.isEmpty(listResponse.getData().getDocTemplates())) {
            return null;
        }

        String docTemplateId = listResponse.getData().getDocTemplates().get(0).getDocTemplateId();
        DocTemplateInfoRequest infoRequest = new DocTemplateInfoRequest();
        infoRequest.setDocTemplateId(docTemplateId);
        DocTemplateInfoResponse infoResponse = esignTemplateApi.docTemplateInfo(infoRequest);
        Assertions.assertNotNull(infoResponse, "e签宝合同模板控件详情响应为空");
        Assertions.assertEquals(0, infoResponse.getCode(), infoResponse.getMessage());
        return infoResponse.getData();
    }

    private List<CreateByDocTemplateRequest.Component> buildComponents(List<DocTemplateInfoResponse.ComponentDTO> components) {
        if (CollectionUtil.isEmpty(components)) {
            return Collections.emptyList();
        }
        List<CreateByDocTemplateRequest.Component> result = new ArrayList<>();
        for (DocTemplateInfoResponse.ComponentDTO component : components) {
            String value = buildComponentValue(component);
            if (StrUtil.isBlank(value)) {
                continue;
            }
            CreateByDocTemplateRequest.Component requestComponent = new CreateByDocTemplateRequest.Component();
            requestComponent.setComponentId(component.getComponentId());
            requestComponent.setComponentValue(value);
            result.add(requestComponent);
        }
        return result;
    }

    private String buildComponentValue(DocTemplateInfoResponse.ComponentDTO component) {
        if (component == null || component.getComponentType() == null) {
            return null;
        }
        switch (component.getComponentName()) {
            case "签订日期":
                return DateUtil.format(DateUtil.date(), DatePattern.CHINESE_DATE_PATTERN);
            case "采购方":
            case "供应商":
            case "采购方负责人手机号":
                return "16730277811";
            case "供应商负责人手机号":
                return "16730277811";
            case "采购方签订日期":
                return DateUtil.format(DateUtil.date(), DatePattern.NORM_DATE_PATTERN);
            case "供应商签订日期":
                return DateUtil.format(DateUtil.date(), DatePattern.NORM_DATE_PATTERN);
            default:
                return null;
        }
    }

    private String firstOption(DocTemplateInfoResponse.ComponentDTO component) {
        DocTemplateInfoResponse.ComponentSpecialAttributeDTO attribute = component.getComponentSpecialAttribute();
        if (attribute == null || CollectionUtil.isEmpty(attribute.getOptions())) {
            return "测试选项";
        }
        return attribute.getOptions().get(0).getOptionContent();
    }

    private String tableContent() {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("column1", "测试内容");
        row.put("insertRow", true);
        return JSONUtil.toJsonStr(Collections.singletonList(row));
    }
}
