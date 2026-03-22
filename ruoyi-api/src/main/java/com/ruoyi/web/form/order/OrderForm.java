package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.List;

@Data

public class OrderForm {


    private Long province;



    private String orderCode;


    private String productName;


    private String brand;



    private String skuName;


    private List<Integer> statusList;


    private Long companyId;
}
