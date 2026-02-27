# 拼多多订单同步与打印功能使用说明

## 概述

本文档说明如何使用完善的 `PddOrderIncrementJob` 类来同步拼多多增量订单，并使用快递100策略模式打印脱敏面单。

## 功能特性

### 1. 订单同步功能

- ✅ 自动拉取拼多多增量订单
- ✅ 智能解析收件人地址（省、市、区拆分）
- ✅ 保存完整的订单信息到数据库
- ✅ 支持多店铺配置
- ✅ 增量同步（基于最后同步时间）

### 2. 面单打印功能

- ✅ 保存快递100脱敏订单打印所需的所有字段
- ✅ 支持快递100平台授权配置
- ✅ 集成策略模式，支持多平台打印
- ✅ 地址自动解析和拆分

## 数据库表结构

### 表名：`pdd_order_increment`

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | BIGINT | 主键ID | 1 |
| order_sn | VARCHAR(64) | 订单号 | PDD202501011234567890 |
| payer_config_id | BIGINT | 付款主体ID | 1 |
| payer_name | VARCHAR(100) | 付款主体名称 | 拼多多店铺A |
| receiver_name | VARCHAR(100) | 收件人姓名 | 张三 |
| receiver_phone | VARCHAR(20) | 收件人手机号 | 13800138000 |
| receiver_address | VARCHAR(500) | 收件人完整地址 | 广东省深圳市南山区粤海街道科技园 |
| order_tag | INT | 订单标签（1=微派） | 1 |
| **encrypted_name** | VARCHAR(255) | **收件人加密姓名** | ~AgAAAAK6MjwFdMbadQAimOGCTHMINDd7/W2xXlWtlao=~4~ |
| **encrypted_mobile** | VARCHAR(255) | **收件人加密电话** | $AgAAAAK6MjwGdMbadQCrzhfdOcmsmHeJ69OhLqUjh80=$4$ |
| **province** | VARCHAR(50) | **收件人省份** | 广东省 |
| **city** | VARCHAR(50) | **收件人城市** | 深圳市 |
| **district** | VARCHAR(50) | **收件人区/县** | 南山区 |
| **encrypted_addr** | VARCHAR(500) | **收件人加密详细地址** | 粤海街道科技园 |
| **detail_addr_enc** | VARCHAR(500) | **收件人加密详细地址（完整版）** | ~AgAAAA...=~4~ |
| **express_com** | VARCHAR(50) | **快递公司编码** | yunda |
| **cargo_name** | VARCHAR(200) | **商品名称** | 手机 |
| **cargo_count** | INT | **商品数量** | 1 |
| **cargo_unit** | VARCHAR(20) | **商品单位** | 件 |
| **cargo_weight** | VARCHAR(20) | **商品重量** | 1.0 |
| **partner_id** | VARCHAR(100) | **合作伙伴ID** | your_partner_id |
| **partner_key** | VARCHAR(200) | **合作伙伴密钥** | your_partner_key |
| **partner_name** | VARCHAR(100) | **合作伙伴名称** | your_partner_name |
| **net** | VARCHAR(50) | **网络标识** | your_net |
| created_time | BIGINT | 订单创建时间戳 | 1704102400 |
| create_time | DATETIME | 创建时间 | 2025-01-01 12:00:00 |
| deleted | INT | 逻辑删除标记 | 0 |

**注意**：加粗字段为新增的快递100脱敏订单打印相关字段。

## 使用步骤

### 第一步：执行数据库迁移

运行 SQL 文件添加新字段：

```bash
mysql -u root -p your_database < sql/pdd_order_increment_add_print_fields.sql
```

### 第二步：配置快递100平台授权

在快递100平台完成拼多多平台授权，获取授权信息：
- partnerId
- partnerKey
- partnerName
- net

### 第三步：更新平台授权信息

方式1：直接更新数据库（推荐）

```sql
UPDATE pdd_order_increment
SET partner_id = 'your_partner_id',
    partner_key = 'your_partner_key',
    partner_name = 'your_partner_name',
    net = 'your_net'
WHERE payer_config_id = 1;
```

方式2：通过管理后台配置（如果有此功能）

### 第四步：运行订单同步任务

Job 类会自动执行，也可以手动触发：

