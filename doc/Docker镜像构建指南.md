# 构建 Fly Music Docker 镜像

## 1. 构建后端 JAR 包

```powershell
mvn clean package -DskipTests
```

## 2. 构建 Docker 镜像

```powershell
# 构建所有服务镜像
docker-compose build

# 或者只构建后端镜像
docker build -t fly-music-backend:latest -f Dockerfile.backend .
```

## 3. 打包镜像为可分发文件

```powershell
# 导出为 tar 文件
docker save -o fly-music.tar fly-music-backend:latest mysql:8.0 redis:7-alpine nginx:alpine

# 压缩
gzip fly-music.tar
```

## 4. 在其他机器上加载镜像

```powershell
# 解压
gunzip fly-music.tar.gz

# 加载镜像
docker load -i fly-music.tar
```

## 5. 完整的一键构建脚本

创建 `build-images.ps1`：

```powershell
Write-Host "=== 构建 Fly Music Docker 镜像 ===" -ForegroundColor Green

# 1. 构建后端
Write-Host "1. 构建后端 JAR 包..." -ForegroundColor Yellow
mvn clean package -DskipTests

# 2. 构建 Docker 镜像
Write-Host "2. 构建 Docker 镜像..." -ForegroundColor Yellow
docker-compose build

# 3. 打包为可分发文件
Write-Host "3. 打包为可分发文件..." -ForegroundColor Yellow
docker save -o fly-music.tar fly-music-backend mysql:8.0 redis:7-alpine nginx:alpine
Compress-Archive -Path fly-music.tar -DestinationPath fly-music-images.zip
Remove-Item fly-music.tar

Write-Host "=== 镜像构建完成 ===" -ForegroundColor Green
Write-Host "分发文件: fly-music-images.zip" -ForegroundColor Cyan
```

这样就可以将镜像打包成分发文件，拿到其他机器上快速部署了。
