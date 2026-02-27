package com.ruoyi.web.form.rule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("规则")
public class RuleListForm {

    @ApiModelProperty("规则ID列表")
    private List<Long> ruleIdList;
}
