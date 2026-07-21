# ADM - 若依管理系统开发规范

## 项目概述

本项目是基于 **RuoYi 框架** 开发的管理系统，采用领域驱动设计(DDD)分层架构。

- **项目名称**: 若依管理系统 (ADM)
- **版本**: 3.9.0
- **构建工具**: Maven

## 规范效力与适用范围

1. 本文件是仓库根目录的强制执行规范，适用于后端、前端、SQL、配置、测试及构建文件。
2. 新增代码必须完全遵守本规范；修改历史代码时，至少要保证本次触及的调用链不新增违规，并应同步修复能够安全收口的既有违规。
3. 不得以“历史代码原来如此”为理由复制跨层调用、手工对象转换、错误事务位置或明文配置。
4. 需求与本规范冲突时，应先说明冲突和风险；只有用户明确授权后才能采用例外方案。
5. 未经用户明确要求，不得提交、推送代码，不得修改接口协议、数据库旧表结构或全局配置行为。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 11 | 必须基于Java 11开发 |
| Spring Boot | 2.6.15 | 核心框架 |
| Spring Framework | 5.3.39 | 基础框架 |
| MyBatis-Plus | 3.5.3.1 | ORM框架 |
| Lombok | 1.18.22 | 简化代码 |
| MapStruct | 1.5.2.Final | 对象映射转换 |
| Druid | 1.2.23 | 数据库连接池 |
| RocketMQ | 5.0.7 | 消息队列 |
| Hutool | 5.8.24 | 工具类库 |
| Jackson | Spring Boot 管理版本 | 唯一允许的 JSON 序列化与解析组件 |
| EasyExcel | 3.3.2 | Excel处理 |
| SLF4J + Logback | 1.2.13 | 日志框架 |

## 项目模块结构

```
adm/
├── ruoyi-admin/          # 主应用启动模块
├── ruoyi-framework/      # 框架层配置
├── ruoyi-system/         # 系统核心模块
├── ruoyi-quartz/         # 定时任务模块
├── ruoyi-common/         # 通用工具模块
├── ruoyi-api/            # API接口模块
├── ruoyi-user/           # 用户业务模块
├── ruoyi-order/          # 订单业务模块
├── ruoyi-finance/        # 财务业务模块
├── ruoyi-analysis/       # 经营分析领域模块
├── ruoyi-starter/        # 启动器模块
└── ruoyi-ui/             # 前端UI
```

## 架构原则

### 模块边界

项目分为组合应用层和领域层，两者的代码位置与职责不得混放。

1. **组合应用层**：位于 `ruoyi-admin`、`ruoyi-api` 等启动或接口模块。
   - 只放置 Controller、Biz、Web Request/Response VO、Web MapStruct 转换器等应用组合代码。
   - Controller 和 Biz 可以组合一个或多个领域能力，但不得实现领域规则或直接访问数据库。
2. **领域层**：位于 `ruoyi-order`、`ruoyi-user`、`ruoyi-finance`、`ruoyi-analysis` 等业务模块。
   - 放置 Facade 接口及实现、Service、Mapper、Mapper XML、Entity、BO、Param、Query、领域 MapStruct 转换器。
   - Facade 是领域模块对组合应用层暴露的唯一入口；Service、Mapper、Entity 均为领域内部实现，不得被 Controller 或 Biz 直接依赖。

### 强制调用链

标准接口调用链必须严格遵守：

`Controller → Biz → Facade → Service → Mapper`

1. **Controller**
   - 只能调用 Biz，不得调用 Facade、Service 或 Mapper。
   - 只负责协议适配、参数校验、权限注解和响应封装，不得编写业务逻辑。
   - 不得接收或返回 Entity；对外响应使用 Web VO/DTO。
