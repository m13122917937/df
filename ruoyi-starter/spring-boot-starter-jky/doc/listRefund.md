# `omsapi-business.refund.listRefund` (分页查询网店售后单)

## 接口信息

| 属性 | 描述 |
| :--- | :--- |
| **接口名称** | 分页查询网店售后单 |
| **接口地址** | `omsapi-business.refund.listrefund` |
| **授权类型** | 必须授权 |
| **最近发布时间** | 2026-03-26 14:20:07.0 |

## 接口说明

分页查询网店售后单。

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `omsapi-business.refund.listrefund` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| `sign` | String | 是 | 32 | 签名 | 见签名生成说明 |
| `token` | String | 否 | 32 | ISV应用授权token(不参与签名) | 见获取token方法 |

## 业务请求参数 (`bizcontent`)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `pageInfo` | Object | **是** | - | 分页信息 | `{"pageIndex":0,"pageSize":50}` |
| `pageIndex` | Integer | 是 | - | 页码 | `0` |
| `pageSize` | Integer | 是 | - | 每页个数 | `50` |
| `memberName` | String | **是** | - | 吉客号 | `jackyun` |
| `isQueryCount` | Boolean | 否 | - | 是否查询总数 | `True` |
| `hasQueryHistory` | Integer | 否 | - | 是否查询归档数据(0未归档，1归档) | `0` |
| `hasGoodsReturn` | Array | 否 | - | 退款类型（0 退款；1退货；2换货；3补寄；4维修） | `[0,1]` |
| `refundType` | Integer | 否 | - | 退款类型（0 退款；1退货；2换货；3补寄） | `0` |
| `refundNo` | Array | 否 | - | 网店售后单号列表 | `[""]` |
| `platOrderNo` | Array | 否 | - | 网店订单号列表 | `[""]` |
| `shopId` | Array | 否 | - | 销售渠道Id列表 | `[]` |
| `gmtModifiedBegin` | Date | 否 | - | 更新时间-起始 | `2022-03-29 00:00:00` |
| `gmtModifiedEnd` | Date | 否 | - | 更新时间-结束 | `2022-09-29 23:59:59` |
| `gmtCreateBegin` | String | 否 | 20 | 创建时间(起始) | `2023-12-12 00:00:00` |
| `gmtCreateEnd` | String | 否 | 20 | 创建时间(结束) | `2023-12-24 00:00:00` |
| `createTimeBegin` | Date | 否 | - | 退款申请时间开始 | `2024-02-01 00:00:00` |
| `createTimeEnd` | Date | 否 | - | 退款申请时间结束 | `2024-02-01 00:00:00` |
| `modifiedTimeBegin` | Date | 否 | - | 退款修改时间开始 | `2024-02-01 00:00:00` |
| `modifiedTimeEnd` | Date | 否 | 20 | 退款修改时间结束 | `2024-02-01 00:00:00` |

## 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `code` | int | 16 | 返回码(200为成功，0为失败) | `200` |
| `msg` | String | 32 | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 32 | 子级编码 | `0230040301` |
| `result` | JackYunResult | - | 返回结果对象 | - |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

## 业务返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `count` | Integer | 50 | 总数 | `0` |
| `tradeAfterOnlineDtoArr` | Array | - | 售后单集合 | `[]` |

