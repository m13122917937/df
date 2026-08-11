# 国补微信小程序及统一账号体系实施文档

## 1. 文档目标

在现有 ADM 项目中新增国补微信小程序及其独立后端服务，完成商品展示、分类、立即购买、微信支付、订单履约和退款闭环，并让微信小程序与微信公众号公用同一套会员账号体系。

后台管理端 `ruoyi-ui` 新增一级菜单“国补”，用于管理商城运营、商品、库存、订单、发货、退款和微信身份冲突。

首期不接入政府补贴资格、额度或核销接口。“国补”作为专区和业务名称使用，订单中的价格差额统一表述为“活动优惠”，不得表述为已经完成政府补贴核销。

## 2. 已确认的业务范围

### 2.1 首期包含

- 原生微信小程序。
- 首页轮播图、分类导航和推荐商品。
- 多级分类、SPU、SKU、商品图片和图文详情。
- SKU 原价、库存、上下架状态。
- 分类级活动优惠比例、优惠封顶额和可售省份。
- 单个 SKU 立即购买，可选择购买数量。
- 收货地址、全场包邮、后台人工发货。
- 微信小程序 JSAPI 支付。
- 待支付、已支付、发货、确认收货、自动完成、退款等订单状态。
- 未发货订单整单退款，后台审核后调用微信原路退款。
- 微信公众号与小程序基于 UnionID 统一账号。
- `ruoyi-ui` 一级菜单“国补”及完整商城管理子菜单。

### 2.2 首期不包含

- 政府补贴资格、实名身份、补贴额度或核销接口。
- 购物车和多商品合并结算。
- 强制绑定微信手机号。
- 运费模板；首期全场包邮。
- 发货后的在线退货退款流程。
- 对接外部 ERP、仓库或物流系统。
- 公众号 H5 商城；公众号仅增加跳转小程序的菜单入口。

## 3. 总体架构

### 3.1 新增模块

| 模块 | 类型 | 主要职责 |
| --- | --- | --- |
| `ruoyi-miniapp` | 原生微信小程序 | 首页、分类、商品详情、结算、地址、支付、订单和退款申请 |
| `ruoyi-app` | 独立 Spring Boot 应用 | 小程序 Controller、Biz、Form/VO、登录态、支付回调和接口编排 |
| `ruoyi-subsidy` | 国补领域模块 | 分类、商品、SKU、库存、订单、支付、退款和履约领域能力 |
| `ruoyi-user` | 既有用户领域 | 会员、微信公众号/小程序身份绑定、UnionID 归并和身份冲突 |
| `ruoyi-admin` | 既有后台应用层 | 国补后台 Controller、Biz、Form/VO 和管理接口 |
| `ruoyi-ui` | 既有后台前端 | 一级菜单“国补”和商城管理页面 |

### 3.2 服务端口

- `ruoyi-admin`：保持 `7772` 不变。
- `ruoyi-api`：保持 `7773` 不变。
- `ruoyi-app`：默认使用 `7774`，作为独立启动和部署单元。

### 3.3 强制调用链

```text
微信小程序
  → ruoyi-app Controller
  → ruoyi-app Biz
  → ruoyi-user Facade / ruoyi-subsidy Facade
  → Service
  → Mapper / Mapper XML
```

```text
ruoyi-ui
  → ruoyi-admin Controller
  → ruoyi-admin Biz
  → ruoyi-user Facade / ruoyi-subsidy Facade
  → Service
  → Mapper / Mapper XML
```

约束：

- Controller 只能调用 Biz。
- Biz 只能调用 Facade，不得直接依赖 Service、Mapper 或 Entity。
- Facade 是领域唯一出口。
- 数据库事务只能位于 Service 的 `public` 方法。
- 微信登录、支付和退款等远程调用不得放在数据库事务内。
- Web VO 与领域对象、领域对象与 Entity 的转换统一使用 MapStruct。

## 4. 微信统一账号体系

