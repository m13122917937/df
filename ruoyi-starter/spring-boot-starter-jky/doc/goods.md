# erp.storage.goodslist (分页查询货品信息)

## 接口说明
这是一个标准接口，需要 **必须授权** 才能调用。

> **重要分页说明**：
> 首次同步时，请使用 `maxSkuId` 参数进行分页，无需使用传统的 `pageIndex`。具体方式是：首次请求传 `maxSkuId: 0`，后续请求使用上一次查询返回结果中最后一条数据的 `skuId` 作为新的 `maxSkuId` 值来获取下一页数据。

| 项目 | 内容 |
| :--- | :--- |
| **最近发布时间** | 2026-02-26 11:55:23.0 |
| **API测试工具** | [链接] |
| **错误码查询** | [链接] |

---

## 1. 公共请求参数

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | `erp.storage.goodslist` |
| appkey | Long | 是 | 32 | 应用编号 | `16242933` |
| version | String | 是 | 32 | 版本号 | `v1.0` |
| contenttype | String | 是 | 32 | 返回格式(暂时只支持json) | `json` |
| timestamp | String | 是 | 32 | 时间 | `2015-01-01 12:00:00` |
| bizcontent | String | 是 | 32 | 业务参数(JSON字符串) | `{"a":"a1","b":"b1"}` |
| contextid | String | 否 | 32 | 上下文报文关联ID(不参与签名) | `131562131` |
| sign | String | 是 | 32 | 签名 | [查看签名方法] |
| token | String | 否 | 32 | ISV应用授权token(不参与签名) | [获取token方法] |

---

## 2. 业务请求参数 (`bizcontent`)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| pageIndex | Integer | 是 | - | 分页页码 | `0` |
| pageSize | Integer | 是 | - | 分页页数 | `50` |
| goodsNo | String | 否 | - | 货品编号 | `A00001` |
| skuBarcode | String | 否 | - | 条码 | `barcode0001` |
| goodsName | String | 否 | - | 货品名称 | `NO0001` |
| skuName | String | 否 | - | 规格名称 | `规格A` |
| abcCate | String | 否 | 15 | ABC分类(A类,B类,C类) | `A类` |
| startDate | String | 否 | - | 货品创建起始时间 | `2021-08-14 00:00:00` |
| endDate | String | 否 | - | 货品创建结束时间 | `2021-09-14 23:59:59` |
| startDateModifiedSku | String | 否 | - | 起始时间(规格修改时间) | `2021-09-14 23:59:59` |
| endDateModifiedSku | String | 否 | - | 终止时间(规格修改时间) | `2021-09-14 23:59:59` |
| startDateModifiedGoods | String | 否 | - | 起始时间(货品修改时间) | `2021-09-14 23:59:59` |
| endDateModifiedGoods | String | 否 | - | 终止时间(货品修改时间) | `2021-09-14 23:59:59` |
| isPackageGood | Integer | 否 | - | 是否组合装（0：否；1：是） | `0` |
| isBlockup | Integer | 否 | - | 货品是否停用（0：否；1：是） | `0` |
| skuIsBlockup | Integer | 否 | - | 规格是否停用（0：否；1：是） | `0` |
| cateName | String | 否 | - | 分类名称 | `奶制品` |
| assistBarcode | String | 否 | - | 辅助条码（支持批量，逗号隔开） | `mb01b,mb01b,zy01,200g/包` |
| goodsNos | String | 否 | 200 | 货品编号,多个用,号隔开 | `NO0001,NO002` |
| isQueryDelete | String | 否 | 2 | 是否返回已删除货品(传1需要) | `1` |
| skuBarcodes | String | 否 | 10000 | 条码多个筛选，逗号隔开 | `12134,4213` |
| skuCodes | String | 否 | 50 | 外部编码(多个用逗号隔开) | `WB00001` |
| **maxSkuId** | **Long** | **否** | - | **【关键分页参数】** 上次查询返回的最大skuId，首次传0 | `124457454645` |

---

## 3. 公共返回参数

