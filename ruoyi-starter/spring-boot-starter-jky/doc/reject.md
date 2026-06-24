### oms.open.trade.audit.reject（销售单驳回审核）

- **接口名称**：销售单驳回审核
- **接口类型**：标准接口
- **授权类型**：必须授权
- **最近发布时间**：2024-08-06 16:52:58.0

---

#### 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | oms.open.trade.audit.reject |
| appkey | Long | 是 | 32 | 应用编号 | 16242933 |
| version | String | 是 | 32 | 版本号 | v1.0 |
| contenttype | String | 是 | 32 | 返回格式（暂时只支持json） | json |
| timestamp | String | 是 | 32 | 时间 | 2015-01-01 12:00:00 |
| bizcontent | String | 是 | 32 | 业务参数（详见下方业务请求参数） | {"a":"a1","b":"b1"} |
| contextid | String | 否 | 32 | 上下文报文关联ID（不参与签名） | 131562131 |
| sign | String | 是 | 32 | 签名 | — |
| token | String | 否 | 32 | 自研应用不需要传；ISV应用需先获用户授权获取 | — |

> **说明**：
> - 应用编号查看方式：[点击查看如何创建应用](https://open.jackyun.com/developer/document.html?alias=create_app)
> - 签名生成方式：[点击查看具体签名](https://open.jackyun.com/developer/document.html?alias=create_testtool)
> - Token获取方式：[点击获取token的方法](https://open.jackyun.com/developer/document.html?alias=1)

---

#### 业务请求参数（bizcontent）

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| tradeNos | String | 是 | — | 销售单号 | ["123123","123123"] |
| reason | String | 否 | — | 驳回原因 | 审核错误 |
| isFreezeOrder | String | 否 | 20 | 是否冻结（0：否；1：是） | 1 |

---

#### 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| code | int | 16 | 返回码（200为成功，0为失败） | 200 |
| msg | String | 32 | 返回消息 | [0230041006]未知错误 |
| subCode | String | 32 | 子级编码 | 0230040301 |
| result | JackYunResult | — | 返回结果 | — |
| data | Data | — | 返回业务数据 | — |
| contextId | String | 32 | 上下文编号 | 412815373296750208 |

---

#### 业务返回参数

无业务数据返回。

---

#### 请求业务参数示例（JSON）

```json
{
    "reason": "审核错误",
    "tradeNos": "[\"123123\",\"123123\"]",
    "isFreezeOrder": "1"
}