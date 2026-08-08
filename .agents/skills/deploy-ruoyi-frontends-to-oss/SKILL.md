---
name: deploy-ruoyi-frontends-to-oss
description: 构建本地 ruoyi-web 与 ruoyi-ui Vue 项目，并发布各自的 dist 目录到指定的阿里云 OSS Bucket。用户要求打包、部署、上传、同步或发布任一若依前端项目到 OSS 时使用。
---

# 若依前端发布到 OSS

使用 `scripts/deploy.ps1` 执行发布。不得在命令、日志或 Skill 文件中保存、输出 AccessKey。

## 前置条件

1. 在 ADM 仓库根目录执行，或显式传入 `-RepositoryRoot`。
2. `node`、`npm` 和 `ossutil` 必须可用；先使用 `ossutil config` 完成认证。
3. 两个项目目录及其 `package.json`、`package-lock.json` 必须存在。

## 发布命令

在用户明确允许覆盖与清理远端文件后，执行：

```powershell
& '.agents\skills\deploy-ruoyi-frontends-to-oss\scripts\deploy.ps1' -Target all -RepositoryRoot 'D:\work\jie\adm' -ApproveOverwriteAndDelete
```

`-Target` 可选 `web`、`ui` 或 `all`，默认 `all`。映射固定为：

- `ruoyi-web/dist` → `oss://wujievip/`
- `ruoyi-ui/dist` → `oss://admin-wujievip-cn/`

脚本先构建并校验全部选中项目；只有全部通过才开始上传。正式同步使用 `ossutil sync --force --delete`，会覆盖同名对象并删除远端目标中不在本地 `dist` 的对象。

## 安全要求

- 未经用户明确授权，不得传入 `-ApproveOverwriteAndDelete`。
- 用户要求预检时传入 `-DryRun`；此模式仅构建和校验，不上传。
- 不自动执行 `npm install`、`npm ci`、依赖升级、Git 操作或 CDN 刷新。
- 构建、校验或上传任一步失败时停止并说明失败项目；不得继续上传其他项目。

## 交付

报告目标项目、构建结果、上传结果、删除数量及未执行项；不得输出凭据。
