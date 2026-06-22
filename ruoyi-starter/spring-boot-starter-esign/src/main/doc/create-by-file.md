# （精简版）基于文件发起签署

> 更新时间：2025-12-24 10:00:11

## 接口描述
开发者可基于**已上传的合同文件**或**由模板填充生成的文件**来发起签署流程。

本接口为《基于文件发起签署》的精简版，与完整版为**同一接口**，但聚焦于开发者常用参数，以降低接入复杂度。

### 注意事项
1.  **签署文件（docs）限制**：
    - 数量：单个流程不超过 **50个**。
    - 大小：单个文件 **≤ 50MB**，文件内单页 **≤ 20MB**，所有文件总和 **≤ 500MB**。
2.  **签署方（signers）限制**：一次性添加不超过 **10个**，整个流程不超过 **50个**。
3.  **签署区（signFields）限制**：单个流程不超过 **300个**。

## 接口地址与请求方法
- **接口地址：** `https://{host}/v3/sign-flow/create-by-file`
- **请求方法：** `POST`

## 请求头格式
具体请求头参数，请查看公共请求头格式。

## 请求参数 (Body)

| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `docs` | array | 否 | 待签署文件信息列表。可传入多个文件ID。 |
| `docs[].fileId` | string | 是 | 待签署文件ID。 |
| `docs[].fileName` | string | 否 | 文件名称（需含后缀名，如 ".pdf"）。<br> **注：** 不可含 `\ / : * " < > | ？` 及emoji，长度 ≤ 100字符。 |
| `signFlowConfig` | object | 是 | 签署流程配置项。 |
| `...signFlowTitle` | string | 是 | 签署流程主题（展示在通知和签署页）。不可含特殊字符。 |
| `...signFlowExpireTime` | int64 | 否 | 签署截止时间（Unix毫秒时间戳）。默认创建后90天，最大不超过90天。 |
| `...autoFinish` | boolean | 否 | 是否自动完结，默认 `false`。设为 `true` 后不可追加签署区和抄送方。 |
| `...notifyUrl` | string | 否 | 接收回调通知的Web地址。 |
| `...noticeConfig` | object | 否 | 通知配置。 |
| `......noticeTypes` | string | 否 | 通知方式：`""`(不通知，默认)、`"1"`(短信)、`"2"`(邮件)、`"3"`(钉钉)、`"5"`(微信)、`"6"`(企微)、`"7"`(飞书)。可组合，如 `"1,2"`。 |
| `......examineNotice` | boolean | 否 | 是否通知用印审批人员（短信+邮件+站内信），默认按 `noticeTypes`。 |
| `...redirectConfig` | object | 否 | 重定向配置。 |
| `......redirectUrl` | string | 否 | 签署完成后跳转地址（需为 `http/https` 协议，且域名需提前配置）。 |
| `signers` | array | 否 | 签署方信息列表。单个流程不超过10个。 |
| `signers[].signConfig` | object | 否 | 签署人配置。 |
| `......signOrder` | int32 | 否 | 签署顺序，值小者先签，值相同为同时签。 |
| `......forcedReadingTime` | int32 | 否 | 页面强制阅读倒计时（秒，最大999）。 |
| `signers[].signerType` | int32 | 是 | **签署方类型**：`0`-个人，`1`-企业/机构，`2`-法定代表人，`3`-经办人。 |
| `signers[].orgSignerInfo` | object | 否 | **企业/机构签署方信息**（当 `signerType` 为 `1` 或 `3` 且手动签时必传）。 |
| `......orgName` | string | 是 | 企业/机构名称。 |
| `......orgInfo` | object | 否 | 企业证件信息（展示在认证页面）。 |
| `.........orgIDCardNum` | string | 否 | 企业证件号。 |
| `.........orgIDCardType` | string | 否 | 证件类型：`CRED_ORG_USCC`(统一代码)，`CRED_ORG_REGCODE`(注册号)。 |
| `......transactorInfo` | object | 否 | **经办人信息**（企业手动签时必传，自动落章时不传）。 |
| `.........psnAccount` | string | 是 | 经办人账号标识（手机号或邮箱）。 |
| `.........psnInfo` | object | 否 | 经办人身份信息。 |
| `............psnName` | string | 是 | 经办人姓名。 |
| `............psnIDCardNum` | string | 否 | 经办人证件号。 |
| `............psnIDCardType` | string | 否 | 证件类型，默认为 `CRED_PSN_CH_IDCARD`（身份证）。 |
| `signers[].psnSignerInfo` | object | 否 | **个人签署方信息**（当 `signerType` 为 `0` 时必传）。 |
| `......psnAccount` | string | 是 | 个人账号标识（手机号或邮箱）。 |
| `......psnInfo` | object | 否 | 个人身份信息。 |
| `.........psnName` | string | 是 | 个人姓名。 |
| `.........psnIDCardNum` | string | 否 | 个人证件号。 |
| `.........psnIDCardType` | string | 否 | 证件类型，默认为 `CRED_PSN_CH_IDCARD`。 |
| `signers[].signFields` | array | 是 | **签署区信息列表**（指定签署方时必须传入）。单个流程不超过300个。 |
| `......fileId` | string | 是 | 签署区所在的文件ID（需已在 `docs` 中）。 |
| `......customBizNum` | string | 否 | 自定义业务编号，回调时原样返回。 |
| `......normalSignFieldConfig` | object | 是 | 签章区配置。 |
| `.........freeMode` | boolean | 否 | 是否自由签章（不限制印章、位置等），默认 `false`。 |
| `.........autoSign` | boolean | 否 | 是否后台自动落章，默认 `false`。<br> **注：** 个人不支持，机构自动落章需印章授权。 |
| `.........assignedSealId` | string | 否 | 指定印章ID。不指定则用默认印章。 |
| `.........signFieldSize` | float | 否 | 签章区尺寸（正方形边长，单位px）。不指定则用印章原始大小。 |
| `.........signFieldStyle` | int32 | 否 | 签章样式：`1`-单页签章，`2`-骑缝签章。 |
| `.........signFieldPosition` | object | 否 | 签章区位置信息。 |
| `............acrossPageMode` | string | 否 | 骑缝章模式：`ALL`(全部页)，`AssignedPages`(指定页)。 |
| `............positionPage` | string | 否 | 页码。单页签章传单页；骑缝章指定页时可用 `1-3,6` 格式。 |
| `............positionX` | float | 否 | X坐标（骑缝章时不生效）。 |
| `............positionY` | float | 否 | Y坐标（骑缝章时不生效）。 |

