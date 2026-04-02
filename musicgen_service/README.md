# MusicGen AI 音乐生成服务

基于 Meta MusicGen 模型的开源 AI 音乐生成服务。

## 功能特性

- 🎵 文本转音乐生成
- 🎸 支持多种音乐风格和心情
- ⚡ GPU 加速支持
- 🌐 RESTful API 接口
- 📦 开箱即用

## 快速开始

### 1. 安装依赖

```bash
# 克隆或下载本项目后，进入目录
cd musicgen_service

# 安装Python依赖
pip install -r requirements.txt
```

### 2. 启动服务

```bash
# Windows
start.bat

# 或手动运行
python musicgen_service.py --port 5000

# Linux/Mac
bash start.sh
```

### 3. 测试服务

```bash
python test_api.py
```

## API 使用示例

### 生成音乐

```bash
curl -X POST http://localhost:5000/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "欢快的电子舞曲，节奏明快",
    "duration": 30
  }'
```

### 查询状态

```bash
curl http://localhost:5000/api/status/music_1234567890
```

## 配置选项

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `--host` | 服务监听地址 | 0.0.0.0 |
| `--port` | 服务监听端口 | 5000 |
| `--model` | Hugging Face 模型名称 | facebook/musicgen-base |
| `--output` | 音频输出目录 | output |

## 可用模型

- `facebook/musicgen-base` - 基础版（推荐，3.4GB）
- `facebook/musicgen-large` - 高质量版（6GB）
- `facebook/musicgen-medium` - 中等版

## 与 Fly Music 集成

详见 [../doc/MusicGen本地部署指南.md](../doc/MusicGen本地部署指南.md)

## 目录结构

```
musicgen_service/
├── musicgen_service.py   # 主服务脚本
├── requirements.txt       # Python依赖
├── start.bat            # Windows启动脚本
├── start.sh            # Linux/Mac启动脚本
├── test_api.py         # API测试脚本
├── README.md           # 本说明文件
└── output/             # 生成的音频文件目录
```

## 注意事项

1. 首次启动需要下载模型（约3-4GB），请耐心等待
2. 建议使用 GPU 以获得更好的生成速度
3. 生成30秒音频约需1-2分钟（GPU）或5-10分钟（CPU）

## 许可证

MusicGen 基于 CC BY-NC 4.0 许可证，仅限非商业用途。
