package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("订单状态筛选")
public class OrderFilter {


    private List<Long> statusList;

}
