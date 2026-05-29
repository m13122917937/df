# `wms-wos.sendOrderUpdateLogisticInfo` (发货单完成并且更新物流信息接口)

## 接口信息

| 属性 | 描述 |
| :--- | :--- |
| **接口名称** | 发货单完成并且更新物流信息接口 |
| **接口地址** | `wms-wos.sendorderupdatelogisticinfo` |
| **授权类型** | 必须授权 |
| **最近发布时间** | 2026-01-13 16:22:24.0 |

## 接口说明

发货单完成并且更新物流信息接口。该接口用于在发货流程完成后，将物流单号、物流公司等信息同步至系统。

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `wms-wos.sendorderupdatelogisticinfo` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| `sign` | String | 是 | 32 | 签名 | 见签名生成说明 |
| `token` | String | 否 | 32 | ISV应用授权token(不参与签名) | 见获取token方法 |

## 业务请求参数 (`bizcontent`)

**注意**：`bizcontent` 是一个包含 `bizdata` 数组的对象，`bizdata` 中的每个元素代表一个需要更新物流信息的发货单。

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `bizdata` | Array | **是** | - | 请求参数数组，支持批量更新 | `[{"orderNo":"FH2404100009","logisticNo":"sf1232142","logisticName":"嘉里物流","logisticCode":""}]` |

### `bizdata` 数组元素结构

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `orderNo` | String | **是** | 50 | 发货单号 | `FH20240418002` |
| `logisticNo` | String | **是** | 50 | 物流单号 | `YT1232142` |
| `logisticName` | String | **是** | 50 | 物流公司名称（必须是【物流档案】中的物流公司列名称，**不是别名**） | `圆通速递` |
| `logisticCode` | String | 否 | 50 | 物流公司编号 | `YT0209` |

## 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `code` | int | 16 | 返回码(200为成功，0为失败) | `200` |
| `msg` | String | 32 | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 32 | 子级编码 | `0230040301` |
| `result` | JackYunResult | - | 返回结果对象 | - |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

## 业务返回参数 (`result.data`)

返回值是一个数组，每个元素对应一个请求的发货单处理结果。

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `isSuccess` | Boolean | - | 是否成功 | `false` |
| `error` | String | 50 | 失败原因（成功时通常为空） | `订单已完成` |
| `orderNo` | String | 50 | 发货单号 | `FH2404070034` |

## 请求示例 (JSON)

```json
{
  "bizdata": [
    {
      "orderNo": "FH20240418002",
      "logisticNo": "YT1232142",
      "logisticName": "圆通速递",
      "logisticCode": "YTO"
    },
    {
      "orderNo": "FH2404100009",
      "logisticNo": "sf1232142",
      "logisticName": "嘉里物流",
      "logisticCode": ""
    }
  ]
}