package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data

public class PickingOrderForm {


    private Integer quantity;


    private String orderCode;


    private List<String> snList;


    private String remark;


    private String warehouseCode;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedTime;


    private Boolean batchInbound;
}
