@echo off
:: 自动切换到 UTF-8 编码
chcp 65001 >nul
::============================================================
:: 阿里云 ACR 自动化构建与推送脚本 (Windows)
:: 功能：Maven 构建 + Docker 登录 + 构建镜像 + 推送 ACR
:: 使用前请修改下方配置项
::============================================================

::---------- 可配置参数 ----------
set ACR_REGISTRY=crpi-4norowu43xncrxa7.cn-shanghai.personal.cr.aliyuncs.com
set ACR_NAMESPACE=default
set IMAGE_NAME=ruoyi-api
set IMAGE_TAG=1.0.2
set ACR_USERNAME=hahhapai
set ACR_PASSWORD=xht654321

:: Maven 构建参数（可选）
set MAVEN_OPTS=-Dmaven.test.skip=true
::--------------------------------

echo ============================
echo  开始执行构建与部署任务
echo ============================
echo 当前时间：%date% %time%

set JAVA_HOME=D:\software\javva\
set PATH=%JAVA_HOME%\bin;%PATH%

:: Step 1: 清理并构建 Maven 项目
echo.
echo [1/5] 正在执行 Maven clean install...
cd ../
set MAVEN_SETTINGS=D:\work\jie\adm\setting.xml
call mvn clean install -B -Dmaven.repo.local="D:\software\maven-rep" -s "%MAVEN_SETTINGS%" %MAVEN_OPTS%
if %errorlevel% neq 0 (
    echo [错误] Maven 构建失败，停止执行。
    pause
    exit /b 1
)
echo [成功] Maven 构建完成。

:: Step 2: 登录阿里云 ACR
echo.
echo [2/5] 正在登录阿里云容器镜像服务...
docker login --username=%ACR_USERNAME% %ACR_REGISTRY% -p %ACR_PASSWORD%
if %errorlevel% neq 0 (
    echo [错误] Docker 登录失败，请检查用户名密码。
    pause
    exit /b 1
)
echo [成功] 阿里云 ACR 登录成功。

:: Step 3: 构建 Docker 镜像（假设 Dockerfile 在项目根目录）
echo.
echo [3/5] 正在构建 Docker 镜像: %IMAGE_NAME%:%IMAGE_TAG%
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

:: Step 4: 打标签（Tag 镜像到 ACR 仓库）
set FULL_IMAGE=%ACR_REGISTRY%/%ACR_NAMESPACE%/%IMAGE_NAME%:%IMAGE_TAG%
echo.
echo [4/5] 正在为镜像打标签: %FULL_IMAGE%
docker tag %IMAGE_NAME%:%IMAGE_TAG% %FULL_IMAGE%
if %errorlevel% neq 0 (
    echo [错误] 镜像打标签失败。
    pause
    exit /b 1
)
echo [成功] 镜像已打标签。

:: Step 5: 推送镜像到 ACR
echo.
echo [5/5] 正在推送镜像到阿里云 ACR...
docker push %FULL_IMAGE%
if %errorlevel% neq 0 (
    echo [错误] 镜像推送失败，请检查权限或网络。
    pause
    exit /b 1
)

echo.
echo ✅ 镜像已成功推送至阿里云 ACR:
echo     %FULL_IMAGE%
echo.
echo 🚀 部署完成！
docker system prune -a -f --volumes
pause