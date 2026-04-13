package com.ruoyi.express.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 快递信息订阅对象 e_route_subscribe
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Data
@Accessors(chain = true)
public class RouteSubscribeQuery {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 快递公司编码
     */
    private String logisticsCode;
    /**
     * 快递单号
     */
    private String logisticsNo;
    /**
     * 快递状态
     */
    private String logisticsStatus;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 快递订阅状态 1已订阅  0未订阅
     */
    private Integer status;
    /**
     * 监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送
     */
    private String traceStatus;

    /**
     * 最新的物流轨迹时间;
     */
    private Date newRouteDate;
    /**
     * 出发地编码
     */
    private String routeFrom;
    /**
     * 目的地编码
     */
    private String routeTo;
    /**
     * 行政区域解析
     */
    private String routeInfo;
    /**
     * 运单号重试次数
     */
    private Long retryNumber;
    /**
     * 是否锁定，延时订阅过程中锁定
     */
    private Integer locked;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    @QueryField( operator = DynamicCondition.Operator.GTE, field = "create_time")
    private Date gtCreateTime;
    /**
     * 修改时间
     */
    private Date updateTime;


    private String orderCode;



    @QueryField( operator = DynamicCondition.Operator.NE, field = "order_code")
    private String notEqOrderCode;

}
