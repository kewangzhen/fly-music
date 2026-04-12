# Fly Music 一键构建部署包

本目录包含将 Fly Music 项目打包成可分发部署包的所有必要文件。

## 快速开始

### 方式一：Windows 一键构建

```powershell
# 双击运行 build.bat
build.bat
```

### 方式二：PowerShell

```powershell
.\build.ps1
```

## 构建流程

1. **自动构建后端 JAR**
2. **自动构建 Docker 镜像**
3. **自动打包成分发文件**
4. **生成部署脚本**

## 输出文件

构建完成后会生成以下文件：

```
output/
├── fly-music.tar           # Docker 镜像包
├── docker-compose.yml      # 部署配置
├── deploy.sh              # Linux 部署脚本
├── deploy.bat             # Windows 部署脚本
└── nginx.conf             # Nginx 配置
```

## 在其他机器部署

1. 复制 `output/` 目录到目标机器
2. 运行 `deploy.bat` 或 `./deploy.sh`
3. 访问 http://localhost
