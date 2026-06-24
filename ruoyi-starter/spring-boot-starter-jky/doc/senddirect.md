### wms-wos.order.senddirect（直接发货）

- **接口名称**：直接发货
- **接口类型**：标准接口
- **授权类型**：必须授权
- **最近发布时间**：—

---

#### 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | wms-wos.order.senddirect |
| appkey | Long | 是 | 32 | 应用编号 | 16242933 |
| version | String | 是 | 32 | 版本号 | v1.0 |
| contenttype | String | 是 | 32 | 返回格式（暂时只支持json） | json |
| timestamp | String | 是 | 32 | 时间 | 2015-01-01 12:00:00 |
| bizcontent | String | 是 | 32 | 业务参数（详见下方业务请求参数） | {"a":"a1","b":"b1"} |
| contextid | String | 否 | 32 | 上下文报文关联ID（不参与签名） | 131562131 |
| sign | String | 是 | 32 | 签名 | — |
| token | String | 否 | 32 | 自研应用不需要传此参数；ISV应用需先获取商家用户授权（不参与签名） | — |

> **说明**：
> - 应用编号查看方式：[点击查看如何创建应用](https://open.jackyun.com/developer/document.html?alias=create_app)
> - 签名生成方式：[点击查看具体签名](https://open.jackyun.com/developer/document.html?alias=create_testtool)
> - Token获取方式：[点击获取token的方法](https://open.jackyun.com/developer/document.html?alias=1)

---

#### 业务请求参数（bizcontent）

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| — | — | — | — | 暂无业务参数数据 | — |

---

#### 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| code | int | 16 | 返回码（200为成功，0为失败） | 200 |
| msg | String | 32 | 返回消息 | — |
| subCode | String | 32 | 子级编码 | — |
| result | JackYunResult | — | 返回结果 | — |
| data | Data | — | 返回业务数据 | — |
| contextId | String | 32 | 上下文编号 | — |

---

#### 业务返回参数

暂无业务返回参数数据。

---

#### 请求业务参数示例（JSON）

暂无示例数据。

---

#### 响应示例（JSON）

暂无示例数据。

---

#### 异常示例（JSON）

暂无示例数据。