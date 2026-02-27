FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:11-jre-slim
MAINTAINER xht
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime &&  echo "Asia/Shanghai" > /etc/timezone
# 替换为国内 Debian 镜像源（以清华源为例）
RUN sed -i 's|http://deb.debian.org|https://mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list && \
    sed -i 's|http://security.debian.org|https://mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list
# 安装字体依赖（防止 EasyExcel 报错）
RUN apt-get update  && apt-get install -y --no-install-recommends  fontconfig libfreetype6 libfontconfig1  && rm -rf /var/lib/apt/lists/* && fc-cache -fv
VOLUME /tmp
ENV PARAMS=""
ENV JAVA_OPTS "-Xms1024m -Xmx2048m -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=test"
ADD target/*.jar /data/app.jar
RUN bash -c "touch /data/app.jar"
WORKDIR /data
EXPOSE 7772
ENTRYPOINT ["sh", "-c","exec java $JAVA_OPTS -jar /data/app.jar $PARAMS"]