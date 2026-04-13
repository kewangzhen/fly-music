# Fly Music 数据库 ER 图

> 完整版见 [ER图-详细版.md](ER图-详细版.md)

## 精简 ER 图

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'primaryColor': '#4A90E2', 'primaryTextColor': '#fff', 'primaryBorderColor': '#4A90E2', 'lineColor': '#666', 'secondaryColor': '#F5A623', 'tertiaryColor': '#fff'}}}%%
erDiagram
    USERS ||--o{ SONGS : "上传"
    USERS ||--o{ PLAYLISTS : "创建"
    USERS ||--o{ PLAY_HISTORY : "播放"
    USERS ||--o{ USER_FAVORITES : "收藏"
    USERS ||--o{ USER_FOLLOWS : "关注"
    USERS ||--o{ POSTS : "发帖"
    USERS ||--o{ AI_MUSIC_GENERATIONS : "生成"

    SONGS ||--o{ PLAY_HISTORY : ""
    SONGS ||--o{ USER_FAVORITES : ""
    SONGS ||--o{ HOT_RANKINGS : ""
    SONGS ||--o{ SONG_HOT_SCORES : ""

    ALBUMS ||--o{ SONGS : "包含"
    ARTISTS ||--o{ ALBUMS : "发行"
    ARTISTS ||--o{ SONG_ARTISTS : "演唱"

    PLAYLISTS ||--o{ PLAYLIST_SONGS : "包含"
    CATEGORIES ||--o{ SONGS : "分类"
    POSTS ||--o{ POST_COMMENTS : ""
    POSTS ||--o{ POST_LIKES : ""

    USERS {
        bigint id PK
        varchar username
        varchar password
        varchar email
        varchar phone
        varchar avatar
        varchar nickname
        tinyint role
        tinyint status
        timestamp created_at
    }

    SONGS {
        bigint id PK
        varchar title
        int duration
        varchar url
        text lyrics
        varchar cover
        bigint album_id
        bigint category_id
        bigint play_count
        tinyint status
        timestamp created_at
    }

    ARTISTS {
        bigint id PK
        varchar name
        varchar avatar
        varchar cover
        text description
        int fan_count
        int song_count
        tinyint status
        timestamp created_at
    }

    ALBUMS {
        bigint id PK
        varchar title
        varchar cover
        date release_date
        bigint artist_id
        int song_count
        tinyint status
        timestamp created_at
    }

    CATEGORIES {
        bigint id PK
        varchar name
        varchar icon
        bigint parent_id
        tinyint level
        tinyint status
    }

    PLAYLISTS {
        bigint id PK
        varchar name
        text description
        varchar cover
        bigint user_id
        tinyint is_public
        int song_count
        tinyint type
    }

    SONG_ARTISTS {
        bigint id PK
        bigint song_id
        bigint artist_id
    }

    PLAYLIST_SONGS {
        bigint id PK
        bigint playlist_id
        bigint song_id
        int position
    }

    PLAY_HISTORY {
        bigint id PK
        bigint user_id
        bigint song_id
        timestamp played_at
        int play_duration
    }

    USER_FAVORITES {
        bigint id PK
        bigint user_id
        tinyint target_type
        bigint target_id
        timestamp created_at
    }

    USER_FOLLOWS {
        bigint id PK
        bigint follower_id
        bigint followed_id
        timestamp created_at
    }

    HOT_RANKINGS {
        bigint id PK
        bigint song_id
        bigint play_count
        int rank_position
    }

    SONG_HOT_SCORES {
        bigint id PK
        bigint song_id
        double total_score
        timestamp calculated_at
    }

    POSTS {
        bigint id PK
        bigint user_id
        text content
        int likes
        int comments_count
        tinyint status
    }

    POST_COMMENTS {
        bigint id PK
        bigint user_id
        bigint post_id
        text content
    }

    POST_LIKES {
        bigint id PK
        bigint user_id
        bigint post_id
    }

    AI_MUSIC_GENERATIONS {
        bigint id PK
        bigint user_id
        text prompt
        varchar music_url
        varchar genre
        tinyint status
        timestamp created_at
    }
```

## 快速查看（表清单）

| 分类 | 表名 | 说明 | 字段数 |
|-----|------|------|--------|
| 用户 | users | 用户表 | 17 |
| 音乐 | songs | 歌曲表 | 16 |
| 音乐 | albums | 专辑表 | 11 |
| 音乐 | artists | 歌手表 | 13 |
| 音乐 | categories | 分类表 | 9 |
| 音乐 | song_artists | 歌曲-歌手关联 | 3 |
| 播放列表 | playlists | 播放列表 | 12 |
| 播放列表 | playlist_songs | 播放列表-歌曲 | 5 |
| 行为 | play_history | 播放历史 | 6 |
| 行为 | user_favorites | 用户收藏 | 6 |
| 行为 | user_follows | 用户关注 | 4 |
| 社交 | posts | 帖子 | 11 |
| 社交 | post_comments | 帖子评论 | 9 |
| 社交 | post_likes | 帖子点赞 | 4 |
| 热榜 | hot_rankings | 热榜 | 5 |
| 热榜 | song_hot_scores | 热度分数 | 12 |
| AI | ai_music_generations | AI音乐生成 | 12 |

**共 31 张表**

---

> 查看完整详细版（含所有字段、中文注释、关系图）：[ER图-详细版.md](ER图-详细版.md)
