# `wms.order.snreport` (发货单SN通知接口)

## 接口信息

| 属性 | 描述 |
| :--- | :--- |
| **接口名称** | 发货单SN通知接口 |
| **接口地址** | `wms.order.snreport` |
| **授权类型** | 必须授权 |
| **最近发布时间** | 2021-01-19 17:23:37.0 |

## 接口说明

发货单SN（序列号）通知接口，用于将出库单的序列号信息同步至仓储系统。

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `wms.order.snreport` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizdata` | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| `sign` | String | 是 | 32 | 签名 | 见签名生成说明 |
| `token` | String | 否 | 32 | ISV应用授权token(不参与签名) | 见获取token方法 |

## 业务请求参数 (`bizdata`)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `deliveryorderno` | String | 是 | 32 | 出库单号 | `9193457120563834` |
| `deliverytype` | String | 是 | 32 | 出库类型（见下方枚举值说明） | `JH_01` |
| `ownerorderno` | String | 是 | 32 | 外部订单号 | `9193457120563834` |
| `warehousecode` | String | 是 | 32 | 仓库编码 | `04` |
| `ownercode` | String | 是 | 32 | 货主编码 | `04` |
| `outbizcode` | String | 是 | 32 | 外部业务编码 | `04` |
| `operatorcode` | String | 是 | 32 | 操作员编码 | `023` |
| `operatorname` | String | 是 | 32 | 操作员姓名 | `023` |
| `operatetime` | String | 是 | 32 | 操作时间 | `2016-09-08 12:00:00` |
| `goods` | Array | 是 | - | 商品集合 | 见下方 `goods` 结构 |

### `deliverytype` 出库类型枚举

| 值 | 描述 |
| :--- | :--- |
| `JH_01` | 交易出库 |
| `JH_02` | 换货出库 |
| `JH_03` | 补发出库 |
| `JH_04` | 普通出库单 |
| `JH_05` | 调拨出库 |
| `JH_06` | B2B出库 |
| `JH_07` | 采购退货出库 |
| `JH_08` | 其他出库 |
| `JH_09` | 自提出库 |
| `JH_10` | B2C销售订单 |
| `JH_11` | 虚拟出库单 |
| `JH_12` | 唯品出库 |
| `JH_13` | 盘亏出库 |
| `JH_99` | 其他出库 |

### `goods` 数组元素结构

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `outSkuCode` | String | 是 | 32 | 外部系统sku | `23423523132` |
| `name` | String | 是 | 32 | 商品名称 | `茶杯` |
| `itemid` | String | 是 | 32 | 仓储系统商品ID | `448122312341` |
| `unit` | String | 是 | 32 | 单位 | `个` |
| `barcode` | String | 否 | 32 | 商品条形码 | `18888201` |
| `snlist` | Array | 是 | 32 | SN列表（序列号） | `["JH_01","JD_02"]` |

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
| `orderNo` | String | 50 | 发货单号 | `S202004260001` |
| `erporderNo` | String | 50 | 外部关联单号 | `JY0000001` |
| `errorMsg` | String | 100 | 错误信息 | `平台返回失败！` |
| `isSuccess` | Boolean | 10 | 是否成功 | `true` |

## 请求示例 (JSON)

```json
{
  "ownercode": "04",
  "operatetime": "2016-09-08 12:00:00",
  "deliveryorderno": "9193457120563834",
  "goods": [
    {
      "itemid": "448122312341",
      "unit": "个",
      "snlist": ["JH_01", "JD_02"],
      "name": "茶杯",
      "barcode": "18888201",
      "outSkuCode": "23423523132"
    }
  ],
  "deliverytype": "JH_01",
  "operatorcode": "023",
  "ownerorderno": "9193457120563834",
  "warehousecode": "04",
  "outbizcode": "04",
  "operatorname": "023"
}