### `tradeAfterOnlineDTO` (售后单原始单实体) 结构

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `tradeAfterOnlineId` | Long | 20 | 网店售后id | `213124123123` |
| `refundNo` | String | 50 | 网店售后单号 | `1231231412` |
| `platOrderNo` | String | 50 | 平台订单号 | `45123214324` |
| `customerName` | String | 400 | 客户名称 | `客户名称` |
| `customerAccount` | String | 400 | 客户账号 | `客户账号` |
| `refundAmount` | BigDecimal | - | 退款金额 | `20` |
| `platRefundAmount` | BigDecimal | - | 平台补贴 | `10.0` |
| `refundStatus` | String | - | 退款状态 | `success` |
| `refundStatusExplain` | String | - | 退款状态显示 | `退款成功` |
| `refundSuccessTime` | LocalDate | - | 退款完成时间 | `2025-03-01 00:00:00` |
| `refundTimeCreate` | Date | - | 退款申请时间 | `2023-12-12 00:00:00` |
| `refundTimeModified` | Date | - | 退款修改时间 | `2023-12-12 00:00:00` |
| `refundPhase` | Integer | - | 售后阶段 | `1` |
| `refundPhaseExplain` | String | - | 售后阶段显示 | `-` |
| `refundDesc` | String | 500 | 退款描述 | `七天无理由` |
| `reason` | String | 200 | 退款原因 | `七天无理由` |
| `hasGoodsReturn` | Integer | - | 退款类型(0退款 1退货 2换货 3补寄 4维修) | `1` |
| `orderStatus` | String | - | 订单状态 | `success` |
| `orderStatusExplain` | String | - | 订单状态显示 | `已完成` |
| `curStatus` | String | - | 递交状态 | `-` |
| `curStatusExplain` | String | - | 处理状态显示 | `待转入审核` |
| `goodsStatus` | String | - | 商品状态 | `-` |
| `goodsStatusExplain` | String | - | 商品状态显示 | `-` |
| `platName` | String | 50 | 平台类型名称 | `淘宝` |
| `shopId` | String | - | 销售渠道ID | `179871833123` |
| `shopName` | String | 50 | 销售渠道 | `-` |
| `warehouseName` | String | - | 退货仓库 | `-` |
| `logisticName` | String | - | 物流公司名称 | `-` |
| `mainPostid` | String | - | 物流单号 | `-` |
| `province` | String | - | 省 | `-` |
| `city` | String | - | 市 | `-` |
| `district` | String | - | 区 | `-` |
| `town` | String | - | 街道 | `-` |
| `address` | String | 700 | 地址 | `-` |
| `phone` | String | 500 | 电话 | `-` |
| `mobile` | String | 500 | 手机号 | `-` |
| `name` | String | 500 | 买家昵称 | `-` |
| `buyerMemo` | String | - | 客户备注 | `-` |
| `sellerMemo` | String | - | 客服备注 | `-` |
| `sysFlagIds` | String | - | 系统标记集 | `-` |
| `gmtCreate` | Date | - | 创建时间 | `2023-12-12 00:00:00` |
| `gmtModified` | Date | - | 更新时间 | `2023-12-12 00:00:00` |
| `platOrderCreateTime` | LocalDate | - | 订单下单时间 | `2025-03-01 00:00:00` |

### `tradeAfterOnlineGoodsDTOList` (原始单货品集合) 结构

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `goodsId` | String | 50 | 货品id | `3412212` |
| `goodsNo` | String | - | 货品编号 | `-` |
| `outerId` | String | - | 商家编码 | `-` |
| `outerSkuId` | String | - | 子商家编码 | `-` |
| `goodsName` | String | - | 货品名称 | `-` |
| `specName` | String | - | 规格名称 | `-` |
| `tradeGoodsName` | String | - | 交易名称 | `-` |
| `tradeGoodsSpec` | String | - | 交易规格(平台规格名称) | `-` |
| `platSkuId` | String | - | 平台sku编码 | `-` |
| `platGoodsId` | String | - | 平台商品id | `-` |
| `subPlatOrderNo` | String | - | 平台子订单号 | `-` |
| `unit` | String | - | 单位 | `-` |
| `sellCount` | BigDecimal | - | 数量 | `1` |
| `price` | BigDecimal | - | 单价 | `1` |
| `sellTotal` | BigDecimal | - | 金额 | `1` |
| `discountFee` | BigDecimal | - | 优惠 | `1` |
| `isFit` | Integer | - | 组合装标记 | `1` |
| `isGift` | Integer | - | 赠品标记 | `1` |
| `isVirtual` | Integer | - | 虚拟货品标记 | `1` |
| `barcode` | String | - | 条码 | `-` |
| `reasonDesc` | String | - | 售后原因描述 | `-` |
| `type` | String | - | 类型 | `-` |

## 请求示例 (JSON)

```json
{
  "pageInfo": {
    "pageIndex": 0,
    "pageSize": 50
  },
  "isQueryCount": true,
  "memberName": "jackyun",
  "hasQueryHistory": 0,
  "hasGoodsReturn": [0,1],
  "refundType": 0,
  "refundNo": [],
  "platOrderNo": [],
  "shopId": [],
  "gmtModifiedBegin": "2022-03-29 00:00:00",
  "gmtModifiedEnd": "2022-09-29 23:59:59",
  "gmtCreateBegin": "2023-12-12 00:00:00",
  "gmtCreateEnd": "2023-12-24 00:00:00",
  "createTimeBegin": "2024-02-01 00:00:00",
  "createTimeEnd": "2024-02-01 00:00:00",
  "modifiedTimeBegin": "2024-02-01 00:00:00",
  "modifiedTimeEnd": "2024-02-01 00:00:00"
}