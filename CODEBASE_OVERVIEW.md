# ADM 项目代码全景梳理

> 基于 2026-07-14 本地代码静态阅读整理。本文描述的是当前仓库的实际结构，不等同于完整接口文档或数据库设计文档。

## 1. 一句话理解项目

ADM 是基于 RuoYi-Vue 二次开发的电商运营管理系统。项目采用 Maven 多模块单体架构，包含两个 Spring Boot 应用入口：面向后台运营人员的 `ruoyi-admin` 和面向业务客户端的 `ruoyi-api`；二者复用系统、用户、商品、订单、物流、财务等领域模块，并通过一个 Vue 2 管理端页面提供操作界面。

系统目前覆盖的核心业务包括：

- 后台用户、角色、菜单、部门、字典和参数管理；
- 公司、会员、公司账户及资金管理；
- 商品与 SKU 管理；
- 订单创建、报价、交易、入库、发货、物流、签收、售后和退货；
- 账单、付款计划、付款方、扣款和交易流水；
- 电子合同与签署；
- 聚水潭、快递 100、淘宝、旺店通、OSS、电子签等外部系统集成；
- Quartz 定时任务和部分异步业务处理。

## 2. 总体架构

```text
浏览器 / 管理端用户
        |
        v
ruoyi-ui（Vue 2 + Element UI）
        |
        | HTTP + Bearer Token
        v
ruoyi-admin（后台管理 API，默认端口 7772）
        |
        +-----------------------+
                                |
业务客户端 / 外部回调           | 共享领域模块
        |                       |
        v                       v
ruoyi-api（业务 API，7773） --> system / user / order / finance
                                |
                                v
                       MyBatis-Plus / Mapper XML
                                |
                                v
                         MySQL + Redis

两个应用还会访问：聚水潭、快递100、电子签、淘宝、旺店通、OSS、微信、RocketMQ 等外部能力。
```

项目不是微服务架构。`ruoyi-admin` 和 `ruoyi-api` 是两个可独立启动、但在源码层共享领域模块的 Spring Boot 应用。

## 3. 顶层模块地图

| 模块 | 定位 | 主要内容 |
|---|---|---|
| `ruoyi-admin` | 后台管理应用入口 | 后台 Controller、Biz 编排、表单/VO、定时业务 Job；启动类 `AdminApplication` |
| `ruoyi-api` | 业务客户端 API 入口 | 客户端 Controller、Biz 编排、表单/VO、支付、外部回调；启动类 `ApiApplication` |
| `ruoyi-framework` | Web 与基础设施框架 | Spring Security、JWT、Redis、MyBatis-Plus、全局异常、数据权限、线程池 |
| `ruoyi-common` | 公共基础库 | 通用返回对象、分页、注解、异常、工具类、校验、公共模型 |
| `ruoyi-system` | 系统管理领域 | 用户、角色、部门、菜单、岗位、字典、参数、通知、Excel 任务 |
| `ruoyi-user` | 用户与商品领域 | 会员、公司、公司银行卡、认证信息、公司资金、资金流水、商品和 SKU |
| `ruoyi-order` | 订单领域 | 订单、交易单、挂单、申请单、IMEI、销售退货、物流订阅、规则 |
| `ruoyi-finance` | 财务领域 | 账单、付款计划、付款方、付款配置、扣款、合同、交易流水 |
| `ruoyi-quartz` | 调度模块 | Quartz 任务、任务日志、执行工具与管理接口 |
| `ruoyi-starter` | 第三方集成适配层 | eSign、聚水潭、快递100、OSS、SN、淘宝、旺店通 Starter |
| `ruoyi-ui` | 后台管理前端 | Vue 2、Vue Router、Vuex、Element UI、Axios |
| `sql` | 增量数据库脚本 | 菜单、订单、售后、合同、任务等功能的迁移或修复 SQL |
| `bin` | 启动脚本 | `ruoyi-admin.bat`、`ruoyi-api.bat` |