### 4.1 前置条件

- 微信公众号和小程序必须在上线前绑定到同一个微信开放平台账号。
- 小程序 AppID 必须与现有微信支付商户号建立关联。
- AppID、AppSecret、商户号、API V3 Key 和证书路径必须从环境变量或外部密钥系统注入。

### 4.2 身份模型

不修改现有 `u_member` 表，新增用户领域表：

- `u_member_wechat_identity`：保存会员、渠道、AppID、OpenID、UnionID、状态和审计时间。
- `u_member_wechat_identity_conflict`：保存重复 UnionID、涉及会员、冲突原因和人工处理结果。

渠道建议使用：

- `MP`：微信公众号。
- `MINIAPP`：微信小程序。

唯一约束：

- `channel + app_id + open_id` 唯一。
- 同一开放平台下，非空 UnionID 只能归属一个有效会员。
- 所有绑定和合并操作必须记录原会员、目标会员、渠道和操作人。

### 4.3 静默登录流程

```text
小程序调用 wx.login()
  → 获得临时 code
  → 调用 /miniapp/auth/silent-login
  → ruoyi-app 调用微信 code2Session
  → 获得 OpenID、UnionID、SessionKey
  → 用户领域解析微信身份
  → 签发 ADM 登录令牌
```

处理规则：

1. UnionID 已唯一关联会员：把当前小程序 OpenID 绑定到该会员。
2. 当前渠道 OpenID 已存在：使用已有会员，并在获得 UnionID 后渐进补全。
3. OpenID、UnionID 都不存在：创建轻量会员和小程序身份。
4. 微信暂未返回 UnionID：创建临时身份，只允许浏览商品，归并完成前不允许下单。
5. 同一 UnionID 指向多个会员：标记身份冲突，禁止自动迁移会员、订单和客户权限。
6. 历史公众号会员在后续关注、扫码或授权时渐进补全 UnionID，不按昵称、手机号猜测合并。

### 4.4 公众号入口

- 保留公众号现有功能和 `ruoyi-web` 页面。
- 新增公众号菜单“国补商城”，直接跳转小程序首页。
- 公众号关注、扫码等获取到 `WxMpUser` 的入口，需要调用统一微信身份 Facade 补全 OpenID 和 UnionID。

## 5. 国补商品模型

### 5.1 领域边界

- 新建 `ruoyi-subsidy` 领域模块。
- 所有国补商城业务表使用 `gb_*` 前缀。
- 不复用或改写现有 `m_*`、`quote_*`、`o_*` 表。
- `ruoyi-app` 和 `ruoyi-admin` 只能通过国补 Facade 访问国补业务数据。

### 5.2 分类

分类支持：

- 多级父子结构。
- 名称、图标、图片、排序、启用状态。
- 活动优惠比例。
- 活动优惠封顶金额。
- 可售省份。

分类下存在已上架商品时，应禁止直接删除；可先停用或完成商品迁移。

### 5.3 SPU

SPU 建议包含：

- 商品编码、名称、副标题和分类。
- 主图、轮播图、图文详情。
- 推荐状态、排序、上下架状态。
- 创建人、更新人和审计时间。

### 5.4 SKU

SKU 建议包含：

- SKU 编码和规格描述。
- 原价。
- 可售库存。
- 销量。
- 上下架状态。
- 版本号或等价并发控制字段。

金额统一使用 `BigDecimal`，数据库使用明确精度的 `DECIMAL`，不得使用浮点类型。

### 5.5 活动优惠计算

优惠规则继承商品所属分类：

```text
商品总价 = SKU 原价 × 购买数量
比例优惠 = 商品总价 × 分类优惠比例
活动优惠 = min(比例优惠, 分类优惠封顶金额)
订单实付 = 商品总价 - 活动优惠
```

