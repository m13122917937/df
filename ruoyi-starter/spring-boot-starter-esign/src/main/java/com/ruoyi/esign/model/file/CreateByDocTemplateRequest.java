package com.ruoyi.esign.model.file;

import lombok.Data;

import java.util.List;

/**
 * 填写模板生成文件请求参数。
 *
 * @author ruoyi
 */
@Data
public class CreateByDocTemplateRequest {

    /**
     * 待填充的模板ID
     */
    private String docTemplateId;

    /**
     * 填充后生成的文件名称
     */
    private String fileName;

    /**
     * 控件列表
     */
    private List<Component> components;

    /**
     * 是否校验PDF模板中必填控件
     */
    private Boolean requiredCheck;

    @Data
    public static class Component {
        private String componentId;
        private String componentKey;
        private String componentValue;
    }
}
