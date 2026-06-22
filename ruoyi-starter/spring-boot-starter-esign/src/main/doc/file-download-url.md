
---

### 文件2: 下载已签署文件及附属材料.md

```markdown
# 下载已签署文件及附属材料

> 更新时间：2026-03-11 15:22:20

## 接口描述
流程**结束（签署完成）**后，获取签署完成的文件以及相关附属材料的下载链接。

**重要说明：**
- 流程未签署完成时调用会报错：`"流程非签署完成状态，不允许下载文档"`。
- 自 2025年12月11日 起，推荐使用新的 `POST` 请求方式，原有 `GET` 方式保留但不再推荐。

---

## 请求方式一：POST（推荐）

### 接口地址与请求方法
- **接口地址：** `https://{host}/v3/sign-flow/{signFlowId}/file-download-url`
- **请求方法：** `POST`

### 请求头格式
具体请求头参数，请查看公共请求头格式。

### 请求参数 (Body)
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `signFlowId` | string | 是 | (path) 已完成状态的签署流程ID |
| `urlAvailableDate` | int | 否 | 下载链接有效期（秒），默认 `3600`。可传入：1-3600。 |
| `internalUrl` | boolean | 否 | 是否内网地址，默认 `false`（外网）。仅专属云产品使用。 |
| `aesEncrypt` | boolean | 否 | 是否使用AES加密文件，默认 `false`。加密后需通过回调获取下载地址。 |
| `rsaSecret` | string | 否 | 文件加密时使用的RSA公钥（base64编码）。需配合 `rsaSecretKey` 使用。 |
| `rsaSecretKey` | string | 否 | RSA公钥版本标识，传入 `rsaSecret` 时必传。 |

### 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息。 |
| `data` | object | 否 | 业务数据 |
| `data.files` | array | 否 | 签署文件信息列表 |
| `data.files[].fileId` | string | 否 | 签署文件ID |
| `data.files[].fileName` | string | 否 | 签署文件名称 |
| `data.files[].downloadUrl` | string | 否 | 已签署文件下载链接 |
| `data.attachments` | array | 否 | 附属材料信息列表 |
| `data.attachments[].fileId` | string | 否 | 附属材料文件ID |
| `data.attachments[].fileName` | string | 否 | 附属材料文件名称 |
| `data.attachments[].downloadUrl` | string | 否 | 附属材料文件下载链接 |
| `data.certificateDownloadUrl` | string | 否 | 海外签证书报告下载地址（中国大陆不返回） |
| `data.aesSecret` | string | 否 | 加密后的密钥（根据加密方式返回） |

### 请求示例
```http
POST https://openapi.esign.cn/v3/sign-flow/b2cb7**3cc54/file-download-url