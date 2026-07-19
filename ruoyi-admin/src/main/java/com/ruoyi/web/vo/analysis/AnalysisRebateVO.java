package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 返利价保活动响应对象。
 */
@Data
public class AnalysisRebateVO {
    private Long id;
    private String activityCode;
    private String activityName;
    private String brand;
    private String supplierName;
    private String scene;
    private String granularity;
    private String calculationMethod;
    private String calculationTime;
    private BigDecimal totalAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String attachmentData;
    private LocalDateTime createdTime;
    private List<DetailVO> details = new ArrayList<>();

    /**
     * 返利价保明细响应对象。
     */
    @Data
    public static class DetailVO {
        private Long id;
        private Long activityId;
        private String goodsNo;
        private String goodsName;
        private String snCode;
        private BigDecimal amount;
        private BigDecimal pointRate;
    }
}