根 `pom.xml` 聚合除前端外的 10 个 Maven 模块，统一声明 Java 11、Spring Boot 2.6.15、MyBatis-Plus 3.5.3.1、MapStruct 1.5.2.Final 等版本。

## 4. 两个后端应用的边界

### 4.1 ruoyi-admin

`ruoyi-admin` 是后台管理系统的 Web 服务，默认端口为 `7772`。它包含约 45 个 Controller，主要接口分组如下：

- `system`：登录、注册、用户、角色、菜单、部门、岗位、字典、参数、通知、Excel 任务；
- `order`：全部订单、新订单、交易、待处理、发货中、异常、售后、销售退货、IMEI、规则等；
- `bill`：账单、付款计划、付款方、扣款、交易流水；
- `company`：公司、银行卡、授权登录；
- `product`：商品管理；
- `contract` / `esign`：合同和电子签回调；
- `express`：物流业务；
- `wx`：微信 Token 相关能力。

后台应用同时承载若干业务定时任务，例如账单生成、合同状态同步、订单下载/流转、聚水潭商品/订单/退款/物流/供应商同步、规则任务和二次销售校验。

### 4.2 ruoyi-api

`ruoyi-api` 是业务客户端接口服务，默认端口为 `7773`，并显式启用了异步执行。它主要提供：

- 会员、公司、公司银行卡、公司资金接口；
- 订单、申请、IMEI、异常订单接口；
- 账单、合同接口；
- 支付接口；
- 物流查询及物流回调；
- 地区字典和首页聚合数据。

它与后台应用复用相同的 `ruoyi-user`、`ruoyi-order`、`ruoyi-finance`、`ruoyi-framework` 和 `ruoyi-common`，但拥有独立的 Controller、Biz、VO/Form 及配置文件。

## 5. 实际分层与对象流转

仓库规范写的是 `Controller -> Biz -> Service -> Mapper`。当前代码实际多出了一层 Facade，主流调用链是：

```text
Controller
  -> BizService
    -> IxxxFacade / Facade 实现
      -> Service
        -> Mapper / Mapper XML
          -> MySQL
```

各层职责可以理解为：

| 层 | 所在位置 | 实际职责 | 常见对象 |
|---|---|---|---|
| Controller | `ruoyi-admin` / `ruoyi-api` | 接收请求、基础校验、获取登录信息、返回统一响应 | Form、VO、AjaxResult、TableDataInfo |
| Biz | 两个入口模块的 `com.ruoyi.biz` | 跨领域业务编排、状态流转、组合查询、调用第三方服务 | Form、VO、BO、Query、Param |
| Facade | system/user/order/finance | 向入口模块暴露领域能力，隔离领域内部实现，并负责部分转换 | BO、Query、Param |
| Service | 领域模块 | 通用持久化操作和可复用领域处理 | Entity、DTO |
| Mapper | 领域模块 | MyBatis-Plus CRUD 与复杂 SQL | Entity、DTO、Query |
| Convert | 各模块 `convert`，入口模块部分在 `mapper` | 使用 MapStruct 在不同层对象间转换 | Form/Param/Query/Entity/BO/VO |

领域模型中常见对象后缀：

- `Form`：Controller 入参；
- `VO`：Controller 出参；
- `Param`：写操作参数；
- `Query`：查询条件；
- `BO`：跨层业务对象；
- `DTO`：Mapper 聚合查询或内部传输对象；
- 无后缀领域类：通常是数据库 Entity。

### 5.1 典型订单列表链路

以后台新订单列表为例：