2. **Biz**
   - 只能调用一个或多个领域 Facade，不得调用 Service、Mapper 或直接操作 Entity。
   - 负责登录用户、文件、HTTP 上下文等应用信息处理，以及跨领域 Facade 的组合编排。
   - 不得执行数据库查询，不得持有 Mapper 或 Service。
3. **Facade**
   - 接口与实现均位于领域模块，是领域能力的唯一对外出口。
   - 可以编排本领域多个 Service；数据访问必须通过 Service，禁止直接注入 Mapper。
   - 负责 Param/Query/BO 边界、`DynamicCondition` 查询条件构造以及 Entity → BO 转换。
   - Facade 对外不得暴露 Mapper、Wrapper 或 Entity。
4. **Service**
   - 位于领域模块，负责统一、可复用的领域能力封装与事务边界。
   - 只能调用 Mapper；确需访问吉客云等外部系统时，只能调用已封装的基础设施客户端。
   - Service 之间禁止互相调用；多个 Service 的业务编排必须上移到 Facade。
   - 不得依赖 Controller、Biz、Web VO 或 HttpServletRequest/Response。
5. **Mapper**
   - 只能处理 Entity 持久化和数据库访问，不得包含业务逻辑。
   - Mapper 不得调用 Service、Facade 或外部接口。
6. **Job、MQ Listener 等入口**
   - 视为组合入口，只能调用 Facade，不得直接调用 Service 或 Mapper。

任何跳层、反向依赖或旁路调用均属于架构违规。

### 分层注解与依赖规则

1. Controller 使用 `@RestController`/`@Controller`，Biz 使用 `@Component`，Facade 实现使用 `@Component`，Service 实现使用 `@Service`，Mapper 使用 `@Mapper` 或由统一扫描注册。
2. Biz 和 Facade 实现禁止使用 `@Service`；Facade 禁止声明 `@Transactional`，避免混淆领域入口与事务服务的职责。
3. Service 是唯一允许声明数据库事务的业务层。Controller、Biz、Facade、Job、Listener 和 Mapper 均不得开启事务。
4. 依赖必须面向接口；构造器注入优先，禁止字段注入和通过 Spring 上下文静态获取 Bean。
5. 领域模块不得依赖 `ruoyi-admin`、`ruoyi-api` 或 `ruoyi-ui`；公共模块不得反向依赖具体业务模块。
6. Service 访问外部系统时，只能依赖 `ruoyi-starter` 等基础设施模块暴露的类型化 Client；Controller、Biz、Facade、Mapper 不得直接使用 HTTP SDK。
7. 外部调用必须位于数据库事务之外；外部 DTO 与领域对象之间必须通过 MapStruct 转换，不得把第三方 DTO 透传到上层。

### 对象与 MapStruct 转换规则

1. 发生对象类型转换时必须使用 MapStruct，禁止通过手工 `new + set`、BeanUtils、JSON 序列化或反射复制对象。
2. 组合应用层使用专属 Web MapStruct 转换器完成 Web VO/DTO ↔ 领域 Param/Query/BO 的转换。
3. 领域层使用专属领域 MapStruct 转换器完成 Param ↔ Entity、Entity ↔ BO、外部接口 DTO ↔ 领域对象的转换。
4. 不同边界的转换器不得跨层复用；Controller/Biz 不得直接使用领域 Entity 转换器。
5. Controller 不得返回 Entity；Facade 不得对外返回 Entity；Mapper 只接收和返回 Entity 或明确的数据库投影对象。
6. 查询条件赋值、持久化主键回填、状态流转和审计字段更新允许使用 `set`，但不得利用 `set` 完成对象复制。

### Mapper 与 SQL 强制规则

