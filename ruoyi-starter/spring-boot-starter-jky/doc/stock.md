# ERP Stock Create And Stockin API 文档

## 基本信息

| 项目 | 内容 |
| :--- | :--- |
| **接口名称** | 创建库存并入库 |
| **方法名称** | `erp.stock.createandstockin` |
| **请求方式** | POST |
| **返回格式** | JSON |

---

## 公共请求参数

所有接口请求都需要携带以下公共参数。

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `erp.stock.createandstockin` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式 | `json` |
| `timestamp` | String | 是 | 32 | 请求时间 | `2015-01-01 12:00:00` |
| `sign_param` | Object | 是 | - | 业务参数（JSON格式） | 见下方业务参数 |
| `sign` | String | 是 | 32 | 签名 | 见签名算法文档 |
| `contextid` | String | 否 | 32 | 上下文关联ID（不参与签名） | `131562131` |
| `token` | String | 否 | 32 | ISV应用授权令牌（不参与签名） | 自研应用无需传递 |

---

## 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `code` | int | 16 | 返回码（200=成功，0=失败） | `200` |
| `msg` | String | 32 | 返回消息 | `success` |
| `subCode` | String | 32 | 子级错误码 | `0230040301` |
| `result` | Object | - | 返回结果对象 | - |
| `data` | Object | - | 业务返回数据 | 见下方业务返回参数 |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

---

## 业务请求参数

> ⚠️ **说明**：以下参数为 `sign_param` 字段的实际内容。由于原文档未提供具体参数，请根据实际业务需求补充。

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `[参数名待补充]` | `[类型待补充]` | `[是/否]` | `[描述待补充]` | `[示例待补充]` |

### 请求示例（JSON）

```json
{
    "method": "erp.stock.createandstockin",
    "appkey": 16242933,
    "version": "v1.0",
    "contenttype": "json",
    "timestamp": "2015-01-01 12:00:00",
    "sign_param": {
        "warehouse_code": "WH001",
        "goods_code": "G12345",
        "quantity": 100,
        "batch_no": "BATCH20230601"
    },
    "sign": "ABCD1234EFGH5678",
    "contextid": "131562131",
    "token": ""
}