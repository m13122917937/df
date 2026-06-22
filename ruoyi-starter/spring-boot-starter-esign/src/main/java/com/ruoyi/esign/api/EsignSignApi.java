package com.ruoyi.esign.api;

import com.ruoyi.esign.model.sign.CreateByFileRequest;
import com.ruoyi.esign.model.sign.CreateByFileResponse;

/**
 * e签宝合同签署API接口
 *
 * @author ruoyi
 */
public interface EsignSignApi {

    /**
     * 基于文件发起签署。
     *
     * @param request 请求参数
     * @return 签署流程响应结果
     */
    CreateByFileResponse createByFile(CreateByFileRequest request);
}
