# wms.extpick.getdeliveryorder (获取发货单)

**接口说明**：标准接口 | 授权类型：必须授权

**最近发布时间**：2026-01-28 15:12:42.0

---

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| method | String | 是 | 32 | 方法名称 | wms.extpick.getdeliveryorder |
| appkey | Long | 是 | 32 | 应用编号 | 16242933 |
| version | String | 是 | 32 | 版本号 | v1.0 |
| contenttype | String | 是 | 32 | 返回格式（暂时只支持json） | json |
| timestamp | String | 是 | 32 | 时间 | 2015-01-01 12:00:00 |
| bizcontent | String | 是 | 32 | 业务参数 | {"a":"a1","b":"b1"} |
| contextid | String | 否 | 32 | 上下文报文关联ID（不参与签名） | 131562131 |
| sign | String | 是 | 32 | 签名 | — |
| token | String | 否 | 32 | 自研应用不需要传此参数，ISV应用必须先得到商家用户授权，获取相关token信息（不参与签名） | — |

---

## 业务请求参数（bizcontent）

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| startCreate | String | 是 | — | 单据创建起始时间 | 2019-07-14 21:14:31 |
| endCreate | String | 是 | — | 单据创建结束时间 | 2019-08-22 21:14:31 |
| pageSize | Integer | 是 | — | 每页记录数 | 50 |
| pageIndex | Integer | 是 | — | 页码 | 0 |
| logisticNoStatus | Integer | 否 | — | 0或者不传表示需要发货单包含物流单号，1表示返回可以不包含物流单号的发货单 | 0 |
| warehouseCode | String | 否 | 50 | 仓库code | 202301 |
| transferOrderStartDate | String | 否 | — | 转按单配货开始时间 | 2019-07-14 21:14:31 |
| transferOrderEndDate | String | 否 | — | 转按单配货结束时间 | 2019-07-28 21:14:31 |
| onlyNeedAllInAgv | Integer | 否 | — | 只返回货品货位全分配在智能区的发货单（1是，0否） | 1 |

---

## 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|
| code | int | 16 | 返回码（200为成功，0为失败） | 200 |
| msg | String | 32 | 返回消息 | [0230041006]未知错误 |
| subCode | String | 32 | 子级编码 | 0230040301 |
| result | JackYunResult | — | 返回结果 | — |
| data | Data | — | 返回业务数据 | — |
| contextId | String | 32 | 上下文编号 | 412815373296750208 |

---

## 业务返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|
| totalPage | Integer | — | 获取到的发货单总数（pageIndex大于0时，不返回总数） | 50 |
| deliveryOrder | Array | — | 发货单明细 | — |
| └ ownerCode | String | 30 | 货主编码 | jackyun_dev |
| └ warehouseCode | String | 30 | 仓库编码 | TEST001 |
| └ deliveryOrderCode | String | 30 | 发货单号 | S201908050052 |
| └ preDeliveryOrderCode | String | 30 | 销售单号 | JY201908050052 |
| └ platOrderNO | String | 300 | 平台单号 | 39665150185000318 |
| └ sourcePlatformCode | String | 30 | 平台代码 | JD01 |
| └ shopNick | String | 30 | 店铺名称 | 京东11 |
| └ logisticsCode | String | 100 | 物流单号 | wms菜鸟(勿动！！！！) |
| └ logisticName | String | 30 | 物流别名 | wms菜鸟(勿动！！！！) |
| └ expressCode | String | 30 | 物流别名 | wms菜鸟(勿动！！！！) |
| └ logisticNO | String | 30 | 物流单号 | 75169279473597 |
| └ totalAmount | BigDecimal | 16 | 订单金额 | 100.00 |
| └ createTime | Date | — | 发货单创建时间 | 2019-08-05 15:22:56 |
| └ placeOrderTime | Date | — | 平台下单时间 | 2019-08-05 15:22:56 |
| └ payTime | Date | — | 支付时间 | 2019-08-22 14:16:16 |
| └ orderLines | Array | — | 货品信息 | — |
| &nbsp;&nbsp;└ orderLineNo | String | 30 | 行号 | 1 |
| &nbsp;&nbsp;└ itemCode | Long | 30 | 规格ID（相当于奇门的ItemCode） | wsy01 |
| &nbsp;&nbsp;└ itemId | Long | 30 | 规格ID（wms系统的skuid） | 641981791668542593 |
| &nbsp;&nbsp;└ inventoryType | String | 30 | 是否正品 | ZP |
| &nbsp;&nbsp;└ goodsCode | String | 30 | 货品编号 | coach大包 |
| &nbsp;&nbsp;└ itemName | String | 30 | 货品名称（相当于奇门的itemName） | coach大包 |
| &nbsp;&nbsp;└ skuProperty | String | 30 | 规格属性名称 | 默认规格 |
| &nbsp;&nbsp;└ barcode | String | 30 | 规格属性名称（相当于奇门的skuProperty） | 0012131415 |
| &nbsp;&nbsp;└ unit | String | 30 | 单位 | 个 |
| &nbsp;&nbsp;└ planQty | BigDecimal | 16 | 数量 | 10.0000 |
| &nbsp;&nbsp;└ isGift | Integer | 4 | 赠品标记 | 10.0000 |
| &nbsp;&nbsp;└ batchCode | String | 30 | 批次编号 | pc002 |
| &nbsp;&nbsp;└ productDate | Date | — | 生产日期 | 2018-08-01 16:52:34 |
| &nbsp;&nbsp;└ expireDate | Date | — | 过期日期 | 2018-09-12 16:53:13 |
| &nbsp;&nbsp;└ positionsId | Long | — | 货位id | 21321 |
| &nbsp;&nbsp;└ positionsName | String | 50 | 货位名称 | A01-01 |
| └ logisticsNo | String | 30 | 物流单号 | zx213213123 |
| └ goodsKinds | Integer | 11 | 货品种类 | 30 |
| └ goodsTotal | BigDecimal | 11 | 货品总数 | 50 |
| └ jitPoNos | String | 30 | JITPO单号 | 15412122 |
| └ jitStorageNo | String | 30 | JIT出仓单号 | 123156 |
| └ jitWarehouse | String | 30 | JIT送货仓库 | 16511212 |
| └ registerTime | Date | — | 发货单登记时间 | 2019-08-05 15:22:56 |
| └ flagNames | String | 100 | 发货单标记 | "标记1,标记2" |
| pageInfo | Object | 4 | 分页对象 | — |
| └ pageIndex | Integer | — | 页码 | 0 |
| └ offset | Integer | — | 偏移量 | 50 |
| └ pageSize | Integer | — | 每页记录数 | 50 |
| └ total | Integer | 4 | 总数 | 2000 |

---

## 请求业务参数示例（JSON）

```json
{
  "pageSize": 50,
  "startCreate": "2019-07-14 21:14:31",
  "endCreate": "2019-08-22 21:14:31",
  "pageIndex": 0,
  "logisticNoStatus": 0,
  "warehouseCode": "202301"
}