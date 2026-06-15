# wms-wos.order.inspect (完成验货)

## 接口说明
- **接口名称**: 完成验货
- **接口方法**: `wms-wos.order.inspect`
- **授权类型**: 必须授权
- **版本号**: `v1.0`
- **返回格式**: JSON
- **最近发布**: 2026-06-08 15:59:37

## 公共请求参数
| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| method | String | 是 | 32 | 方法名称 | `wms-wos.order.inspect` |
| appkey | Long | 是 | 32 | 应用编号 | `16242933` |
| version | String | 是 | 32 | 版本号 | `v1.0` |
| contenttype | String | 是 | 32 | 返回格式 | `json` |
| timestamp | String | 是 | 32 | 请求时间 | `2015-01-01 12:00:00` |
| bizcontent | String | 是 | - | 业务参数（JSON字符串） | `{"number":"ORDER001"}` |
| contextid | String | 否 | 32 | 上下文关联ID（不参与签名） | `131562131` |
| sign | String | 是 | 32 | 签名 | 按规则生成 |
| token | String | 否 | 32 | ISV应用授权token（不参与签名） | 获取方法详见平台 |

## 业务请求参数 (`bizcontent` 字段内容)

| 参数 | 类型 | 必填 | 长度 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| number | String | **是** | 50 | 单据号（发货单/物流单号） | `78101177282303` |
| boxNoList | String | 否 | 50 | 发货箱号列表（JSON数组字符串） | `["boxNo1","boxNo2"]` |
| checkerId | Long | 否 | 20 | 验货人ID（通过 `erp.user.search` 获取） | `738309617757199488` |
| checker | String | 否 | 20 | 验货人姓名 | `张三` |
| inspectDetailList | Array | 否 | - | 验货商品明细列表 | 见下方子参数 |
| ∟ skuId | Long | 否* | 20 | 货品ID | `738309617757199488` |
| ∟ skuBarcode | String | 否* | 20 | 货品条码 | `sku11` |
| ∟ goodsNo | String | 否* | 20 | 货品编号 | `goodsNo11` |
| ∟ skuName | String | 否* | 20 | 货品规格名称 | `件` |
| ∟ snList | Array | 否 | 200 | 唯一码列表（JSON数组字符串） | `["sn1","sn2"]` |
| materialList | Array | 否 | 2000 | 包材信息列表 | `[{"skuBarcode":"LW1267301627986","skuCount":1}]` |
| isRefundDetect | Integer | 否 | - | 退款/异常订单检测：`1`-需要, `0/null`-不需要 | `0` |
| isCheckOrder | Integer | 否 | - | 检查订单状态：`1`-需要, `0/null`-不需要 | `0` |
| isCheckSnWare | Integer | 否 | - | 校验唯一码仓库：`1`-按配置校验, `0/null`-默认校验 | `1` |
| isOwnerMaterial | Integer | 否 | - | 包材货主范围：`1`-仅当前单据货主, `0/null`-不限制 | `1` |
| isSnQuerySku | Integer | 否 | - | 通过唯一码定位货品：`1`-是, `0/null`-否 | `1` |

> *注：在 `inspectDetailList` 中，`skuId`、`skuBarcode`、`goodsNo` 与 `skuName` 必须至少填写一组来定位货品。

## 公共返回参数
| 参数 | 类型 | 描述 | 示例值 |
| :--- | :--- | :--- | :--- |
| code | int | 返回码（`200` 成功，`0` 失败） | `200` |
| msg | String | 返回消息 | `[0230041006]未知错误` |
| subCode | String | 子级错误码 | `0230040301` |
| result | JackYunResult | 结果对象 | - |
| ∟ data | Data | 业务数据对象 | `{}` |
| ∟ contextId | String | 上下文编号 | `2295058368085590400` |

## 请求示例

### JSON 格式
```json
{
  "number": "78101177282303",
  "boxNoList": "[\"boxNo1\",\"boxNo2\"]",
  "inspectDetailList": [
    {
      "goodsNo": "goodsNo11",
      "skuName": "件",
      "snList": "[\"sn1\",\"sn2\"]",
      "skuBarcode": "sku11",
      "skuId": "738309617757199488"
    }
  ],
  "isCheckOrder": 0,
  "materialList": "[{\"skuBarcode\":\"LW1267301627986\",\"skuCount\":1}]",
  "checkerId": "738309617757199488",
  "checker": "张三",
  "isRefundDetect": 0
}