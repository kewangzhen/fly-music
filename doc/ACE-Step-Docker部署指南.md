# ACE-Step 1.5 Docker 部署指南

## 1. 简介

本文档提供了使用 Docker 部署 ACE-Step 1.5 音乐生成模型的详细步骤，包括环境准备、镜像构建、容器运行和配置说明。

## 2. 环境要求

| 资源 | 最低要求 | 推荐配置 |
|------|---------|---------|
| CPU | 4核 | 8核以上 |
| 内存 | 8GB | 16GB以上 |
| 显存 | 4GB (量化模式) | 16GB以上 (完整功能) |
| 存储 | 50GB | 100GB以上 |
| Docker | 20.10.0+ | 20.10.20+ |
| NVIDIA Docker | 2.0+ (仅GPU模式) | 2.1+ |

## 3. 安装 Docker

### 3.1 Windows

1. 下载并安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
2. 启用 WSL 2 功能
3. 重启系统后启动 Docker Desktop

### 3.2 Linux

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io

# CentOS/RHEL
sudo yum install docker
sudo systemctl start docker
sudo systemctl enable docker
```

### 3.3 macOS

1. 下载并安装 [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)
2. 启动 Docker Desktop

## 4. 安装 NVIDIA Docker (仅GPU模式)

### 4.1 安装 NVIDIA 驱动

确保系统已安装最新的 NVIDIA 驱动。

### 4.2 安装 NVIDIA Container Toolkit

```bash
# 添加 NVIDIA 包仓库
distribution=$(. /etc/os-release;echo $ID$VERSION_ID)
curl -s -L https://nvidia.github.io/nvidia-docker/gpgkey | sudo apt-key add -
curl -s -L https://nvidia.github.io/nvidia-docker/$distribution/nvidia-docker.list | sudo tee /etc/apt/sources.list.d/nvidia-docker.list

# 安装 NVIDIA Container Toolkit
sudo apt update
sudo apt install -y nvidia-container-toolkit
sudo systemctl restart docker
```

## 5. 构建 Docker 镜像

### 5.1 克隆 ACE-Step 项目

```bash
git clone https://github.com/ACE-Step/ACE-Step-1.5.git
theme ACE-Step-1.5
```

### 5.2 创建 Dockerfile

在项目根目录创建 `Dockerfile`：

```dockerfile
FROM python:3.10-slim

# 设置工作目录
WORKDIR /app

