package com.ruoyi.mapper.order;

import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.SupplierPushParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.form.order.OrderAddForm;
import com.ruoyi.web.form.order.OrderNewForm;
import com.ruoyi.web.form.order.WaitPushForm;
import com.ruoyi.web.vo.order.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderConvert {

    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    List<BrandCountVO> toBrandCountVO(List<BrandCountBO> brandCountBOS);

    List<OrderAreaCountVO> toOrderAreaCountVO(List<ProvinceCountBO> provinceCountBOS);


    List<OrderListVO> toWaitVOList(List<OrderBO> list);

    OrderParam toParam(OrderAddForm orderAddForm);

    OrderQuery paramToQuery(OrderNewForm orderNewParam);

    List<OrderStatusVO> toOrderStatus(List<OrderStatusDTO> orderStatusDTOS);

    List<OrderDeliveryVO> toOrderDeliveryVO(List<OrderBO> list);


    @Mapping(source = "createStartTime", target = "createTimeStart")
    @Mapping(source = "createEndTime", target = "createTimeEnd")
    OrderQuery allParamToQuery(AllOrderForm allOrderForm);


    List<AllOrderVO> toAllOrderVOList(List<OrderBO> data);


    List<OrderListVO> toOrderVOList(List<SendOrderLisBO> data);

    /**
     * 转换定向推送供应商参数。
     *
     * @param form 页面推送参数
     * @param member 供应商成员
     * @param company 供应商企业
     * @param operatorId 操作人
     * @return 领域推送参数
     */
    @Mapping(source = "form.orderCodeList", target = "orderCodeList")
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.companyName", target = "companyName")
    @Mapping(source = "company.nickName", target = "companyNickName")
    @Mapping(source = "member.userId", target = "userId")
    @Mapping(source = "member.nickName", target = "userName")
    @Mapping(source = "member.phone", target = "userPhone")
    @Mapping(source = "form.price", target = "price")
    @Mapping(source = "form.deliveryTime", target = "deliveryTime")
    @Mapping(source = "form.accountingPeriod", target = "accountingPeriod")
    @Mapping(source = "operatorId", target = "operatorId")
    SupplierPushParam toSupplierPushParam(WaitPushForm form,
                                          MemberBO member,
                                          CompanyBO company,
                                          Long operatorId);

}
