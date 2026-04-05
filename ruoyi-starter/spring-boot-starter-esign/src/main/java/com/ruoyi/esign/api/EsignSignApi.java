package com.ruoyi.esign.api;

import com.ruoyi.esign.model.*;

/**
 * e签宝合同签署API接口
 *
 * @author ruoyi
 */
public interface EsignSignApi {

    /**
     * 创建签署流程
     *
     * @param request 创建签署流程请求参数
     * @return 创建签署流程响应结果
     */
    CreateFlowResponse createFlow(CreateFlowRequest request);

    /**
     * 添加签署文件
     *
     * @param request 添加签署文件请求参数
     * @return 添加签署文件响应结果
     */
    AddFileResponse addFile(AddFileRequest request);

    /**
     * 添加签署人
     *
     * @param request 添加签署人请求参数
     * @return 添加签署人响应结果
     */
    AddSignerResponse addSigner(AddSignerRequest request);

    /**
     * 发起签署流程
     *
     * @param flowId 签署流程ID
     * @return 发起签署流程响应结果
     */
    StartFlowResponse startFlow(String flowId);

    /**
     * 查询签署流程详情
     *
     * @param flowId 签署流程ID
     * @return 签署流程详情响应结果
     */
    QueryFlowResponse getFlowDetail(String flowId);

    /**
     * 获取签署页面URL
     *
     * @param request 获取签署页面URL请求参数
     * @return 获取签署页面URL响应结果
     */
    GetSignUrlResponse getSignUrl(GetSignUrlRequest request);
}
