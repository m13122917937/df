# 接口文档：获取机构认证&授权页面链接

**更新时间**：2026-05-08 14:40:10

> **重要提示**：自2024年9月12日起，认证授权涉及权限范围（`authorizedScopes`）部分功能需要购买e签宝高级版或生态合作版本方可支持！

## 1. 接口描述

用于获取组织机构的认证授权页面链接，通过此链接经办人可为组织机构进行实名认证、资源授权等操作。

- 调用前，建议通过【查询机构认证信息】确认企业是否已实名，已实名无需重复操作
- 可接收实名认证通过或授权完成后的回调通知
- 流程详情可通过【查询认证授权流程详情】接口获取

## 2. 接口地址与请求方法

| 项目 | 值 |
| :--- | :--- |
| **接口地址** | `https://{host}/v3/org-auth-url` |
| **请求方法** | `POST` |
| **请求头格式** | 请参考公共请求头格式 |

## 3. 请求参数 (Body)

### 3.1 顶层参数

| 参数名 | 类型 | 必选 | 描述 |
| :--- | :--- | :--- | :--- |
| `orgAuthConfig` | object | **是** | 组织机构认证配置项 |
| `authorizeConfig` | object | 否 | 机构授权配置项（不传则仅为实名认证） |
| `redirectConfig` | object | 否 | 认证完成后的重定向配置 |
| `clientType` | string | 否 | 客户端类型：`ALL`(默认)、`H5`、`PC` |
| `notifyUrl` | string | 否 | 接收回调通知的Web地址 |
| `transactorUseSeal` | boolean | 否 | 非企业管理员是否获取全部印章用印权限，默认`false` |
| `orgIdentityVerify` | boolean | 否 | 企业信息校验，默认`false` |
| `appScheme` | string | 否 | AppScheme，用于唤起指定App |

### 3.2 `orgAuthConfig` 详情

| 参数名 | 类型 | 必选 | 描述 |
| :--- | :--- | :--- | :--- |
| `orgName` | string | 二选一 | 组织机构名称 |
| `orgId` | string | 二选一 | 机构账号ID |
| `orgInfo` | object | 否 | 机构身份附加信息 |
| `orgAuthPageConfig` | object | 否 | 页面认证方式配置 |
| `transactorInfo` | object | 否 | 经办人身份信息 |

#### 3.2.1 `orgInfo` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `orgIDCardNum` | string | 组织机构证件号 |
| `orgIDCardType` | string | 证件类型：`CRED_ORG_USCC`(统一社会信用代码) / `CRED_ORG_REGCODE`(工商注册号) |
| `legalRepName` | string | 法定代表人姓名 |
| `legalRepIDCardNum` | string | 法定代表人证件号 |
| `legalRepIDCardType` | string | 法人证件类型：`CRED_PSN_CH_IDCARD`(身份证) / `CRED_PSN_CH_HONGKONG`(回乡证) / `CRED_PSN_CH_MACAO` / `CRED_PSN_CH_TWCARD` / `CRED_PSN_PASSPORT` |
| `orgBankAccountNum` | string | 企业对公打款银行账户 |

#### 3.2.2 `orgAuthPageConfig` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `orgDefaultAuthMode` | string | 默认认证方式：`ORG_BANK_TRANSFER` / `ORG_ALIPAY_CREDIT` / `ORG_LEGALREP_INVOLVED` |
| `orgAvailableAuthModes` | list | 可选择的认证方式列表 |
| `orgEditableFields` | list | 页面可编辑字段：`orgNum` / `legalRepName` / `orgBankAccountNum` |

#### 3.2.3 `transactorInfo` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `psnId` | string | 经办人账号ID |
| `psnAccount` | string | 经办人账号标识（手机号或邮箱） |
| `psnInfo` | object | 经办人身份信息（姓名、证件号、银行卡号等） |
| `psnIdentityVerify` | boolean | 是否校验身份信息一致性 |

### 3.3 `authorizeConfig` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `authorizedScopes` | list | 授权的权限范围列表 |

**支持的权限范围：**

| 分类 | 可选值 | 说明 |
| :--- | :--- | :--- |
| **账号信息** | `get_org_identity_info`、`get_psn_identity_info` | 获取企业/个人基本信息 |
| **发起签署** | `org_initiate_sign`、`psn_initiate_sign` | 代用户发起合同签署 |
| **资源管理** | `manage_org_member`、`manage_org_seal`、`manage_org_template`、`use_org_template`、`manage_org_resource`、`manage_psn_resource` | 组织/个人资源管理 |
| **合同管理** | `manage_org_contract` | 合同管理权限 |
| **文件存储** | `org_sign_file_storage`、`psn_sign_file_storage` | 合同文件存储 |
| **申请出证** | `apply_org_evidence`、`apply_psn_evidence` | 申请出证权限 |
| **其他** | `org_approval_info`、`use_org_order`、`org_function_benefits` | 审批信息、订单、权益等 |

### 3.4 `redirectConfig` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `redirectUrl` | string | 认证完成后跳转页面地址（需https/http） |
| `redirectDelayTime` | string | 重定向延迟时间（秒），0为直接跳转 |

## 4. 响应参数

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int32 | 业务码，**0 表示成功** |
| `message` | string | 业务信息 |
| `data` | object | 业务数据 |

### 4.1 `data` 详情

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `authUrl` | string | 机构认证授权长链接（有效期30天） |
| `authShortUrl` | string | 机构认证授权短链接（有效期30天） |
| `authFlowId` | string | 本次认证授权流程ID |

## 5. 请求示例

```json
{
    "orgAuthConfig": {
        "orgName": "示例公司",
        "orgInfo": {
            "orgIDCardNum": "9133010****8110212",
            "orgIDCardType": "CRED_ORG_USCC",
            "legalRepName": "法定代表人姓名",
            "legalRepIDCardNum": "110101********1001"
        },
        "orgAuthPageConfig": {
            "orgDefaultAuthMode": "ORG_BANK_TRANSFER"
        },
        "transactorInfo": {
            "psnAccount": "153****0000",
            "psnInfo": {
                "psnName": "经办人姓名",
                "psnMobile": "151****0050"
            }
        }
    },
    "authorizeConfig": {
        "authorizedScopes": ["get_org_identity_info", "get_psn_identity_info"]
    },
    "redirectConfig": {
        "redirectUrl": "https://www.xxx.cn/"
    },
    "clientType": "ALL",
    "notifyUrl": "http://******/notify"
}