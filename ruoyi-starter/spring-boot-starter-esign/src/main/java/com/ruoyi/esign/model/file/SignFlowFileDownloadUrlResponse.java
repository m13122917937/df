package com.ruoyi.esign.model.file;

import lombok.Data;

import java.util.List;

/**
 * 下载已签署文件及附属材料响应结果。
 *
 * @author ruoyi
 */
@Data
public class SignFlowFileDownloadUrlResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private List<FileDTO> files;
        private List<FileDTO> attachments;
        private String certificateDownloadUrl;
        private String aesSecret;
    }

    @Data
    public static class FileDTO {
        private String fileId;
        private String fileName;
        private String downloadUrl;
    }
}
