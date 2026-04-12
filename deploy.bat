@echo off
chcp 65001 >nul
echo === Fly Music 一键部署脚本 ===

echo 1. 拉取最新代码...
git pull origin master

echo 2. 构建并启动所有服务...
docker-compose up -d --build

echo 3. 等待服务启动...
timeout /t 30 /nobreak

echo 4. 检查服务状态...
docker-compose ps

echo.
echo === 部署完成 ===
echo 前端地址: http://localhost
echo 后端地址: http://localhost:8080
echo MySQL: localhost:3306
echo Redis: localhost:6379

pause
