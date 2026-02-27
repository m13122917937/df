package com.ruoyi.wangdian.param.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 业务查询参数 (params 对象)
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TradeQueryParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * [必填] 修改起始时间 (格式: YYYY-MM-DD HH:mm:ss)
     * 若无订单编号/原始单号/物流单号，则为必填。最大跨度60分钟。
     */
    @JsonProperty("start_time")
    private String startTime;

    /**
     * [必填] 修改结束时间 (格式: YYYY-MM-DD HH:mm:ss)
     */
    @JsonProperty("end_time")
    private String endTime;

    /**
     * [选填] 仓库编号
     */
    @JsonProperty("warehouse_no")
    private String warehouseNo;

    /**
     * [选填] 订单状态 (多个用逗号分隔)
     * 例如: 55,95,110 (55:已审核, 95:已发货, 110:已完成)
     */
    @JsonProperty("status")
    private String status;

    /**
     * [选填] 订单编号 (旺店通系统订单号)
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    /**
     * [选填] 店铺编号 (暂不支持批量)
     */
    @JsonProperty("shop_no")
    private String shopNo;

    /**
     * [选填] 物流单号 (V1.4.9.9+ 版本支持不传时间范围查询)
     */
    @JsonProperty("logistics_no")
    private String logisticsNo;

    /**
     * [选填] 原始单号 (平台订单号，多个用英文逗号分隔)
     */
    @JsonProperty("src_tid")
    private String srcTid;

    /**
     * [选填] 是否使用从库查询 (true/false)，默认 false
     */
    @JsonProperty("is_slave")
    private Boolean isSlave;

    /**
     * [选填] 是否计算分摊邮费 (true/false)，默认 false
     */
    @JsonProperty("cal_share_post_amount")
    private Boolean calSharePostAmount;

    /**
     * [选填] 订单来源 (1:API抓单, 2:手工建单, 3:导入, 4:复制订单, 5:接口推送, 6:补发订单, 7:PDA选货开单, 8:分销补发订单)
     */
    @JsonProperty("trade_from")
    private String tradeFrom;

    /**
     * [选填] 排序类型 (0:默认, 1:修改时间降序)
     */
    @JsonProperty("order_type")
    private Integer orderType;

    /**
     * [选填] 时间类型 (1:修改时间, 2:付款时间, 3:下单时间)，默认 1
     */
    @JsonProperty("time_type")
    private Integer timeType;

    /**
     * [选填] 是否返回赠品关联关系 (true:返回, false:不返回)，默认 true
     */
    @JsonProperty("need_gift_relation")
    private Boolean needGiftRelation;

    /**
     * [选填] 是否强制指定 src_tid 精准查询 (true:是, false:否)，默认 false
     */
    @JsonProperty("accurate_query")
    private Boolean accurateQuery;

    /**
     * [选填] 是否截取物流单号 (true:截取, false:不截取)，默认 false
     */
    @JsonProperty("cut_logistics_no")
    private Boolean cutLogisticsNo;
}