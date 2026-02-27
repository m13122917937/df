# 快递100脱敏订单打印策略模式使用说明

## 概述

本模块实现了基于策略设计模式的快递100脱敏订单打印功能，支持拼多多、抖音、快手、菜鸟淘宝等多个电商平台的脱敏面单打印。

## 架构设计

### 策略模式结构

```
┌─────────────────────────────────────────────────────────┐
│                   DesensitizedOrderStrategy              │
│                   (策略接口)                              │
│  + buildOrderParam(request): EOrderRequestParam          │
└──────────────────────┬──────────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       │               │               │
┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────┐
│  Pinduoduo  │ │   Douyin   │ │  Kuaishou  │ ...
│  Strategy   │ │  Strategy  │ │  Strategy  │
└─────────────┘ └────────────┘ └────────────┘
       │               │               │
       └───────────────┴───────────────┘
                       │
            ┌──────────▼──────────┐
            │  AbstractStrategy   │
            │   (抽象基类)         │
            └─────────────────────┘
```

### 核心组件

1. **策略接口** (`DesensitizedOrderStrategy`)
   - 定义所有策略必须实现的方法

2. **抽象基类** (`AbstractDesensitizedOrderStrategy`)
   - 提供通用的订单构建逻辑
   - 减少代码重复

3. **具体策略实现**
   - `PinduoduoDesensitizedOrderStrategy` - 拼多多策略
   - `DouyinDesensitizedOrderStrategy` - 抖音策略
   - `KuaishouDesensitizedOrderStrategy` - 快手策略
   - `TaobaoDesensitizedOrderStrategy` - 菜鸟淘宝策略

4. **策略工厂** (`DesensitizedOrderStrategyFactory`)
   - 根据平台类型返回对应的策略实例

5. **上下文** (`EExpressClient`)
   - 使用策略来处理脱敏订单

## 各平台脱敏规则

### 1. 拼多多 (PINDUODUO)

- **平台授权**: pinduoduoWx
- **电话加密格式**: `$...=4$`
- **姓名加密格式**: `~...=~4~`
- **地址结构**: province, city, district, addr
- **需要 detailAddrEnc**: 是
- **特有字段**: partnerId, partnerKey, partnerName, net, thirdOrderId

示例:
```json
{
  "recMan": {
    "name": "~AgAAAAK6MjwFdMbadQAimOGCTHMINDd7/W2xXlWtlao=~4~",
    "mobile": "$AgAAAAK6MjwGdMbadQCrzhfdOcmsmHeJ69OhLqUjh80=$4$",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "addr": "~AgAAAAK6MjwIdMbadQG53SyjOLyy04ZtKOi1EBOn5YQC/uQ1PceHzGU+Fp/SjVHcpR6fyzXzvnsLY4NoveXR7JgfAK1rUayLlT79BDdjydA=~4~",
    "detailAddrEnc": "~AgAAAAK6MjwIdMbadQG53SyjOLyy04ZtKOi1EBOn5YQC/uQ1PceHzGU+Fp/SjVHcpR6fyzXzvnsLY4NoveXR7JgfAK1rUayLlT79BDdjydA=~4~"
  }
}
```

### 2. 抖音 (DOUYIN)

- **平台授权**: douyin
- **电话加密格式**: `$$...=$1$$` (长格式加密)
- **姓名加密格式**: `##...#1##` (长格式加密)
- **地址结构**: province, city, district, addr
- **需要 detailAddrEnc**: 是
- **特有字段**: partnerId, partnerKey, partnerName, net, thirdOrderId

示例:
```json
{
  "recMan": {
    "name": "##N+06KglUtfp/I/Vp6XdLbHJ9pfiJRW15YIOY2AeklPpHpQkTQyD6hV4VcGO2WcZgXFlM6eFc7rya/LkYrf3X3sEK5rodZ2Gr5ilcZigo_CgYIASAHKAESPgo8ThN5zZXaW+iMWid5gmZZZ8TUfmakRVo4oaUw0umP2foh/zFyimMcSDN/AsoYgmr5VZHQGYZh8fiZNL4rGgA=#1##",
    "mobile": "$$MrwRxZLQ6eyoiBb0hHAVwa3PY8Tn2Lc6f1tJWkxjvlfrxNCR5Fb9NGm2l82YUKWKYvrl9OUs1NJO0m3abAAeBBRMePnnFHDCnHrN3HlZ5z9Eino=_CgYIASAHKAESPgo8VDWCrMBRiJhedYjhAiEkaBHRzPv0qV7ZKkdBS/NTTFIG5/R4GpXRomylvsbhkyYa8H/Ct3qSyv6clV3lGgA=$1$$",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "addr": "##fLm0CXukwu35yzQXuls0et8ZInKeT4X0kmO/kr3TfV41ZDfpRi16nui9qXSj9X+Wuz1w/q2qctG0YSVhheEPc5JIs6LPIyQe+vWm2DyAv8mrCeDG+NEFqw3ZXtexQ8c+8Cz6N2ZxwYk8Gs7qTjpdBPWD9La3H1Ev_CgYIASAHKAESPgo88I2EinrcLxopENPGWaZwj0Cm8QP69eeT7hMQKb3JEmyc8B1j1zdYtcL836uO7oEz3zLSM2aKKAzwTprKGgA=#1##",
    "detailAddrEnc": "##fLm0CXukwu35yzQXuls0et8ZInKeT4X0kmO/kr3TfV41ZDfpRi16nui9qXSj9X+Wuz1w/q2qctG0YSVhheEPc5JIs6LPIyQe+vWm2DyAv8mrCeDG+NEFqw3ZXtexQ8c+8Cz6N2ZxwYk8Gs7qTjpdBPWD9La3H1Ev_CgYIASAHKAESPgo88I2EinrcLxopENPGWaZwj0Cm8QP69eeT7hMQKb3JEmyc8B1j1zdYtcL836uO7oEz3zLSM2aKKAzwTprKGgA=#1##"
  }
}
```

