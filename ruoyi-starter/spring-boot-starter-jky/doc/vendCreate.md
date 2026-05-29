# `erp.vend.create` (供应商创建)

## 接口信息

| 属性 | 描述 |
| :--- | :--- |
| **接口名称** | 供应商创建 |
| **接口地址** | `erp.vend.create` |
| **授权类型** | 必须授权 |
| **最近发布时间** | 2025-05-08 09:09:55.0 |

## 接口说明

开放平台供应商创建对外接口，用于在ERP系统中创建新的供应商档案。

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `erp.vend.create` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| `sign` | String | 是 | 32 | 签名 | 见签名生成说明 |
| `token` | String | 否 | 32 | ISV应用授权token(不参与签名) | 见获取token方法 |

## 业务请求参数 (`bizcontent`)

### 基本信息

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `code` | String | **是** | 50 | 供应商编码 | `DF001123` |
| `name` | String | 否 | 200 | 供应商名称 | `笛佛123` |
| `abbreviation` | String | 否 | 200 | 简称 | `徽商` |
| `classCode` | String | 否 | 10 | 分类编号 | `BMG国内` |
| `className` | String | **是** | 16 | 分类名称 | `BMG国内` |
| `levelCode` | String | 否 | - | 级别（01：一级，02：二级，03：三级） | `01` |
| `assistantCode` | String | 否 | 200 | 助记码 | `DF` |
| `leader` | String | 否 | 50 | 负责人 | `liuyi` |
| `developDate` | Date | 否 | - | 发展日期 | `2025-12-20` |
| `businessScope` | String | 否 | 255 | 经营范围 | `软件` |
| `memo` | String | 否 | 500 | 备注 | `这是供应商备注` |
| `flagData` | String | 否 | 255 | 标记Id集合（多个用逗号分隔） | `1,2` |

### 联系信息

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `countryName` | String | 否 | 50 | 国家 | `中国` |
| `provinceName` | String | 否 | 50 | 省 | `浙江省` |
| `cityName` | String | 否 | 50 | 市 | `杭州市` |
| `townName` | String | 否 | 50 | 区 | `西湖区` |
| `streetName` | String | 否 | 50 | 街道名称 | `三墩镇` |
| `address` | String | 否 | 200 | 地址 | `西园路10号` |
| `postcode` | String | 否 | 10 | 邮编 | `310000` |
| `email` | String | 否 | 30 | 电子邮件 | `123@qq.com` |
| `website` | String | 否 | 500 | 网址 | `www.wdgj.com` |
| `tel` | String | 否 | 50 | 电话 | `13656895698` |
| `fax` | String | 否 | 20 | 传真 | `fax.com` |

### `vendLinkmanList` (供应商联系人列表)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `linkman` | String | **是** | 30 | 联系人姓名 | `李小路` |
| `position` | String | 否 | 50 | 职务 | `经理` |
| `linktel` | String | 否 | 30 | 联系电话 | `13526268989` |
| `qq` | String | 否 | 30 | QQ号 | `95221582` |
| `wechat` | String | 否 | 30 | 微信号 | `去年买了个表` |
| `email` | String | 否 | 30 | 电子邮件 | `96645@sina.com` |
| `memo` | String | 否 | 30 | 备注 | `这是联系人备注` |

### `vendPayAccountList` (供应商收款账户列表)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `accountTypeName` | String | **是** | 50 | 账户类型名称 | `银行` |
| `bankbranch` | String | **是** | 100 | 收款开户行 | `广州银行` |
| `bankacct` | String | **是** | 100 | 银行账号 | `123261378216786346328` |
| `accName` | String | **是** | 50 | 账户名称 | `剩下三个` |
| `bankName` | String | 否 | - | 所属银行 | `招商银行` |
| `isDefault` | Integer | 否 | - | 是否默认账户（0：否，1：是） | `0` |

### `vendPurchUserList` (采购员列表)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | Long | 否 | - | 采购员主键 | `1111` |
| `companyId` | Long | 否 | - | 公司id | `1519603033279497600` |
| `companyName` | String | 否 | - | 公司名称 | `EC分销公司` |
| `purchUserId` | Long | 否 | - | 采购员id | `1520171974907036800` |
| `purchUserName` | String | 否 | - | 采购员名字 | `于池池大美女` |
| `purchOfficeName` | String | 否 | - | 采购办公室名称 | `医药健康` |
| `isDefault` | Integer | 否 | - | 是否默认采购员（0：否，1：是） | `1` |

### `vendSellerList` (销方主体列表)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `sellerName` | String | 否 | - | 销方名称 | `123` |
| `sellerTaxCode` | String | 否 | - | 销方税号 | `销方税号` |
| `sellerBankAccount` | String | 否 | - | 销方银行账户 | `99999` |
| `sellerAddressPhone` | String | 否 | - | 销方地址电话 | `123123` |
| `isDefault` | Integer | 否 | - | 是否默认账户（0：否，1: 是） | `0` |

## 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `code` | int | 16 | 返回码(200为成功，0为失败) | `200` |
| `msg` | String | 32 | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 32 | 子级编码 | `0230040301` |
| `result` | JackYunResult | - | 返回结果对象 | - |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

## 业务返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `vendId` | String | 20 | 创建成功的供应商ID | `397503922165638145` |

## 请求示例 (JSON)

```json
{
  "code": "DF001123",
  "name": "笛佛123",
  "abbreviation": "徽商",
  "className": "BMG国内",
  "classCode": "BMG国内",
  "levelCode": "01",
  "assistantCode": "DF",
  "leader": "liuyi",
  "developDate": "2025-12-20",
  "businessScope": "软件",
  "memo": "这是供应商备注",
  "flagData": "1,2",
  "countryName": "中国",
  "provinceName": "浙江省",
  "cityName": "杭州市",
  "townName": "西湖区",
  "streetName": "三墩镇",
  "address": "西园路10号",
  "postcode": "310000",
  "email": "123@qq.com",
  "website": "www.wdgj.com",
  "tel": "13656895698",
  "fax": "fax.com",
  "vendLinkmanList": [
    {
      "linkman": "李小路",
      "position": "经理",
      "linktel": "13526268989",
      "qq": "95221582",
      "wechat": "去年买了个表",
      "email": "96645@sina.com",
      "memo": "这是联系人备注"
    }
  ],
  "vendPayAccountList": [
    {
      "accountTypeName": "银行",
      "bankbranch": "广州银行",
      "bankacct": "123261378216786346328",
      "accName": "剩下三个",
      "bankName": "招商银行",
      "isDefault": 0
    }
  ],
  "vendPurchUserList": [
    {
      "companyId": "1519603033279497600",
      "companyName": "EC分销公司",
      "purchUserId": "1520171974907036800",
      "purchUserName": "于池池大美女",
      "purchOfficeName": "医药健康",
      "isDefault": 1
    }
  ],
  "vendSellerList": [
    {
      "sellerName": "123",
      "sellerTaxCode": "销方税号",
      "sellerBankAccount": "99999",
      "sellerAddressPhone": "123123",
      "isDefault": 0
    }
  ]
}