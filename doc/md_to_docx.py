#!/usr/bin/env python3
"""
Markdown 转 DOCX 转换器
直接使用 python-docx 生成 docx 文件
"""

from docx import Document
from docx.shared import Inches, Pt, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
import os
import re

def read_md_content(md_file):
    """读取markdown文件内容"""
    with open(md_file, 'r', encoding='utf-8') as f:
        return f.read()

def parse_tables(md_content):
    """解析markdown中的表格"""
    tables = []
    lines = md_content.split('\n')
    current_table = []
    in_table = False
    
    for line in lines:
        line = line.strip()
        if line.startswith('|') and line.endswith('|'):
            cells = [cell.strip() for cell in line.split('|')[1:-1]]
            if not all(c == '' or c == '-' for c in cells):
                current_table.append(cells)
        else:
            if current_table:
                tables.append(current_table)
                current_table = []
    
    if current_table:
        tables.append(current_table)
    
    return tables

def create_docx(md_file, docx_file):
    """创建docx文件"""
    doc = Document()
    
    # 设置文档标题
    title = doc.add_heading('Fly Music 系统测试用例文档', 0)
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    
    doc.add_paragraph('各功能模块测试用例说明')
    doc.add_paragraph('')
    
    content = read_md_content(md_file)
    
    # 解析并添加表格
    tables = parse_tables(content)
    
    for table_data in tables:
        if not table_data:
            continue
            
        # 创建表格
        rows = len(table_data)
        cols = len(table_data[0]) if table_data else 0
        
        if rows > 0 and cols > 0:
            table = doc.add_table(rows=rows, cols=cols)
            table.style = 'Table Grid'
            
            for i, row_data in enumerate(table_data):
                for j, cell_data in enumerate(row_data):
                    cell = table.rows[i].cells[j]
                    cell.text = cell_data
                    
                    # 设置表头样式
                    if i == 0:
                        cell.paragraphs[0].runs[0].bold = True if cell.paragraphs[0].runs else False
                        for run in cell.paragraphs[0].runs:
                            run.bold = True
                    
                    # 设置字体大小
                    for paragraph in cell.paragraphs:
                        for run in paragraph.runs:
                            run.font.size = Pt(10)
            
            doc.add_paragraph('')
    
    # 保存文档
    doc.save(docx_file)
    print(f"转换成功: {docx_file}")

if __name__ == '__main__':
    script_dir = os.path.dirname(os.path.abspath(__file__))
    md_file = os.path.join(script_dir, '测试用例.md')
    docx_file = os.path.join(script_dir, '测试用例.docx')
    
    if not os.path.exists(md_file):
        print(f"错误: 文件不存在 {md_file}")
        exit(1)
    
    create_docx(md_file, docx_file)
