import os
import re

songs_dir = "/Users/kewz/pupu/idea_workspace/fly-music/songs"
files = os.listdir(songs_dir)

def detect_category(filename):
    chinese_pattern = re.compile(r'[\u4e00-\u9fff]')
    has_chinese = bool(chinese_pattern.search(filename))

    if has_chinese:
        return 9
    else:
        return 10

def parse_filename(filename):
    filename = filename.replace('.mp3', '').replace('.MP3', '')

    if ' - ' in filename:
        parts = filename.split(' - ')
        artist = parts[0].strip()
        title = parts[1].strip() if len(parts) > 1 else filename
    else:
        title = filename
        artist = "未知"

    title = re.sub(r'\s*\(.*?\)\s*', '', title)
    title = re.sub(r'\s*\[.*?\]\s*', '', title)

    return artist, title

all_values = []
count = 0
for i, filename in enumerate(files):
    if not (filename.endswith('.mp3') or filename.endswith('.MP3')):
        continue

    artist, title = parse_filename(filename)
    url = f"/api/songs/file/{filename}"
    category_id = detect_category(filename)
    play_count = 10000 + (i * 100)

    title_escaped = title.replace("'", "\\'")
    all_values.append(f"('{title_escaped}', 0, '{url}', {category_id}, 1, {play_count})")
    count += 1

print(f"Total songs: {count}")

batch_size = 100
batches = [all_values[i:i+batch_size] for i in range(0, len(all_values), batch_size)]

output = "DELETE FROM songs;\n\n"

for batch in batches:
    sql = "INSERT INTO songs (title, duration, url, category_id, status, play_count) VALUES \n" + ",\n".join(batch) + ";"
    output += sql + "\n\n"

with open('/Users/kewz/pupu/idea_workspace/fly-music/songs_data.sql', 'w', encoding='utf-8') as f:
    f.write(output)

print(f"SQL file generated with {len(batches)} batches")
