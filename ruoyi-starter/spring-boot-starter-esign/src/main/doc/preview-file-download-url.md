# 下载签署中文件

> 更新时间：2026-01-26 15:11:24

## 接口描述
通过该接口可以下载流程**正在签署中**的文件进行预览查看，用于开发者内部系统展示合同内容。

**重要特性：**
- 下载的文件为过程文件，**不支持验签和出证**。
- 文件中会带有 **“本文档仅供预览查看”** 的水印字样。

## 接口地址与请求方法
- **接口地址：** `https://{host}/v3/sign-flow/{signFlowId}/preview-file-download-url`
- **请求方法：** `GET`

## 请求参数
| 参数名称 | 参数类型 | 必选 | 参数位置 | 参数说明 |
| :--- | :--- | :--- | :--- | :--- |
| `signFlowId` | string | 是 | path | 签署中状态的签署流程ID |
| `docFileId` | string | 是 | query | 本次签署流程中的**文件ID**。<br> **注：** 仅支持签署文件（PDF），不支持附件和OFD文件。 |
| `urlAvailableDate` | int | 否 | query | 下载链接有效期（单位：秒），默认 `3600` 秒。可传入范围：1-3600。 |

## 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息，请根据 `code` 判断错误。 |
| `data` | object | 否 | 业务数据对象 |
| `data.fileId` | string | 否 | 文件ID |
| `data.fileName` | string | 否 | 文件名称 |
| `data.fileDownloadUrl` | string | 否 | 文件下载链接（默认有效期60分钟，过期后可重新获取） |

## 请求示例
```http
GET https://openapi.esign.cn/v3/sign-flow/c57fb***88afb7b97d5/preview-file-download-url?docFileId=a24a7***d2406eb23