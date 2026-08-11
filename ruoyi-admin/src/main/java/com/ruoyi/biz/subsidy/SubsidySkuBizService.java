package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.facade.IGbProductSkuFacade;
import com.ruoyi.subsidy.model.query.GbProductSkuQuery;
import com.ruoyi.web.form.subsidy.SubsidySkuForm;
import com.ruoyi.web.mapper.subsidy.SubsidySkuWebConvert;
import com.ruoyi.web.vo.subsidy.SubsidySkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补后台 SKU 应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidySkuBizService {
    private final IGbProductSkuFacade skuFacade;
    /** 查询指定商品 SKU。 */
    public List<SubsidySkuVO> list(final Long productId) { return SubsidySkuWebConvert.INSTANCE.toVOList(skuFacade.list(new GbProductSkuQuery().setProductId(productId))); }
    /** 新增 SKU。 */
    public void save(final SubsidySkuForm form) { skuFacade.save(SubsidySkuWebConvert.INSTANCE.toParam(form)); }
    /** 更新 SKU。 */
    public void update(final Long id, final SubsidySkuForm form) { skuFacade.update(id, SubsidySkuWebConvert.INSTANCE.toParam(form)); }
}
