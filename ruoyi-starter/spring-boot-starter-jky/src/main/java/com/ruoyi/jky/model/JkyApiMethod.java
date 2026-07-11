package com.ruoyi.jky.model;

public final class JkyApiMethod {

    public static final String VENDOR_CREATE = "erp.vend.create";

    public static final String VENDOR_LIST = "erp.vend.get";

    public static final String GOODS_LIST = "erp.storage.goodslist";

    public static final String WAREHOUSE_LIST = "erp.warehouse.get";

    public static final String ORDER_QUERY = "oms.trade.fullinfoget";

    public static final String QM_ORDER_QUERY = "jackyun.tradenotsensitiveinfos.list.get";

    public static final String REFUND_LIST = "omsapi-business.refund.listrefund";

    public static final String LOGISTICS_UPDATE = "wms-wos.sendOrderUpdateLogisticInfo";

    public static final String SN_REPORT = "wms.order.snreport";

    public static final String STOCK_CREATE_AND_STOCK_IN = "erp.stock.createandstockin";

    public static final String INSPECT = "wms-wos.order.inspect";

    public static final String DELIVERY_ORDER_GET = "wms.extpick.getdeliveryorder";

    public static final String REJECT = "oms.open.trade.audit.reject";

    public static final String SEND_DIRECT = "wms-wos.order.senddirect";

    public static final String CREATE_CASH_OR_COST_REC_PAY_BILL = "fin-fbs.billinfo.createcashorcostrecpaybill";

    private JkyApiMethod() {
    }

}
