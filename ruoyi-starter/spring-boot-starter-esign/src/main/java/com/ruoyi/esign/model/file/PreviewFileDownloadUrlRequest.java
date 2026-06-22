package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 下载签署中文件请求参数。
 *
 * @author ruoyi
 */
@Data
public class PreviewFileDownloadUrlRequest {

    /**
     * 签署流程ID
     */
    private String signFlowId;

    /**
     * 签署流程中的文件ID
     */
    private String docFileId;

    /**
     * 下载链接有效期，单位秒
     */
    private Integer urlAvailableDate;
}
