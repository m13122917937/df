package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.List;

@Data

public class PickingOrderForm {


    private Integer quantity;


    private String orderCode;


    private List<String> snList;


    private String remark;


    private String warehouseCode;
}
