package com.ruoyi.esign.model.template;

import lombok.Data;

import java.util.List;

/**
 * 查询合同模板中控件详情响应结果。
 *
 * @author ruoyi
 */
@Data
public class DocTemplateInfoResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private String docTemplateId;
        private String docTemplateName;
        private Long createTime;
        private Long updateTime;
        private String fileDownloadUrl;
        private List<ComponentDTO> components;
    }

    @Data
    public static class ComponentDTO {
        private String componentId;
        private String componentKey;
        private String componentName;
        private Boolean required;
        private Integer componentType;
        private ComponentPositionDTO componentPosition;
        private ComponentSizeDTO componentSize;
        private ComponentSpecialAttributeDTO componentSpecialAttribute;
        private NormalSignFieldDTO normalSignField;
        private RemarkSignFieldDTO remarkSignField;
        private ComponentTextFormatDTO componentTextFormat;
    }

    @Data
    public static class ComponentPositionDTO {
        private Float componentPositionX;
        private Float componentPositionY;
        private Integer componentPageNum;
    }

    @Data
    public static class ComponentSizeDTO {
        private Float componentWidth;
        private Float componentHeight;
    }

    @Data
    public static class ComponentSpecialAttributeDTO {
        private String dateFormat;
        private String imageType;
        private List<OptionDTO> options;
        private List<Object> tableContent;
        private String numberFormat;
        private String componentMaxLength;
        private String componentMaxRows;
        private String signerRole;
        private String tickComponentType;
        private List<String> componentAssociatedId;
    }

    @Data
    public static class OptionDTO {
        private String optionContent;
        private Integer optionOrder;
        private Boolean selected;
    }

    @Data
    public static class NormalSignFieldDTO {
        private Integer showSignDate;
        private String dateFormat;
        private Integer signFieldStyle;
        private Integer sealSpecs;
    }

    @Data
    public static class RemarkSignFieldDTO {
        private Integer inputType;
        private Integer aiCheck;
        private String remarkContent;
        private String remarkFontSize;
    }

    @Data
    public static class ComponentTextFormatDTO {
        private Integer font;
        private Float fontSize;
        private String textColor;
        private Boolean bold;
        private Boolean italic;
        private String horizontalAlignment;
        private String verticalAlignment;
        private Float textLineSpacing;
    }
}
