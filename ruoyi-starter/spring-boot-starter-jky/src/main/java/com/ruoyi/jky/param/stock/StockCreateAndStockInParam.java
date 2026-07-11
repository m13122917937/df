package com.ruoyi.jky.param.stock;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class StockCreateAndStockInParam {

    /** 供应商编码 */
    private String vendCode;

    /** 部门编号 */
    private String applyDepartCode;

    /** 公司编号 */
    private String applyCompanyCode;

    /** 仓库编码 */
    private String inWarehouseCode;

    /** 入库类型：101=采购入库，102=调拨入库，103=盘盈入库，104=其他入库，105=销售退货，106=生产完工入库 */
    private Integer inType = 101;

    /** 关联单据编号 */
    private String relDataId;

    /** 申请人ID */
    private String applyUserId;

    /** 申请人姓名 */
    private String applyUserName;

    /** 申请部门 */
    private String applyDepartName;

    /** 申请时间 */
    private Date applyDate;

    /** 备注 */
    private String memo;

    /** 审核人 */
    private String auditUserName;

    /** 审核时间 */
    private Date auditDate;

    /** 通知单号，固定值100 */
    private String notificationCode;

    /** 制单人 */
    private String operator;

    /** 来源，固定值OPEN */
    private String source = "OPEN";

    /** 币种 */
    private String currencyCode;

    /** 汇率 */
    private BigDecimal currencyRate;

    /** 标记 */
    private String flagData;

    /** 预计入库时间 */
    private Date planInDate;

    /** 物流公司名称 */
    private String logisticName;

    /** 物流类型 */
    private String logisticType;

    /** 物流单号 */
    private String logisticNo;

    /** 物流公司编号 */
    private String logisticCode;

    /** 系统标识 */
    private String systemMark;

    /** 入库明细数组 */
    private List<StockInDetailView> stockInDetailViews;

    @Data
    @Accessors(chain = true)
    public static class StockInDetailView {

        /** 外部货品编号，用于匹配货品 */
        private String outSkuCode;

        /** 货品条码 */
        private String skuBarcode;

        /** 关联实际业务明细表ID */
        private Long relDetailId;

        /** 计量单位 */
        private String unitName;

        /** 入库数量 */
        private BigDecimal skuCount;

        /** 入库单价 */
        private BigDecimal skuPrice;

        /** 入库金额 */
        private BigDecimal totalAmount;

        /** 是否正品：1=是，0=否 */
        private Integer isCertified;

        /** 明细备注 */
        private String rowRemark;

        /** 批次明细 */
        private List<Batch> batchList;

        /** 序列号明细 */
        private List<Serial> serialList;

        /** 唯一码与批次关联列表，使用后serialList字段失效 */
        private List<SerialNoBatchNoRel> serialNoBatchNoRelList;
    }

    @Data
    @Accessors(chain = true)
    public static class Batch {

        /** 批号 */
        private String batchNo;

        /** 数量 */
        private BigDecimal quantity;

        /** 单价 */
        private BigDecimal cuPrice;

        /** 生产日期 */
        private Date productionDate;

        /** 保质期 */
        private BigDecimal shelfLife;

        /** 保质期单位 */
        private String shelfLiftUnit;

        /** 到期日期 */
        private Date expirationDate;

        /** 批次备注 */
        private String batchMemo;
    }

    @Data
    @Accessors(chain = true)
    public static class Serial {

        /** 序列号 */
        private String serialNo;
        /** 86 */
        private String serialNo2;

        /** 箱号 */
        private String caseNumber;

        /** 单价 */
        private BigDecimal cuPrice;

        /** 备注 */
        private String rowRemark;
    }

    @Data
    @Accessors(chain = true)
    public static class SerialNoBatchNoRel {

        /** 唯一码 */
        private String no;

        /** 批次号 */
        private String batchNo;
    }

}
