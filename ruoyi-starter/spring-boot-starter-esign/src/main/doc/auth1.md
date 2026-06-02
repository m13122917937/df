# 接口文档：查询机构认证信息

**更新时间**：2025-12-15 20:24:24

## 1. 接口描述

查询机构实名认证信息。

**注意事项**：入参中 `orgId`、`orgName` 和 `orgIDCardNum` 三个参数**只选择一个传入**即可查询机构认证信息。

**查询优先级**：`orgId` > `orgName` > `orgIDCardNum`

## 2. 接口地址与请求方法

| 项目 | 值 |
| :--- | :--- |
| **接口地址** | `https://{host}/v3/organizations/identity-info` |
| **请求方法** | `GET` |
| **请求头格式** | 请参考公共请求头格式 |

## 3. 请求参数 (Query)

| 参数名 | 类型 | 必选 | 参数位置 | 描述 |
| :--- | :--- | :--- | :--- | :--- |
| `orgId` | string | 否 | query | 机构账号ID |
| `orgName` | string | 否 | query | 组织机构名称 |
| `orgIDCardNum` | string | 否 | query | 组织机构证件号 |
| `orgIDCardType` | string | 否 | query | 组织机构证件类型（传 `orgIDCardNum` 时**必传**）<br>- `CRED_ORG_USCC` - 统一社会信用代码<br>- `CRED_ORG_REGCODE` - 工商注册号 |

## 4. 响应参数

### 4.1 顶层参数

| 参数名 | 类型 | 必选 | 描述 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，**0 表示成功**，非0表示异常 |
| `message` | string | 否 | 业务信息（请根据 code 判断错误，不应依赖 message） |
| `data` | object | 否 | 业务数据 |

### 4.2 `data` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `realnameStatus` | int32 | 实名认证状态：`0` - 未实名，`1` - 已实名 |
| `authorizeUserInfo` | boolean | 是否授权身份信息给当前应用：`true` - 已授权，`false` - 未授权<br>**【注】** 需要授权 `get_org_identity_info` 权限并完成授权操作 |
| `orgId` | string | 机构账号ID |
| `orgName` | string | 机构名称 |
| `orgAuthMode` | string | 机构实名认证完成时使用的认证方式（取最近一次）<br>可选值：`ORG_BANK_TRANSFER`(对公打款) / `ORG_LEGALREP_AUTHORIZATION`(法人授权) / `ORG_LEGALREP`(法人本人) / `ORG_LEGALREP_WILLINGNESS`(法人意愿) / `ORG_ALIPAY_QUICK`(法人快捷) / `ORG_ALIPAY_CREDIT`(企业支付宝) / `ORG_MANUAL`(人工审核) / `ORG_OTHER`(其他) |
| `orgInfo` | object | 机构认证信息（详见下方） |
| `adminName` | string | 机构管理员姓名（**脱敏显示**） |
| `adminAccount` | string | 机构管理员联系方式（**脱敏显示**，优先返回手机号） |

### 4.3 `orgInfo` 详情

> **注意**：以下字段在机构已实名状态下才会返回，部分敏感字段需要授权 `get_org_identity_info` 权限。

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `orgIDCardNum` | string | 组织机构证件号 |
| `orgIDCardType` | string | 证件号类型：`CRED_ORG_USCC` / `CRED_ORG_REGCODE` |
| `legalRepName` | string | 法定代表人姓名 |
| `legalRepIDCardNum` | string | 法定代表人证件号（**需授权**） |
| `legalRepIDCardType` | string | 法人证件类型：`CRED_PSN_CH_IDCARD`(身份证) / `CRED_PSN_CH_HONGKONG`(回乡证) / `CRED_PSN_CH_MACAO` / `CRED_PSN_CH_TWCARD` / `CRED_PSN_PASSPORT`（**需授权**） |
| `corporateAccount` | string | 机构对公账户名称（仅对公打款认证返回，**需授权**） |
| `orgBankAccountNum` | string | 机构对公打款银行卡号（仅对公打款认证返回，**需授权**） |
| `cnapsCode` | string | 机构对公打款银行联行号（**需授权**） |
| `authorizationDownloadUrl` | string | 对公打款单位实名认证授权委托书文件下载地址<br>**注**：有效期1小时，文件保存180天（**需授权**） |
| `licenseDownloadUrl` | string | 机构营业执照照片文件下载地址<br>**注**：需开通OCR上传功能，有效期1小时（**需授权**） |
| `adminName` | string | 机构管理员姓名（**脱敏显示**） |
| `adminAccount` | string | 机构管理员联系方式（**脱敏显示**） |

## 5. 请求示例

```http
GET https://openapi.esign.cn/v3/organizations/identity-info?orgName=xxxx企业