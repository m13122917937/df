# fin-fbs.billinfo.createcashorcostrecpaybill

**开放平台收付款单创建（支持类型现收收入、支付费用）对外接口**

| 属性 | 内容 |
|---|---|
| 接口名称 | `fin-fbs.billinfo.createcashorcostrecpaybill` |
| 接口说明 | 开放平台收付款单创建（支持类型现收收入、支付费用）对外接口 |
| 授权类型 | 必须授权 |
| 最近发布时间 | 2026-05-09 13:23:18.0 |


## 公共请求参数

| 参数名 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| `method` | String | 是 | 32 | 方法名称 | `fin-fbs.billinfo.createcashorcostrecpaybill` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式（暂时只支持 json） | `json` |
| `timestamp` | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | 32 | 业务参数（JSON 字符串） | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联 ID（不参与签名） | `131562131` |
| `sign` | String | 是 | 32 | 签名 | — |
| `token` | String | 否 | 32 | 自研应用不需要传此参数；ISV 应用需先获取商家授权 token（不参与签名） | — |


## 业务请求参数（bizcontent）

### 顶层参数

| 参数名 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| `billSource` | Integer | 是 | — | 单据来源，2-手工新建 | `2` |
| `companyName` | String | 是 | 100 | 公司名称 | `吉客云` |
| `companyFcCode` | String | 是 | 20 | 公司币种编码 | `CNY` |
| `companyFcName` | String | 是 | 100 | 公司币种名称 | `人民币` |
| `billDate` | String | 是 | 100 | 单据日期 | `2021-04-16` |
| `fcCode` | String | 是 | 20 | 结算账户币种编码 | `CNY` |
| `fcName` | String | 是 | 20 | 结算账户币种名称 | `人民币` |
| `oriBillNum` | String | 否 | 100 | 单据号 | `123` |
| `textLine` | String | 是 | 500 | 摘要 | `摘要123` |
| `tranAmountSum` | BigDecimal | 是 | — | 总金额 | `100` |
| `baseAmountSum` | BigDecimal | 否 | — | 本币总金额，根据获取汇率自动计算 | `100` |
| `fcRate` | BigDecimal | 否 | — | 根据结算账户币种获取单据日期当天汇率 | `1` |
| `settAccId` | Integer | 否 | 11 | 结算账户 ID（ID 和名称必传其一；为多种结算时不传） | `99` |
| `settAccName` | String | 否 | 100 | 结算账户名称（ID 和名称必传其一；为多种结算时不传） | `吉客云` |
| `bankAccount` | String | 否 | 20 | 账户类型编码（若为多种结算，必传且传值 `-1`） | `CASH` |
| `bankAccountName` | String | 否 | 50 | 账户类型名称 | `现金` |
| `unitCode` | String | 否 | 255 | 客户往来单位编码（编码和名称必传其一） | `3090001` |
| `companyCode` | String | 是 | 6 | 公司编码 | `JKY` |
| `companyId` | Long | 否 | — | 公司 ID | `769411337252275200` |
| `billType` | Integer | 是 | 2 | 单据类型：11-收款单，12-付款单 | `12` |
| `isCheckRepeat` | Integer | 否 | 1 | 传值为 1 进行重复性校验拦截创建 | `1` |
| `billTransDetailCreateDtoList` | Array | 是 | — | 业务明细列表（详见下方） | — |
| `bankAccountVOS` | Array | 否 | — | 多种结算详情（详见下方） | — |

### billTransDetailCreateDtoList（业务明细）

