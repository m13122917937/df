package com.ruoyi.web.form.order;

import com.alibaba.excel.EasyExcel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WarehousingImportVOTest {

    @Test
    void shouldReadTPlusAccountingPeriodAsText() throws Exception {
        Path filePath = Files.createTempFile("warehousing-import-", ".xlsx");
        WarehousingImportVO source = new WarehousingImportVO();
        source.setAccountingPeriod("T+3");
        source.setTrackingNumber("SF1234567890");

        try {
            EasyExcel.write(filePath.toFile(), WarehousingImportVO.class)
                    .sheet("模板").doWrite(Collections.singletonList(source));
            List<WarehousingImportVO> rows = EasyExcel.read(filePath.toFile())
                    .head(WarehousingImportVO.class).sheet().doReadSync();

            assertEquals("T+3", rows.get(0).getAccountingPeriod());
            assertEquals("SF1234567890", rows.get(0).getTrackingNumber());
        } finally {
            Files.deleteIfExists(filePath);
        }
    }
}
