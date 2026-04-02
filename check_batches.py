import re

with open('/Users/kewz/pupu/idea_workspace/fly-music/songs_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

lines = content.split('\n')
current_batch = []
batches = []
in_insert = False

for line in lines:
    if line.startswith('INSERT INTO songs'):
        in_insert = True
        current_batch = [line]
    elif in_insert:
        current_batch.append(line)
        if line.strip().endswith(';'):
            batches.append('\n'.join(current_batch))
            current_batch = []
            in_insert = False

print(f"Found {len(batches)} batches")
for i, b in enumerate(batches):
    print(f"Batch {i+1}: {len(b)} chars")
