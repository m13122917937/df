@echo off
chcp 65001 >nul
@echo off
::============================================================
:: Alibaba Cloud ACR Build & Push Script (Windows)
:: Func: Maven Build + Docker Login + Build Image + Push ACR
::============================================================

::---------- Config ----------
set ACR_REGISTRY=crpi-4norowu43xncrxa7.cn-shanghai.personal.cr.aliyuncs.com
set ACR_NAMESPACE=fydf
set IMAGE_NAME=ruoyi-admin
set IMAGE_TAG=1.0.3
set ACR_USERNAME=hahhapai
set ACR_PASSWORD=xht654321.

set MAVEN_OPTS=-Dmaven.test.skip=true
::--------------------------------

echo ============================
echo   Start build and deploy
echo ============================
echo Current time: %date% %time%

set JAVA_HOME=D:\software\javva\
set PATH=%JAVA_HOME%\bin;%PATH%

:: Step 1: Maven clean install
echo.
echo [1/4] Running Maven clean install...
cd ..\
set MAVEN_SETTINGS=D:\work\jie\adm\setting.xml

call mvn clean install -B -s "%MAVEN_SETTINGS%" %MAVEN_OPTS%
if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed, stop.
    pause
    exit /b 1
)
echo [SUCCESS] Maven build completed.

:: Step 2: Login Alibaba Cloud ACR
echo.
echo [2/4] Logging into Alibaba Cloud ACR...
docker login --username=%ACR_USERNAME% %ACR_REGISTRY% -p %ACR_PASSWORD%
if %errorlevel% neq 0 (
    echo [ERROR] Docker login failed, check username and password.
    pause
    exit /b 1
)
echo [SUCCESS] ACR login succeeded.

:: Step 3: Build Docker image
set FULL_IMAGE=%ACR_REGISTRY%/%ACR_NAMESPACE%/%IMAGE_NAME%:%IMAGE_TAG%
echo.
echo [3/4] Building Docker image: %FULL_IMAGE%
cd ./%IMAGE_NAME%/
set DOCKER_BUILDKIT=0
set DOCKER_CONTENT_TRUST=0
docker build --no-cache -t %FULL_IMAGE% .
if %errorlevel% neq 0 (
    echo [ERROR] Docker build failed, check Dockerfile.
    pause
    exit /b 1
)
echo [SUCCESS] Docker image build completed.

:: Step 4: Push image to ACR
echo.
echo [4/4] Pushing image to Alibaba Cloud ACR...
docker push %FULL_IMAGE%
if %errorlevel% neq 0 (
    echo [ERROR] Push failed, check permission or network.
    pause
    exit /b 1
)

echo.
echo Success: Image pushed to Alibaba Cloud ACR
echo     %FULL_IMAGE%
echo.
echo [Cleanup] Cleaning up dangling images...
for /f "tokens=*" %%i in ('docker images -f "dangling=true" -q') do docker rmi -f %%i >nul 2>&1
echo.
echo Done! Deployment finished.
pause
