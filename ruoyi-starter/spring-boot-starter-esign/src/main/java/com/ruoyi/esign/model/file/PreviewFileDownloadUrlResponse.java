package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 下载签署中文件响应结果。
 *
 * @author ruoyi
 */
@Data
public class PreviewFileDownloadUrlResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private String fileId;
        private String fileName;
        private String fileDownloadUrl;
    }
}
