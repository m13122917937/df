package com.ruoyi.wangdian.rep.goods;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.wangdian.rep.WDRep;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品查询（带规格）返回结果
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsQueryWithSpecRep extends WDRep {

    private static final long serialVersionUID = 1L;

    /**
     * 数据
     */
    @JsonProperty("data")
    private DataRepairs data;

    @Data
    public static class DataRepairs implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 商品列表
         */
        @JsonProperty("goods_list")
        private List<GoodsInfo> goodsList;

        /**
         * 总记录数
         */
        @JsonProperty("total_count")
        private Integer totalCount;
    }

    @Data
    public static class GoodsInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 商品ID
         */
        @JsonProperty("goods_id")
        private Long goodsId;

        /**
         * 商品编号
         */
        @JsonProperty("goods_no")
        private String goodsNo;

        /**
         * 商品名称
         */
        @JsonProperty("goods_name")
        private String goodsName;

        /**
         * 商品简称
         */
        @JsonProperty("short_name")
        private String shortName;

        /**
         * 商品别名
         */
        @JsonProperty("alias")
        private String alias;

        /**
         * 商品类型 1:普通商品 2:服务商品 3:组装商品 4:捆绑商品 5:赠品 6:商品劵 7:拼商品 8:虚商品 9:阶梯价商品
         */
        @JsonProperty("goods_type")
        private Integer goodsType;

        /**
         * 水洗标签
         */
        @JsonProperty("washing_label")
        private String washingLabel;

        /**
         * 规格数量
         */
        @JsonProperty("spec_count")
        private Integer specCount;

        /**
         * 拼音
         */
        @JsonProperty("pinyin")
        private String pinyin;

        /**
         * 品牌ID
         */
        @JsonProperty("brand_id")
        private Long brandId;

        /**
         * 品牌编号
         */
        @JsonProperty("brand_no")
        private String brandNo;

        /**
         * 备注
         */
        @JsonProperty("remark")
        private String remark;

        /**
         * 自定义属性1
         */
        @JsonProperty("prop1")
        private String prop1;

        /**
         * 自定义属性2
         */
        @JsonProperty("prop2")
        private String prop2;

        /**
         * 自定义属性3
         */
        @JsonProperty("prop3")
        private String prop3;

        /**
         * 自定义属性4
         */
        @JsonProperty("prop4")
        private String prop4;

        /**
         * 自定义属性5
         */
        @JsonProperty("prop5")
        private String prop5;

        /**
         * 自定义属性6
         */
        @JsonProperty("prop6")
        private String prop6;

        /**
         * 自定义属性7
         */
        @JsonProperty("prop7")
        private String prop7;

        /**
         * 自定义属性8
         */
        @JsonProperty("prop8")
        private String prop8;

        /**
         * 自定义属性9
         */
        @JsonProperty("prop9")
        private String prop9;

        /**
         * 自定义属性10
         */
        @JsonProperty("prop10")
        private String prop10;

        /**
         * 产地
         */
        @JsonProperty("origin")
        private String origin;

        /**
         * 分类ID
         */
        @JsonProperty("class_id")
        private Long classId;

        /**
         * 分类名称
         */
        @JsonProperty("class_name")
        private String className;

        /**
         * 品牌名称
         */
        @JsonProperty("brand_name")
        private String brandName;

        /**
         * 标签ID
         */
        @JsonProperty("flag_id")
        private Long flagId;

        /**
         * 标签名称
         */
        @JsonProperty("flag_name")
        private String flagName;

        /**
         * 单位ID
         */
        @JsonProperty("unit")
        private Integer unit;

        /**
         * 单位名称
         */
        @JsonProperty("unit_name")
        private String unitName;

        /**
         * 辅助单位ID
         */
        @JsonProperty("aux_unit")
        private Integer auxUnit;

        /**
         * 辅助单位名称
         */
        @JsonProperty("aux_unit_name")
        private String auxUnitName;

        /**
         * 商品修改时间戳
         */
        @JsonProperty("goods_modified")
        private Long goodsModified;

        /**
         * 删除标记 0:未删除 1:已删除
         */
        @JsonProperty("deleted")
        private Integer deleted;

        /**
         * 创建时间
         */
        @JsonProperty("goods_created")
        private String goodsCreated;

        /**
         * 修改时间
         */
        @JsonProperty("modified")
        private String modified;

        /**
         * 图片地址
         */
        @JsonProperty("img_url")
        private String imgUrl;

        /**
         * 店铺ID
         */
        @JsonProperty("shop_id")
        private Long shopId;

        /**
         * 商品标签
         */
        @JsonProperty("goods_label")
        private String goodsLabel;

        /**
         * 是否不使用快递 0:否 1:是
         */
        @JsonProperty("is_not_use_air")
        private Integer isNotUseAir;

        /**
         * 定制价格1
         */
        @JsonProperty("custom_price1")
        private BigDecimal customPrice1;

        /**
         * 定制价格2
         */
        @JsonProperty("custom_price2")
        private BigDecimal customPrice2;

        /**
         * 打包积分
         */
        @JsonProperty("pack_score")
        private Integer packScore;

        /**
         * 捡货积分
         */
        @JsonProperty("pick_score")
        private Integer pickScore;

        /**
         * 扫描积分
         */
        @JsonProperty("scan_score")
        private Integer scanScore;

        /**
         * 销售积分
         */
        @JsonProperty("sale_score")
        private Integer saleScore;

        /**
         * 规格标签ID
         */
        @JsonProperty("spec_flag_id")
        private Long specFlagId;

        /**
         * 规格标签名称
         */
        @JsonProperty("spec_flag_name")
        private String specFlagName;

        /**
         * 兑换名称
         */
        @JsonProperty("exchange_name")
        private String exchangeName;

        /**
         * 限购数量
         */
        @JsonProperty("max_limit_num")
        private Integer maxLimitNum;

        /**
         * 规格列表
         */
        @JsonProperty("spec_list")
        private List<SpecInfo> specList;
    }

    @Data
    public static class SpecInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 规格ID
         */
        @JsonProperty("spec_id")
        private Long specId;

        /**
         * 商品ID
         */
        @JsonProperty("goods_id")
        private Long goodsId;

        /**
         * 规格商家编码
         */
        @JsonProperty("spec_no")
        private String specNo;

        /**
         * 规格编码
         */
        @JsonProperty("spec_code")
        private String specCode;

        /**
         * 规格条码
         */
        @JsonProperty("barcode")
        private String barcode;

        /**
         * 规格名称
         */
        @JsonProperty("spec_name")
        private String specName;

        /**
         * 最低价格
         */
        @JsonProperty("lowest_price")
        private BigDecimal lowestPrice;

        /**
         * 零售价
         */
        @JsonProperty("retail_price")
        private BigDecimal retailPrice;

        /**
         * 批发价
         */
        @JsonProperty("wholesale_price")
        private BigDecimal wholesalePrice;

        /**
         * 会员价
         */
        @JsonProperty("member_price")
        private BigDecimal memberPrice;

        /**
         * 市场价
         */
        @JsonProperty("market_price")
        private BigDecimal marketPrice;

        /**
         * 创建时间
         */
        @JsonProperty("spec_created")
        private String specCreated;

        /**
         * 有效天数
         */
        @JsonProperty("validity_days")
        private Integer validityDays;

        /**
         * 销售天数
         */
        @JsonProperty("sales_days")
        private Integer salesDays;

        /**
         * 收货天数
         */
        @JsonProperty("receive_days")
        private Integer receiveDays;

        /**
         * 重量
         */
        @JsonProperty("weight")
        private BigDecimal weight;

        /**
         * 长度
         */
        @JsonProperty("length")
        private BigDecimal length;

        /**
         * 宽度
         */
        @JsonProperty("width")
        private BigDecimal width;

        /**
         * 高度
         */
        @JsonProperty("height")
        private BigDecimal height;

        /**
         * sn管理 1:需要sn 2:不需要sn
         */
        @JsonProperty("sn_type")
        private Integer snType;

        /**
         * 是否低价  false:否 true:是
         */
        @JsonProperty("is_lower_cost")
        private Boolean isLowerCost;

        /**
         * 税率
         */
        @JsonProperty("tax_rate")
        private BigDecimal taxRate;

        /**
         * 仓储处理掩码
         */
        @JsonProperty("wms_process_mask")
        private Integer wmsProcessMask;

        /**
         * 删除标记 0:未删除 1:已删除
         */
        @JsonProperty("deleted")
        private Integer deleted;

        /**
         * 是否不需要审核 0:需要 1:不需要
         */
        @JsonProperty("is_not_need_examine")
        private Integer isNotNeedExamine;

        /**
         * 大件类型
         */
        @JsonProperty("large_type")
        private Integer largeType;

        /**
         * 备注
         */
        @JsonProperty("remark")
        private String remark;

        /**
         * 修改时间戳
         */
        @JsonProperty("spec_modified")
        private Long specModified;

        /**
         * 自定义属性1
         */
        @JsonProperty("prop1")
        private String prop1;

        /**
         * 自定义属性2
         */
        @JsonProperty("prop2")
        private String prop2;

        /**
         * 自定义属性3
         */
        @JsonProperty("prop3")
        private String prop3;

        /**
         * 自定义属性4
         */
        @JsonProperty("prop4")
        private String prop4;

        /**
         * 自定义属性5
         */
        @JsonProperty("prop5")
        private String prop5;

        /**
         * 自定义属性6
         */
        @JsonProperty("prop6")
        private String prop6;

        /**
         * 自定义属性7
         */
        @JsonProperty("prop7")
        private String prop7;

        /**
         * 自定义属性8
         */
        @JsonProperty("prop8")
        private String prop8;

        /**
         * 自定义属性9
         */
        @JsonProperty("prop9")
        private String prop9;

        /**
         * 自定义属性10
         */
        @JsonProperty("prop10")
        private String prop10;

        /**
         * 图片地址
         */
        @JsonProperty("img_url")
        private String imgUrl;

        /**
         * 规格单位ID
         */
        @JsonProperty("spec_unit")
        private Integer specUnit;

        /**
         * 规格辅助单位ID
         */
        @JsonProperty("spec_aux_unit")
        private Integer specAuxUnit;

        /**
         * 规格单位名称
         */
        @JsonProperty("spec_unit_name")
        private String specUnitName;

        /**
         * 规格辅助单位名称
         */
        @JsonProperty("spec_aux_unit_name")
        private String specAuxUnitName;

        /**
         * 单位
         */
        @JsonProperty("unit")
        private Integer unit;

        /**
         * 辅助单位
         */
        @JsonProperty("aux_unit")
        private Integer auxUnit;

        /**
         * 最大库存
         */
        @JsonProperty("stock_max")
        private BigDecimal stockMax;

        /**
         * 最小库存
         */
        @JsonProperty("stock_min")
        private BigDecimal stockMin;

        /**
         * 库存
         */
        @JsonProperty("stock")
        private BigDecimal stock;

        /**
         * 进货价
         */
        @JsonProperty("cost_price")
        private BigDecimal costPrice;

        /**
         * 条形码列表
         */
        @JsonProperty("barcode_list")
        private List<BarcodeInfo> barcodeList;

        /**
         * 条码数量
         */
        @JsonProperty("barcode_count")
        private Integer barcodeCount;

        /**
         * 税收分类编码
         */
        @JsonProperty("tax_code")
        private String taxCode;

        /**
         * 修改时间
         */
        @JsonProperty("modified")
        private String modified;

        /**
         * 是否启用 0:禁用 1:启用
         */
        @JsonProperty("enabled")
        private Integer enabled;
    }

    @Data
    public static class BarcodeInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 条码
         */
        @JsonProperty("barcode")
        private String barcode;

        /**
         * 类型 1:商家编码 2:code码 3:序列号
         */
        @JsonProperty("type")
        private Integer type;

        /**
         * 是否主码 1:是 0:否
         */
        @JsonProperty("is_master")
        private Integer isMaster;

        /**
         * 修改时间
         */
        @JsonProperty("modified")
        private String modified;
    }
}
