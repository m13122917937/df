package com.ruoyi.esign.api;

import com.ruoyi.esign.model.file.CreateByDocTemplateRequest;
import com.ruoyi.esign.model.file.CreateByDocTemplateResponse;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlResponse;
import com.ruoyi.esign.model.file.SignFlowFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.SignFlowFileDownloadUrlResponse;

/**
 * e签宝文件服务API接口
 *
 * @author ruoyi
 */
public interface EsignFileApi {

    /**
     * 下载已签署文件及附属材料。
     *
     * @param request 请求参数
     * @return 下载链接响应结果
     */
    SignFlowFileDownloadUrlResponse fileDownloadUrl(SignFlowFileDownloadUrlRequest request);

    /**
     * 下载签署中文件。
     *
     * @param request 请求参数
     * @return 预览下载链接响应结果
     */
    PreviewFileDownloadUrlResponse previewFileDownloadUrl(PreviewFileDownloadUrlRequest request);

    /**
     * 填写模板生成文件。
     *
     * @param request 请求参数
     * @return 生成文件响应结果
     */
    CreateByDocTemplateResponse createByDocTemplate(CreateByDocTemplateRequest request);
}
