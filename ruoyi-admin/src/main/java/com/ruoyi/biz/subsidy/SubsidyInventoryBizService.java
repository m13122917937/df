package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.facade.IGbProductSkuFacade;
import com.ruoyi.web.form.subsidy.SubsidyInventoryAdjustForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 国补后台库存应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyInventoryBizService {
    private final IGbProductSkuFacade skuFacade;

    /** 调整 SKU 库存。 */
    public void adjust(final Long skuId, final SubsidyInventoryAdjustForm form) {
        skuFacade.adjustInventory(skuId, form.getDelta(), form.getRemark());
    }
}
