#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MusicGen AI 音乐生成服务
基于 Meta MusicGen 模型

使用方法:
    python musicgen_service.py [--port 5000] [--host 0.0.0.0] [--model facebook/musicgen-base]

依赖安装:
    pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121
    pip install transformers accelerate scipy soundfile flask numpy pillow
"""

import os
import sys
import time
import uuid
import argparse
import threading
import logging
from pathlib import Path

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 全局变量
music_service = None

try:
    import torch
    from transformers import AutoModelForCausalLM, AutoProcessor
    import scipy.io.wavfile as wavfile
    import numpy as np
    from flask import Flask, request, jsonify, send_file, make_response
    from flask_cors import CORS
except ImportError as e:
    logger.error(f"缺少依赖库，请运行: pip install torch transformers scipy soundfile flask flask-cors")
    logger.error(f"错误: {e}")
    sys.exit(1)


class MusicGenService:
    """MusicGen 音乐生成服务类"""

    def __init__(self, model_name="facebook/musicgen-base", output_dir="output"):
        """
        初始化 MusicGen 服务

        Args:
            model_name: Hugging Face 模型名称
            output_dir: 音频输出目录
        """
        self.model_name = model_name
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(parents=True, exist_ok=True)

        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = None
        self.processor = None

        logger.info(f"使用设备: {self.device}")
        logger.info(f"正在加载模型: {model_name}")

        self.load_model()

    def load_model(self):
        """加载模型和处理器"""
        try:
            logger.info("正在下载/加载模型，这可能需要几分钟...")
            self.processor = AutoProcessor.from_pretrained(self.model_name)

            # 根据设备选择合适的数据类型
            torch_dtype = torch.float16 if self.device == "cuda" else torch.float32

            self.model = AutoModelForCausalLM.from_pretrained(
                self.model_name,
                torch_dtype=torch_dtype,
                device_map="auto" if self.device == "cuda" else None
            )

            if self.device == "cpu":
                self.model = self.model.to(self.device)

            logger.info(f"模型加载完成! 模型大小: {self.get_model_size():.2f} MB")

        except Exception as e:
            logger.error(f"模型加载失败: {e}")
            raise

    def generate(self, prompt, duration=30, genre=None, mood=None):
        """
        根据文本生成音乐

        Args:
            prompt: 音乐描述文本
            duration: 生成时长（秒）
            genre: 音乐风格（可选）
            mood: 音乐心情（可选）

        Returns:
            dict: 包含生成信息的字典
        """
        # 构建完整的提示词
        full_prompt = prompt
        if genre:
            full_prompt = f"{genre}, {full_prompt}"
        if mood:
            full_prompt = f"{full_prompt}, {mood} mood"

        logger.info(f"正在生成音乐: {full_prompt}")
        logger.info(f"目标时长: {duration}秒")

        try:
            # 处理输入
            inputs = self.processor(
                text=[full_prompt],
                padding=True,
                return_tensors="pt",
                truncation=True,
                max_length=512
            )

            # 移动到设备
            if self.device == "cuda":
                inputs = {k: v.to(self.device) for k, v in inputs.items()}

            # 计算 token 数量 (每秒约50个token)
            max_new_tokens = int(duration * 50)

            # 生成音频
            with torch.no_grad():
                audio_values = self.model.generate(
                    **inputs,
                    max_new_tokens=max_new_tokens,
                    do_sample=True,
                    guidance_scale=1.0,
                    temperature=0.8,
                    top_k=250,
                    top_p=0.95,
                )

            # 移除 batch 维度并转换为 numpy
            audio = audio_values.cpu().float().numpy()[0]

            # 转换为 16-bit PCM
            audio = audio.T  # 转置为 (samples, channels)
            if audio.shape[1] == 1:
                audio = audio.squeeze()

            # 归一化到 16-bit 范围
            audio = np.clip(audio, -1.0, 1.0)
            audio_int16 = (audio * 32767).astype(np.int16)

            # 生成唯一ID
            task_id = f"music_{int(time.time())}_{uuid.uuid4().hex[:8]}"

            # 保存文件
            output_path = self.output_dir / f"{task_id}.wav"
            wavfile.write(str(output_path), 32000, audio_int16)

            # 生成相对URL路径
            audio_url = f"/audio/{task_id}.wav"

            logger.info(f"生成完成! 文件保存至: {output_path}")

            return {
                "id": task_id,
                "prompt": prompt,
                "full_prompt": full_prompt,
                "duration": duration,
                "audio_url": audio_url,
                "status": "completed",
                "model": self.model_name,
                "device": self.device
            }

        except Exception as e:
            logger.error(f"生成失败: {e}")
            return {
                "status": "failed",
                "error": str(e)
            }

    def get_model_size(self):
        """获取模型大小（MB）"""
        param_size = sum(p.numel() * p.element_size() for p in self.model.parameters())
        buffer_size = sum(b.numel() * b.element_size() for b in self.model.buffers())
        return (param_size + buffer_size) / (1024 * 1024)

    def get_info(self):
        """获取服务信息"""
        info = {
            "model_name": self.model_name,
            "device": self.device,
            "model_size_mb": self.get_model_size(),
            "cuda_available": torch.cuda.is_available(),
        }

        if torch.cuda.is_available():
            info["cuda_device_name"] = torch.cuda.get_device_name(0)
            info["cuda_memory_allocated"] = torch.cuda.memory_allocated(0) / (1024 * 1024)
            info["cuda_memory_reserved"] = torch.cuda.memory_reserved(0) / (1024 * 1024)

        return info


# Flask 应用
app = Flask(__name__)
CORS(app)

# 配置
app.config['MUSIC_SERVICE'] = None
app.config['OUTPUT_DIR'] = 'output'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max

# 任务存储
tasks = {}


@app.route('/')
def index():
    """首页"""
    return {
        "name": "MusicGen AI Music Service",
        "version": "1.0.0",
        "endpoints": {
            "POST /api/generate": "生成音乐",
            "GET /api/status/<task_id>": "查询任务状态",
            "GET /api/info": "获取服务信息",
            "GET /audio/<filename>": "下载音频文件"
        }
    }


@app.route('/api/info', methods=['GET'])
def get_info():
    """获取服务信息"""
    service = app.config['MUSIC_SERVICE']
    if service is None:
        return {"error": "服务未初始化"}, 500

    try:
        info = service.get_info()
        return {
            "code": 200,
            "message": "获取成功",
            "data": info
        }
    except Exception as e:
        return {
            "code": 500,
            "message": str(e)
        }, 500


@app.route('/api/generate', methods=['POST'])
def generate_music():
    """
    生成音乐接口

    请求体:
    {
        "prompt": "音乐描述",
        "duration": 30,  // 可选，默认30秒
        "genre": "electronic",  // 可选
        "mood": "happy"  // 可选
    }
    """
    service = app.config['MUSIC_SERVICE']
    if service is None:
        return {
            "code": 500,
            "message": "服务未初始化，请稍后重试"
        }, 500

    try:
        data = request.get_json()

        if not data or 'prompt' not in data:
            return {
                "code": 400,
                "message": "请提供 prompt 参数"
            }, 400

        prompt = data.get('prompt', '')
        duration = int(data.get('duration', 30))
        genre = data.get('genre')
        mood = data.get('mood')

        # 限制最大时长
        duration = min(duration, 180)

        # 异步生成（避免阻塞）
        task_id = f"task_{int(time.time())}_{uuid.uuid4().hex[:8]}"
        tasks[task_id] = {"status": "processing"}

        def generate_async():
            result = service.generate(prompt, duration, genre, mood)
            tasks[task_id] = result

        thread = threading.Thread(target=generate_async)
        thread.start()

        return {
            "code": 202,
            "message": "生成任务已提交",
            "data": {
                "id": task_id,
                "status": "processing",
                "prompt": prompt,
                "duration": duration
            }
        }

    except Exception as e:
        logger.error(f"生成请求处理失败: {e}")
        return {
            "code": 500,
            "message": f"服务器错误: {str(e)}"
        }, 500


@app.route('/api/status/<task_id>', methods=['GET'])
def get_status(task_id):
    """查询任务状态"""
    if task_id not in tasks:
        return {
            "code": 404,
            "message": "任务不存在"
        }, 404

    task = tasks[task_id]

    # 如果任务完成，返回结果
    if task.get("status") == "completed":
        # 生成完整的访问URL
        if "audio_url" in task:
            base_url = request.host_url.rstrip('/')
            task["audio_url"] = f"{base_url}{task['audio_url']}"

    return {
        "code": 200,
        "message": "获取成功",
        "data": task
    }


@app.route('/api/tasks', methods=['GET'])
def list_tasks():
    """列出所有任务"""
    return {
        "code": 200,
        "message": "获取成功",
        "data": {
            "tasks": list(tasks.keys()),
            "count": len(tasks)
        }
    }


@app.route('/audio/<filename>', methods=['GET'])
def get_audio(filename):
    """获取音频文件"""
    audio_dir = app.config['MUSIC_SERVICE'].output_dir if app.config['MUSIC_SERVICE'] else Path('output')
    file_path = audio_dir / filename

    if not file_path.exists():
        return {"error": "文件不存在"}, 404

    try:
        response = make_response(send_file(
            file_path,
            mimetype='audio/wav',
            as_attachment=False,
            download_name=filename
        ))
        # 添加 CORS 头
        response.headers['Access-Control-Allow-Origin'] = '*'
        return response
    except Exception as e:
        return {"error": str(e)}, 500


@app.route('/api/health', methods=['GET'])
def health():
    """健康检查"""
    return {
        "code": 200,
        "message": "服务运行正常",
        "data": {
            "service": "MusicGen",
            "status": "running" if music_service else "initializing"
        }
    }


def init_service(model_name, output_dir):
    """初始化服务"""
    global music_service

    try:
        music_service = MusicGenService(
            model_name=model_name,
            output_dir=output_dir
        )
        app.config['MUSIC_SERVICE'] = music_service
        return True
    except Exception as e:
        logger.error(f"服务初始化失败: {e}")
        return False


def main():
    """主函数"""
    parser = argparse.ArgumentParser(description='MusicGen AI Music Service')
    parser.add_argument('--host', type=str, default='0.0.0.0', help='服务监听地址')
    parser.add_argument('--port', type=int, default=5000, help='服务监听端口')
    parser.add_argument('--model', type=str, default='facebook/musicgen-base',
                        help='Hugging Face 模型名称')
    parser.add_argument('--output', type=str, default='output', help='音频输出目录')

    args = parser.parse_args()

    # 初始化服务
    logger.info("=" * 50)
    logger.info("MusicGen AI 音乐生成服务")
    logger.info("=" * 50)

    if not init_service(args.model, args.output):
        logger.error("服务初始化失败，程序退出")
        sys.exit(1)

    # 启动 Flask 服务
    logger.info(f"启动服务: http://{args.host}:{args.port}")
    logger.info(f"模型: {args.model}")
    logger.info(f"输出目录: {args.output}")
    logger.info("=" * 50)

    app.run(
        host=args.host,
        port=args.port,
        debug=False,
        threaded=True
    )


if __name__ == '__main__':
    main()
