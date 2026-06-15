# ERP API 文档：erp.stock.createandstockin (创建入库申请单并入库)

## 接口说明

- **接口名称**: `erp.stock.createandstockin`
- **接口描述**: 创建入库申请单并直接完成入库操作。
- **授权类型**: 必须授权
- **最近发布时间**: 2026-06-01 14:56:15.0
- **请求方式**: `POST`
- **返回格式**: JSON

## 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `method` | String | 是 | 32 | 方法名称 | `erp.stock.createandstockin` |
| `appkey` | Long | 是 | 32 | 应用编号 | `16242933` |
| `version` | String | 是 | 32 | 版本号 | `v1.0` |
| `contenttype` | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| `timestamp` | String | 是 | 32 | 时间戳 | `2015-01-01 12:00:00` |
| `bizcontent` | String | 是 | - | 业务参数，JSON格式字符串 | `{"a":"a1","b":"b1"}` |
| `contextid` | String | 否 | 32 | 上下文报文关联ID（不参与签名） | `131562131` |
| `sign` | String | 是 | 32 | 签名 | [点击查看签名方法] |
| `token` | String | 否 | 32 | ISV应用用户授权token（不参与签名） | [获取token方法] |

## 业务请求参数 (bizcontent)

### 主表参数

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `vendCode` | String | 是 | 供应商编码 | `DF001` |
| `applyDepartCode` | String | 是 | 部门编号（需与公司编号同时填写） | `applyDepartCode` |
| `applyCompanyCode` | String | 是 | 公司编号（需与部门编号同时填写） | `applyCompanyCode` |
| `inWarehouseCode` | String | 是 | 仓库编码 | `110` |
| `inType` | Integer | 是 | 入库类型：<br/>101=采购入库<br/>102=调拨入库<br/>103=盘盈入库<br/>104=其他入库<br/>105=销售退货<br/>106=生产完工入库 | `101` |
| `relDataId` | String | 是 | 关联单据编号 | `1` |
| `applyUserId` | String | 否 | 申请人ID | `0` |
| `applyUserName` | String | 是 | 申请人姓名 | `超级用户` |
| `applyDepartName` | String | 否 | 申请部门 | `笛佛-开发部` |
| `applyDate` | Date | 是 | 申请时间 | `2018-04-23 10:48:38` |
| `memo` | String | 否 | 备注 | `186` |
| `auditUserName` | String | 否 | 审核人 | `jackyun_dev` |
| `auditDate` | Date | 否 | 审核时间 | `2018-04-23 10:51:03` |
| `notificationCode` | String | 否 | 固定值`100` | `0` |
| `operator` | String | 是 | 制单人 | `jackyun_dev` |
| `source` | String | 是 | 写死：`OPEN` | `OPEN` |
| `currencyCode` | String | 否 | 币种 | `人民币` |
| `currencyRate` | BigDecimal | 否 | 汇率 | `1.0000` |
| `flagData` | String | 否 | 标记 | `338894404322968576` |
| `planInDate` | Date | 否 | 预计入库时间 | `2018-08-01 09:45:08` |
| `logisticName` | String | 否 | 物流公司名称 | `中通快递杭州三墩-菜鸟` |
| `logisticType` | String | 否 | 物流类型 | `1` |
| `logisticNo` | String | 是 | 物流单号 | `21321` |
| `logisticCode` | String | 否 | 物流公司编号 | `381557868419429761` |
| `systemMark` | String | 否 | 系统标识 | `测试` |
| `field1` - `field30` | String | 否 | 主表自定义字段 | `主表自定义字段1` |

### 发件人信息参数

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `sendCompanyName` | String | 否 | 发件公司名称 | `笛佛` |
| `send` | String | 否 | 发件人 | `1212121` |
| `sendTel` | String | 否 | 发件人电话 | `110` |
| `sendPhone` | String | 否 | 发件人手机号 | `11012011911` |
| `sendEmail` | String | 否 | 发件人邮箱 | `111` |
| `sendCountryId` | Integer | 否 | 发件人国家ID | `555` |
| `sendCountryName` | String | 否 | 发件人国家 | `中国` |
| `sendProvinceId` | Integer | 否 | 发件人省份ID | `110000` |
| `sendProvinceName` | String | 否 | 发件人省份 | `浙江省` |
| `sendCityId` | Integer | 否 | 发件人城市ID | `370600` |
| `sendCityName` | String | 否 | 发件人城市 | `杭州市` |
| `sendTownId` | Integer | 否 | 发件人区/县ID | `370602` |
| `sendTownName` | String | 否 | 发件人区/县 | `芝罘区` |
| `sendStreetId` | Integer | 否 | 发件人街道ID | `37060203` |
| `sendStreetName` | String | 否 | 发件人街道 | `芝罘岛街道` |
| `sendAddress` | String | 否 | 发件人详细地址 | `222222` |

