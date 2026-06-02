# IMEI 接口文档

## 1. 查询待发货订单IMEI信息

- **URL**: `POST /apply/imei/query`
- **是否需要登录**: 否

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| brandSet | List\<String\> | 否 | 品牌列表 |
| categoryList | List\<String\> | 否 | 品类列表 |

### 请求示例

```json
{
  "brandSet": ["苹果", "小米"],
  "categoryList": ["手机"]
}
```

### 响应参数

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200=成功 |
| msg | String | 提示信息 |
| data | Array | 数据列表 |
| data[].orderCode | String | 订单编号 |
| data[].sn | String | SN码 |
| data[].imei | String | IMEI/86码 |

### 响应示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "orderCode": "ORD202605130001",
      "sn": "SN123456",
      "imei": "860123456789012"
    }
  ]
}
```

### 业务说明

查询近15天内待发货状态（status=4）且激活状态为 SUCCESS（已查询没问题）的订单串码信息。

---

## 2. 修改平台校验状态

- **URL**: `POST /apply/imei/platform`
- **是否需要登录**: 否

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderCode | String | 是 | 订单编号 |
| sn | String | 否 | SN码 |
| imei | String | 否 | IMEI/86码 |
| status | Integer | 是 | 平台校验状态：2=正常，1=风险 |

### 请求示例

```json
{
  "orderCode": "ORD202605130001",
  "sn": "SN123456",
  "imei": "860123456789012",
  "status": 2
}
```

### 响应示例

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

### 业务逻辑

1. 根据 `orderCode` + `sn` + `imei` 更新 `o_imei` 表的 `platform_imei` 和 `platform_time` 字段
2. 若更新未命中记录，直接返回成功
3. 根据 `status` 值执行不同操作：

| status | 值 | 订单状态变更 | 附加操作 |
|--------|----|-------------|---------|
| 正常(NORMAL) | 2 | 有物流路由：状态改为当日发货(5) | 创建旺店通入库单 |
| 正常(NORMAL) | 2 | 无物流路由：子状态改为待填写物流单号(45) | - |
| 风险(RISK) | 1 | 子状态改为平台已销售/待销售异常(44) | - |
