# MusicGen 本地部署指南

## 概述

本文档介绍如何在本地部署 Meta 公司的 MusicGen 开源模型，用于 AI 音乐生成。

MusicGen 是 Meta 公司开源的音频生成模型，可以根据文本描述生成音乐。

---

## 硬件要求

| 配置 | 最低要求 | 推荐配置 |
|------|----------|----------|
| 操作系统 | Windows 10/11 / Ubuntu 20.04+ / macOS 12+ | Windows 11 / Ubuntu 22.04+ |
| 内存 | 16GB | 32GB |
| 显卡 | NVIDIA GTX 1060 6GB | NVIDIA RTX 3060 12GB / RTX 4080 |
| 存储空间 | 10GB | 20GB |

---

## 环境准备

### 1. 安装 Python

建议使用 Python 3.10 或更高版本：

```bash
# 检查Python版本
python --version

# 如果没有Python，下载安装：https://www.python.org/downloads/
```

### 2. 安装 CUDA (NVIDIA 显卡用户)

```bash
# 检查CUDA版本
nvidia-smi

# 如果没有CUDA，下载安装：https://developer.nvidia.com/cuda-downloads
# 推荐安装 CUDA 11.8 或 12.1
```

### 3. 创建虚拟环境

```bash
# 使用 conda 创建环境
conda create -n musicgen python=3.10
conda activate musicgen

# 或者使用 venv
python -m venv musicgen
# Windows: musicgen\Scripts\activate
# Linux/Mac: source musicgen/bin/activate
```

### 4. 安装 PyTorch

```bash
# CUDA 12.1 版本
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121

# 或者 CUDA 11.8 版本
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```

### 5. 安装依赖库

```bash
pip install transformers accelerate scipy soundfile flask numpy
```

---

## 模型下载

首次运行服务时，模型会自动下载（约 3-4GB）。

如果需要手动下载：

```python
from transformers import AutoModelForCausalLM, AutoProcessor

# 下载 Base 模型（约 3.4GB）
processor = AutoProcessor.from_pretrained("facebook/musicgen-base")
model = AutoModelForCausalLM.from_pretrained("facebook/musicgen-base")

# 或者 Large 模型（约 6GB）
model = AutoModelForCausalLM.from_pretrained("facebook/musicgen-large")
```

---

## 启动服务

### 快速启动

```bash
cd musicgen_service
python musicgen_service.py
```

服务将在 `http://localhost:5000` 启动。

---

## API 接口说明

### 1. 生成音乐

**请求**
```bash
POST /api/generate
Content-Type: application/json

{
    "prompt": "欢快的电子舞曲，节奏明快，充满活力",
    "duration": 30,
    "genre": "electronic",
    "mood": "happy"
}
```

**响应**
```json
{
    "code": 200,
    "message": "生成成功",
    "data": {
        "id": "music_1234567890",
        "prompt": "欢快的电子舞曲，节奏明快，充满活力",
        "duration": 30,
        "audio_url": "/audio/music_1234567890.wav",
        "status": "completed"
    }
}
```

### 2. 查询生成状态

```bash
GET /api/status/{task_id}
```

### 3. 获取模型信息

```bash
GET /api/info
```

---

## 使用示例

### Python 调用

```python
import requests

url = "http://localhost:5000/api/generate"
data = {
    "prompt": "舒缓的钢琴曲，适合睡前放松",
    "duration": 60
}

response = requests.post(url, json=data)
result = response.json()
print(result)
```

### cURL 调用

```bash
curl -X POST http://localhost:5000/api/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "欢快的电子舞曲", "duration": 30}'
```

---

## 与 Fly Music 项目集成

修改 Fly Music 后端的 `AiMusicServiceImpl.java`：

```java
@Value("${ai.music.service.url:http://localhost:5000}")
private String musicServiceUrl;

private String callMusicGenService(String prompt, String genre, String mood, Integer duration) {
    RestTemplate restTemplate = new RestTemplate();
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    Map<String, Object> request = new HashMap<>();
    request.put("prompt", prompt);
    request.put("duration", duration);
    request.put("genre", genre);
    request.put("mood", mood);
    
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
    
    ResponseEntity<Map> response = restTemplate.postForEntity(
        musicServiceUrl + "/api/generate",
        entity,
        Map.class
    );
    
    // 处理响应，返回音频URL
    return (String) response.getBody().get("data").get("audio_url");
}
```

---

## 常见问题

### Q1: 生成速度慢怎么办？
- 确认使用了 GPU 加速
- 降低生成时长
- 使用 Base 版本而非 Large 版本

### Q2: 内存不足怎么办？
- 减少 batch_size
- 使用较小的模型版本
- 增加虚拟内存

### Q3: 模型下载失败？
- 配置国内镜像源
- 使用离线模型文件

### Q4: CUDA 不可用？
- 检查 PyTorch 是否正确安装 CUDA 版本
- 确认 NVIDIA 驱动是最新的

---

## 性能优化

### 1. 使用混合精度
```python
model = model.to(dtype=torch.float16)
```

### 2. 批量处理
```python
inputs = processor(text=[prompt1, prompt2], return_tensors="pt", padding=True)
```

### 3. 启用Flash Attention（可选）
```bash
pip install flash-attn
```

---

## 参考资源

- [Meta MusicGen 官方仓库](https://github.com/facebookresearch/audiocraft)
- [Hugging Face Model Card](https://huggingface.co/facebook/musicgen-base)
- [PyTorch 官方文档](https://pytorch.org/)
- [CUDA 安装指南](https://docs.nvidia.com/cuda/)

---

## 许可证

MusicGen 基于 [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/) 许可证，仅限非商业用途。

---

*文档最后更新：2024*
