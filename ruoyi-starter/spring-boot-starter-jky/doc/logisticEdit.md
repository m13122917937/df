### oms.order.logistic.edit（物流信息回传）

- **接口名称**：物流信息回传
- **接口类型**：定制接口
- **授权类型**：非必须授权
- **最近发布时间**：2024-11-28 18:47:47.0

> **说明**：代发货待递交的时候才可以修改。

---

#### 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | oms.order.logistic.edit |
| appkey | Long | 是 | 32 | 应用编号 | 16242933 |
| version | String | 是 | 32 | 版本号 | v1.0 |
| contenttype | String | 是 | 32 | 返回格式（暂时只支持json） | json |
| timestamp | String | 是 | 32 | 时间 | 2015-01-01 12:00:00 |
| bizcontent | String | 是 | 32 | 业务参数（详见下方业务请求参数） | {"a":"a1","b":"b1"} |
| contextid | String | 否 | 32 | 上下文报文关联ID（不参与签名） | 131562131 |
| sign | String | 是 | 32 | 签名 | — |
| token | String | 否 | 32 | 自研应用不需要传此参数；ISV应用必须先得到商家用户授权，获取相关token信息后传递（不参与签名） | — |

> **说明**：
> - 应用编号查看方式：[点击查看如何创建应用](https://open.jackyun.com/developer/document.html?alias=create_app)
> - 签名生成方式：[点击查看具体签名](https://open.jackyun.com/developer/document.html?alias=create_testtool)
> - Token获取方式：[点击获取token的方法](https://open.jackyun.com/developer/document.html?alias=1)

---

#### 业务请求参数（bizcontent）

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| logisticInfoList | Array | 是 | — | 物流信息回传列表 | — |
| tradeNo | String | 是 | — | 订单编号 | JY202012281051 |
| logisticId | Long | 是 | — | 物流公司ID | 1056279953918401664 |
| logisticName | String | 是 | — | 物流公司名称 | 测试物流公司 |
| mainPostid | String | 是 | — | 运单号 | 5454777777 |
| extraLogisticNo | Array | 否 | — | 额外物流单号（注意：传入额外物流后，会清空之前的额外物流信息，重新保存新的额外物流） | ["78787","345345"] |

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

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| isSuccess | Boolean | — | 是否成功，全部成功才为成功，即failedResults数组为空时为true | TRUE |
| failedResults | Array | — | 取消订单处理失败结果 | — |
| tradeNo | String | — | 销售订单号 | 101999000000 |
| msg | String | — | 订单处理失败消息 | 未找到销售订单信息 |

---

#### 请求业务参数示例（JSON）

```json
{
    "logisticInfoList": [
        {
            "logisticId": "1056279953918401664",
            "logisticName": "测试物流公司",
            "tradeNo": "JY202012281051",
            "extraLogisticNo": "[\"78787\",\"345345\"]",
            "mainPostid": "5454777777"
        }
    ]
}