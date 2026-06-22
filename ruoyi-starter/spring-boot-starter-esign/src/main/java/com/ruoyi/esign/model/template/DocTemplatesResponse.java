package com.ruoyi.esign.model.template;

import lombok.Data;

import java.util.List;

/**
 * 查询合同模板列表响应结果。
 *
 * @author ruoyi
 */
@Data
public class DocTemplatesResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private Integer total;
        private List<DocTemplateDTO> docTemplates;
    }

    @Data
    public static class DocTemplateDTO {
        private String docTemplateId;
        private String docTemplateName;
        private Long createTime;
        private Long updateTime;
    }
}
