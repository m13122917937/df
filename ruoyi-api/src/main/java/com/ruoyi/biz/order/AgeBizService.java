package com.ruoyi.biz.order;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.excel.DeliveryListener;
import com.ruoyi.biz.express.ExpressBizService;
import com.ruoyi.web.form.order.OrderImportVO;
import com.ruoyi.web.vo.order.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class AgeBizService {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    ImeiBizService imeiBizService;

    @Autowired
    ExpressBizService expressBizService;

//    @Async
    public void importDelivery(MultipartFile file) throws IOException {

        DeliveryListener deliveryListener = new DeliveryListener(orderBizService, imeiBizService, expressBizService);
        EasyExcel.read(file.getInputStream(), OrderImportVO.class, deliveryListener).sheet().doRead();

    }
}
