#!/bin/bash
# Fly Music 一键部署脚本

echo "=== Fly Music 一键部署脚本 ==="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

echo "1. 拉取最新代码..."
git pull origin master

echo "2. 构建并启动所有服务..."
docker-compose up -d --build

echo "3. 等待服务启动..."
sleep 30

echo "4. 检查服务状态..."
docker-compose ps

echo ""
echo "=== 部署完成 ==="
echo "前端地址: http://localhost"
echo "后端地址: http://localhost:8080"
echo "MySQL: localhost:3306"
echo "Redis: localhost:6379"
