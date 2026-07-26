package com.ruoyi.order.convert;

import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.model.param.SupplierPushParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 定向推送供应商领域对象转换器。
 */
@Mapper
public interface SupplierPushCov {

    SupplierPushCov INSTANCE = Mappers.getMapper(SupplierPushCov.class);

    /**
     * 转换挂单实体。
     *
     * @param param 推送参数
     * @param order 订单实体
     * @return 挂单实体
     */
    @Mapping(source = "order.orderCode", target = "orderId")
    @Mapping(source = "param.price", target = "priceHighest")
    @Mapping(source = "param.accountingPeriod", target = "accountingPeriod")
    @Mapping(source = "param.userId", target = "lastCompeteUser")
    @Mapping(source = "param.companyId", target = "lastCompeteCompany")
    @Mapping(source = "param.companyId", target = "merchantCompanyId")
    @Mapping(source = "param.deliveryTime", target = "deliveryTime")
    HangingOrder toHangingOrder(SupplierPushParam param, Order order);

    /**
     * 转换成交实体。
     *
     * @param param 推送参数
     * @param order 订单实体
     * @return 成交实体
     */
    @Mapping(source = "order.orderCode", target = "orderId")
    @Mapping(source = "param.price", target = "tradePrice")
    @Mapping(source = "param.userId", target = "tradeUserId")
    @Mapping(source = "param.userName", target = "tradeUserName")
    @Mapping(source = "param.userPhone", target = "tradeUserPhone")
    @Mapping(source = "param.companyId", target = "tradeCompanyId")
    @Mapping(source = "param.companyName", target = "tradeCompanyName")
    @Mapping(source = "param.companyNickName", target = "tradeNickName")
    @Mapping(source = "param.accountingPeriod", target = "accountingPeriod")
    @Mapping(source = "order.productName", target = "productName")
    @Mapping(source = "order.brand", target = "brand")
    @Mapping(source = "order.skuName", target = "skuName")
    @Mapping(source = "order.skuCode", target = "skuCode")
    @Mapping(source = "order.province", target = "province")
    @Mapping(source = "order.quantity", target = "quantity")
    @Mapping(source = "order.orderType", target = "orderType")
    TradeOrder toTradeOrder(SupplierPushParam param, Order order);
}
