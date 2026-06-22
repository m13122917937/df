# 查询合同模板列表

> 更新时间：2025-10-29 17:13:57

## 接口描述
查询已创建的合同模板列表。

## 接口地址与请求方法
- **接口地址：** `https://{host}/v3/doc-templates?pageNum=1&pageSize=20`
- **请求方法：** `GET`

## 请求头格式
具体请求头参数，请查看公共请求头格式。

## 请求参数 (Query)
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `pageNum` | int32 | 否 | 查询页码，默认值 `1`。 |
| `pageSize` | int32 | 否 | 每页显示的数量，最大值 `20`，默认值 `20`。 |

## 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息，请根据 `code` 判断错误。 |
| `data` | object | 否 | 业务数据对象 |
| `data.total` | int32 | 否 | 查询结果中模板的总数量。 |
| `data.docTemplates` | array | 否 | 合同模板列表。 |
| `data.docTemplates[].docTemplateId` | string | 否 | 合同模板ID。 |
| `data.docTemplates[].docTemplateName` | string | 否 | 合同模板名称。 |
| `data.docTemplates[].createTime` | int64 | 否 | 模板创建时间（Unix时间戳，单位：毫秒）。 |
| `data.docTemplates[].updateTime` | int64 | 否 | 模板更新时间（Unix时间戳，单位：毫秒）。 |

## 请求示例
```http
GET https://openapi.esign.cn/v3/doc-templates?pageNum=1&pageSize=20