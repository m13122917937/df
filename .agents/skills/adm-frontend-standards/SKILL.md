---
name: adm-frontend-standards
description: ADM 两个 Vue 2 前端项目 ruoyi-ui 与 ruoyi-web 的开发、重构、界面实现和代码审查规范。修改页面、组件、API、路由、状态、样式、构建或前端安全逻辑时使用。
---

# ADM 前端代码规范

约束 `ruoyi-ui` 与 `ruoyi-web` 的前端变更。保留各自运行时、认证和请求链路差异，不跨项目复制内部实现。

## 项目边界

- `ruoyi-ui` 是后台管理端，使用 Vue 2.6、Vue Router 3、Vuex 3、Element UI 2 与 Axios。
- `ruoyi-web` 是业务客户端，使用独立 Vue 2 运行时、Axios 与既有 AES 响应解密链路。
- 两项目独立安装、构建、API 和请求拦截器；不跨目录共享内部代码，除非先建立明确公共包。
- 使用当前 Vue Options API、`@` 路径别名和文件局部风格；不为局部需求引入新框架或状态库。

## 接口与安全

- 所有请求只放各项目 `src/api`；页面/组件不得硬编码后端域名或自行创建 Axios 实例。
- API 按业务域归档，函数采用语义化名称与明确 `params`/`data`；保持 URL、HTTP 方法与响应字段兼容。
- 复用 `src/utils/request.js` 的认证、错误、下载和重复提交处理。
- 修改 `ruoyi-web` 时保留 AES 解密、认证和异常语义；修改 `ruoyi-ui` 时保留 Bearer Token、RuoYi 标准响应和下载行为。
- 不在日志、缓存、错误提示或页面中输出 Token、AES Key、个人信息、支付信息或其他敏感数据。

## 页面、组件与状态

- 页面放 `src/views/<业务域>`；页面私有能力放同域 `components/`，跨页面复用能力放 `src/components/`。
- 页面处理路由参数、加载状态和业务编排；表单、弹窗、表格工具栏、筛选区按职责拆分。
- Props 使用明确类型和默认值，保持单向数据流；子组件通过事件通知父组件，不直接修改 prop。
- 异步请求覆盖 loading、成功、空数据和失败；写操作避免重复提交，成功后刷新必要数据。
- 仅在确有需求时修改路由、Vuex、权限或菜单；前端展示权限不替代后端鉴权。

## 样式与体验

- 优先复用全局设计变量、Element UI、既有布局和公共样式。
- 默认使用 `<style scoped lang="scss">`；仅在 `append-to-body` 等必要情形使用最小范围非 scoped 样式并说明原因。
- 不通过页面级全局覆盖修复单页问题；验证浅色/深色、常见分辨率和相邻页面。
- 表格优先使用合理列宽、换行与响应式布局；不得隐藏横向滚动条。
- 保持键盘可用性、表单标签、错误反馈、空状态和危险操作确认。

## 质量与验证

- 保持 import、引号、缩进等局部风格；不做无关全量格式化。
- 禁止新增 `console.log`、`console.warn`、调试代码与敏感信息输出；不直接修改 `dist` 或 `node_modules`。
- `ruoyi-ui` 至少执行 `npm run lint` 与 `npm run build:prod`；`ruoyi-web` 至少执行 `npm run build:prod`，改动受测逻辑时执行对应 Jest 测试。
- 实施前读取目标页面、关联 API、请求拦截器、路由/权限、相邻组件与样式。
- 交付时说明页面/API 改动、兼容性、验证结果和未验证风险。
