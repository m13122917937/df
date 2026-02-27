package com.ruoyi.express.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.utils.JacksonUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 快递信息订阅对象 e_route_subscribe
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Data
@Accessors(chain = true)
@TableName("e_route_subscribe")
public class RouteSubscribe {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId
    private Long id;
    /**
     * 订单号id.
     */
    private String orderCode;

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
     * 物流明细数据
     */
    private String detailInfo;
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
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    @TableLogic
    private Integer deleted;

}