- 优惠封顶按订单商品行计算。
- 首期一张订单只有一个商品行。
- 金额按人民币分进行四舍五入。
- 订单必须保存原价、购买数量、优惠比例、封顶额、优惠额和实付额快照。
- 预支付前重新校验商品状态、地区限制和金额，前端提交金额不得作为支付依据。

## 6. 小程序功能

### 6.1 页面结构

```text
pages/
  home/index              首页
  category/index          分类商品
  product/detail          商品详情
  checkout/index          确认订单
  address/list            地址列表
  address/edit            地址编辑
  order/list              订单列表
  order/detail            订单详情
  refund/apply            退款申请
  profile/index           我的
```

### 6.2 首页

- 后台配置轮播图。
- 展示启用分类。
- 展示推荐商品。
- 支持下拉刷新、加载中、空数据和失败重试。

### 6.3 商品列表与详情

- 分类筛选和商品名称搜索。
- 商品上下架、库存和可售省份校验。
- 商品详情展示轮播图、规格、原价、活动优惠和预计实付价。
- 选择 SKU 和数量后进入立即购买结算。
- 首期不提供购物车。

### 6.4 收货地址

- 用户维护姓名、联系电话、省市区和详细地址。
- 不强制将收货电话绑定为会员手机号。
- 创建订单时复制地址快照，用户后续修改地址不得影响历史订单。
- 商品所属分类的可售省份不包含收货省份时，禁止创建订单。

## 7. 订单与库存

### 7.1 订单状态

建议状态：

- `PENDING_PAY`：待支付。
- `PAID`：已支付待发货。
- `SHIPPED`：已发货。
- `COMPLETED`：已完成。
- `CANCELED`：已取消。
- `REFUND_APPLYING`：退款申请中。
- `REFUNDING`：退款中。
- `REFUNDED`：已退款。
- `REFUND_FAILED`：退款失败待处理。

### 7.2 下单

- 首期只支持一个 SKU 立即购买。
- 服务端读取 SKU 和分类规则重新计算价格。
- 创建订单时校验库存大于零，但不锁定、不扣减库存。
- 待支付订单默认 15 分钟关闭。
- 关闭待支付订单不需要恢复库存。
- 同一个支付订单只能生成一次有效预支付请求。

### 7.3 库存策略

采用“支付成功后扣库存”：

- 微信支付成功回调后扣减 SKU 库存。
- 允许库存扣减为负数，系统明确接受并发支付导致的超卖。
- 负库存必须生成库存流水、后台告警和待补货记录。
- 负库存订单仍进入已支付待发货状态，不自动退款。
- 后台补货后通过库存调整流水恢复可用库存。

### 7.4 发货与完成

- 后台录入物流公司和运单号。
- 发货成功后订单进入已发货状态。
- 用户可以在小程序确认收货。
- 用户未操作时，发货满 7 天由定时任务自动完成。
- 定时任务只能调用国补 Facade，不得直接调用 Service 或 Mapper。

## 8. 微信支付

### 8.1 支付方式

- 复用现有微信支付商户号。
- 新小程序 AppID 关联现有商户号。
- 使用微信支付 V3 JSAPI 下单。
- 现有保证金充值支付业务不得直接复用为商品支付订单。
- 微信支付能力应抽取为类型化基础设施 Client，由国补领域编排商品订单支付。

### 8.2 预支付

预支付前必须检查：

- 当前会员身份不存在冲突，且已完成 UnionID 安全归并。
- 订单属于当前会员。
- 订单状态为待支付且未过期。
- 商品与 SKU 仍可销售。
- 收货省份仍在可售范围。
- 服务端重新计算金额与订单快照一致。

### 8.3 支付回调

- 回调地址由 `ruoyi-app` 独立域名配置生成。
- 必须校验微信签名、商户号、AppID、订单号、金额和交易状态。
- 以业务订单号和微信交易号实现数据库幂等。
- 重复回调不得重复扣减库存或重复更新订单。
- 外部回调验签和数据库事务分离。
- 日志不得输出回调明文、OpenID、UnionID、SessionKey、证书和密钥。