1. 前端调用 `/order/new/list`；
2. `OrderController` 接收 `OrderNewForm`，补充订单类型和分页参数；
3. `OrderBizService` 使用 MapStruct 将 Form 转为 `OrderQuery`，补充时间范围等业务条件；
4. Biz 调用 `IOrderFacade.listPage`；
5. `OrderFacade` 通过 `DynamicCondition` 生成 MyBatis 查询条件，并调用 `OrderService`；
6. `OrderService` 调用 `OrderMapper` / Mapper XML 查询数据库；
7. Facade 将 Entity 转为 `OrderBO`，Biz 再转为 `OrderListVO` 并补齐省市名称；
8. Controller 返回 RuoYi 标准分页结构 `TableDataInfo`。

这条链路体现了项目的主流设计，但代码中也存在 Biz 直接组合多个 Facade、调用第三方 SDK、甚至使用事务注解的情况，见“现状与风险”。

## 6. 主要业务域

### 6.1 系统与权限域

`ruoyi-system` 延续 RuoYi 的 RBAC 模型，核心对象包括系统用户、角色、菜单、部门、岗位、字典、系统参数和通知。前端菜单由后端按登录用户权限返回，再由前端动态生成路由。

认证链路大致为：

```text
登录请求 -> SysLoginService -> 校验用户 -> TokenService 生成 JWT/会话
后续请求 -> Authorization: Bearer <token>
         -> JwtAuthenticationTokenFilter
         -> Spring Security 权限判断
         -> Controller
```

`SecurityConfig` 负责配置匿名地址、认证入口、退出处理、JWT 过滤器和接口访问策略；`DataScopeAspect` 负责数据权限范围处理。

### 6.2 用户、公司与资金域

`ruoyi-user` 实际包含三个子域：

- `user`：会员、公司、会员公司关系、银行卡、认证信息；
- `capital`：公司资金账户、支付记录、资金流水；
- `product`：商品和 SKU。

公司既是订单参与方，也是账单、合同、付款方配置和资金变更的关联主体，因此该模块被订单与财务 Biz 广泛依赖。

### 6.3 订单域

`ruoyi-order` 是业务复杂度最高的领域之一，包含：

- 订单主表及订单状态流转；
- 交易订单、挂起订单和申请单；
- IMEI 及 IMEI-SKU 关系；
- 销售退货；
- 物流路由订阅；
- 报价/订单规则。

入口模块的 Biz 负责把订单能力与公司资金、商品、地区、账单、物流和外部 ERP 串联起来。订单状态贯穿新建、报价、交易、入库、发货、在途、签收、异常、撤销、完成、售后等阶段。

### 6.4 财务与合同域

`ruoyi-finance` 以 `bill` 包为主体，包括：

- `Bill`：账单；
- `BillPayPlan`：付款计划；
- `Payer` / `PayerConfig`：付款方及配置；
- `Deduction`：扣款；
- `Transactions`：交易流水；
- `Contract`：合同。

合同签署通过第三方电子签 Starter 对接；账单与订单、公司资金及付款计划存在紧密关系。

### 6.5 外部系统适配

`ruoyi-starter` 将外部 SDK 和配置包装为 Spring Boot Starter：

- `spring-boot-starter-jky`：聚水潭 ERP；
- `spring-boot-starter-kuaidi100`：快递查询、订阅和电子面单；
- `spring-boot-starter-esign`：电子签；
- `spring-boot-starter-taobao`：淘宝开放平台；
- `spring-boot-starter-wangdian`：旺店通；
- `spring-boot-starter-oss`：对象存储；
- `spring-boot-starter-sn`：SN/序列号相关接口。

Starter 负责客户端、配置属性、请求响应模型和异常封装，真正的业务调用主要发生在 `ruoyi-admin` / `ruoyi-api` 的 Biz 或 Job 中。

## 7. 前端代码结构

`ruoyi-ui` 是 Vue 2.6 + Vue Router 3 + Vuex 3 + Element UI 2 的单页应用。

主要目录：

