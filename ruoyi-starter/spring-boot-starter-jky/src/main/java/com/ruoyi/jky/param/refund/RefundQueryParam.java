package com.ruoyi.jky.param.refund;

import lombok.Data;

import java.util.List;

@Data
public class RefundQueryParam {

    private PageInfo pageInfo;

    private String memberName;

    private Boolean isQueryCount;

    private Integer hasQueryHistory;

    private List<Integer> hasGoodsReturn;

    private Integer refundType;

    private List<String> refundNo;

    private List<String> platOrderNo;

    private List<String> shopId;

    private String gmtModifiedBegin;

    private String gmtModifiedEnd;

    private String gmtCreateBegin;

    private String gmtCreateEnd;

    private String createTimeBegin;

    private String createTimeEnd;

    private String modifiedTimeBegin;

    private String modifiedTimeEnd;

    @Data
    public static class PageInfo {

        private Integer pageIndex;

        private Integer pageSize;

    }

}
