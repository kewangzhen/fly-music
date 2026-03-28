@echo off
:: MusicGen 服务启动脚本 (Windows)

echo ========================================
echo   MusicGen AI 音乐生成服务启动脚本
echo ========================================
echo.

:: 检查Python是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Python，请先安装Python 3.10+
    pause
    exit /b 1
)

:: 检查是否在虚拟环境中
if defined VIRTUAL_ENV (
    echo [信息] 已激活虚拟环境: %VIRTUAL_ENV%
) else (
    echo [提示] 建议使用虚拟环境运行
    echo.
)

:: 安装依赖
echo [1/3] 检查并安装依赖...
pip install -r requirements.txt --quiet
if errorlevel 1 (
    echo [错误] 依赖安装失败
    pause
    exit /b 1
)

echo.
echo [2/3] 启动MusicGen服务...
echo.

:: 启动服务
python musicgen_service.py --host 0.0.0.0 --port 5000

pause
