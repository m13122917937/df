# ERP API 文档：erp.vend.get (供应商查询)

## 接口说明

- **接口名称**: `erp.vend.get`
- **接口描述**: 开放平台供应商查询接口
- **授权类型**: 必须授权
- **最近发布时间**: 2025-10-29 17:30:41.0
- **请求方式**: `POST`
- **返回格式**: JSON

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `erp.vend.get` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间戳 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | - | 业务参数，JSON格式字符串 | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID（不参与签名） | `131562131` |
| `sign` | String | 是 | 32 | 签名 | [点击查看签名方法] |
| `token` | String | 否 | 32 | ISV应用用户授权token（不参与签名） | [获取token方法] |

## 业务请求参数 (bizcontent)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `pageIndex` | Integer | 否 | 页码（默认0） | `0` |
| `pageSize` | Integer | 是 | 每页记录数（默认50） | `50` |
| `code` | String | 否 | 供应商编号，模糊查询 | `1` |
| `codes` | String | 否 | 供应商编号，逗号分隔，精准查询 | `1,2` |
| `name` | String | 否 | 供应商名称，模糊查询 | `1` |
| `names` | String | 否 | 供应商名称，逗号分隔，精准查询 | `1,2` |
| `gmtCreateStart` | String | 否 | 创建时间起 | `2022-01-04 10:35:39` |
| `gmtCreateEnd` | String | 否 | 创建时间止 | `2022-01-04 10:35:39` |
| `gmtModifiedStart` | String | 否 | 修改时间起 | `2022-01-04 10:35:39` |
| `gmtModifiedEnd` | String | 否 | 修改时间止 | `2022-01-04 10:35:39` |
| `needAllPayAccount` | Integer | 否 | 是否返回所有收款账户：<br/>0=仅返回默认收款账户<br/>1=返回所有收款账户 | `0` |
| `includeDeleteAndBlockup` | Integer | 否 | 是否包含已删除/停用数据：<br/>1=需要返回<br/>0=不需要返回 | `1` |

## 返回参数

### 公共返回参数

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `code` | int | 返回码：200=成功，0=失败 | `200` |
| `msg` | String | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 子级错误编码 | `0230040301` |
| `result` | JackYunResult | 返回结果对象 | - |
| `contextId` | String | 上下文编号 | `412815373296750208` |

### 业务返回参数 (result.data 数组)

