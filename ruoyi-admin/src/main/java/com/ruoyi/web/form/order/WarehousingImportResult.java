package com.ruoyi.web.form.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class WarehousingImportResult {

    private Integer totalCount;

    private Integer successCount;

    private Integer errorCount;

    private List<WarehousingImportRowResult> rows;
}
