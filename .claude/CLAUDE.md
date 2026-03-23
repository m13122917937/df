# ADM - 若依管理系统开发规范

## 项目概述

本项目是基于 **RuoYi 框架** 开发的管理系统，采用领域驱动设计(DDD)分层架构。

- **项目名称**: 若依管理系统 (ADM)
- **版本**: 3.9.0
- **构建工具**: Maven

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
| FastJSON2 | 2.0.58 | JSON解析 |
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
├── ruoyi-starter/        # 启动器模块
└── ruoyi-ui/             # 前端UI
```

## 架构原则

### 分层架构规则

1. **严格分层调用**：
   - Controller 层 → 只能调用 Biz 层
   - Biz 层 → 只能调用 Service 层
   - Service 层 → 只能调用 Mapper 层
   - **严禁跨层调用**

2. **职责分工**：
   - **Controller 层**: 只做参数校验和请求响应处理，**严禁编写业务逻辑**
   - **Biz 层**: 业务逻辑处理，编排业务流程
   - **Service 层**: 通用方法封装，可复用的业务逻辑
   - **Mapper 层**: 数据访问层

3. **对象转换规则**：
   - 每一层之间的对象转化必须通过 MapStruct 进行
   - 每一层只能使用**专属**的 MapStruct 对象
   - Controller **不返回 Entity**，只返回 Response DTO
   - Entity → DTO 转换必须通过 MapStruct

### 事务管理

- `@Transactional` 注解必须加在 **Service 层**
- 注意代理失效问题（避免自调用）
- 事务粒度要小，避免在事务中进行远程RPC调用或耗时操作

### 配置管理

- 使用 `@ConfigurationProperties` 绑定配置，**严禁在代码中硬编码配置项**
- 敏感信息必须通过环境变量注入，不得提交到代码仓库

### 编码规范

- 项目所有代码必须使用 **UTF-8** 编码
- **禁止使用 `var`**，必须显式声明类型
- 所有公共方法必须有 **JavaDoc** 注释
- 函数长度不得超过 **50 行**
- 严禁使用 `System.out.println`，必须使用 SLF4J 日志
- 必须采用领域驱动设计(DDD)，业务逻辑不准写在 Controller 层

## 开发流程

1. **修改代码前必须先读取文件**
2. **每次只输出一个文件的修改内容**
3. 提交前自我检查是否符合所有规范

## 质量检查清单

在完成代码后，请检查：

- [ ] 是否遵守了分层调用规则？
- [ ] 业务逻辑是否不在Controller层？
- [ ] 对象转换是否使用了MapStruct？
- [ ] Controller是否只返回DTO而不返回Entity？
- [ ] @Transactional是否正确加在Service层？
- [ ] 配置是否使用@ConfigurationProperties绑定？
- [ ] 是否禁用了var，所有变量都显式声明类型？
- [ ] 所有公共方法都有JavaDoc注释？
- [ ] 函数长度不超过50行？
- [ ] 没有使用System.out.println，只用SLF4J日志？
- [ ] 编码是UTF-8？
