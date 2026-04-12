#!/usr/bin/env python3
"""
Fly Music 数据库一键备份脚本
通过 MCP MySQL 工具执行备份
"""

import os
import json
import sys
from datetime import datetime

def get_all_tables(mcp_func):
    """获取所有表名"""
    result = mcp_func(query="SHOW TABLES")
    tables = []
    for row in result:
        for key in row.values():
            tables.append(key)
    return tables

def get_table_schema(mcp_func, table_name):
    """获取表结构"""
    query = f"""
    SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'fly_music' AND TABLE_NAME = '{table_name}'
    ORDER BY ORDINAL_POSITION
    """
    return mcp_func(query=query)

def get_table_data(mcp_func, table_name):
    """获取表数据"""
    query = f"SELECT * FROM {table_name}"
    return mcp_func(query=query)

def escape_value(value):
    """转义 SQL 值"""
    if value is None:
        return 'NULL'
    elif isinstance(value, bool):
        return '1' if value else '0'
    elif isinstance(value, (int, float)):
        return str(value)
    elif isinstance(value, datetime):
        return f"'{value.strftime('%Y-%m-%d %H:%M:%S')}'"
    elif isinstance(value, str):
        return f"'{value.replace('\\', '\\\\').replace(\"'\", \"\\\\'\")}'"
    else:
        return f"'{str(value).replace('\\', '\\\\').replace(\"'\", \"\\\\'\")}''"

def generate_create_table(mcp_func, table_name):
    """生成 CREATE TABLE 语句"""
    columns = get_table_schema(mcp_func, table_name)
    
    col_defs = []
    for col in columns:
        col_name = col['COLUMN_NAME']
        col_type = col['COLUMN_TYPE']
        nullable = 'NULL' if col['IS_NULLABLE'] == 'YES' else 'NOT NULL'
        default = f"DEFAULT {col['COLUMN_DEFAULT']}" if col['COLUMN_DEFAULT'] else ''
        extra = col['EXTRA'] or ''
        
        col_def = f"  {col_name} {col_type} {nullable} {default} {extra}".strip()
        col_defs.append(col_def)
    
    return f"CREATE TABLE {table_name} (\n  {', '.join(col_defs)}\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

def generate_insert_statements(mcp_func, table_name):
    """生成 INSERT 语句"""
    data = get_table_data(mcp_func, table_name)
    if not data:
        return []
    
    columns = list(data[0].keys())
    col_list = ', '.join(columns)
    
    statements = []
    for row in data:
        values = ', '.join([escape_value(row[col]) for col in columns])
        statements.append(f"INSERT INTO {table_name} ({col_list}) VALUES ({values});")
    
    return statements

def backup_database(mcp_func, output_file):
    """执行数据库备份"""
    print("正在获取所有表...")
    tables = get_all_tables(mcp_func)
    print(f"找到 {len(tables)} 个表")
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- Fly Music 数据库备份\n")
        f.write(f"-- 备份时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
        f.write("SET FOREIGN_KEY_CHECKS=0;\n\n")
        
        for i, table in enumerate(tables, 1):
            print(f"[{i}/{len(tables)}] 备份表: {table}")
            
            # 生成表结构
            create_sql = generate_create_table(mcp_func, table)
            f.write(f"\n-- Table: {table}\n")
            f.write(f"DROP TABLE IF EXISTS {table};\n")
            f.write(create_sql + ";\n\n")
            
            # 生成数据
            insert_stmts = generate_insert_statements(mcp_func, table)
            if insert_stmts:
                f.write(f"-- Data for table {table}\n")
                for stmt in insert_stmts:
                    f.write(stmt + "\n")
        
        f.write("\nSET FOREIGN_KEY_CHECKS=1;\n")
    
    print(f"\n备份完成: {output_file}")
    return output_file

def main():
    """主函数 - 由 MCP 调用"""
    try:
        # 从环境变量或参数获取输出路径
        output_dir = os.path.dirname(os.path.abspath(__file__)) or "."
        output_file = os.path.join(output_dir, f"dump-fly_music-{datetime.now().strftime('%Y%m%d%H%M%S')}.sql")
        
        # 检查是否有 mcp_func 可用
        if 'mcp_func' in globals():
            backup_database(mcp_func, output_file)
        else:
            print("错误: 需要通过 MCP 环境调用")
            print("使用方法: 在支持 MCP 的环境中导入此模块并调用 main(mcp_func)")
            sys.exit(1)
            
    except Exception as e:
        print(f"备份失败: {e}")
        sys.exit(1)

if __name__ == "__main__":
    # 演示用 - 实际通过 MCP 调用
    print("请通过 MCP 工具调用 backup_database 函数")
    print(f"输出文件将保存在当前目录")
