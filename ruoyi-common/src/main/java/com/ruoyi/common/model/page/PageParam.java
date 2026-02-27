package com.ruoyi.common.model.page;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PageParam {


    private Integer pageNum;

    private Integer pageSize;
}