1. 每一个 Mapper 接口都必须存在同名的 `Mapper.xml`，路径必须符合全局 `mapper-locations` 配置。
2. `Mapper.xml` 的 `namespace` 必须与 Mapper 接口全限定名完全一致。
3. 标准单表查询必须由 Facade 使用 `DynamicCondition.toWrapper(query[, sort])` 构造 Wrapper，再调用 Service 的统一 `list/getOne/count/update/remove` 方法。
4. 领域 Query 必须使用 `@QueryField` 明确非默认操作符和特殊数据库列名；禁止在 Facade 中手写重复的 `eq/ge/le/in` 条件链。
5. Service 不得自行拼装 `LambdaQueryWrapper`、`QueryWrapper` 或通过 `.last()` 拼接 SQL。
6. 多表关联、聚合统计、批量 SQL、特殊有效期匹配以及 `DynamicCondition` 无法表达的查询，必须声明 Mapper 方法并在同名 XML 中实现。
7. Mapper XML 只负责 SQL，不得包含领域判断或业务状态流转。
8. Mapper 方法使用明确的参数和返回类型；多参数必须使用 `@Param`，禁止使用无类型 Map 传递业务参数。
9. 新增 Mapper 时必须同步新增 XML 完整性测试，验证文件存在且 `namespace` 正确。

### Analysis 模块数据库隔离规则

1. `ruoyi-analysis` 的业务持久化实体只能映射到 `ana_*` 新表，严禁映射、修改或写入既有业务表。
2. 分析模块的业务 SQL 只能读写 `ana_*` 表；只有用户明确授权的菜单和定时任务初始化 SQL 可以写入 `sys_menu`、`sys_job`。
3. 新增或修改分析模块持久化实体后，必须通过数据库隔离测试，确保 `@TableName` 仍指向 `ana_*` 表。

### 数据库变更规则

1. 未经用户明确授权，禁止修改既有业务表的结构、数据、索引、触发器和存储过程，禁止复用旧表承载新模块数据。
2. 新模块必须使用独立表名前缀和独立增量 SQL；建表、索引、菜单及任务脚本应按功能拆分，禁止改写历史迁移脚本。
3. 只有菜单和定时任务初始化可按需求写入 `sys_menu`、`sys_job`；不得借此修改其他系统表。
4. SQL 脚本不得包含无授权的历史数据回填、全表更新或破坏性操作。需要初始化数据时必须说明幂等键和重复执行行为。
5. Entity 字段、Mapper SQL 和数据库脚本必须同步变更；字段名、类型、精度、空值规则及唯一键必须保持一致。
6. 所有幂等同步场景必须建立数据库唯一约束，并在业务层处理重复写入，不能只依赖先查询后插入。

### 事务管理

1. `@Transactional` 只能加在 Service 的 `public` 方法上；写操作默认使用 `rollbackFor = Exception.class`。
2. 禁止事务方法自调用，禁止在 Controller、Biz、Facade、Job、Listener 或 Mapper 上添加事务注解。
3. 事务内禁止远程 RPC、HTTP 调用、文件上传下载、Excel 解析、消息等待和大批量耗时循环。
4. 多个持久化动作需要原子性时，应由一个 Service 事务方法统一调用相关 Mapper；不得通过 Service 互调拼接事务。
5. Facade 负责调用外部系统与事务 Service 的顺序编排；跨系统一致性使用幂等、状态机、重试或补偿，不使用超长本地事务。
6. 调整历史事务位置时，必须先确认原有回滚边界，不能只删除上层注解而导致原子性丢失。

### 配置管理

1. 使用 `@ConfigurationProperties` 绑定配置，严禁在业务代码中通过散落的 `@Value` 或常量硬编码配置项。
2. 密码、Token、AccessKey、Secret、AppKey 等敏感信息必须通过环境变量或外部密钥系统注入，不得提交到仓库。
3. 日志、异常和测试数据不得输出真实敏感信息；发现已泄露凭据时应停止传播并提示立即轮换。
4. 新增配置必须提供清晰前缀、默认值策略、校验规则和环境配置说明。

### JSON 强制规则

