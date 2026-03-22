package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AftersalesForm {


    @NotEmpty(message = "订单号不能为空")
    private List<String> orderCodeSet;


}