### 3. 快手 (KUAISHOU)

- **平台授权**: kuaishou
- **电话加密格式**: `$...=$1$$`
- **姓名加密格式**: `#...#1##`
- **地址结构**: province, city, district, street, addr
- **需要 detailAddrEnc**: 是 (`~...=~1~~`)
- **特有字段**: partnerId, partnerKey, partnerName, net, thirdOrderId

示例:
```json
{
  "recMan": {
    "street": "",
    "name": "#aYUfZile#Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSIOKcWztpYSpaTq11Cp11zmDObNe9eu3VYZfW6VBFXDTcGhKshLB07Uzy/CvuBE9bj3C7hUciID5qIOnT7c/nfhCyoaCB0VTrwo4SZsgcqohha5Qtuv18KAUwAQ==&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMHnnAveIhZ7HwCr5eJbcf63Kj7fRDkRehISS489TB3SdPK1jzX9Wdad2icwMSJ/o+RoS5vZo7at9XZdPFvIjv/lgi7H7IiCNRqixfOfZJbdoV+J8Z9ERYtF9erEpeEbWPXRpv1kNJSgFMAE=#1##",
    "mobile": "$odMHb6BGqeJkEqkNoNTdfyoXdqOaFiDxHohBIWBom4M=$Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSICBZ27H1fwuCozjE6GHOJNCUPpjzYRmFqq+TFdbu2ER3GhICu0DaU0XezGbk6UOn3iNFJrAiIG0KT6MCjELash0cy3edgjuHmGLF0agctZ3NgvaN/ZSWKAUwAQ==&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMPwht2YV6hrokZh1HpOn/EhibeorheKZQfAYzfry6LQL3WOGWodSRPK7EhAoczky7RoSJHJGtusVhTZO7YNcF2W6loQAIiAXKke9OBANJ1NYoNdObd2RA2lu26+THvFchsDNvSjukygFMAE=$1$$",
    "city": "深圳市",
    "detailAddrEnc": "~4ZIlCF4TRCihgCsI4ZIlCF4TukhyDEOI~Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMDaZx7Qn73skKnMl6QgGQK2k0De/g0BCZOZJBmJN4Qr+xtZDEvsdFWdg8hHMLivSYxoSRZuvzpEyLU49SQXTqEJouleEIiDlcWQEfAGQm0rHsczwjFBpWwFva6stoL4TtKfvAiQgXygFMAE=&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMLPNJii8BAi9NGRBeT9hXfaIuXTvMsJSxIMhoimTb3E0vPe+9ciFDANlzOuDo1WjmBoS8Q6ReyhN/OXe2ik4umdb4sRAIiBoLKSlqzotpdyBO1jHn4xlMgvpu99hBoXjJ59eY0IMxigFMAE=~1~~",
    "district": "南山区",
    "addr": "~4ZIlCF4TRCihgCsI4ZIlCF4TukhyDEOI~Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMDaZx7Qn73skKnMl6QgGQK2k0De/g0BCZOZJBmJN4Qr+xtZDEvsdFWdg8hHMLivSYxoSRZuvzpEyLU49SQXTqEJouleEIiDlcWQEfAGQm0rHsczwjFBpWwFva6stoL4TtKfvAiQgXygFMAE=&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMLPNJii8BAi9NGRBeT9hXfaIuXTvMsJSxIMhoimTb3E0vPe+9ciFDANlzOuDo1WjmBoS8Q6ReyhN/OXe2ik4umdb4sRAIiBoLKSlqzotpdyBO1jHn4xlMgvpu99hBoXjJ59eY0IMxigFMAE=~1~~",
    "province": "广东省"
  }
}
```

