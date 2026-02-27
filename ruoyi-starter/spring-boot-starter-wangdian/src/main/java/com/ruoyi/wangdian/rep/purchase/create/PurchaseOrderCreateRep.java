package com.ruoyi.wangdian.rep.purchase.create;

import com.ruoyi.wangdian.rep.WDRep;
import lombok.Data;

@Data
public class PurchaseOrderCreateRep extends WDRep {

    private CreateRep data;

    public static class CreateRep {
        private Integer status;
        private String message;
    }
}
