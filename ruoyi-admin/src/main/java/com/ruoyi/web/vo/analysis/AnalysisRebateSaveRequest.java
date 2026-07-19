package com.ruoyi.web.vo.analysis;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 返利价保活动保存请求。
 */
@Data
public class AnalysisRebateSaveRequest {
    private Long id;
    @NotBlank(message = "活动编码不能为空")
    private String activityCode;
    @NotBlank(message = "活动名称不能为空")
    private String activityName;
    private String brand;
    private String supplierName;
    @NotBlank(message = "业务场景不能为空")
    private String scene;
    @NotBlank(message = "核算颗粒度不能为空")
    private String granularity;
    @NotBlank(message = "计算方式不能为空")
    private String calculationMethod;
    private String calculationTime;
    private BigDecimal totalAmount;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    private String status;
    private String attachmentData;
    @Valid
    private List<DetailRequest> details = new ArrayList<>();

    /**
     * 返利价保商品或串码明细请求。
     */
    @Data
    public static class DetailRequest {
        private Long id;
        private Long activityId;
        private String goodsNo;
        private String goodsName;
        private String snCode;
        private BigDecimal amount;
        private BigDecimal pointRate;
    }
}
