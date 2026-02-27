package com.ruoyi.order.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderTabCountQuery {

    private List<Integer> statusList;

    private String brand;

    private List<String> categoryList;

    private Integer addressStatus;

    private Long province;

    private Long city;

    private String productName;

    private String skuName;

    private Date gtCreateDateTime;

}
