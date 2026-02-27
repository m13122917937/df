@echo off
:: 自动切换到 UTF-8 编码
chcp 65001 >nul
::============================================================
:: 华为云 SWR 自动化构建与推送脚本 (Windows)
:: 功能：Maven 构建 + Docker 登录 + 构建镜像 + 推送 SWR
:: 使用前请修改下方配置项
::============================================================

::---------- 可配置参数 ----------
set REGION=cn-north-4
set SWR_REGISTRY=swr.%REGION%.myhuaweicloud.com
set PROJECT_NAMESPACE=xiehaitao
set IMAGE_NAME=ruoyi-api
set IMAGE_TAG=1.0.2

:: Maven 构建参数（可选）
set MAVEN_OPTS=-Dmaven.test.skip=true
::--------------------------------

echo ============================
echo  开始执行构建与部署任务
echo ============================
echo 当前时间：%date% %time%

:: Step 1: 清理并构建 Maven 项目
echo.
echo [1/4] 正在执行 Maven clean install...
cd ../
call mvn clean install -B -Dmaven.repo.local="D:\software\maven\rep" %MAVEN_OPTS%
if %errorlevel% neq 0 (
    echo [错误] Maven 构建失败，停止执行。
    pause
    exit /b 1
)
echo [成功] Maven 构建完成。

:: Step 2: 构建 Docker 镜像（假设 Dockerfile 在项目根目录）
echo.
echo [2/4] 正在构建 Docker 镜像: %IMAGE_NAME%:%IMAGE_TAG%
cd ./%IMAGE_NAME%/
set DOCKER_BUILDKIT=0
set DOCKER_CONTENT_TRUST=0
docker build --no-cache  -t %IMAGE_NAME%:%IMAGE_TAG% .
if %errorlevel% neq 0 (
    echo [错误] Docker 构建失败，请检查 Dockerfile。
    pause
    exit /b 1
)
echo [成功] Docker 镜像构建完成。

:: Step 4: 打标签（Tag 镜像到 SWR 仓库）
set FULL_IMAGE=%SWR_REGISTRY%/%PROJECT_NAMESPACE%/%IMAGE_NAME%:%IMAGE_TAG%
echo.
echo [3/4] 正在为镜像打标签: %FULL_IMAGE%
docker tag %IMAGE_NAME%:%IMAGE_TAG% %FULL_IMAGE%
if %errorlevel% neq 0 (
    echo [错误] 镜像打标签失败。
    pause
    exit /b 1
)
echo [成功] 镜像已打标签。

:: Step 5: 推送镜像到 SWR
echo.
echo [4/4] 正在推送镜像到 SWR...
docker push %FULL_IMAGE%
if %errorlevel% neq 0 (
    echo [错误] 镜像推送失败，请检查权限或网络。
    pause
    exit /b 1
)

echo.
echo ✅ 镜像已成功推送至 SWR:
echo     %FULL_IMAGE%
echo.
echo 🚀 部署完成！
docker system prune -a -f --volumes
pause