package com.ruoyi.wangdian.rep.purchase.in;

import com.ruoyi.wangdian.rep.WDRep;
import lombok.Data;

@Data
public class PurchaseInRep extends WDRep {

    private InRep data;

    public static class InRep {
        private Integer total_count;
    }






}
