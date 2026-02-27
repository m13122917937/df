package com.ruoyi.biz.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.web.form.order.OrderAddForm;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class ExcelOrderReadListener implements ReadListener<OrderAddForm> {

    private OrderBizService orderBizService;


    public ExcelOrderReadListener(OrderBizService orderBizService) {
        this.orderBizService = orderBizService;
    }

    @Override
    public void invoke(OrderAddForm orderAddForm, AnalysisContext analysisContext) {
        log.info("导入excel数据:{}", JacksonUtil.toJson(orderAddForm));
        List<String> lines = StrUtil.splitTrim(orderAddForm.getAddress(), '\n');
        if(lines.size() >= 3){
            orderAddForm.setAddressee(lines.get(0));
            if(Objects.equals(lines.get(1), "收起")){
                orderAddForm.setPhone(lines.get(3));
                orderAddForm.setAddress(lines.get(4));
            }else{
                orderAddForm.setPhone(lines.get(1));
                orderAddForm.setAddress(lines.get(2));
            }
        }
        try {
            orderBizService.add(orderAddForm);
        }catch (Exception e){
            log.error("导入异常:{}", JacksonUtil.toJson(orderAddForm), e);
        }finally {

        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
