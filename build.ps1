# Fly Music 一键构建部署包

## 快速开始

```powershell
# 运行构建脚本
.\build.ps1
```

## 构建流程

1. 检查 Docker 环境
2. 构建后端 JAR 包
3. 构建 Docker 镜像
4. 打包为可分发文件

## 输出

构建完成后在 `output` 目录生成：
- `fly-music.tar` - Docker 镜像包
- `deploy.bat` - 部署脚本
- 配置文件

## 部署

将 `output` 目录复制到目标机器，运行 `deploy.bat` 即可。