@echo off
chcp 65001 >nul
echo ========================================
echo   Fly Music 一键构建部署包
echo ========================================
echo.

echo [1/5] 检查环境...
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: Docker 未安装
    echo 请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo      Docker 已安装

echo.
echo [2/5] 构建后端 JAR 包...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 错误: 后端构建失败
    pause
    exit /b 1
)
echo      后端构建完成

echo.
echo [3/5] 构建 Docker 镜像...
docker-compose build
if %errorlevel% neq 0 (
    echo 错误: Docker 镜像构建失败
    pause
    exit /b 1
)
echo      Docker 镜像构建完成

echo.
echo [4/5] 打包为可分发文件...
if not exist "output" mkdir output
docker save fly-music-backend mysql:8.0 redis:7-alpine nginx:alpine -o output/fly-music.tar
echo      打包完成: output\fly-music.tar

echo.
echo [5/5] 复制部署文件...
copy docker-compose.yml output\ >nul
copy nginx.conf output\ >nul
copy deploy.bat output\ >nul
echo      部署文件复制完成

echo.
echo ========================================
echo   构建完成!
echo ========================================
echo.
echo 输出目录: output\
echo.
echo 部署说明:
echo   1. 将 output\ 文件夹复制到目标机器
echo   2. 确保目标机器安装 Docker
echo   3. 运行 deploy.bat 启动服务
echo.
echo 访问地址: http://localhost
echo.
pause
