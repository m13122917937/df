package com.ruoyi.jky.param.logistics;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsUpdateParam {

    private List<LogisticsUpdateItem> bizdata;

    @Data
    public static class LogisticsUpdateItem {

        private String orderNo;

        private String logisticNo;

        private String logisticName;

        private String logisticCode;

    }

}
