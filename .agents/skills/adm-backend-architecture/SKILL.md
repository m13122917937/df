---
name: adm-backend-architecture
description: ADM 后端的架构设计、功能开发、重构与代码审查规范。处理 Java、Spring Boot、MyBatis-Plus、MapStruct、Mapper XML、SQL、事务、配置、测试或模块依赖时使用。
---

# ADM 后端架构与规范

遵循本 Skill 与仓库根目录 `AGENTS.md`。历史代码中的跨层调用、错误事务位置或手工转换不得作为新代码先例。

## 技术约束

- 使用 Java 11、Spring Boot 2.6、MyBatis-Plus、MapStruct、Jackson、SLF4J 与 Hutool。
- 通用工具能力必须优先使用 Hutool，禁止重复手写已有的日期、集合、字符串、ID、校验、加解密、文件等工具逻辑。
- JSON 是例外：只能使用 `JacksonUtil` 或 Jackson `ObjectMapper`/`JsonNode`，禁止 FastJSON、FastJSON2 与 Hutool JSON。
- 不使用 `var`、`System.out.println`、BeanUtils 或反射复制对象；金额使用 `BigDecimal`。
- Maven 命令必须显式使用 `mvn -s D:\work\jie\adm\setting.xml ...`。
- 不提交密钥、Token、AccessKey、密码或真实敏感测试数据。

## 应用层与领域层

遵循 `Controller → Biz → Facade → Service → Mapper`：

- `ruoyi-admin`、`ruoyi-api` 是组合应用层，只放 Controller、Biz、Web Form/VO 与 Web MapStruct 转换器。
- `ruoyi-user`、`ruoyi-order`、`ruoyi-finance`、`ruoyi-analysis`、`ruoyi-system` 是领域层；领域模块不得依赖 admin、api、ui。
- `ruoyi-starter` 仅提供第三方系统的类型化基础设施客户端；`ruoyi-common`、`ruoyi-framework` 不反向依赖业务模块。
- Controller 只处理协议、校验、权限与标准响应，只调用 Biz，不接收或返回 Entity。
- Biz 处理登录态、HTTP/文件上下文与跨领域编排，只调用 Facade，不访问 Service、Mapper 或 Entity。
- Facade 是领域唯一出口，编排本领域 Service、构造 `DynamicCondition`、完成领域边界转换；不得直接注入 Mapper 或暴露 Entity、Wrapper。
- Service 是唯一事务边界与可复用领域能力，只调用本领域 Mapper 或基础设施 Client；Service 间不得互调。
- Mapper 只处理本领域持久化和 SQL。Job、MQ Listener 等入口只调用 Facade。
- 构造器注入且面向接口；Controller 用 `@RestController`/`@Controller`，Biz 与 Facade 用 `@Component`，Service 用 `@Service`。

## 领域与数据库表边界

| 领域 | 所属模块 | 允许新增/维护的业务表前缀 | 边界 |
| --- | --- | --- | --- |
| 系统与权限 | `ruoyi-system` | `sys_*` | 用户、角色、菜单、字典、参数等系统能力；不得借新需求改动既有系统表结构。 |
| 用户与资金 | `ruoyi-user` | `u_*` | 会员、公司、认证、公司账户、充值与资金流水。 |
| 主数据与商品 | `ruoyi-user` | `m_*` | 主体、银行账户、销售渠道、商品与 SKU。 |
| 报价 | `ruoyi-user` | `quote_*` | 品牌、类目、报价产品与报价历史。 |
| 订单与售后 | `ruoyi-order` | `o_*` | 订单、交易单、申请单、IMEI、挂单、退货与规则。 |
| 物流与 ERP 集成状态 | `ruoyi-order` | 既有 `e_*`、`jky_*` | 仅维护既有物流订阅、吉客云任务表；`o_express_contrast` 是历史例外，不得作为新增表前缀先例。 |
| 财务与合同 | `ruoyi-finance` | `f_*` | 账单、付款计划、付款方、扣款、合同与交易流水。 |
| 经营分析 | `ruoyi-analysis` | `ana_*` | 事实、指标、配置与同步日志；业务实体和 SQL 只能读写 `ana_*`。 |

执行以下规则：

1. 新功能先确定唯一领域归属，再创建 Entity、Facade、Service、Mapper、XML 与增量 SQL；不得因调用方便把表放进其他领域。
2. 领域模块只能直接读写本表中列出的前缀。跨领域需要数据时，通过对方 Facade 获取 BO/Query 结果，不得注入对方 Service/Mapper、引用对方 Entity 或直接跨前缀写表。
3. 跨领域查询由 Biz 组合多个 Facade 结果；不得为页面便利在业务 Mapper 中新增跨领域 Join。确需跨域报表/同步时，先明确数据所有权、读取范围与一致性方案。
4. `ruoyi-analysis` 可通过 Facade 读取源领域数据后写入 `ana_*`，不得回写 `u_*`、`m_*`、`quote_*`、`o_*`、`f_*`、`e_*` 或 `jky_*`。只有用户明确授权的菜单/任务初始化 SQL 可写 `sys_menu`、`sys_job`。
5. 新数据库前缀、修改既有业务表、跨域回写或复用旧表均需要用户明确授权；未经授权不得猜测或创建前缀。
6. 所有同步和外部回调以业务唯一键保障幂等性；数据库建立唯一约束，业务层处理重复写入。

## 对象、SQL 与事务

- Web Form/VO 与 Param/Query/BO、Param/Query/BO 与 Entity 的转换一律使用各层专属 MapStruct 转换器。
- 单表查询由 Facade 通过 `DynamicCondition.toWrapper(query[, sort])` 构造条件并调用 Service；关联、聚合、批量和特殊查询写入同领域 Mapper XML。
- 每个 Mapper 必须有同名 XML，`namespace` 等于 Mapper 接口全限定名；多参数使用 `@Param`。
- `@Transactional(rollbackFor = Exception.class)` 仅放 Service 的 `public` 写方法；事务内禁止 HTTP/RPC、第三方 SDK、文件、Excel、消息等待和大循环。
- 外部调用由 Facade 在事务外编排；外部 DTO 必须经 MapStruct 转换。

## 配置、兼容与验证

- 配置使用 `@ConfigurationProperties`，敏感值由环境变量或密钥系统提供。
- 保持既有 URL、HTTP 方法、字段、鉴权、签名、加密与响应语义兼容，除非用户明确授权。
- 实施前读取目标文件、调用方、被调用方、转换器、Mapper XML 与测试。
- 新业务规则补单元测试；Mapper、事务、外部接口补集成或契约测试；新 Mapper 补 XML 完整性测试；分析模块补 `ana_*` 隔离测试。
- 完成后执行受影响模块测试或打包，检查 Git diff 中不存在密钥、构建产物或无关改动。

交付时说明领域归属、表前缀、调用链、改动模块、验证命令/结果与未验证风险。