1. 项目 JSON 序列化、反序列化和树模型解析只能使用 Jackson，优先复用 `com.ruoyi.common.utils.JacksonUtil`。
2. 禁止引入或使用 FastJSON、FastJSON2，包括 `com.alibaba.fastjson.*`、`com.alibaba.fastjson2.*`。
3. 禁止使用 Hutool JSON，包括 `cn.hutool.json.JSONUtil`、`JSONObject`、`JSONArray` 等类型。
4. 新增 Maven 依赖时必须检查传递依赖；发现 FastJSON/FastJSON2 时必须排除，不得进入运行时依赖树。
5. 外部接口 JSON 字符串统一通过 `JacksonUtil` 或 Jackson `ObjectMapper`、`JsonNode` 处理。

### 编码规范

- 项目所有代码必须使用 **UTF-8** 编码
- **禁止使用 `var`**，必须显式声明类型
- 所有公共方法必须有 **JavaDoc** 注释
- 函数长度不得超过 **50 行**
- 严禁使用 `System.out.println`，必须使用 SLF4J 日志
- 必须采用领域驱动设计(DDD)，业务逻辑不准写在 Controller 层

### 接口兼容与异常处理

1. 未经明确授权，不得修改现有 URL、HTTP 方法、请求字段、响应字段、枚举值及其业务语义。
2. Controller 统一返回项目标准响应对象；业务异常使用项目统一异常类型和错误码，禁止返回堆栈或底层数据库异常。
3. 空值、分页、金额、时间和枚举的语义必须明确；金额使用 `BigDecimal`，禁止使用浮点数计算。
4. 接口加密、签名和鉴权属于协议的一部分，修复业务问题时不得旁路或擅自修改。

### 前端执行规范

1. API 请求统一放在 `src/api`，页面组件不得散落硬编码后端地址。
2. 页面间距、颜色、字体、卡片、表格、弹窗和深色模式必须复用全局设计变量；禁止用页面级全局覆盖修补单页问题。
3. 修改列表字段、弹窗或主题时必须限定作用域，验证浅色/深色、常见分辨率和相邻页面，不得影响未授权区域。
4. 列表应优先采用合理列宽、换行和响应式布局；确需横向滚动时应由统一表格容器负责，不得使用固定遮罩隐藏滚动条。
5. 前端不得在浏览器保存或打印敏感信息；权限展示不能替代后端权限校验。

### 依赖与构建规则

0. 本项目所有 Maven 命令必须显式使用项目配置：`mvn -s D:\work\jie\adm\setting.xml ...`；禁止使用用户目录下的默认 Maven 配置。
   `setting.xml` 已将本地仓库固定为 `D:\software\maven-rep`，不得改用 `C:\Users\...\.m2\repository`，也不得在命令中覆盖 `maven.repo.local`。
1. 新增依赖前必须确认现有依赖无法满足需求，并检查版本兼容、许可证、漏洞及传递依赖。
2. 删除依赖前必须结合源码引用、插件使用、反射加载、运行时配置和 `dependency:tree` 核实，不得只依据静态分析结论。
3. 禁止在父 POM 或模块 POM 中永久配置 `skipTests=true`；临时跳过测试只能由执行命令显式指定并在交付说明中披露。
4. 修改依赖后必须执行受影响模块编译和测试；涉及公共模块、父 POM 或运行时依赖时必须执行完整构建。

## 开发流程

1. 开始任务前读取本文件、相关子目录规范、当前 Git 状态和目标调用链，明确改动范围及验收条件。
2. 修改代码前必须先读取目标文件及其直接调用方、被调用方、对象转换器、Mapper XML 和相关测试。
3. 保留工作区中用户已有改动；不得覆盖无关文件，不得使用 `git reset --hard`、`git checkout --` 等破坏性命令。
4. 每次补丁只修改一个文件；跨文件任务按调用链逐层实施，避免未完成的半套架构长期留在工作区。
5. 禁止顺手重构无关代码；发现范围外问题应记录并说明，未经授权不得扩大修改范围。
6. 实现完成后执行格式检查、编译、自动化测试和必要的接口/页面验证；失败必须定位，不能仅通过跳过测试交付。
7. 提交前检查 Git diff，确认没有密钥、构建产物、临时文件、无关格式化或数据库旧表变更。
8. 未经用户明确要求，不得执行 `git add`、`git commit`、`git push`；执行后必须如实报告分支、提交和推送结果。
9. 最终交付必须说明修改文件、核心行为、验证命令与结果，以及尚未验证的风险。

