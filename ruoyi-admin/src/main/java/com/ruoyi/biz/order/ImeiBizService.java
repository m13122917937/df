package com.ruoyi.biz.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.order.ImeiConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IImeiSkuRelFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.vo.order.ImeiRelVO;
import com.ruoyi.web.vo.order.ImeiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImeiBizService {

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IImeiSkuRelFacade imeiSkuRelFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;


    public List<ImeiVO> list(final String orderCode) {

        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderCode).setHangingOrderId(hangingOrderBO.getId()));
        return ImeiConvert.INSTANCE.toVo(list);


    }

    public PageBO<ImeiRelVO> listRel(ImeiForm imeiForm, PageParamV2 pageParamV2) {
        ImeiSkuRelQuery query = ImeiConvert.INSTANCE.toQuery(imeiForm);
        PageBO<ImeiSkuRelBO> imeiSkuRelBOPageBO = imeiSkuRelFacade.listPage(query, pageParamV2);
        return new PageBO<ImeiRelVO>().setData(ImeiConvert.INSTANCE.toRelVo(imeiSkuRelBOPageBO.getData())).setTotal(imeiSkuRelBOPageBO.getTotal());

    }


    public void refuse(Long id) {
        ImeiSkuRelBO imeiSkuRelBO = imeiSkuRelFacade.getOne(new ImeiSkuRelQuery().setId(id));
        Assert.notNull(imeiSkuRelBO, "订单不存在");
        imeiSkuRelFacade.update(new ImeiSkuRelParam().setStatus(ImeiConsts.ImeiRel.CANCEL.getCode()), new ImeiSkuRelQuery().setId(id));
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setActivated(ImeiConsts.Activated.NOT_ACTIVATED.getCode()).setProductName(imeiSkuRelBO.getProductName()).setSkuName(imeiSkuRelBO.getSkuName()));
        for (ImeiBO imeiBO : imeiBOS) {
            imeiFacade.update(new ImeiParam().setActivated(ImeiConsts.Activated.MODEL_NOT_CONSISTENT.getCode()), new ImeiQuery().setId(imeiBO.getId()));
        }
    }

    @Transactional
    public void agree(Long id) {
        ImeiSkuRelBO imeiSkuRelBO = imeiSkuRelFacade.getOne(new ImeiSkuRelQuery().setId(id));
        Assert.notNull(imeiSkuRelBO, "订单不存在");
        imeiSkuRelFacade.update(new ImeiSkuRelParam().setStatus(ImeiConsts.ImeiRel.OK.getCode()), new ImeiSkuRelQuery().setId(id));
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setActivated(ImeiConsts.Activated.NOT_ACTIVATED.getCode()).setProductName(imeiSkuRelBO.getProductName()).setSkuName(imeiSkuRelBO.getSkuName()));
        for (ImeiBO imeiBO : imeiBOS) {
            imeiFacade.update(new ImeiParam().setActivated(ImeiConsts.Activated.SUCCESS.getCode()), new ImeiQuery().setId(imeiBO.getId()));
        }
    }

    public void del(Long id) {

        imeiSkuRelFacade.del(new ImeiSkuRelQuery().setId(id));

    }

    public Long count(Integer status) {

        Long count = imeiSkuRelFacade.count(new ImeiSkuRelQuery().setStatus(status));
        return count;
    }
}
