package com.ruoyi.web.form.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ConfirmSplitForm {

    @ApiModelProperty("账单集合")
    List<SplitForm> splitForms;
}
