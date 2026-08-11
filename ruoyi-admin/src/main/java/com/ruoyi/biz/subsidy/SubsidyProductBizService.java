package com.ruoyi.biz.subsidy;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.subsidy.facade.IGbProductFacade;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.query.GbProductQuery;
import com.ruoyi.web.form.subsidy.SubsidyProductForm;
import com.ruoyi.web.mapper.subsidy.SubsidyProductWebConvert;
import com.ruoyi.web.vo.subsidy.SubsidyProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 国补后台商品应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyProductBizService {
    private final IGbProductFacade productFacade;

    /** 分页查询商品。 */
    public PageBO<SubsidyProductVO> page(final String productName, final Integer status, final PageParamV2 pageParam) {
        PageBO<GbProductBO> page = productFacade.page(new GbProductQuery().setProductNameLike(productName).setStatus(status), pageParam);
        return new PageBO<>(SubsidyProductWebConvert.INSTANCE.toVOList(page.getData()), page.getTotal());
    }

    /** 新增商品。 */
    public SubsidyProductVO save(final SubsidyProductForm form) {
        return SubsidyProductWebConvert.INSTANCE.toVO(productFacade.save(SubsidyProductWebConvert.INSTANCE.toParam(form)));
    }

    /** 更新商品。 */
    public void update(final Long productId, final SubsidyProductForm form) {
        productFacade.update(productId, SubsidyProductWebConvert.INSTANCE.toParam(form));
    }
}
