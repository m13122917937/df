package com.ruoyi.jky.rep.vendor;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VendorListRep {

    /** 供应商信息列表 */
    private List<VendorInfoRep> vendInfo;

    @Data
    public static class VendorInfoRep {

        /** 供应商ID */
        private Long vendId;

        /** 供应商编码 */
        private String code;

        /** 供应商名称 */
        private String name;

        /** 分类ID */
        private Integer classId;

        /** 分类名称 */
        private String className;

        /** 简称 */
        private String abbreviation;

        /** 国家名称 */
        private String countryName;

        /** 国家ID */
        private Integer countryId;

        /** 省份名称 */
        private String provinceName;

        /** 省份ID */
        private Integer provinceId;

        /** 城市名称 */
        private String cityName;

        /** 城市ID */
        private Integer cityId;

        /** 区/县名称 */
        private String townName;

        /** 区/县ID */
        private Integer townId;

        /** 街道名称 */
        private String streetName;

        /** 街道ID */
        private Integer streetId;

        /** 详细地址 */
        private String address;

        /** 邮编 */
        private String postcode;

        /** 电子邮箱 */
        private String email;

        /** 网址 */
        private String website;

        /** 助记码 */
        private String assistantCode;

        /** 发展日期 */
        private String developDate;

        /** 负责人 */
        private String leader;

        /** 电话 */
        private String tel;

        /** 传真 */
        private String fax;

        /** 备注 */
        private String memo;

        /** 标记ID集合 */
        private String flagData;

        /** 部门ID */
        private Long departId;

        /** 部门名称 */
        private String departName;

        /** 是否货物采购：1=是，0=否 */
        private Integer purchOfGoods;

        /** 是否委外加工厂：1=是，0=否 */
        private Integer outsourcedFactory;

        /** 是否自有工厂：1=是，0=否 */
        private Integer ownFactory;

        /** 是否委外托管仓：1=是，0=否 */
        private Integer outsourcedManagedWarehouse;

        /** 是否物流配送：1=是，0=否 */
        private Integer logisiticService;

        /** 是否配装师傅：1=是，0=否 */
        private Integer isAssembler;

        /** 默认采购税率 */
        private BigDecimal taxRate;

        /** 公司ID */
        private Long companyId;

        /** 公司名称 */
        private String companyName;

        /** 默认配送方式 */
        private String logisticType;

        /** 默认采购办公室ID */
        private Long purchOfficeId;

        /** 默认采购办公室名称 */
        private String purchOfficeName;

        /** 默认采购员ID */
        private Long purchUserId;

        /** 默认采购员名称 */
        private String purchUserName;

        /** 纳税人识别号 */
        private String taxIdentifyNumber;

        /** 创立时间 */
        private String foundDate;

        /** 等级编码：01=一级，02=二级，03=三级 */
        private String levelCode;

        /** 等级名称 */
        private String levelName;

        /** 交付周期，单位：天 */
        private Integer arrivePeriod;

        /** 特别提醒 */
        private String remindMsg;

        /** 序号 */
        private Integer orderIndex;

        /** 货源地 */
        private String goodsSupplyAddress;

        /** 默认预付款比例 */
        private BigDecimal advancePayPercent;

        /** 默认结算方式：1=欠款计应付，2=现付，3=冲预付，4=多种结算，5=现结，6=现料，7=欠料计应付 */
        private String settType;

        /** 默认结算账期 */
        private String paymentMethodName;

        /** 默认采购币种 */
        private String currencyName;

        /** 默认物流公司 */
        private String logisticName;

        /** 默认开票类型 */
        private String defaultInvoiceType;

        /** 默认供应商虚拟仓仓库编号 */
        private String warehouseCode;

        /** 默认供应商虚拟仓仓库名称 */
        private String warehouseName;

        /** 总经理 */
        private String generalManager;

        /** 注册资本 */
        private String registeredCapital;

        /** 法定代表人 */
        private String legalPerson;

        /** 开票税号 */
        private String taxNumber;

        /** 资质到期日期 */
        private String qualificationDueDate;

        /** 认证体系 */
        private String certificationSystem;

        /** 仓储面积 */
        private String storageArea;

        /** 员工人数 */
        private String numberOfEmployees;

        /** 年产值 */
        private String annualOutputValue;

        /** 主要产品及服务 */
        private String mainProductsAndServices;

        /** 主要设备及工艺 */
        private String mainEquipmentAndProcess;

        /** 月供货能力 */
        private String monthlySupplyCapacity;

        /** 供货占比 */
        private String supplyProportion;

        /** 供货合格率 */
        private String qualifiedRateOfSupply;

        /** 预估产能 */
        private BigDecimal estimatedProduction;

        /** 支持代发：1=是，0=否 */
        private Integer isSupportDistribution;

        /** 主要客户群体 */
        private String majorCustomerGroups;

        /** 厂房面积 */
        private String plantArea;

        /** 是否停用：1=是，0=否 */
        private Integer isBlockup;

        /** 是否删除：1=是，0=否 */
        private Integer isDelete;

        /** 经营范围 */
        private String businessScope;

        /** 创建时间 */
        private String gmtCreate;

        /** 修改时间 */
        private String gmtModified;

        /** 自定义字段1 */
        private String field1;

        /** 自定义字段2 */
        private String field2;

        /** 自定义字段3 */
        private String field3;

        /** 自定义字段4 */
        private String field4;

        /** 自定义字段5 */
        private String field5;

        /** 自定义字段6 */
        private String field6;

        /** 自定义字段7 */
        private String field7;

        /** 自定义字段8 */
        private String field8;

        /** 自定义字段9 */
        private String field9;

        /** 自定义字段10 */
        private String field10;

        /** 自定义字段11 */
        private String field11;

        /** 自定义字段12 */
        private String field12;

        /** 自定义字段13 */
        private String field13;

        /** 自定义字段14 */
        private String field14;

        /** 自定义字段15 */
        private String field15;

        /** 自定义字段16 */
        private String field16;

        /** 自定义字段17 */
        private String field17;

        /** 自定义字段18 */
        private String field18;

        /** 自定义字段19 */
        private String field19;

        /** 自定义字段20 */
        private String field20;

        /** 收款账户信息 */
        private List<VendorPayAccountRep> vendPayAccountList;
    }

    @Data
    public static class VendorPayAccountRep {

        /** 账户名称 */
        private String accName;

        /** 银行账号 */
        private String bankacct;

        /** 开户行 */
        private String bankbranch;

        /** 账户类型 */
        private String accountTypeName;

        /** 国家/地区 */
        private String countriesRegions;

        /** IBAN，国际银行账户号码 */
        private String internationalBankAccount;

        /** BIC/SWIFT Code，银行国际代码 */
        private String swiftCode;

        /** 所属银行 */
        private String bankName;
    }
}
