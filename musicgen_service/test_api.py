#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MusicGen API 测试脚本

使用方法:
    python test_api.py
"""

import requests
import json
import time
import sys

BASE_URL = "http://localhost:5000"


def print_response(response, title=""):
    """格式化打印响应"""
    if title:
        print(f"\n{'='*50}")
        print(f"  {title}")
        print(f"{'='*50}")

    try:
        data = response.json()
        print(json.dumps(data, indent=2, ensure_ascii=False))
    except:
        print(response.text)


def test_health():
    """测试健康检查"""
    print("\n[测试] 健康检查")
    response = requests.get(f"{BASE_URL}/api/health")
    print_response(response, "健康检查")
    return response.status_code == 200


def test_info():
    """测试获取服务信息"""
    print("\n[测试] 获取服务信息")
    response = requests.get(f"{BASE_URL}/api/info")
    print_response(response, "服务信息")
    return response.status_code == 200


def test_generate():
    """测试音乐生成"""
    print("\n[测试] 音乐生成")

    test_cases = [
        {
            "name": "测试1: 欢快电子音乐",
            "prompt": "欢快的电子舞曲，节奏明快，充满活力",
            "duration": 15
        },
        {
            "name": "测试2: 舒缓钢琴曲",
            "prompt": "舒缓的钢琴曲，适合睡前放松",
            "duration": 15
        },
        {
            "name": "测试3: 摇滚音乐",
            "prompt": "激烈的摇滚音乐，电吉他solo",
            "duration": 15
        }
    ]

    for case in test_cases:
        print(f"\n{case['name']}")
        print(f"  描述: {case['prompt']}")
        print(f"  时长: {case['duration']}秒")

        response = requests.post(
            f"{BASE_URL}/api/generate",
            json={
                "prompt": case["prompt"],
                "duration": case["duration"]
            }
        )

        print_response(response, "提交任务")

        if response.status_code == 202:
            task_data = response.json()
            task_id = task_data["data"]["id"]
            print(f"\n  任务ID: {task_id}")
            print("  等待生成...")

            # 轮询任务状态
            max_wait = 120  # 最多等待120秒
            wait_time = 0
            while wait_time < max_wait:
                time.sleep(5)
                wait_time += 5

                status_response = requests.get(f"{BASE_URL}/api/status/{task_id}")
                status_data = status_response.json()

                if status_data["data"].get("status") == "completed":
                    print(f"\n  ✅ 生成完成!")
                    print(f"  音频URL: {status_data['data'].get('audio_url', 'N/A')}")
                    break
                elif status_data["data"].get("status") == "failed":
                    print(f"\n  ❌ 生成失败: {status_data['data'].get('error', '未知错误')}")
                    break
                else:
                    print(f"  进度: {wait_time}/{max_wait}秒...", end="\r")

            if wait_time >= max_wait:
                print(f"\n  ⏱️ 等待超时")


def main():
    print("="*50)
    print("  MusicGen API 测试脚本")
    print("="*50)
    print(f"\n服务地址: {BASE_URL}")

    # 测试健康检查
    if not test_health():
        print("\n[错误] 服务未启动，请先运行: python musicgen_service.py")
        sys.exit(1)

    # 测试获取信息
    test_info()

    # 测试生成（生成时间较长，可以注释掉）
    print("\n" + "="*50)
    print("  即将开始音乐生成测试")
    print("  每个测试生成约15秒的音频")
    print("  总耗时约3-5分钟")
    print("="*50)

    response = input("\n是否继续测试生成功能? (y/n): ")
    if response.lower() == 'y':
        test_generate()

    print("\n" + "="*50)
    print("  测试完成!")
    print("="*50)


if __name__ == "__main__":
    main()