## 响应参数
| 参数名称 | 参数类型 | 必选 | 参数说明 |
| :--- | :--- | :--- | :--- |
| `code` | int32 | 是 | 业务码，`0` 表示成功。 |
| `message` | string | 否 | 业务信息。 |
| `data` | object | 否 | 业务数据。 |
| `data.signFlowId` | string | 否 | 签署流程ID（建议保存）。 |

## 请求示例
以下示例为**个人用户**与**平台自身（应用所属企业）** 两方签署场景。
```json
{
    "docs": [
        {
            "fileId": "55607bb*****702b5f92ed565",
            "fileName": "xx企业劳动合同.pdf"
        }
    ],
    "signFlowConfig": {
        "signFlowTitle": "企业员工劳动合同签署",
        "signFlowExpireTime": 169111118000,
        "autoFinish": true,
        "notifyUrl": "http://xxx/asyn/notify",
        "redirectConfig": {
            "redirectUrl": "http://www.xx.cn/"
        }
    },
    "signers": [
        {
            "signConfig": { "signOrder": 1 },
            "noticeConfig": { "noticeTypes": "1" },
            "signerType": 0,
            "psnSignerInfo": {
                "psnAccount": "15****50",
                "psnInfo": { "psnName": "张三" }
            },
            "signFields": [
                {
                    "customBizNum": "自定义编码001",
                    "fileId": "55607bb5*******ed565",
                    "normalSignFieldConfig": {
                        "signFieldStyle": 1,
                        "signFieldPosition": {
                            "positionPage": "1",
                            "positionX": 200,
                            "positionY": 200
                        }
                    }
                }
            ]
        },
        {
            "signConfig": { "signOrder": 2 },
            "signerType": 1,
            "signFields": [
                {
                    "customBizNum": "自定义编码002",
                    "fileId": "55607b********2ed565",
                    "normalSignFieldConfig": {
                        "autoSign": true,
                        "signFieldStyle": 1,
                        "signFieldPosition": {
                            "positionPage": "1",
                            "positionX": 458,
                            "positionY": 200
                        }
                    }
                }
            ]
        }
    ]
}