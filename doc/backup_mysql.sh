#!/bin/bash
# Fly Music 数据库备份脚本 (需要安装 MySQL 客户端)
# 使用方法: ./backup_mysql.sh

DB_NAME="fly_music"
DB_USER="root"
DB_PASS="123456"
BACKUP_DIR="$(cd "$(dirname "$0")" && pwd)"
DATE=$(date +%Y%m%d%H%M%S)
BACKUP_FILE="$BACKUP_DIR/dump-${DB_NAME}-${DATE}.sql"

# 检查 mysqldump 是否可用
if ! command -v mysqldump &> /dev/null; then
    echo "错误: 未找到 mysqldump 命令"
    echo "请确保已安装 MySQL 客户端"
    exit 1
fi

echo "正在备份数据库 $DB_NAME ..."

# 执行备份
mysqldump -u"$DB_USER" -p"$DB_PASS" \
    --single-transaction \
    --quick \
    --lock-tables=false \
    --add-drop-table \
    --add-drop-database \
    "$DB_NAME" > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    FILE_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    echo "✓ 备份成功: $BACKUP_FILE ($FILE_SIZE)"
    
    # 压缩
    gzip "$BACKUP_FILE"
    echo "✓ 已压缩: ${BACKUP_FILE}.gz"
    
    # 清理7天前的备份
    find "$BACKUP_DIR" -name "dump-${DB_NAME}-*.sql.gz" -mtime +7 -delete
    echo "✓ 已清理7天前的旧备份"
    
    echo ""
    echo "备份文件列表:"
    ls -lh "$BACKUP_DIR"/dump-"${DB_NAME}"-*.sql.gz 2>/dev/null
else
    echo "✗ 备份失败!"
    rm -f "$BACKUP_FILE"
    exit 1
fi
