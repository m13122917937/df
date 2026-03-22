package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.List;

@Data

public class WarehousingOrderParam {


    private String brand;


    private String productName;


    private Long companyId;


    private List<Integer> statusList;



    private Long deliveryCode;

}
