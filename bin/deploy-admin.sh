#!/usr/bin/env bash
# ============================================================
# ruoyi-admin 服务器部署脚本（构建 + 备份 + 重启）
# 用法: bash deploy-admin.sh [--tag <镜像标签>]
# ============================================================
set -e

# ---------- 配置区（按实际环境修改） ----------
APP_DIR="/root/adm"                         # 代码仓库目录
GIT_URL="git@github.com:m13122917937/df.git"  # Git 仓库地址（SSH）
SERVICE_DIR="ruoyi-admin"                   # 服务模块目录
IMAGE_NAME="ruoyi-admin"
IMAGE_TAG="latest"
CONTAINER_NAME="ruoyi-admin"
PORT_MAP="7772:7772"                        # 宿主端口:容器端口
RUN_EXTRA_OPTS=""                           # 额外 docker run 参数（--network、-v、--link 等）
SPRING_PROFILE="prod"                       # 启动环境: prod / dev（对应 application-prod.yml / application-dev.yml）
JAVA_OPTS="-Xms1024m -Xmx2048m -Djava.awt.headless=true -Dspring.profiles.active=${SPRING_PROFILE}"
PARAMS=""
BACKUP_DIR="${APP_DIR}/backup"              # 备份目录
HOST_LOG_DIR="/data/logs/${IMAGE_NAME}"     # 宿主机日志目录
# ---------- 配置区结束 ----------

TIMESTAMP=$(date +%Y%m%d%H%M%S)

for tool in git mvn docker; do
  command -v "$tool" >/dev/null 2>&1 || { echo "[ERROR] 缺少工具: $tool"; exit 1; }
done

echo "[1/6] 拉取最新代码..."
if [ ! -d "$APP_DIR/.git" ]; then
  echo "[首次部署] 克隆代码仓库..."
  mkdir -p "$(dirname "$APP_DIR")"
  git clone "$GIT_URL" "$APP_DIR"
  cd "$APP_DIR"
else
  cd "$APP_DIR"
  git pull --ff-only || { echo "[ERROR] git pull 失败，请先处理本地改动"; exit 1; }
fi

echo "[2/6] 备份旧产物..."
mkdir -p "$BACKUP_DIR"
JAR_FILE=$(ls "${APP_DIR}/${SERVICE_DIR}"/target/*.jar 2>/dev/null || true)
if [ -n "$JAR_FILE" ]; then
  cp "$JAR_FILE" "$BACKUP_DIR/${IMAGE_NAME}-jar-${TIMESTAMP}.jar"
  echo "已备份 jar: $BACKUP_DIR/${IMAGE_NAME}-jar-${TIMESTAMP}.jar"
fi
if docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
  OLD_IMAGE=$(docker inspect --format '{{.Image}}' "$CONTAINER_NAME")
  docker tag "$OLD_IMAGE" "${IMAGE_NAME}:backup-${TIMESTAMP}"
  echo "已备份旧镜像: ${IMAGE_NAME}:backup-${TIMESTAMP}"
fi

echo "[3/6] Maven 打包 (跳过测试)..."
mvn -f "$APP_DIR/pom.xml" clean package -DskipTests

echo "[4/6] 构建 Docker 镜像..."
docker build --no-cache -t "${IMAGE_NAME}:${IMAGE_TAG}" "$APP_DIR/${SERVICE_DIR}"

echo "[5/6] 重启容器..."
mkdir -p "$HOST_LOG_DIR"
docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1 || true
docker run -d --name "$CONTAINER_NAME" \
  -p "$PORT_MAP" \
  -v "${HOST_LOG_DIR}:/data/logs" \
  -e JAVA_OPTS="$JAVA_OPTS" \
  -e PARAMS="$PARAMS" \
  --restart unless-stopped \
  $RUN_EXTRA_OPTS \
  "${IMAGE_NAME}:${IMAGE_TAG}"

echo "[6/6] 清理悬空镜像..."
docker image prune -f >/dev/null 2>&1 || true

echo "=========================================="
echo "部署完成: ${CONTAINER_NAME} (${IMAGE_NAME}:${IMAGE_TAG})"
echo "备份: ${BACKUP_DIR}/ + ${IMAGE_NAME}:backup-${TIMESTAMP}"
echo "回滚: docker run 使用镜像 ${IMAGE_NAME}:backup-${TIMESTAMP}"
echo "=========================================="
docker ps --filter "name=${CONTAINER_NAME}"