## 9. 退款规则

首期只支持已支付且未发货订单的整单退款：

```text
用户提交退款申请
  → 后台审核
  → 调用微信原路退款
  → 微信退款回调
  → 更新退款与订单状态
  → 恢复 SKU 库存
```

约束：

- 不支持部分退款。
- 不支持已发货订单在线退货退款。
- 退款申请、审核、发起退款和回调处理均须幂等。
- 退款金额必须等于订单实际支付金额。
- 退款成功后恢复库存并写入库存流水。
- 退款失败进入人工处理状态，不得直接标记退款成功。

## 10. ruoyi-ui“国补”菜单

新增一级菜单“国补”，建议包含：

1. 运营首页
2. 轮播图管理
3. 分类管理
4. 商品管理
5. SKU 与库存管理
6. 负库存与待补货
7. 订单管理
8. 发货管理
9. 退款审核
10. 微信身份管理
11. 身份冲突处理

每个页面在 `ruoyi-ui/src/views/subsidy` 下按业务域拆分，接口统一放入 `ruoyi-ui/src/api/subsidy`。

菜单和按钮权限至少区分：

- 查询、新增、修改、删除。
- 上架、下架、推荐、取消推荐。
- 库存调整。
- 发货。
- 退款审核。
- 身份冲突处理。

菜单初始化使用独立增量 SQL 写入 `sys_menu`，不得改写历史迁移文件。

## 11. 数据库表规划

### 11.1 用户领域

- `u_member_wechat_identity`
- `u_member_wechat_identity_conflict`

### 11.2 国补领域

- `gb_banner`
- `gb_category`
- `gb_product`
- `gb_product_image`
- `gb_product_sku`
- `gb_stock_log`
- `gb_order`
- `gb_order_item`
- `gb_order_address`
- `gb_payment`
- `gb_refund`

### 11.3 数据规则

- 新表通过独立增量 SQL 创建。
- 不修改未经授权的旧业务表。
- 订单号、支付单号、退款单号和微信交易号建立唯一约束。
- Entity、Mapper XML 和建表 SQL 的字段、类型、精度、空值规则保持一致。
- 每个 Mapper 接口必须存在同名 XML，namespace 必须完全匹配。

## 12. 公共接口规划

### 12.1 登录与会员

- `POST /miniapp/auth/silent-login`
- `GET /miniapp/auth/profile`
- `POST /miniapp/auth/logout`

### 12.2 首页与商品

- `GET /miniapp/home`
- `GET /miniapp/categories`
- `GET /miniapp/products`
- `GET /miniapp/products/{productId}`

### 12.3 地址

- `GET /miniapp/addresses`
- `POST /miniapp/addresses`
- `PUT /miniapp/addresses/{addressId}`
- `DELETE /miniapp/addresses/{addressId}`

### 12.4 订单

- `POST /miniapp/orders/preview`
- `POST /miniapp/orders`
- `GET /miniapp/orders`
- `GET /miniapp/orders/{orderNo}`
- `POST /miniapp/orders/{orderNo}/cancel`
- `POST /miniapp/orders/{orderNo}/confirm`

### 12.5 支付与退款

- `POST /miniapp/payments/{orderNo}/prepay`
- `POST /miniapp/payments/wechat/notify`
- `GET /miniapp/payments/{orderNo}/status`
- `POST /miniapp/refunds`
- `GET /miniapp/refunds/{refundNo}`
- `POST /miniapp/refunds/wechat/notify`

创建订单、预支付、取消、确认收货、退款申请和支付/退款回调必须具备幂等控制。

## 13. 配置与安全

`ruoyi-app` 使用独立外部配置，至少包括：

数据库、Redis 与 MyBatis 配置通过环境变量 `RUOYI_APP_EXTERNAL_CONFIG` 指向受控外部文件加载。
仓库中的 `ruoyi-app/src/main/resources/application-external.example.yml` 仅为无敏感值示例，严禁提交真实凭据或证书。