```java
@Autowired
private PddOrderIncrementJob pddOrderIncrementJob;

public void manualSync() {
    pddOrderIncrementJob.execute();
}
```

### 第五步：打印脱敏面单

使用快递100策略模式打印面单：

```java
@Autowired
private EExpressClient eExpressClient;

@Autowired
private IPddOrderIncrementFacade pddOrderIncrementFacade;

public void printPddOrder(Long orderId) {
    // 1. 查询订单
    PddOrderIncrementBO order = pddOrderIncrementFacade.getById(orderId);

    // 2. 构建平台授权信息
    PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
            .partnerId(order.getPartnerId())
            .partnerKey(order.getPartnerKey())
            .partnerName(order.getPartnerName())
            .net(order.getNet())
            .build();

    // 3. 构建收件人加密信息（注意：这些应该是已加密的数据）
    DesensitizedOrderRequest.RecipientEncryptedInfo recipientInfo =
        DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
            .name(order.getEncryptedName())  // 加密姓名
            .mobile(order.getEncryptedMobile())  // 加密电话
            .province(order.getProvince())
            .city(order.getCity())
            .district(order.getDistrict())
            .addr(order.getEncryptedAddr())  // 加密地址
            .detailAddrEnc(order.getDetailAddrEnc())  // 加密详细地址
            .build();

    // 4. 构建商品信息
    DesensitizedOrderRequest.CargoInfo cargoInfo =
        DesensitizedOrderRequest.CargoInfo.builder()
            .name(order.getCargoName())
            .count(String.valueOf(order.getCargoCount()))
            .unit(order.getCargoUnit())
            .weight(order.getCargoWeight())
            .build();

    // 5. 构建请求
    DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
            .platformType(PlatformType.PINDUODUO)
            .authInfo(authInfo)
            .thirdOrderId(order.getOrderSn())
            .expressCom(order.getExpressCom())
            .recipientInfo(recipientInfo)
            .cargoInfo(cargoInfo)
            .build();

    // 6. 提交打印
    EOrderResult result = eExpressClient.submitDesensitizedOrder(request);

    log.info("面单打印完成，订单号: {}, 结果: {}", order.getOrderSn(), result);
}
```

## 地址解析工具

系统提供了 `AddressParser` 工具类，用于自动解析拼多多收件人地址：

```java
String fullAddress = "广东省深圳市南山区粤海街道科技园";
AddressParser.AddressInfo info = AddressParser.parseAddress(fullAddress);

// 结果：
// province = "广东省"
// city = "深圳市"
// district = "南山区"
// detailAddress = "粤海街道科技园"
```

## 加密数据处理

### 方案1：实时加密（推荐）

打印时调用快递100加密接口对收件人信息进行加密：

```java
// 伪代码示例
String encryptedName = encryptName(order.getReceiverName());
String encryptedMobile = encryptMobile(order.getReceiverPhone());
String encryptedAddr = encryptAddress(order.getDetailAddress());

// 更新到数据库
order.setEncryptedName(encryptedName);
order.setEncryptedMobile(encryptedMobile);
order.setEncryptedAddr(encryptedAddr);
pddOrderIncrementFacade.update(order);
```

### 方案2：提前批量加密

在订单同步后，批量对收件人信息进行加密并保存：

```java
@Scheduled(cron = "0 0/30 * * * ?")  // 每30分钟执行一次
public void batchEncryptOrders() {
    // 查询未加密的订单
    List<PddOrderIncrementBO> orders = pddOrderIncrementFacade.list(
        new PddOrderIncrementQuery()
            .encryptedNameIsNull(true)
            .limit(100)
    );

    for (PddOrderIncrementBO order : orders) {
        // 加密收件人信息
        String encryptedName = encryptName(order.getReceiverName());
        String encryptedMobile = encryptMobile(order.getReceiverPhone());

        // 更新数据库
        pddOrderIncrementFacade.update(
            new PddOrderIncrementParam()
                .setId(order.getId())
                .setEncryptedName(encryptedName)
                .setEncryptedMobile(encryptedMobile),
            new PddOrderIncrementQuery().setId(order.getId())
        );
    }
}
```

## 配置项说明

### 快递公司编码

常用快递公司编码：