| 参数名 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| `trTaxValue` | BigDecimal | 是 | — | 含税金额 | `100` |
| `taxRate` | BigDecimal | 否 | — | 税率（跟税额二者传其一即可） | `0.2` |
| `trTax` | BigDecimal | 否 | — | 税额（跟税率二者传其一即可） | `20` |
| `jd` | Integer | 是 | — | 借贷方向：1-借，2-贷 | `1` |
| `itemCode` | String | 是 | — | 其他收支项目编码 | `DB002` |
| `itemName` | String | 是 | — | 其他收支项目名称 | `大件运费险` |
| `textLine` | String | 否 | 500 | 摘要 | — |
| `custId` | Long | 否 | — | 客户 ID | `849984446059915392` |
| `custName` | String | 否 | — | 客户名称 | — |
| `vendId` | Long | 否 | — | 供应商 ID | `849984446059915392` |
| `vendName` | String | 否 | — | 供应商名称 | — |
| `userId` | Long | 否 | — | 员工 ID | `849984446059915392` |
| `userName` | String | 否 | — | 员工名称 | — |
| `deptId` | Long | 否 | — | 部门 ID | `849984446059915392` |
| `deptName` | String | 否 | — | 部门名称 | — |
| `projId` | Long | 否 | — | 核算项目 ID | `849984446059915392` |
| `projName` | String | 否 | — | 核算项目名称 | — |
| `shopId` | Long | 否 | — | 销售渠道 ID | `849984446059915392` |
| `shopName` | String | 否 | — | 销售渠道名称 | — |
| `sellerId` | Long | 否 | — | 业务员 ID | `1708707322357908992` |
| `seller` | String | 否 | — | 业务员 | `gerwgegwe(323)` |
| `salesId` | Long | 否 | — | 销售团队 ID | `1708707322357908992` |
| `salesName` | String | 否 | — | 销售团队名称 | `zhangl测试` |
| `valueb` | String | 否 | 50 | 销售/采购订单号（超 50 截断） | `xs55555555` |
| `onlineShopTradeNo` | String | 否 | 100 | 网店订单号 | `545wd232222` |

### bankAccountVOS（多种结算详情）

| 参数名 | 类型 | 必填 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|---|
| `bankAccount` | String | 否 | 50 | 账户类型编码 | `CASH` |
| `bankAccountName` | String | 否 | — | 账户类型名称 | `现金` |
| `settAccId` | Integer | 否 | — | 结算账户 ID | `99` |
| `settAccName` | String | 否 | — | 结算账户名称 | `吉客云` |
| `trValue` | BigDecimal | 否 | — | 金额 | `100` |


## 返回参数

### 公共返回参数

| 参数名 | 类型 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|
| `code` | int | 16 | 返回码（200 为成功，0 为失败） | `200` |
| `msg` | String | 32 | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 32 | 子级编码 | `0230040301` |
| `result` | JackYunResult | — | 返回结果 | — |
| `data` | Data | — | 返回业务数据 | — |
| `contextId` | String | 32 | 上下文编号 | `412815373296750208` |

### 业务返回参数（result.data）

| 参数名 | 类型 | 长度 | 描述 | 示例值 |
|---|---|---|---|---|
| `billNum` | String | 20 | 系统单据号 | `YF202103020001` |
| `billStatus` | Integer | 4 | 单据状态：1-草稿，2-待审核，3-已审核，4-待修改，5-已创建凭证 | `2` |
| `id` | Long | 20 | 单据 ID | `1137472001712215296` |
| `billTransDetailCreateVoList` | Array | — | 业务明细返回列表 | — |


## 示例

### 请求业务参数示例（JSON）

```json
{
  "seller": "gerwgegwe(323)",
  "isCheckRepeat": 1,
  "billTransDetailCreateDtoList": [
    {
      "deptName": "-",
      "projId": "849984446059915392",
      "itemCode": "DB002",
      "shopName": "-",
      "itemName": "大件运费险",
      "custId": "849984446059915392",
      "trTax": 20,
      "shopId": "849984446059915392",
      "projName": "-",
      "deptId": "849984446059915392",
      "trTaxValue": 100,
      "textLine": "-",
      "vendName": "-",
      "custName": "-",
      "userName": "-",
      "userId": "849984446059915392",
      "taxRate": 0.2,
      "jd": 1,
      "vendId": "849984446059915392"
    }
  ],
  "companyFcName": "人民币",
  "billSource": 2,
  "companyName": "吉客云",
  "settAccName": "吉客云",
  "shopName": "lxy测试1",
  "fcName": "人民币",
  "sellerId": "1708707322357908992",
  "salesId": "1708707322357908992",
  "fcRate": 1,
  "settAccId": 99,
  "unitCode": "3090001",
  "tranAmountSum": 100,
  "onlineShopTradeNo": "545wd232222",
  "shopId": "1697243904379749888",
  "companyFcCode": "CNY",
  "oriBillNum": "123",
  "bankAccount": "CASH",
  "bankAccountName": "现金",
  "companyCode": "JKY",
  "bankAccountVOS": [
    {
      "settAccName": "吉客云",
      "settAccId": 99,
      "trValue": 100,
      "bankAccount": "CASH",
      "bankAccountName": "现金"
    }
  ],
  "billType": 12,
  "billDate": "2021-04-16",
  "fcCode": "CNY",
  "textLine": "摘要123",
  "baseAmountSum": 100,
  "companyId": "769411337252275200",
  "salesName": "zhangl测试",
  "valueb": "xs55555555"
}