| 路径 | 职责 |
|---|---|
| `src/views` | 页面，按业务分为需求管理、拣货、售后、财务、系统等 |
| `src/api` | Axios 接口封装，共约 36 个 JS 接口文件 |
| `src/router` | 固定路由与少量本地动态路由 |
| `src/store` | 登录用户、权限、字典、设置等全局状态 |
| `src/permission.js` | 路由守卫、登录判断、用户信息加载、动态路由注入 |
| `src/utils/request.js` | Axios 实例、Token 注入、统一响应和下载处理 |
| `src/components` | 分页、上传、富文本、字典、通知等通用组件 |
| `src/layout` | 后台框架布局、侧边栏、导航等 |

页面规模较大的业务区域是 `demandManage`，其次是系统管理、资金/财务和拣货管理。前端大部分菜单不是写死在路由文件中，而是登录后从后端菜单权限数据转换得到。

前端请求流程：

```text
Vue 页面 -> src/api/*.js -> Axios request 拦截器
        -> 自动附加 Authorization: Bearer token
        -> ruoyi-admin
        -> 统一处理 code / msg / rows / total
```

开发环境使用 `/dev-api` 代理到 `VUE_APP_BASE_URL`；生产环境直接使用 `VUE_APP_BASE_API`。

## 8. 数据访问、分页与缓存

- ORM 主体是 MyBatis-Plus，复杂查询放在各模块 `resources/mapper/**/*Mapper.xml`；
- `DynamicCondition` 根据 Query 对象及字段注解动态生成 Wrapper；
- 分页同时使用 PageHelper，并封装为 `PageParamV2`、`PageBO` 和 `TableDataInfo`；
- 数据库类型配置为 MySQL；
- Redis 用于登录会话、字典或其他业务缓存；
- Entity 默认支持下划线转驼峰，逻辑删除值为 1、未删除值为 0。

## 9. 定时任务与异步处理

`ruoyi-quartz` 提供通用 Quartz 管理能力，后台管理接口可以维护任务和查看执行日志。业务 Job 主要放在 `ruoyi-admin`，当前可见任务包括：

- 当日账单、合同授权和合同状态同步；
- 订单下载、流转、发货；
- 聚水潭商品、订单、退款、物流任务、供应商同步；
- 物流异常检测；
- 订单规则与二次销售校验。

`ruoyi-api` 入口启用了 `@EnableAsync`；公共框架还提供线程池和异步管理器。RocketMQ 依赖及配置已经存在，但具体使用强度需要结合运行配置和消息监听类继续专项确认。

## 10. 构建与启动

### 后端

