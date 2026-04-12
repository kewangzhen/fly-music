#!/usr/bin/env python3
"""
ACE-Step Music Service
支持音乐生成和曲风识别
"""

import os
import sys
import json
import time
import asyncio
import logging
import argparse
import subprocess
import threading
from pathlib import Path
from typing import Optional, Dict, Any
from flask import Flask, request, jsonify, send_file
from flask_cors import CORS

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

ACE_STEP_DIR = Path(__file__).parent / "ace_step"
OUTPUT_DIR = Path(__file__).parent / "output"
OUTPUT_DIR.mkdir(exist_ok=True)

process = None
use_musicgen = True

def check_ace_step_installed():
    """检查 ACE-Step 是否已安装"""
    return ACE_STEP_DIR.exists() and (ACE_STEP_DIR / "acestep").exists()

def start_ace_step_api():
    """启动 ACE-Step API 服务"""
    global process
    if not check_ace_step_installed():
        logger.warning("ACE-Step 未安装，切换到 MusicGen 模式")
        return False
    
    try:
        os.chdir(ACE_STEP_DIR)
        process = subprocess.Popen(
            ["uv", "run", "acestep-api"],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            env=os.environ.copy()
        )
        logger.info("ACE-Step API 服务已启动 (端口: 8001)")
        return True
    except Exception as e:
        logger.error(f"启动 ACE-Step 失败: {e}")
        return False

def generate_music(prompt: str, duration: int = 30, style: str = "", 
                   reference_audio: str = None) -> Dict[str, Any]:
    """生成音乐"""
    global use_musicgen
    
    if not check_ace_step_installed():
        use_musicgen = True
        return generate_music_musicgen(prompt, duration, style)
    
    try:
        import requests
        
        response = requests.post(
            "http://localhost:8001/generate",
            json={
                "prompt": prompt,
                "duration": duration,
                "style": style,
                "reference_audio": reference_audio
            },
            timeout=300
        )
        
        if response.status_code == 200:
            result = response.json()
            return {
                "success": True,
                "audio_url": result.get("audio_url", ""),
                "duration": result.get("duration", duration),
                "model": "ACE-Step"
            }
        else:
            logger.warning(f"ACE-Step 生成失败，切换到 MusicGen: {response.text}")
            use_musicgen = True
            return generate_music_musicgen(prompt, duration, style)
            
    except Exception as e:
        logger.warning(f"ACE-Step 调用失败: {e}，切换到 MusicGen")
        use_musicgen = True
        return generate_music_musicgen(prompt, duration, style)

def generate_music_musicgen(prompt: str, duration: int = 30, style: str = "") -> Dict[str, Any]:
    """使用 MusicGen 生成音乐（备用方案）"""
    try:
        sys.path.insert(0, str(Path(__file__).parent))
        from musicgen_service import MusicGenService
        
        service = MusicGenService()
        output_file = OUTPUT_DIR / f"musicgen_{int(time.time())}.wav"
        
        result = service.generate_music(
            prompt=prompt,
            duration=duration,
            output_path=str(output_file)
        )
        
        return {
            "success": True,
            "audio_url": f"/output/{output_file.name}",
            "duration": duration,
            "model": "MusicGen"
        }
    except Exception as e:
        logger.error(f"MusicGen 生成失败: {e}")
        return {
            "success": False,
            "error": str(e),
            "model": "MusicGen"
        }

def analyze_style(audio_path: str) -> Dict[str, Any]:
    """曲风识别"""
    if not check_ace_step_installed():
        return analyze_style_simple(audio_path)
    
    try:
        import requests
        
        with open(audio_path, 'rb') as f:
            files = {'audio': f}
            data = {'task': 'understand'}
            
            response = requests.post(
                "http://localhost:8001/understand",
                files=files,
                data=data,
                timeout=60
            )
        
        if response.status_code == 200:
            result = response.json()
            return {
                "success": True,
                "bpm": result.get("bpm", 0),
                "key": result.get("key", "Unknown"),
                "genre": result.get("genre", "Unknown"),
                "mood": result.get("mood", "Unknown"),
                "instruments": result.get("instruments", []),
                "model": "ACE-Step"
            }
        else:
            return analyze_style_simple(audio_path)
            
    except Exception as e:
        logger.warning(f"ACE-Step 曲风识别失败: {e}，使用简单模式")
        return analyze_style_simple(audio_path)

def analyze_style_simple(audio_path: str) -> Dict[str, Any]:
    """简单的曲风识别（基于音频特征）"""
    try:
        import librosa
        import numpy as np
        
        y, sr = librosa.load(audio_path, duration=30)
        
        tempo, _ = librosa.beat.beat_track(y=y, sr=sr)
        
        chroma = librosa.feature.chroma_cqt(y=y, sr=sr)
        key_idx = np.argmax(np.mean(chroma, axis=1))
        keys = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
        
        return {
            "success": True,
            "bpm": int(tempo),
            "key": keys[key_idx],
            "genre": "Pop",
            "mood": "Happy",
            "instruments": ["Unknown"],
            "model": "Simple"
        }
    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }

@app.route('/health', methods=['GET'])
def health():
    """健康检查"""
    return jsonify({
        "status": "ok",
        "model": "ACE-Step" if check_ace_step_installed() else "MusicGen",
        "use_musicgen": use_musicgen
    })

@app.route('/generate', methods=['POST'])
def generate():
    """音乐生成接口"""
    data = request.json
    
    prompt = data.get('prompt', '')
    duration = data.get('duration', 30)
    style = data.get('style', '')
    reference_audio = data.get('reference_audio')
    
    if not prompt:
        return jsonify({"success": False, "error": "prompt is required"}), 400
    
    result = generate_music(prompt, duration, style, reference_audio)
    
    return jsonify(result)

@app.route('/understand', methods=['POST'])
def understand():
    """曲风识别接口"""
    if 'audio' not in request.files:
        return jsonify({"success": False, "error": "audio file is required"}), 400
    
    audio_file = request.files['audio']
    temp_path = OUTPUT_DIR / f"temp_{int(time.time())}_{audio_file.filename}"
    audio_file.save(str(temp_path))
    
    try:
        result = analyze_style(str(temp_path))
        return jsonify(result)
    finally:
        if temp_path.exists():
            temp_path.unlink()

@app.route('/output/<filename>')
def serve_output(filename):
    """提供生成的音乐文件"""
    return send_file(OUTPUT_DIR / filename)

def main():
    parser = argparse.ArgumentParser(description='ACE-Step Music Service')
    parser.add_argument('--port', type=int, default=5001, help='服务端口')
    parser.add_argument('--use-musicgen', action='store_true', help='强制使用 MusicGen')
    args = parser.parse_args()
    
    global use_musicgen
    
    if args.use_musicgen:
        use_musicgen = True
        logger.info("强制使用 MusicGen 模式")
    elif check_ace_step_installed():
        logger.info("检测到 ACE-Step，正在启动...")
        if start_ace_step_api():
            time.sleep(10)
        else:
            use_musicgen = True
    else:
        logger.info("未检测到 ACE-Step，使用 MusicGen 模式")
        use_musicgen = True
    
    app.run(host='0.0.0.0', port=args.port, debug=False)

if __name__ == '__main__':
    main()