#### 供应商基本信息 (vendInfo)

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `vendId` | Long | 供应商ID | `11` |
| `code` | String | 供应商编码 | - |
| `name` | String | 供应商名称 | - |
| `classId` | Integer | 分类ID | `11` |
| `className` | String | 分类名称 | - |
| `abbreviation` | String | 简称 | - |
| `countryName` / `countryId` | String/Integer | 国家名称/ID | `中国` / `1` |
| `provinceName` / `provinceId` | String/Integer | 省份名称/ID | - / `1` |
| `cityName` / `cityId` | String/Integer | 城市名称/ID | - / `1` |
| `townName` / `townId` | String/Integer | 区/县名称/ID | - / `1` |
| `streetName` / `streetId` | String/Integer | 街道名称/ID | - / `1` |
| `address` | String | 详细地址 | - |
| `postcode` | String | 邮编 | - |
| `email` | String | 电子邮箱 | - |
| `website` | String | 网址 | - |
| `assistantCode` | String | 助记码 | - |
| `developDate` | Date | 发展日期 | `2025-10-10 15:15:15` |
| `leader` | String | 负责人 | - |
| `tel` | String | 电话 | - |
| `fax` | String | 传真 | - |
| `memo` | String | 备注 | - |
| `flagData` | String | 标记ID集合 | - |
| `departId` / `departName` | Long/String | 部门ID/名称 | `663526019443727488` / `rgr44` |
| `purchOfGoods` | Boolean | 是否货物采购（1是/0否） | `false` |
| `outsourcedFactory` | Boolean | 是否委外加工厂 | `false` |
| `ownFactory` | Boolean | 是否自有工厂 | `false` |
| `outsourcedManagedWarehouse` | Boolean | 是否委外托管仓 | `false` |
| `logisiticService` | Boolean | 是否物流配送 | `false` |
| `isAssembler` | Boolean | 是否配装师傅（0否1是） | `false` |
| `taxRate` | BigDecimal | 默认采购税率 | `22.22` |
| `companyId` / `companyName` | Long/String | 公司ID/名称 | `123465` / `aaaaa` |
| `logisticType` | String | 默认配送方式 | `送货上门` |
| `purchOfficeId` / `purchOfficeName` | Long/String | 默认采购办公室ID/名称 | `65161` / `采购办公室` |
| `purchUserId` / `purchUserName` | Long/String | 默认采购员ID/名称 | `16516` / `采购员a` |
| `taxIdentifyNumber` | String | 纳税人识别号 | `16453` |
| `foundDate` | Date | 创立时间 | `2025-10-10 15:15:15` |
| `levelCode` / `levelName` | String | 等级（01:一级,02:二级,03:三级） | `01` / `一级` |
| `arrivePeriod` | Integer | 交付周期（天） | `1` |
| `remindMsg` | String | 特别提醒 | `测试` |
| `orderIndex` | Integer | 序号 | `1` |
| `goodsSupplyAddress` | String | 货源地 | `测试12131221` |
| `advancePayPercent` | BigDecimal | 默认预付款比例 | `1` |
| `settType` | String | 默认结算方式：<br/>1-欠款计应付，2-现付，3-冲预付，4-多种结算，5-现结，6-现料，7-欠料计应付 | `1` |
| `paymentMethodName` | String | 默认结算账期 | `立即支付` |
| `currencyName` | String | 默认采购币种 | `人民币` |
| `logisticName` | String | 默认物流公司 | `公司名称` |
| `defaultInvoiceType` | String | 默认开票类型：<br/>000-无,025-电子发票,026-增值税普通发票,004-增值税专用发票,031-增值税电子普通发票,032-增值税电子专用发票,033-增值税普通发票（卷票）,034-普通发票（全电）,035-专用发票（全电）,036-国税通用机打发票 | `000` |
| `warehouseCode` / `warehouseName` | String | 默认供应商虚拟仓仓库编号/名称 | `00122` / `仓库名称` |
| `generalManager` | String | 总经理 | `张三` |
| `registeredCapital` | String | 注册资本 | `123` |
| `legalPerson` | String | 法定代表人 | `李四` |
| `taxNumber` | String | 开票税号 | `213213131` |
| `qualificationDueDate` | Date | 资质到期日期 | `2021-10-10` |
| `certificationSystem` | String | 认证体系 | `1231` |
| `storageArea` | String | 仓储面积 | `300` |
| `numberOfEmployees` | String | 员工人数（个） | `5` |
| `annualOutputValue` | String | 年产值 | `200000` |
| `mainProductsAndServices` | String | 主要产品及服务 | `测试1231` |
| `mainEquipmentAndProcess` | String | 主要设备及工艺 | `测试1231` |
| `monthlySupplyCapacity` | String | 月供货能力 | `测试1213` |
| `supplyProportion` | String | 供货占比 | `5` |
| `qualifiedRateOfSupply` | String | 供货合格率 | `2` |
| `estimatedProduction` | BigDecimal | 预估产能 | `99999` |
| `isSupportDistribution` | Integer | 支持代发：1-是，0-否 | `1` |
| `majorCustomerGroups` | String | 主要客户群体 | `用户` |
| `plantArea` | String | 厂房面积 | `1` |
| `isBlockup` | Integer | 是否停用：1-是，0-否 | `0` |
| `isDelete` | Integer | 是否删除：1-是，0-否 | `1` |
| `businessScope` | String | 经营范围 | `1` |
| `field1` - `field20` | String | 自定义字段1-20 | `...` |

#### 收款账户信息 (vendPayAccountList)

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `accName` | String | 账户名称 | `987654` |
| `bankacct` | String | 银行账号 | `123456789` |
| `bankbranch` | String | 开户行 | `6228 2562 2516 1254 326` |
| `accountTypeName` | String | 账户类型 | `银行` |
| `countriesRegions` | String | 国家/地区 | `中国` |
| `internationalBankAccount` | String | IBAN（国际银行账户号码） | - |
| `swiftCode` | String | BIC/SWIFT Code（银行国际代码） | - |
| `bankName` | String | 所属银行 | `工商银行` |

## 示例

### 请求示例 (JSON)

```json
{
  "codes": "1,2",
  "gmtCreateStart": "2022-01-04 10:35:39",
  "code": "1",
  "names": "1,2",
  "pageIndex": 0,
  "gmtModifiedEnd": "2022-01-04 10:35:39",
  "name": "1",
  "pageSize": 50,
  "gmtModifiedStart": "2022-01-04 10:35:39",
  "gmtCreateEnd": "2022-01-04 10:35:39"
}