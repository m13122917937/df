# 填写模板生成文件

> 更新时间：2025-06-18 09:29:42

## 接口描述
基于合同模板编号（`docTemplateId`）和模板中的控件，填充自定义内容，最终生成一份PDF文件。

**注意事项：**
- 使用HTML动态模板时，填充的表格行数**不能超过2000行**（性能限制）。
- HTML模板填充完样式可能产生变化，不能保证与设计稿完全一致。

## 接口地址与请求方法
- **接口地址：** `https://{host}/v3/files/create-by-doc-template`
- **请求方法：** `POST`

## 请求头格式
具体请求头参数，请查看公共请求头格式。

## 请求参数 (Body)
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `docTemplateId` | string | 是 | 待填充的模板ID（通过【获取制作合同模板页面】接口获取） |
| `fileName` | string | 是 | 填充后生成的文件名称。<br> **注：**<br> 1. 不可含有 `\ / : * " < > | ？` 及所有emoji表情。<br> 2. 长度不超过100字符。 |
| `components` | array | 是 | 控件列表，**`componentId` 和 `componentKey` 二选一传值**。 |
| `components[].componentId` | string | 否 | 控件ID（设置模板时由系统自动生成）。 |
| `components[].componentKey` | string | 否 | 控件Key（设置模板时由用户自定义）。 |
| `components[].componentValue` | string | 否 | 控件填充值。<br> **补充说明：**<br> 1. 根据控件类型进行填充，[点击查看填充值示例](示例链接)。<br> 2. 填充动态表格控件时，若需新增一行数据，`insertRow` 参数必须传 `true`。<br> 3. [点击查看如何填充动态表格](示例链接)。 |
| `requiredCheck` | boolean | 否 | 是否校验PDF模板中必填控件，默认 `false`。<br> - `false`：不校验，`components` 可传空数组。<br> - `true`：校验，必填控件未传值会报错 `"创建合同失败: 'XX控件名称'填充内容缺失"`。<br> **注：** 该参数仅对**PDF模板**生效。**HTML模板会强制校验必填控件。** |

## 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息，请根据 `code` 判断错误。 |
| `data` | object | 否 | 业务数据对象 |
| `data.fileId` | string | 否 | 填充后生成的文件ID。 |
| `data.fileDownloadUrl` | string | 否 | 文件下载地址（默认有效期60分钟，过期后可重新获取）。<br> **注：**<br> - 填充**PDF模板**时，返回文件下载地址。<br> - 填充**HTML模板**时，默认返回 `null`。如需获取地址，建议调用【查询文件上传状态】接口，传入返回的 `fileId`。 |

## 请求示例
```json
{
    "docTemplateId": "8726f6b***03a56d",
    "fileName": "某公司的交易协议签署文件",
    "components": [
        {
            "componentId": "59af7766***36ef41b",
            "componentKey": "",
            "componentValue": "这里是填充的文本"
        },
        {
            "componentId": "7315e9af**72d2dac40",
            "componentKey": "",
            "componentValue": "2022/01/01"
        }
    ]
}