# 吉客云开放平台 API 文档

## 目录
1. [销售单查询接口 (oms.trade.fullinfoget)](#1-销售单查询接口-omstradefullinfoget)
2. [签名生成说明](#2-签名生成说明)

---

## 1. 销售单查询接口 (`oms.trade.fullinfoget`)

### 接口信息

| 属性 | 描述 |
| :--- | :--- |
| **接口名称** | 销售单查询 |
| **接口地址** | `oms.trade.fullinfoget` |
| **授权类型** | 必须授权 |
| **最近发布时间** | 2026-05-22 15:48:07.0 |

### 接口说明

1.  **分页查询**：使用游标(`scrollId`)分页。首次查询时入参增加 `scrollId=""` 并在 `fields` 字段中添加 `scrollId`，后续查询将上次返回的 `scrollId` 作为入参请求下一页。
2.  **字段说明**：部分列表字段 `fields` 填写说明详见：[https://s.jkyun.biz/2jWhg8v](https://s.jkyun.biz/2jWhg8v)
3.  **数据核对**：销售单取数核对方式参考：[https://s.jkyun.biz/T5x1lE5](https://s.jkyun.biz/T5x1lE5)
4.  **时间查询**：销售单号、网店订单号、分批发货申请单号、修改时间、创建时间、审核时间、发货时间、完成时间、下单时间**必传其一**，且起止时间不能超过7天。
5.  **重要提示**：
    - 如果对接数据包含淘系订单（淘宝、天猫等），需确认客户具备奇门资质。详情参考：[https://s.jkyun.biz/0Wuqdit](https://s.jkyun.biz/0Wuqdit)
    - 开通奇门资质仅用于获取订单货品金额、数量、编号等信息，**无法获取收货人信息**。

### 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `oms.trade.fullinfoget` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| `sign` | String | 是 | 32 | 签名 | 见签名生成说明 |
| `token` | String | 否 | 32 | ISV应用授权token(不参与签名) | 见获取token方法 |

### 业务请求参数 (`bizcontent`)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `fields` | String | **是** | 需要返回的字段列表，多个用半角逗号分隔 | `tradeNo,orderNo,goodsDetail.outerId,scrollId` |
| `scrollId` | String | 否 | 游标id。首次查询传空字符串 `""` | `e435cb66390bb1eac99aa5755805f9f7` |
| `pageSize` | Integer | 否 | 每页记录数，默认50，最大200 | `5` |
| `tradeNo` | String | 否* | 销售单号，多个用半角逗号分隔 | `JY202406050002` |
| `tradeIds` | Array | 否* | 订单Id数组 | `[2307317872088616192,2325498819791326465]` |
| `sourceTradeNos` | String | 否* | 网店订单号 | `D241015171823K00002Z01` |
| `tradeDeliveryNo` | String | 否* | 分批发货单号，多个用半角逗号分隔 | `"JY202512160001,JY202512160002"` |
| `startModified` | Date | 否* | 最后修改时间（起始） | `2024-06-05 11:26:00` |
| `endModified` | Date | 否* | 最后修改时间（截止） | `2024-06-11 11:26:00` |
| `startCreated` | Date | 否* | 创建时间（起始） | `2024-06-05 11:26:00` |
| `endCreated` | Date | 否* | 创建时间（截止） | `2024-06-11 11:26:00` |
| `startAuditTime` | Date | 否* | 审核时间（起始） | `2024-06-05 11:26:00` |
| `endAuditTime` | Date | 否* | 审核时间（截止） | `2024-06-11 11:26:00` |
| `startConsignTime` | Date | 否* | 发货时间（起始） | `2024-10-15 11:26:00` |
| `endConsignTime` | Date | 否* | 发货时间（截止） | `2024-10-19 11:26:00` |
| `startCompleteTime` | Date | 否* | 订单完成时间（起始） | `2025-12-01 00:00:00` |
| `endCompleteTime` | Date | 否* | 订单完成时间（截止） | `2025-12-16 23:59:59` |
| `startTradeTime` | Date | 否* | 下单时间（开始） | `2025-12-01 00:00:00` |
| `endTradeTime` | Date | 否* | 下单时间（截止） | `2025-12-16 23:59:59` |
| `startSigningTime` | Date | 否* | 订单签收时间（起始） | `2024-06-05 11:26:00` |
| `endSigningTime` | Date | 否* | 订单签收时间（截止） | `2024-06-11 11:26:00` |
| `tradeStatus` | Integer | 否 | 销售单状态 | `9090` (已完成) |
| `tradeStatusList` | Array | 否 | 销售单状态列表 | `[1010, 1050, 6000]` |
| `tradeType` | Integer | 否 | 销售单类型 | `1` (零售业务) |
| `shopIds` | Array | 否 | 店铺id数组 | `["1647727244400592896"]` |
| `warehouseIds` | Array | 否 | 仓库Id数组 | `["1718178122057024640"]` |
| `isTableSwitch` | Integer | 否 | 分表查询（1：日常表；2：归档数据） | `1` |
| `isDelete` | String | 否 | 是否删除（0：否；1：是） | `0` |

> **注意**：标有 `*` 的时间或单号类参数，在请求时必须至少提供一个有效的查询条件。

### 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `code` | int | 16 | 返回码 (200为成功，0为失败) | `200` |
| `msg` | String | 32 | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 32 | 子级编码 | `0230040301` |
| `result` | JackYunResult | - | 返回结果对象 | - |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

### 业务返回参数 (`result.data`)

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `scrollId` | String | 游标id，用于下次请求 | `e435cb66390bb1eac99aa5755805f9f7` |
| `trades` | Array | 销售单数组 | 见下方结构 |

#### `trades` 数组内部结构

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `tradeNo` | String | 订单编号 | `JY201906050001` |
| `tradeId` | String | 销售单id (系统编码) | `123` |
| `tradeStatus` | Integer | 销售单状态 | `1010` (待审核) |
| `tradeTime` | Date | 下单时间 | `2019-06-05 11:26:00` |
| `payTime` | Date | 支付时间 | `2019-06-05 11:26:00` |
| `consignTime` | Date | 发货时间 | `2019-06-05 11:26:00` |
| `auditTime` | Date | 审核时间 | `2019-06-05 11:26:00` |
| `completeTime` | Date | 平台完成时间 | `2020-01-01 00:00:00` |
| `signingTime` | Date | 签收时间 | `2022-01-07 15:30:46` |
| `shopId` | Long | 店铺id | `123` |
| `shopName` | String | 店铺名称 | - |
| `companyName` | String | 公司名称 | - |
| `warehouseName` | String | 仓库名称 | - |
| `logisticName` | String | 物流名称 | - |
| `mainPostid` | String | 物流单号 | `1000111` |
| `sourceTradeNo` | String | 网店主订单号 | `111` |
| `totalFee` | BigDecimal | 商品金额 | `200` |
| `payment` | BigDecimal | 应收金额 | `190` |
| `discountFee` | BigDecimal | 优惠金额 | `10` |
| `sellerMemo` | String | 客服备注 | - |
| `buyerMemo` | String | 客户备注 | - |
| `flagNames` | String | 标记名称 | `冻结` |
| `goodsDetail` | Array | 货品详情数组 | 见下方 `goodsDetail` 结构 |

#### `goodsDetail` 数组内部结构

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `goodsNo` | String | 货品编号 | `goodsDetail.goodsNo` |
| `goodsName` | String | 货品名称 | `goodsDetail.goodsName` |
| `specName` | String | 规格名称 | - |
| `sellCount` | BigDecimal | 数量 | `10` |
| `sellPrice` | BigDecimal | 单价 | `2` |
| `sellTotal` | BigDecimal | 总金额 | `200` |
| `isGift` | Integer | 赠品标记 (0：否；1：是) | `0` |
| `outerId` | String | 外部id (商品编码) | - |
| `goodsPlatDiscountFee` | BigDecimal | 货品平台优惠 | `111` |

### 请求示例 (JSON)

```json
{
  "startModified": "2024-06-05 11:26:00",
  "endModified": "2024-06-11 11:26:00",
  "pageSize": 5,
  "fields": "tradeNo,orderNo,shopName,goodsDetail.goodsNo,scrollId",
  "scrollId": ""
}