```yaml
server:
  port: 7774

wechat:
  miniapp:
    app-id: ${WECHAT_MINIAPP_APP_ID}
    secret: ${WECHAT_MINIAPP_SECRET}
  pay:
    mch-id: ${WECHAT_PAY_MCH_ID}
    api-v3-key: ${WECHAT_PAY_API_V3_KEY}
    private-key-path: ${WECHAT_PAY_PRIVATE_KEY_PATH}
    certificate-path: ${WECHAT_PAY_CERTIFICATE_PATH}
    notify-base-url: ${WECHAT_PAY_NOTIFY_BASE_URL}
```

安全要求：

- 不向仓库提交任何真实 AppSecret、商户密钥、证书或 Token。
- 不在日志打印微信 code、SessionKey、OpenID、UnionID 和完整回调数据。
- 登录 code 只能使用一次并设置短有效期。
- 支付与退款回调必须验签。
- 订单、地址、支付和退款接口必须校验数据归属。
- 后台展示身份信息时需要脱敏。

## 14. 测试与验收

### 14.1 账号测试

- 公众号和小程序相同 UnionID 归并为同一会员。
- 小程序 OpenID 重复登录保持幂等。
- 无 UnionID 时只能浏览，不能下单。
- UnionID 冲突时不自动合并。
- 历史公众号会员访问时渐进补全 UnionID。

### 14.2 商品与优惠测试

- 多级分类、上下架、推荐和排序。
- SPU/SKU 详情和图片展示。
- 分类优惠比例和封顶额计算。
- 订单金额按分舍入并保存快照。
- 分类可售省份校验。

### 14.3 订单与库存测试

- 待支付订单 15 分钟关闭。
- 创建订单不扣库存。
- 支付回调成功后扣减库存。
- 并发支付导致负库存时生成告警和补货记录。
- 重复支付回调不重复扣库存。
- 退款成功恢复库存。
- 发货、确认收货和 7 天自动完成。

### 14.4 支付与退款测试

- JSAPI 预下单参数和签名。
- 支付金额篡改拦截。
- 微信支付成功、失败和重复回调。
- 未发货整单退款审核。
- 退款成功、失败和重复通知。

### 14.5 构建验证

后端命令必须显式使用项目 Maven 配置：

```powershell
mvn -s D:\work\jie\adm\setting.xml -pl ruoyi-user,ruoyi-subsidy,ruoyi-app,ruoyi-admin -am test
mvn -s D:\work\jie\adm\setting.xml -pl ruoyi-app,ruoyi-admin -am package
```

前端至少执行：

```powershell
cd D:\work\jie\adm\ruoyi-ui
npm run lint
npm run build:prod
```

小程序使用微信开发者工具验证静默登录、首页、分类、商品、地区限制、地址、下单、JSAPI 支付、订单和退款申请。

## 15. 上线前置条件

- 公众号与小程序绑定同一微信开放平台。
- 小程序 AppID 关联现有微信支付商户号。
- 配置小程序 request 合法域名和业务域名。
- 将 `ruoyi-app` 的 HTTPS 域名写入小程序发布配置；开发期可通过 Storage 键 `miniappApiBaseUrl` 注入，仓库中的 `ruoyi-miniapp/config.example.js` 仅为示例。
- 配置微信支付、退款和回调证书。
- `ruoyi-app` 支付回调域名可被微信公网访问。
- 完成 `gb_*`、微信身份表和 `sys_menu` 增量 SQL 审核。
- 完成开发/测试环境全链路验证后再安排生产发布。

## 16. 已接受的业务风险

- 首期不对接政府资格和核销接口，不能把活动优惠认定为政府已核销补贴。
- 库存采用支付成功后扣减，并允许库存为负数，系统明确接受超卖风险。
- 负库存订单不会自动退款，需要后台及时补货和履约。
- 首期不支持发货后的在线退货退款，由客服线下处理。
