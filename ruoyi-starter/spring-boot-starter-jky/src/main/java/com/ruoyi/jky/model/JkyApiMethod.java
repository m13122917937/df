package com.ruoyi.jky.model;

public final class JkyApiMethod {

    public static final String VENDOR_CREATE = "erp.vend.create";

    public static final String VENDOR_LIST = "erp.vend.get";

    public static final String GOODS_LIST = "erp.storage.goodslist";

    public static final String WAREHOUSE_LIST = "erp.warehouse.get";

    public static final String ORDER_QUERY = "oms.trade.fullinfoget";

    public static final String REFUND_LIST = "omsapi-business.refund.listrefund";

    public static final String LOGISTICS_UPDATE = "wms-wos.sendorderupdatelogisticinfo";

    public static final String SN_REPORT = "wms.order.snreport";

    public static final String STOCK_CREATE_AND_STOCK_IN = "erp.stock.createandstockin";

    public static final String INSPECT = "wms-wos.order.inspect";

    public static final String DELIVERY_ORDER_GET = "wms.extpick.getdeliveryorder";

    private JkyApiMethod() {
    }

}