### 收件人信息参数

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `receiveCompanyName` | String | 否 | 收件公司名称 | `笛声` |
| `receive` | String | 否 | 收件人 | `1212` |
| `receiveTel` | String | 否 | 收件人电话 | `110125126` |
| `receivePhone` | String | 否 | 收件人手机号 | `419862652` |
| `receiveEmail` | String | 否 | 收件人邮箱 | `1424` |
| `receiveCountryId` | Integer | 否 | 收件人国家ID | `555` |
| `receiveCountryName` | String | 否 | 收件人国家 | `中国` |
| `receiveProvinceId` | Integer | 否 | 收件人省份ID | `51000` |
| `receiveProvinceName` | String | 否 | 收件人省份 | `四川省` |
| `receiveCityId` | Integer | 否 | 收件人城市ID | `510800` |
| `receiveCityName` | String | 否 | 收件人城市 | `广元市` |
| `receiveTownId` | Integer | 否 | 收件人区/县ID | `510811` |
| `receiveTownName` | String | 否 | 收件人区/县 | `昭化区` |
| `receiveStreetId` | Integer | 否 | 收件人街道ID | `51081106` |
| `receiveStreetName` | String | 否 | 收件人街道 | `明觉镇` |
| `receiveAddress` | String | 否 | 收件人详细地址 | `尚坤·生态创意园` |

### 入库明细数组 (stockInDetailViews)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `outSkuCode` | String | 否 | 外部货品编号，用于匹配货品 | `S0200221` |
| `skuBarcode` | String | 是 | 货品条码 | `4580397175001` |
| `relDetailId` | Long | 是 | 关联实际业务明细表的ID | `0` |
| `unitName` | String | 否 | 计量单位 | `箱` |
| `skuCount` | BigDecimal | 是 | 入库数量 | `100.000000` |
| `skuPrice` | BigDecimal | 否 | 入库单价 | `100.000000` |
| `totalAmount` | BigDecimal | 否 | 入库金额 | `100.000000` |
| `isCertified` | Integer | 是 | 是否正品：1=是，0=否 | `1` |
| `rowRemark` | String | 否 | 明细备注 | `186` |
| `detailField1`-`detailField30` | String | 否 | 货品明细自定义字段 | `货品明细自定义字段1` |

#### 批次明细 (batchList)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `batchNo` | String | 是 | 批号 | `BATCH001` |
| `quantity` | BigDecimal | 是 | 数量 | `1` |
| `cuPrice` | BigDecimal | 否 | 单价 | `1` |
| `productionDate` | Date | 是 | 生产日期 | `2025-09-30` |
| `shelfLife` | BigDecimal | 否 | 保质期 | `1` |
| `shelfLiftUnit` | String | 否 | 保质期单位 | `年` |
| `expirationDate` | Date | 否 | 到期日期 | `2025-09-30` |
| `batchMemo` | String | 否 | 批次备注 | `备注信息` |

#### 序列号明细 (serialList)

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `serialNo` | String | 是 | 序列号 | `SN001` |
| `caseNumber` | String | 否 | 箱号 | `BOX001` |
| `cuPrice` | BigDecimal | 否 | 单价 | `1` |
| `rowRemark` | String | 否 | 备注 | `186` |

#### 唯一码与批次关联列表 (serialNoBatchNoRelList)
*注：使用此列表后，`serialList`字段将失效。*

| 参数 | 类型 | 必填 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| `no` | String | 是 | 唯一码 | `12321` |
| `batchNo` | String | 否 | 批次号 | `1232132` |

## 返回参数

### 公共返回参数

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `code` | int | 返回码：200=成功，0=失败 | `200` |
| `msg` | String | 返回消息 | `[0230041006]未知错误` |
| `subCode` | String | 子级错误编码 | `0230040301` |
| `result` | JackYunResult | 返回结果对象 | - |
| `contextId` | String | 上下文编号 | `412815373296750208` |

### 业务返回参数 (result.data)

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| `inNo` | String | 生成的入库单编号 | - |

## 示例

### 请求示例 (JSON)

```json
{
  "sendProvinceId": 110000,
  "memo": "186",
  "source": "OPEN",
  "operator": "jackyun_dev",
  "inType": 101,
  "inWarehouseCode": "110",
  "applyUserName": "超级用户",
  "relDataId": "1",
  "applyDepartCode": "applyDepartCode",
  "applyCompanyCode": "applyCompanyCode",
  "logisticNo": "21321",
  "stockInDetailViews": [
    {
      "skuPrice": 100,
      "skuCount": 100,
      "isCertified": 1,
      "relDetailId": "0",
      "skuBarcode": "4580397175001"
    }
  ],
  "applyDate": "2018-04-23 10:48:38"
}