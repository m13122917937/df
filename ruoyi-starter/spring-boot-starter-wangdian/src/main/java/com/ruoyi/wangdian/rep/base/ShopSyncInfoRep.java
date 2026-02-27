package com.ruoyi.wangdian.rep.base;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单个店铺信息
 */
@Data
public class ShopSyncInfoRep {

    // 平台ID - 必须
    private Integer platformId;

    // 子平台ID - 必须
    private Integer subPlatformId;

    // 店铺编号 - 必须
    private String shopNo;

    // 店铺名称 - 必须
    private String shopName;

    // 平台店铺账号ID - 必须
    private String accountNick;

    // 省市区编码 - 必须
    private Integer province;
    private Integer city;
    private Integer district;

    // 省市区名称 - 必须
    private String provinceName;
    private String cityName;
    private String districtName;

    // 地址 - 必须
    private String address;

    // 联系人 - 必须
    private String contact;

    // 联系电话 - 必须
    private String mobile;

    // 邮箱 - 必须
    private String email;

    // 备注 - 必须
    private String remark;

    // 是否启用 - 必须，默认1=启用，0=停用
    private Integer isDisabled;

    // 支付方式 - 必须
    private String payAccount;

    // 授权状态 - 必须，0=未授权，1=已授权，2=授权失效，3=授权停用
    private Integer authState;

    // 是否支持换货 - 必须，0=不支持，1=支持
    private Integer supportReturn;

    // 授权开始时间 - 必须
    private LocalDateTime authTime;

    // session过期时间 - 必须
    private LocalDateTime expireTime;

    // refresh_token过期时间 - 必须
    private LocalDateTime reExpireTime;

    // 活动授权码 - 必须
    private String maasAuthCode;

    // 物流公司 - 必须
    private String warehouseId;

    // 使用物流 - 必须
    private String logisticsId;

    // 发票类型 - 必须
    private String country;

    // 地址ID - 必须
    private String addressId;

    // 店铺网址 - 必须
    private String website;

    // 是否支持开票 - 必须
    private Integer isSupportInvoice;

    // 是否支持电子发票 - 必须
    private Integer isSupportEInvoice;

    // 是否支持打印订单 - 必须
    private Integer isSupportPrintOrder;

    // 服务端口 - 必须
    private Integer invoiceProviDerId;

    // 分组ID - 必须
    private Integer groupId;

    // 是否开启合并 - 必须
    private Integer isMergeEnable;

    // 请求数量自动合并 - 必须
    private Integer isNumMerge;

    // 请求数量自动合并 - 必须
    private Integer isReqMerge;

    // 是否禁止退货 - 必须
    private Integer isForbiddenReturn;

    // 是否禁用销售 - 必须
    private Integer isSuspendSales;

    // 商品发货方式 - 必须
    private String goodsType;

    // 是否支持扫码 - 必须
    private Integer isScanSupport;

    // 是否支持入仓订单 - 必须
    private Integer isDepotOrder;

    // 是否启用 - 必须
    private Integer isEnabled;

    // 是否支持库存同步 - 必须
    private Integer isStockSync;

    // 是否支持账单同步 - 必须
    private Integer isBillSync;

    // 是否支持发票同步 - 必须
    private Integer isInvoiceSync;

    // 是否支持退货 - 必须
    private Integer isReturnSupport;

    // 是否支持发货通知 - 必须
    private Integer isSendNotify;

    // 是否支持退款通知 - 必须
    private Integer isRefundNotify;

    // 是否支持订单通知 - 必须
    private Integer isOrderNotify;

    // 是否支持物流通知 - 必须
    private Integer isLogisticsNotify;

    // 是否支持售后通知 - 必须
    private Integer isAfterSaleNotify;

    // 是否支持客服通知 - 必须
    private Integer isCustomerServiceNotify;

    // 是否支持营销通知 - 必须
    private Integer isMarketingNotify;

    // 是否支持财务通知 - 必须
    private Integer isFinanceNotify;

    // 是否支持数据同步 - 必须
    private Integer isDataSync;

    // 是否支持报表同步 - 必须
    private Integer isReportSync;

    // 是否支持库存预警 - 必须
    private Integer isStockAlert;

    // 是否支持价格预警 - 必须
    private Integer isPriceAlert;

    // 是否支持销量预警 - 必须
    private Integer isSalesAlert;

    // 是否支持促销预警 - 必须
    private Integer isPromotionAlert;

    // 是否支持订单预警 - 必须
    private Integer isOrderAlert;

    // 是否支持物流预警 - 必须
    private Integer isLogisticsAlert;

    // 是否支持售后预警 - 必须
    private Integer isAfterSaleAlert;

    // 是否支持客服预警 - 必须
    private Integer isCustomerServiceAlert;

    // 是否支持营销预警 - 必须
    private Integer isMarketingAlert;

    // 是否支持财务预警 - 必须
    private Integer isFinanceAlert;

    // 是否支持数据预警 - 必须
    private Integer isDataAlert;

    // 是否支持报表预警 - 必须
    private Integer isReportAlert;

    // 是否支持大促模式 - 必须
    private Integer isBigPromoMode;

    // 大促模式仓库 - 必须
    private Integer greatDealWarehouse;

    // 最后修改时间 - 必须
    private LocalDateTime modified;

    // 创建时间 - 必须
    private LocalDateTime created;

}
