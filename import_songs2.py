import subprocess
import sys

with open('/Users/kewz/pupu/idea_workspace/fly-music/songs_data.sql', 'r', encoding='utf-8') as f:
    sql_content = f.read()

sql_statements = [s.strip() + ";" for s in sql_content.split(';') if s.strip() and not s.startswith('DELETE')]

print(f"Found {len(sql_statements)} INSERT statements")

for i, stmt in enumerate(sql_statements):
    print(f"Executing batch {i+1}/{len(sql_statements)}...")

    result = subprocess.run([
        'mysql',
        '-u', 'root',
        '-p123456',
        'fly_music',
        '-e', stmt
    ], capture_output=True, text=True)

    if result.returncode != 0:
        print(f"Error: {result.stderr}")
        break
    else:
        print(f"Success: {result.stdout[:50] if result.stdout else 'OK'}")

print("Done!")
