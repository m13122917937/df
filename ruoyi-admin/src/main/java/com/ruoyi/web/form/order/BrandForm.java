package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data

public class BrandForm {


    @NotNull(message = "订单状态不能为空")
    Integer status;


    List<Integer> statusList;


    private Long province;

}
