package com.ruoyi.esign.model.sign;

import lombok.Data;

/**
 * 基于文件发起签署响应结果。
 *
 * @author ruoyi
 */
@Data
public class CreateByFileResponse {

    private Integer code;

    private String message;

    private DataDTO data;

    @Data
    public static class DataDTO {
        private String signFlowId;
    }
}
