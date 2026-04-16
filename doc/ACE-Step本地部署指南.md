# ACE-Step 1.5 本地部署指南

## 简介

ACE-Step 1.5 是目前最强的开源音乐生成模型，支持：
- 音乐生成（10秒 ~ 10分钟）
- 曲风识别（BPM、调性、风格）
- 本地部署，REST API 接口

## 硬件要求

| 显存 | 配置 |
|-----|------|
| 4-6GB | 禁用语言模型，仅用 DiT + INT8 量化 |
| 8-16GB | 0.6B 语言模型 + vLLM |
| 16-24GB | 1.7B 语言模型 |
| 24GB+ | 4B 语言模型（最佳效果） |

## 安装步骤

### 1. 克隆项目

```powershell
cd C:\path\to\fly-music
git clone https://github.com/ACE-Step/ACE-Step-1.5.git ace_step_service\ace_step
```

### 2. 安装依赖

```powershell
# 进入目录
cd ace_step_service\ace_step

# 安装 uv (Windows PowerShell)
irm https://astral.sh/uv/install.ps1 | iex

# 同步依赖
uv sync
```

### 3. 启动服务

```powershell
# 启动 REST API (端口 8001)
uv run acestep-api

# 或启动 Web UI (端口 7860)
uv run acestep
```

### 4. 配置 Fly Music 后端

修改 `application.properties`：

```properties
# AI 音乐服务配置
ai.music.service.url=http://localhost:5001
```

### 5. 启动服务

```bash
# 启动 ACE-Step 服务
cd ace_step_service
pip install -r requirements.txt
python ace_step_service.py

# 启动后端
cd ..
./maven/bin/mvn spring-boot:run
```

## API 接口

### 音乐生成

```bash
POST http://localhost:5001/generate
Content-Type: application/json

{
  "prompt": "一首欢快的流行歌曲",
  "duration": 30,
  "style": "pop"
}
```

### 曲风识别

```bash
POST http://localhost:5001/understand
Content-Type: multipart/form-data

file: audio.wav
```

### 健康检查

```bash
GET http://localhost:5001/health
```

## 使用说明

1. 访问 http://localhost:5173 进入 Fly Music
2. 点击「AI 实验室」
3. 选择「AI 音乐生成」或「AI 听歌识曲」
4. 开始创作！

## 故障排除

### 模型下载失败

首次启动会自动下载模型，确保网络通畅。如遇问题，可手动下载：

```bash
# 从 HuggingFace 下载模型
huggingface-cli download ACE-Step/ace-step-1.5-base
```

### 显存不足

使用量化模式：

```powershell
uv run acestep-api --use-quantization
```

### 端口被占用

修改端口：

```powershell
uv run acestep-api --port 8002
```

## 性能优化

- 使用 vLLM 后端提升推理速度
- 启用 INT8 量化减少显存占用
- 批量生成提升效率
