#!/bin/bash
# MusicGen 服务启动脚本 (Linux/Mac)

echo "========================================"
echo "  MusicGen AI 音乐生成服务启动脚本"
echo "========================================"
echo ""

# 检查Python
if ! command -v python &> /dev/null; then
    echo "[错误] 未检测到Python，请先安装Python 3.10+"
    exit 1
fi

echo "[信息] Python版本: $(python --version)"

# 创建虚拟环境（如果不存在）
if [ ! -d "venv" ]; then
    echo "[提示] 创建虚拟环境..."
    python -m venv venv
fi

# 激活虚拟环境
source venv/bin/activate

# 安装依赖
echo "[1/3] 安装依赖..."
pip install -r requirements.txt

echo ""
echo "[2/3] 启动MusicGen服务..."
echo ""

# 启动服务
python musicgen_service.py --host 0.0.0.0 --port 5000