### 4. 菜鸟淘宝 (TAOBAO)

- **平台授权**: taobao/cainiao
- **电话格式**: 脱敏格式 (如 `138****25` 或 `******_5336`)
- **姓名格式**: 脱敏格式 (如 `锦*_`)
- **地址结构**: province, city, district, addr
- **需要 detailAddrEnc**: 否
- **特有字段**: partnerId, partnerKey, partnerName, net, thirdOrderId, **oaid**

示例:
```json
{
  "recMan": {
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "addr": "北太**街道知春号锦秋国际韵达",
    "mobile": "138****_25",
    "name": "锦*_"
  }
}
```

## 使用方法

### 1. 注入 EExpressClient

```java
@Autowired
private EExpressClient eExpressClient;
```

### 2. 构建请求参数

```java
// 1. 构建平台授权信息
PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
    .partnerId("your_partner_id")
    .partnerKey("your_partner_key")
    .partnerName("your_partner_name")
    .net("your_net")
    // 菜鸟淘宝需要额外添加 oaid
    .oaid("tb_oaid_123456")  // 仅菜鸟淘宝需要
    .build();

// 2. 构建脱敏订单请求
DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
    .platformType(PlatformType.PINDUODUO)  // 选择平台类型
    .authInfo(authInfo)
    .thirdOrderId("订单ID")
    .expressCom("yunda")  // 快递公司编码
    .recipientInfo(...)   // 收件人信息
    .senderInfo(...)      // 寄件人信息
    .cargoInfo(...)       // 货物信息
    .build();
```

### 3. 提交订单

```java
EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
```

## 扩展新平台

如需支持新的电商平台，只需：

1. 在 `PlatformType` 枚举中添加新平台
2. 创建新的策略类继承 `AbstractDesensitizedOrderStrategy`
3. 在 `DesensitizedOrderStrategyFactory` 中注册新策略
4. 实现 `buildOrderParam` 方法

示例：

```java
@Component
public class NewPlatformDesensitizedOrderStrategy extends AbstractDesensitizedOrderStrategy {

    @Override
    public EOrderRequestParam buildOrderParam(DesensitizedOrderRequest request) {
        // 实现新平台的订单构建逻辑
        return ...;
    }

    @Override
    public String getPlatformCode() {
        return PlatformType.NEWPLATFORM.getCode();
    }
}
```

## 文件结构

```
ruoyi-starter/spring-boot-starter-kuaidi100/src/main/java/com/ruoyi/kuaidi100/
├── model/
│   ├── strategy/
│   │   ├── PlatformType.java                    # 平台类型枚举
│   │   ├── PlatformAuthInfo.java                # 平台授权信息
│   │   └── DesensitizedOrderRequest.java        # 脱敏订单请求
│   └── e/
│       └── EOrderRequestParam.java               # 电子面单请求参数（已扩展）
├── strategy/
│   ├── DesensitizedOrderStrategy.java           # 策略接口
│   └── impl/
│       ├── AbstractDesensitizedOrderStrategy.java    # 抽象基类
│       ├── PinduoduoDesensitizedOrderStrategy.java  # 拼多多策略
│       ├── DouyinDesensitizedOrderStrategy.java     # 抖音策略
│       ├── KuaishouDesensitizedOrderStrategy.java   # 快手策略
│       └── TaobaoDesensitizedOrderStrategy.java     # 菜鸟淘宝策略
├── factory/
│   └── DesensitizedOrderStrategyFactory.java    # 策略工厂
├── EExpressClient.java                           # 快递客户端（已扩展）
├── KuaidiConfig.java                             # 配置类（已更新）
└── example/
    └── DesensitizedOrderExample.java            # 使用示例
```

## 注意事项

1. **数据加密**: 请确保收件人信息已经按照对应平台的规则进行加密/脱敏处理
2. **平台授权**: 使用前必须先在快递100进行平台授权，获取相应的 partnerId、partnerKey 等信息
3. **oaid 参数**: 菜鸟淘宝平台必须提供 oaid（淘宝订单收件人ID），否则订单会失败
4. **货物信息**: cargo 是必传字段，建议传商品列表里的第一个
5. **地址处理**: 对于没有区的城市，可以传入城市名或镇名作为 district

## API 文档参考

- [拼多多脱敏订单打印](https://api.kuaidi100.com/document/pinduoduotuominmiandandayin)
- [抖音脱敏订单打印](https://api.kuaidi100.com/document/douyinmiandandayin)
- [快手脱敏订单打印](https://api.kuaidi100.com/document/dou-yin-tuo-min-mian-dan)
- [菜鸟淘宝脱敏订单打印](https://api.kuaidi100.com/document/caimiaotaobaomiandandayin)