## 测试与验收门槛

1. 新增或修改业务规则必须有单元测试；涉及数据库、Mapper XML、事务或外部接口时必须增加相应集成测试或契约测试。
2. 架构调整必须验证：分层依赖、MapStruct 转换、Mapper XML namespace、`DynamicCondition` 查询、事务位置及数据库表隔离。
3. 同步任务必须覆盖上线日期边界、幂等键、重复执行、失败重试、延迟修改、退款变化和全店铺/全仓库范围。
4. 修复缺陷必须至少包含一个能够复现原问题的回归用例。
5. 后端最低验收为受影响模块 `test`/`package` 成功；前端最低验收为 lint（若项目提供）与生产构建成功。
6. 因环境、外部服务或存量失败无法完成验证时，必须列出未执行项、具体原因和潜在影响，不得描述为“已全部验证”。

## 质量检查清单

在完成代码后，请检查：

- [ ] 是否遵守了分层调用规则？
- [ ] Controller → Biz → Facade → Service → Mapper 调用链是否完整，是否存在跨层依赖？
- [ ] Controller、Biz 是否位于 `ruoyi-admin`/`ruoyi-api` 等组合应用模块？
- [ ] Facade、Service、Mapper、Entity 是否位于对应领域模块？
- [ ] Controller 是否只调用 Biz，Biz 是否只调用 Facade？
- [ ] Facade 是否未直接调用 Mapper，Service 是否未调用其他 Service？
- [ ] Job、MQ Listener 是否只调用 Facade？
- [ ] 业务逻辑是否未写入 Controller？
- [ ] 对象类型转换是否全部使用 MapStruct？
- [ ] 是否不存在通过 `new + set`、BeanUtils 或反射完成的层间对象转换？
- [ ] Controller 是否只使用 Web VO/DTO，Facade 是否未暴露 Entity 或 Wrapper？
- [ ] 每个 Mapper 是否都有同名 XML，且 namespace 正确？
- [ ] 标准单表查询是否使用 `DynamicCondition.toWrapper()`，复杂 SQL 是否位于 Mapper XML？
- [ ] Analysis 模块是否只读写 `ana_*` 业务表？
- [ ] 是否未修改未经授权的旧业务表，新增模块是否使用独立表和增量 SQL？
- [ ] `@Transactional` 是否仅位于 Service 公共方法，事务中是否没有远程或耗时操作？
- [ ] 配置是否使用 `@ConfigurationProperties` 绑定？
- [ ] JSON 是否只使用 Jackson，且不存在 FastJSON/FastJSON2/Hutool JSON？
- [ ] 是否保持现有接口协议、鉴权、加密和响应语义兼容？
- [ ] 前端样式是否复用全局变量，并验证浅色、深色及相邻页面？
- [ ] 新增/删除依赖是否有依据，并完成依赖树和构建验证？
- [ ] 是否禁用了 `var`，所有变量都显式声明类型？
- [ ] 所有公共方法是否都有 JavaDoc？
- [ ] 函数长度是否不超过 50 行？
- [ ] 是否没有使用 `System.out.println`，只使用 SLF4J？
- [ ] 编码是否为 UTF-8？
- [ ] 是否补齐回归测试并通过受影响模块构建？
- [ ] Git diff 是否不存在密钥、构建产物、临时文件和无关改动？