| 快递公司 | 编码 | 快递公司 | 编码 |
|---------|------|---------|------|
| 圆通速递 | yunda | 申通快递 | shentong |
| 中通快递 | zhongtong | 韵达快递 | yunda |
| 顺丰速运 | shunfeng | 韵达快递 | yunda |

### 默认值配置

当前代码中使用的默认值：

```java
param.setExpressCom("yunda");  // 圆通速递
param.setCargoName("商品");  // 商品名称
param.setCargoCount(1);  // 1件
param.setCargoUnit("件");  // 件
param.setCargoWeight("1.0");  // 1kg
```

**建议**：这些值应该从配置表中读取，而不是硬编码。

## 后续优化建议

### 1. 扩展 PayerConfig 表

建议在 `f_payer_config` 表中添加快递100授权字段：

```sql
ALTER TABLE `f_payer_config`
    ADD COLUMN `kuaidi100_partner_id` VARCHAR(100) NULL COMMENT '快递100合作伙伴ID',
    ADD COLUMN `kuaidi100_partner_key` VARCHAR(200) NULL COMMENT '快递100合作伙伴密钥',
    ADD COLUMN `kuaidi100_partner_name` VARCHAR(100) NULL COMMENT '快递100合作伙伴名称',
    ADD COLUMN `kuaidi100_net` VARCHAR(50) NULL COMMENT '快递100网络标识',
    ADD COLUMN `default_express_com` VARCHAR(50) NULL COMMENT '默认快递公司编码';
```

### 2. 修改 Job 逻辑

从 `PayerConfig` 中读取授权信息：

```java
private PddOrderIncrementParam buildOrderParam(...) {
    // ...

    // 从配置中读取快递100授权信息
    param.setPartnerId(payerConfig.getKuaidi100PartnerId());
    param.setPartnerKey(payerConfig.getKuaidi100PartnerKey());
    param.setPartnerName(payerConfig.getKuaidi100PartnerName());
    param.setNet(payerConfig.getKuaidi100Net());
    param.setExpressCom(payerConfig.getDefaultExpressCom());

    // ...
}
```

### 3. 获取真实商品信息

如果拼多多API返回商品信息，可以使用真实数据：

```java
// 从订单详情中获取商品信息
List<OrderGoods> goodsList = orderInfo.getGoodsList();
if (goodsList != null && !goodsList.isEmpty()) {
    OrderGoods firstGoods = goodsList.get(0);
    param.setCargoName(firstGoods.getGoodsName());
    param.setCargoCount(firstGoods.getGoodsCount());
    param.setCargoUnit("件");
    param.setCargoWeight(firstGoods.getGoodsWeight());
}
```

## 常见问题

### Q1：地址解析不准确怎么办？

A：`AddressParser` 使用正则表达式匹配，可能无法覆盖所有情况。建议：
1. 对于解析失败的地址，手动补充省市区信息
2. 使用更专业的地址解析API
3. 建立地址库进行匹配

### Q2：如何获取加密的收件人信息？

A：有三种方式：
1. 拼多多API可能直接返回加密信息（需查阅拼多多API文档）
2. 调用快递100加密接口进行加密
3. 使用拼多多提供的加密工具/SDK

### Q3：平台授权信息如何获取？

A：
1. 登录快递100控制台
2. 进入"第三方平台账号授权"
3. 选择"拼多多"平台进行授权
4. 授权成功后获取 partnerId、partnerKey、partnerName、net

### Q4：如何测试面单打印功能？

A：
1. 先使用测试环境（快递100提供测试环境）
2. 使用测试订单号进行测试
3. 确认加密格式正确
4. 验证面单打印效果

## 相关文档

- [拼多多脱敏订单如何打印](https://api.kuaidi100.com/document/pinduoduotuominmiandandayin)
- [快递100电子面单V2文档](https://api.kuaidi100.com/document/elecorder/)
- [策略模式使用说明](../../ruoyi-starter/spring-boot-starter-kuaidi100/DESENSITIZED_ORDER_STRATEGY.md)

## 更新日志

- 2025-02-10：初始版本，添加快递100脱敏订单打印相关字段
- 支持地址自动解析和拆分
- 集成策略模式，支持多平台面单打印