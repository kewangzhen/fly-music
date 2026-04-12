#!/bin/bash

# Fly Music 数据库一键备份脚本

# 配置
DB_NAME="fly_music"
DB_USER="root"
DB_PASS="123456"
BACKUP_DIR="$(dirname "$0")/../doc"
DATE=$(date +%Y%m%d%H%M%S)
BACKUP_FILE="$BACKUP_DIR/dump-${DB_NAME}-${DATE}.sql"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 执行备份
echo "正在备份数据库 $DB_NAME ..."
mysqldump -u"$DB_USER" -p"$DB_PASS" --single-transaction --quick --lock-tables=false "$DB_NAME" > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "备份成功: $BACKUP_FILE"
    
    # 压缩备份文件
    gzip "$BACKUP_FILE"
    echo "已压缩: ${BACKUP_FILE}.gz"
    
    # 删除7天前的备份
    find "$BACKUP_DIR" -name "dump-${DB_NAME}-*.sql.gz" -mtime +7 -delete
    echo "已清理7天前的旧备份"
else
    echo "备份失败!"
    exit 1
fi
