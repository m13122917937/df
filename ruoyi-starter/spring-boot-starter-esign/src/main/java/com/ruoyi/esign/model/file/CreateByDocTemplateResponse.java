package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 填写模板生成文件响应结果。
 *
 * @author ruoyi
 */
@Data
public class CreateByDocTemplateResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private String fileId;
        private String fileDownloadUrl;
    }
}
