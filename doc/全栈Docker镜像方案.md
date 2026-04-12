# Fly Music 全栈 Docker 镜像（推荐方案）

## 方案说明

将所有依赖打包到 Docker 镜像中，通过 docker-compose 管理多个容器。

## 镜像包含

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | Java 运行环境 |
| Maven | 3.9 | Java 构建工具 |
| Node.js | 20 | 前端构建 |
| MySQL | 8.0 | 数据库 |
| Redis | 7 | 缓存 |
| Nginx | Alpine | Web 服务器 |

## 使用方法

### 1. 构建镜像

```powershell
# 构建并打包
docker build -t fly-music:full -f Dockerfile.full .
```

### 2. 打包成分发文件

```powershell
# 打包为 tar 文件（约 3-5GB）
docker save fly-music:full -o fly-music-full.tar

# 压缩
gzip fly-music-full.tar
```

### 3. 在其他机器部署

```powershell
# 加载镜像
docker load -i fly-music-full.tar.gz

# 启动
docker run -d -p 80:80 -p 8080:8080 -p 3306:3306 -p 6379:6379 --name fly-music fly-music:full
```

## 完整 Dockerfile

```dockerfile
# =============================================
# Fly Music 全栈 Docker 镜像
# 包含：JDK 21、Maven 3.9、Node.js 20、MySQL 8.0、Redis 7
# =============================================

FROM ubuntu:22.04

# ---------------- 环境变量 ----------------
ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/opt/jdk-21
ENV MAVEN_HOME=/opt/maven
ENV NODE_HOME=/opt/node-20
ENV MYSQL_ROOT_PASSWORD=root123456
ENV MYSQL_DATABASE=fly_music

# PATH
ENV PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$NODE_HOME/bin:/usr/local/mysql/bin:/usr/local/bin:$PATH

# ---------------- 安装系统依赖 ----------------
RUN apt-get update && apt-get install -y \
    wget curl git unzip xz-utils \
    # MySQL
    mysql-server mysql-client \
    # Redis
    redis-server \
    # Nginx
    nginx \
    # Python（用于某些工具）
    python3 python3-pip \
    && rm -rf /var/lib/apt/lists/*

# ---------------- 安装 JDK 21 ----------------
RUN wget -q https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz -O /tmp/jdk.tar.gz && \
    mkdir -p /opt && tar -xzf /tmp/jdk.tar.gz -C /opt && \
    mv /opt/jdk-21* /opt/jdk-21 && rm /tmp/jdk.tar.gz

# ---------------- 安装 Maven 3.9 ----------------
RUN wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz -O /tmp/maven.tar.gz && \
    mkdir -p /opt && tar -xzf /tmp/maven.tar.gz -C /opt && \
    mv /opt/apache-maven-3.9.6 /opt/maven && rm /tmp/maven.tar.gz

# ---------------- 安装 Node.js 20 ----------------
RUN wget -q https://nodejs.org/dist/v20.11.1/node-v20.11.1-linux-x64.tar.xz -O /tmp/node.tar.xz && \
    mkdir -p /opt && tar -xJf /tmp/node.tar.xz -C /opt && \
    mv /opt/node-v20.11.1-linux-x64 /opt/node-20 && rm /tmp/node.tar.xz

# ---------------- 初始化 MySQL ----------------
RUN mkdir -p /var/lib/mysql /run/mysqld && \
    chown -R mysql:mysql /var/lib/mysql /run/mysqld && \
    mysql_install_db --user=mysql --datadir=/var/lib/mysql

# ---------------- 复制项目文件 ----------------
WORKDIR /app
COPY . /app/

# ---------------- 暴露端口 ----------------
# 80: Nginx前端
# 8080: Spring Boot后端
# 3306: MySQL
# 6379: Redis
EXPOSE 80 8080 3306 6379

# ---------------- 启动脚本 ----------------
COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh"]
```

## 启动脚本 start.sh

```bash
#!/bin/bash
set -e

echo "=== 启动 MySQL ==="
service mysql start
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';"
mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE};"

echo "=== 启动 Redis ==="
redis-server --daemonize yes

echo "=== 构建后端 ==="
cd /app/backend
mvn clean package -DskipTests

echo "=== 启动后端 ==="
java -jar target/*.jar &

echo "=== 构建前端 ==="
cd /app/frontend
npm install
npm run build

echo "=== 启动 Nginx ==="
nginx

echo "=== Fly Music 启动完成 ==="
echo "前端: http://localhost"
echo "后端: http://localhost:8080"
echo "MySQL: localhost:3306"
echo "Redis: localhost:6379"

# 保持容器运行
tail -f /dev/null
```

## 打包命令

```powershell
# 构建镜像（约 3-5GB）
docker build -t fly-music:full -f Dockerfile.full .

# 打包
docker save fly-music:full | gzip > fly-music-full.tar.gz

# 分发到其他机器
# 复制 fly-music-full.tar.gz 到目标机器

# 目标机器上加载
docker load -i fly-music-full.tar.gz

# 启动
docker run -d \
    --name fly-music \
    -p 80:80 \
    -p 8080:8080 \
    -p 3306:3306 \
    -p 6379:6379 \
    fly-music:full
```