| 参数 | 类型 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- |
| code | int | 16 | 返回码(200为成功，0为失败) | `200` |
| msg | String | 32 | 返回消息 | `[0230041006]未知错误` |
| subCode | String | 32 | 子级编码 | `0230040301` |
| result | JackYunResult | - | 返回结果 | - |
| contextId | String | 32 | 上下文编号 | `412815373296750208` |

---

## 4. 业务返回参数 (`result.data`)

### 基础信息

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| goodsId | Long | 货品ID | `1123123` |
| goodsNo | String | 货品编号 | `货品编号` |
| goodsName | String | 货品名称 | `货品名称` |
| skuId | Long | 规格ID | `1212` |
| skuName | String | 规格名称 | `规格名称` |
| skuBarcode | String | 条码 | `code_0001` |
| skuNo | String | 规格编号 | `规格编号` |
| unitName | String | 单位 | `个` |
| abcCate | String | ABC分类 | `A类` |
| brandId | Integer | 品牌ID | `12` |
| brandName | String | 品牌 | `活鼎红` |
| cateId | Integer | 分类ID | `12` |
| cateName | String | 分类 | `服装` |
| cateFullName | String | 分类全称 | `空调` |
| goodsAttr | Integer | 货品属性（1：成品；2：半成品；3：原料...） | `1` |
| isDelete | String | 是否删除（0：否；1：是） | `0` |
| isPackageGood | Integer | 是否是组合装 1-是 0-否 | `0` |

### 自定义字段

| 参数范围 | 类型 | 描述 |
| :--- | :--- | :--- |
| goodsField1 - goodsField50 | String | 货品自定义字段1-50 |
| skuField1 - skuField30 | String | 规格自定义字段1-30 |

### 尺寸重量

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| skuLength | BigDecimal | 长 | `2` |
| skuWidth | BigDecimal | 宽 | `1` |
| skuHeight | BigDecimal | 高 | `4` |
| skuWeight | BigDecimal | 重量(g) | `1.2` |
| volume | BigDecimal | 体积（cm³） | `1` |

### 图片相关

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| skuImgUrl | String | 规格图片URL |
| imgUrlList | Array | 货品图片详情列表 |
| └ goodsId | Long | 货品ID |
| └ imgUrl | String | 图片地址 |
| └ isMainImage | String | 是否主图（0：否；1：是） |
| └ imagePosition | String | 图片位置(main/left/right/top/bottom) |
| └ imgKey | String | 图片地址key |

### 时间与供应商

| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| gmtCreate | Date | 货品创建时间 | `1631020179000` |
| skuGmtCreate | Date | 规格级创建时间 | `1631020179000` |
| goodsGmtModified | Date | 货品修改时间 | `1631020179000` |
| skuGmtModified | Date | 规格级修改时间 | `1631020179000` |
| defaultVendId | Long | 默认供应商ID | `129885456456466` |
| defaultVendName | String | 默认供应商 | `笛佛供应商` |
| warehouseId | Long | 默认存放仓库ID | `123469988849846` |
| warehouseName | String | 默认存放仓库名称 | `笛佛仓` |

### 其他标志

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| isBatchMgmt | Integer | 是否批次管理（1=开启） |
| isSerialManagement | Integer | 是否唯一码管理（1=开启） |
| isStopSelling | Boolean | 停止销售 |
| isStopPurchasing | Boolean | 停止采购 |
| inStoreProcessing | Boolean | 是否仓内加工 |

> 由于返回字段众多，此处仅列出核心字段。完整列表请参见原始文档。

---

## 5. 请求与响应示例

### 请求示例 (JSON)

```json
{
  "goodsNo": "A00001",
  "skuCodes": "WB00001",
  "endDate": "2021-09-14 23:59:59",
  "isPackageGood": 0,
  "pageSize": 50,
  "maxSkuId": "124457454645",
  "pageIndex": 0,
  "skuBarcode": "barcode0001",
  "goodsName": "NO0001",
  "startDate": "2021-08-14 00:00:00"
}