前置条件：JDK 11、Maven、MySQL、Redis，以及业务所需的外部服务配置。

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
java -jar ruoyi-api/target/ruoyi-api.jar
```

也可以分别运行：

- `com.ruoyi.AdminApplication`；
- `com.ruoyi.ApiApplication`。

默认激活 `dev` Profile。仓库还提供 `bin/ruoyi-admin.bat` 和 `bin/ruoyi-api.bat`。

### 前端

```bash
cd ruoyi-ui
npm install
npm run dev
npm run build:prod
```

## 11. 代码现状与重点风险

以下结论来自静态阅读，建议作为后续治理清单，而不是直接等同于线上故障：

1. **配置中存在明文敏感信息。** 应用 YAML 中可见 Token 密钥、第三方 `appSecret`、API Key 等内容，资源目录还包含支付证书文件。应尽快轮换已暴露凭据，并改为环境变量或密钥管理服务注入。
2. **默认配置直接激活 `dev`。** 打包后若没有外部覆盖，可能误用开发配置；建议由部署环境显式指定 Profile。
3. **配置项疑似拼写错误。** 两个主配置均使用 `spring.mian.allow-circular-references`，正确键应为 `spring.main.allow-circular-references`，当前配置可能没有生效。
4. **事务边界与项目规范不完全一致。** 规范要求事务只放 Service 层，但部分 Biz（例如订单编排类）直接导入/使用 `@Transactional`。跨多个 Facade 的业务确实需要一致性设计，但应明确统一的事务边界。
5. **实际架构比规范多 Facade 层。** 代码主链是 Controller → Biz → Facade → Service → Mapper，建议把 Facade 正式写入开发规范，避免新人误判为违规跨层。
6. **部分 Facade 直接获取 `Service.getBaseMapper()`。** 这会绕过 Service 封装，与严格分层目标存在偏差。
7. **Biz 类职责偏重。** 订单 Biz 同时承担查询转换、状态编排、地区补全、资金、物流、第三方 ERP 等职责，类和方法容易继续膨胀，适合按用例或业务阶段拆分。
8. **两个入口存在重复 Biz。** admin/api 中均有 Order、Bill、Company、Contract 等同名 Biz，长期可能出现规则不一致；可提炼共享应用服务，同时保留不同端的 DTO 适配。
9. **源码注释显示乱码风险。** 多个文件在当前终端读取时中文呈现乱码，可能是历史文件编码不统一，也可能只是终端解码问题；应使用 IDE 检查真实编码并统一为 UTF-8。
10. **测试覆盖与执行策略需确认。** `ruoyi-api` Maven 配置固定跳过测试，仓库测试数量也明显少于业务规模，关键订单/资金/合同状态流转应补充单元与集成测试。
11. **技术版本较旧。** Vue 2、Spring Boot 2.6 和 Java 11 已进入偏维护状态。升级需作为独立项目规划，避免与业务迭代混做。
12. **前端开发服务默认端口配置较特殊。** `vue.config.js` 默认端口为 80，普通用户本地启动可能遇到端口占用或权限问题。

## 12. 新人阅读代码的推荐顺序

1. 阅读根 `pom.xml` 和两个应用的 `application.yml`，先理解模块与运行环境；
2. 阅读 `AdminApplication`、`SecurityConfig`、`JwtAuthenticationTokenFilter` 和 `TokenService`，掌握启动及认证；
3. 阅读前端 `main.js`、`permission.js`、`router/index.js` 和 `utils/request.js`，理解页面入口；
4. 选择一条实际业务链，例如订单列表，按 Controller → Biz → Facade → Service → Mapper XML 顺序跟读；
5. 阅读订单常量/枚举，画出订单状态机；
6. 再扩展到公司资金、账单、合同及外部 ERP，理解跨域协作；
7. 最后阅读 Job 和 SQL 增量脚本，补齐后台自动流程与历史演进。

## 13. 建议继续补充的专项文档

这份文档适合作为全景导航。若要支持开发、交接和排障，下一步建议按优先级补充：

1. 订单状态机与每个状态的进入/退出条件；
2. 订单—账单—付款计划—资金流水的数据关系图；
3. admin/api 接口清单及权限标识；
4. 核心数据库表和字段字典；
5. 聚水潭、快递100、电子签等外部调用时序及失败补偿机制；
6. Quartz 任务清单、Cron、幂等策略和告警方式；
7. 本地开发环境搭建及脱敏配置模板。

## 14. 关键代码索引

- Maven 聚合与版本：`pom.xml`
- 后台启动类：`ruoyi-admin/src/main/java/com/ruoyi/AdminApplication.java`
- API 启动类：`ruoyi-api/src/main/java/com/ruoyi/ApiApplication.java`
- 安全配置：`ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java`
- JWT 过滤器：`ruoyi-framework/src/main/java/com/ruoyi/framework/security/filter/JwtAuthenticationTokenFilter.java`
- Token 服务：`ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java`
- 典型订单 Controller：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/order/OrderController.java`
- 典型订单 Biz：`ruoyi-admin/src/main/java/com/ruoyi/biz/order/OrderBizService.java`
- 订单 Facade：`ruoyi-order/src/main/java/com/ruoyi/order/facade/impl/OrderFacade.java`
- 前端入口：`ruoyi-ui/src/main.js`
- 前端路由守卫：`ruoyi-ui/src/permission.js`
- 前端请求封装：`ruoyi-ui/src/utils/request.js`
- 前端路由：`ruoyi-ui/src/router/index.js`

