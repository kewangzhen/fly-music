# Fly Music 数据库 ER 图

> 使用 Mermaid 渲染，可在支持 Mermaid 的编辑器中查看

## 完整 ER 图

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar username
        varchar password
        varchar email
        varchar phone
        varchar avatar
        varchar nickname
        integer gender
        datetime birthdate
        text description
        tinyint role
        tinyint status
        datetime vip_expire_at
        timestamp created_at
        timestamp updated_at
    }

    SONGS {
        bigint id PK
        varchar title
        integer duration
        varchar url
        text lyrics
        varchar cover
        bigint album_id
        bigint category_id
        bigint play_count
        integer download_count
        integer share_count
        tinyint status
        tinyint is_original
        bigint user_id
        tinyint user_upload
        timestamp created_at
        timestamp updated_at
    }

    ARTISTS {
        bigint id PK
        varchar name
        varchar avatar
        varchar cover
        text description
        integer gender
        datetime birthdate
        varchar region
        integer song_count
        integer album_count
        integer fan_count
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    ALBUMS {
        bigint id PK
        varchar title
        varchar cover
        datetime release_date
        text description
        bigint artist_id
        integer song_count
        bigint play_count
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    CATEGORIES {
        bigint id PK
        varchar name
        text description
        bigint parent_id
        integer level
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    PLAYLISTS {
        bigint id PK
        varchar name
        text description
        varchar cover
        bigint user_id
        boolean is_public
        integer song_count
        bigint play_count
        tinyint type
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    PLAY_HISTORIES {
        bigint id PK
        bigint user_id
        bigint song_id
        datetime play_time
        integer duration
        tinyint status
        timestamp created_at
    }

    USER_FAVORITES {
        bigint id PK
        bigint user_id
        bigint song_id
        timestamp created_at
    }

    PLAYLIST_FAVORITES {
        bigint id PK
        bigint user_id
        bigint playlist_id
        timestamp created_at
    }

    ARTIST_FOLLOWS {
        bigint id PK
        bigint user_id
        bigint artist_id
        timestamp created_at
    }

    USER_FOLLOWS {
        bigint id PK
        bigint follower_id
        bigint following_id
        timestamp created_at
    }

    USER_BEHAVIOR {
        bigint id PK
        bigint user_id
        bigint song_id
        integer behavior_type
        integer weight
        timestamp created_at
    }

    USER_INTEREST_PROFILE {
        bigint id PK
        bigint user_id
        json genre_preferences
        json artist_preferences
        json mood_preferences
        timestamp updated_at
    }

    SONG_HOT_SCORE {
        bigint id PK
        bigint song_id
        double hot_score
        double play_score
        double favorite_score
        double share_score
        double comment_score
        timestamp updated_at
    }

    HOT_RANKING {
        bigint id PK
        tinyint type
        bigint song_id
        integer rank
        double score
        timestamp updated_at
    }

    RADAR_PLAYLISTS {
        bigint id PK
        bigint user_id
        varchar name
        text description
        varchar cover
        tinyint type
        integer song_count
        timestamp created_at
        timestamp updated_at
    }

    AI_MUSIC_GENERATION {
        bigint id PK
        bigint user_id
        text prompt
        integer duration
        varchar style
        varchar audio_url
        tinyint status
        text error_message
        timestamp created_at
        timestamp updated_at
    }

    POSTS {
        bigint id PK
        bigint user_id
        text content
        json image_urls
        integer likes
        integer comments
        integer shares
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    POST_COMMENTS {
        bigint id PK
        bigint post_id
        bigint user_id
        text content
        bigint parent_id
        integer likes
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    POST_LIKES {
        bigint id PK
        bigint post_id
        bigint user_id
        timestamp created_at
    }

    COMMENTS {
        bigint id PK
        bigint song_id
        bigint user_id
        text content
        bigint parent_id
        integer likes
        tinyint status
        timestamp created_at
        timestamp updated_at
    }

    SYSTEM_CONFIGS {
        bigint id PK
        varchar config_key
        text config_value
        varchar description
        tinyint status
        timestamp updated_at
    }

    SYSTEM_LOGS {
        bigint id PK
        bigint user_id
        varchar operation
        text description
        varchar ip_address
        text user_agent
        timestamp created_at
    }

    PLAYLIST_SONGS {
        bigint playlist_id
        bigint song_id
        integer order_index
        timestamp added_at
    }

    SONG_ARTISTS {
        bigint song_id
        bigint artist_id
    }

    USERS ||--o{ PLAYLISTS : "creates"
    USERS ||--o{ PLAY_HISTORIES : "plays"
    USERS ||--o{ USER_FAVORITES : "favorites"
    USERS ||--o{ PLAYLIST_FAVORITES : "favorites"
    USERS ||--o{ USER_FOLLOWS : "follows"
    USERS ||--o{ ARTIST_FOLLOWS : "follows"
    USERS ||--o{ USER_BEHAVIOR : "behaves"
    USERS ||--o{ USER_INTEREST_PROFILE : "has"
    USERS ||--o{ POSTS : "posts"
    USERS ||--o{ AI_MUSIC_GENERATION : "generates"
    USERS ||--o{ SYSTEM_LOGS : "logs"

    SONGS ||--o{ PLAY_HISTORIES : "played"
    SONGS ||--o{ USER_FAVORITES : "favorited"
    SONGS ||--o{ USER_BEHAVIOR : "target"
    SONGS ||--o{ SONG_HOT_SCORE : "scores"
    SONGS ||--o{ HOT_RANKING : "ranked"
    SONGS ||--o{ PLAYLIST_SONGS : "in"
    SONGS ||--o{ COMMENTS : "commented"

    ALBUMS ||--o{ SONGS : "contains"
    ALBUMS }o--|| ARTISTS : "created"

    CATEGORIES ||--o{ SONGS : "categorizes"

    PLAYLISTS ||--o{ PLAYLIST_FAVORITES : "favorited"
    PLAYLISTS ||--o{ PLAYLIST_SONGS : "contains"

    POSTS ||--o{ POST_COMMENTS : "has"
    POSTS ||--o{ POST_LIKES : "liked"

    ARTISTS ||--o{ ALBUMS : "releases"
    ARTISTS ||--o{ ARTIST_FOLLOWS : "followed"
    ARTISTS ||--o{ SONG_ARTISTS : "performs"
```

## 核心实体 ER 图

```mermaid
erDiagram
    USERS ||--o{ PLAYLISTS : creates
    USERS ||--o{ USER_FAVORITES : favorites
    USERS ||--o{ POSTS : posts
    
    PLAYLISTS ||--o{ PLAYLIST_SONGS : contains
    PLAYLIST_SONGS }o--|| SONGS : includes
    
    SONGS ||--o{ ALBUMS : belongs
    SONGS ||--o{ ARTISTS : has
    
    ALBUMS }o--|| ARTISTS : created_by
    
    ARTISTS {
        bigint id PK
        varchar name
        varchar avatar
        text description
    }

    ALBUMS {
        bigint id PK
        varchar title
        varchar cover
        bigint artist_id
    }

    SONGS {
        bigint id PK
        varchar title
        varchar url
        varchar cover
        bigint album_id
    }

    PLAYLISTS {
        bigint id PK
        varchar name
        varchar cover
        bigint user_id
    }

    USERS {
        bigint id PK
        varchar username
        varchar nickname
    }

    POSTS {
        bigint id PK
        text content
        bigint user_id
    }
```

---

**文档版本**：1.2
**更新日期**：2026-04-12