package com.ruoyi.biz.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.biz.express.ExpressBizService;
import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.web.form.ExpressOrderForm;
import com.ruoyi.web.form.order.ImeiOrderParam;
import com.ruoyi.web.form.order.OrderImportVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class DeliveryListener implements ReadListener<OrderImportVO> {

    private OrderBizService orderBizService;

    private ImeiBizService imeiBizService;

    private ExpressBizService expressBizService;

    Map<String, String> dictCache;

    public DeliveryListener(OrderBizService orderBizService, ImeiBizService imeiBizService, ExpressBizService expressBizService) {
        this.orderBizService = orderBizService;
        this.imeiBizService = imeiBizService;
        this.expressBizService = expressBizService;
        dictCache = DictUtils.getDictCache(DictDataConsts.P_EXPRESS_COMPANY).stream().collect(Collectors.toMap(SysDictData::getDictLabel, SysDictData::getDictValue));
    }


    @Override
    public void invoke(OrderImportVO orderVO, AnalysisContext analysisContext) {
        log.info("导入excel数据:{}", JacksonUtil.toJson(orderVO));

        try {
            // 保存串码
            imeiBizService.verifyImei(ImeiOrderParam.builder().orderCode(orderVO.getOrderCode()).sn(orderVO.getSnList()).imeiCode(orderVO.getImeiList()).build());
            // 保存物流信息
            String expressCode = dictCache.getOrDefault(orderVO.getExpress(), LogisticsCode.SHUNFENG.getCode());
            expressBizService.saveExpress(ExpressOrderForm.builder().cellphone(orderVO.getSendPhone()).trackingNumber(orderVO.getExpressNO())
                    .trackingCompany(orderVO.getExpress()).trackingCompanyCode(expressCode).orderCode(orderVO.getOrderCode()).build());
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("保存物流信息失败:{}", JacksonUtil.toJson(orderVO));
            throw new RuntimeException(e);
        }

    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
