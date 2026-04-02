import mysql.connector

db = mysql.connector.connect(
    host='localhost',
    user='root',
    password='123456',
    database='fly_music'
)

cursor = db.cursor()

with open('/Users/kewz/pupu/idea_workspace/fly-music/songs_data.sql', 'r', encoding='utf-8') as f:
    sql_content = f.read()

sql_statements = sql_content.split(';')

count = 0
for stmt in sql_statements:
    stmt = stmt.strip()
    if stmt and not stmt.startswith('DELETE FROM songs'):
        try:
            cursor.execute(stmt)
            count += 1
            print(f"Executed batch {count}")
        except Exception as e:
            print(f"Error: {e}")
            print(f"Statement: {stmt[:100]}...")

db.commit()
print(f"\nTotal executed: {count} statements")

cursor.close()
db.close()
