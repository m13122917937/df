package com.ruoyi.web.form.order;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder

public class ImeiOrderParam {


    @NotBlank(message = "订单编码不能为空")
    private String orderCode;


    @NotNull(message = "订单编码不能为空")
    private List<String> imei;



    private String sn;


    private String imeiCode;
}
