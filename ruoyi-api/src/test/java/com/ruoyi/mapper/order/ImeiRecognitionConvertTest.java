package com.ruoyi.mapper.order;

import com.ruoyi.order.convert.ImeiCov;
import com.ruoyi.order.domain.Imei;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.web.vo.order.ImeiVO;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImeiRecognitionConvertTest {

    @Test
    void shouldKeepRecognitionSnapshotAcrossConversion() {
        ImeiParam param = new ImeiParam()
                .setRecognizedProductName("识别商品")
                .setRecognizedSkuName("识别规格");

        Imei domain = ImeiCov.INSTANCE.paramToDomain(param);
        ImeiBO businessObject = ImeiCov.INSTANCE.toBO(domain);
        ImeiVO viewObject = ImeiConvert.INSTANCE.listvo(Collections.singletonList(businessObject)).get(0);

        assertEquals("识别商品", viewObject.getRecognizedProductName());
        assertEquals("识别规格", viewObject.getRecognizedSkuName());
    }
}