# 安装系统依赖
RUN apt update && apt install -y \n    git \n    wget \n    curl \n    gcc \n    g++ \n    libsndfile1 \n    libsndfile1-dev \n    && rm -rf /var/lib/apt/lists/*

# 安装 uv 包管理器
RUN curl -LsSf https://astral.sh/uv/install.sh | sh

# 复制项目文件
COPY . .

# 安装依赖
RUN uv sync

# 暴露端口
EXPOSE 8001 7860

# 启动命令
CMD ["uv", "run", "acestep-api"]
```

### 5.3 构建镜像

```bash
docker build -t ace-step:1.5 .
```

## 6. 运行容器

### 6.1 CPU 模式

```bash
docker run -d \
  --name ace-step \
  -p 8001:8001 \  # REST API 端口
  -p 7860:7860 \  # Web UI 端口
  -v ./models:/app/models \  # 模型存储目录
  -v ./output:/app/output \  # 输出目录
  ace-step:1.5
```

### 6.2 GPU 模式

```bash
docker run -d \
  --name ace-step \
  --gpus all \
  -p 8001:8001 \  # REST API 端口
  -p 7860:7860 \  # Web UI 端口
  -v ./models:/app/models \  # 模型存储目录
  -v ./output:/app/output \  # 输出目录
  ace-step:1.5
```

### 6.3 自定义配置

```bash
docker run -d \
  --name ace-step \
  --gpus all \
  -p 8001:8001 \
  -p 7860:7860 \
  -v ./models:/app/models \
  -v ./output:/app/output \
  -e ACE_STEP_MODEL_PATH=/app/models \
  -e ACE_STEP_OUTPUT_PATH=/app/output \
  -e ACE_STEP_PORT=8001 \
  -e ACE_STEP_GRADIO_PORT=7860 \
  ace-step:1.5
```

## 7. 使用 Docker Compose

### 7.1 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  ace-step:
    build: .
    image: ace-step:1.5
    container_name: ace-step
    ports:
      - "8001:8001"  # REST API
      - "7860:7860"  # Web UI
    volumes:
      - ./models:/app/models
      - ./output:/app/output
    environment:
      - ACE_STEP_MODEL_PATH=/app/models
      - ACE_STEP_OUTPUT_PATH=/app/output
      - ACE_STEP_PORT=8001
      - ACE_STEP_GRADIO_PORT=7860
    restart: unless-stopped
    # 仅 GPU 模式使用
    # deploy:
    #   resources:
    #     reservations:
    #       devices:
    #         - capabilities: ["gpu"]
```

### 7.2 启动服务

```bash
docker-compose up -d
```

### 7.3 停止服务

```bash
docker-compose down
```

## 8. 配置与使用

### 8.1 访问服务

- **REST API**: http://localhost:8001
- **Web UI**: http://localhost:7860

### 8.2 API 接口

#### 8.2.1 音乐生成

```bash
POST http://localhost:8001/generate
Content-Type: application/json

{
  "prompt": "一首欢快的流行歌曲",
  "duration": 30,
  "style": "pop"
}
```

#### 8.2.2 曲风识别

```bash
POST http://localhost:8001/understand
Content-Type: multipart/form-data

file: audio.wav
```

#### 8.2.3 健康检查

```bash
GET http://localhost:8001/health
```

### 8.3 集成到 Fly Music

修改 `application.properties`：

```properties
# ACE-Step服务配置
ai.acestep.enabled=true
ai.acestep.service.url=http://localhost:8001
```

## 9. 故障排除

### 9.1 镜像构建失败

- 检查网络连接
- 增加 Docker 构建内存限制
- 使用国内镜像源加速

### 9.2 容器启动失败

- 检查端口是否被占用
- 检查 GPU 驱动是否正确安装
- 查看容器日志：`docker logs ace-step`

### 9.3 模型下载失败

- 确保网络通畅
- 手动下载模型到 `./models` 目录
- 使用代理加速下载

### 9.4 显存不足

- 使用量化模式：`docker run -e ACE_STEP_USE_QUANTIZATION=true ...`
- 减小模型大小：使用 0.6B 或 1.7B 模型

## 10. 性能优化

### 10.1 资源限制

```bash
docker run -d \
  --name ace-step \
  --gpus all \
  --cpus 8 \
  --memory 16g \
  -p 8001:8001 \
  -p 7860:7860 \
  ace-step:1.5
```

### 10.2 缓存优化

- 使用本地模型目录挂载
- 预下载模型到挂载目录

### 10.3 网络优化

- 使用主机网络模式：`--network host`
- 配置 DNS 以加速模型下载

## 11. 常见问题

### 11.1 Q: Docker 容器无法访问 GPU

**A**: 确保已安装 NVIDIA Container Toolkit，并使用 `--gpus all` 参数。

### 11.2 Q: 模型下载速度慢

**A**: 使用国内镜像源或手动下载模型到挂载目录。

### 11.3 Q: 服务启动后无法访问

**A**: 检查防火墙设置，确保端口已开放。

### 11.4 Q: 生成音乐时出现内存错误

**A**: 增加容器内存限制，或使用量化模式。

## 12. 维护与更新

### 12.1 更新 ACE-Step

```bash
git pull
# 重新构建镜像
docker build -t ace-step:1.5 .
# 重启容器
docker-compose down
docker-compose up -d
```

### 12.2 备份数据

```bash
# 备份模型和输出
cp -r ./models ./models_backup
cp -r ./output ./output_backup
```

### 12.3 日志管理

```bash
# 查看容器日志
docker logs ace-step

# 清理日志
docker logs ace-step > /dev/null
```

## 13. 部署架构

### 13.1 单机部署

适用于开发和测试环境：

```
┌─────────────────┐
│   Docker Host   │
├─────────────────┤
│                 │
│  ┌───────────┐  │
│  │ ACE-Step  │  │
│  │ Container │  │
│  └───────────┘  │
│                 │
└─────────────────┘
```

### 13.2 多实例部署

适用于生产环境：

```
┌─────────────────────┐
│  Load Balancer      │
├─────────────────────┤
         │
┌────────┼────────┐
│        │        │
┌────────▼─┐  ┌────────▼─┐
│ ACE-Step │  │ ACE-Step │
│ Instance │  │ Instance │
└──────────┘  └──────────┘
```

## 14. 总结

使用 Docker 部署 ACE-Step 可以提供以下优势：

1. **环境隔离**：避免依赖冲突
2. **部署简便**：一键启动服务
3. **可移植性**：跨平台运行
4. **资源控制**：精确管理资源使用
5. **版本管理**：方便更新和回滚

通过本指南，您可以快速部署 ACE-Step 服务，并将其集成到 Fly Music 系统中，为用户提供强大的音乐生成和曲风识别功能。

---

**文档版本**：1.0
**更新日期**：2026-04-12
**适用版本**：ACE-Step 1.5