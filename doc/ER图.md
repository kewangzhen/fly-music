# Fly Music 数据库 ER 图

> 使用 Mermaid 渲染，可在支持 Mermaid 的编辑器中查看

## 完整 ER 图

```mermaid
erDiagram
    USERS {
        bigint id PK "用户ID"
        varchar username "用户名"
        varchar password "密码"
        varchar email "邮箱"
        varchar phone "手机号"
        varchar avatar "头像"
        varchar nickname "昵称"
        tinyint gender "性别"
        date birthdate "生日"
        text description "个人简介"
        tinyint role "角色"
        tinyint status "状态"
        timestamp vip_expire_at "VIP过期时间"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
        timestamp last_login_at "最后登录时间"
        varchar reset_token "重置密码Token"
        timestamp reset_token_expire_at "Token过期时间"
    }

    SONGS {
        bigint id PK "歌曲ID"
        varchar title "歌曲标题"
        int duration "时长(秒)"
        varchar url "音频URL"
        text lyrics "歌词"
        varchar cover "封面图"
        bigint album_id FK "专辑ID"
        bigint category_id FK "分类ID"
        bigint play_count "播放次数"
        int download_count "下载次数"
        int share_count "分享次数"
        tinyint status "状态"
        tinyint is_original "是否原创"
        bigint user_id FK "上传用户ID"
        tinyint user_upload "是否用户上传"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    ARTISTS {
        bigint id PK "歌手ID"
        varchar name "歌手名称"
        varchar avatar "头像"
        varchar cover "封面图"
        text description "简介"
        tinyint gender "性别"
        date birthdate "生日"
        varchar region "地区"
        int fan_count "粉丝数"
        int song_count "歌曲数"
        int album_count "专辑数"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    ALBUMS {
        bigint id PK "专辑ID"
        varchar title "专辑标题"
        varchar cover "封面图"
        date release_date "发行日期"
        text description "简介"
        bigint artist_id FK "艺术家ID"
        int song_count "歌曲数"
        bigint play_count "播放次数"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    CATEGORIES {
        bigint id PK "分类ID"
        varchar name "分类名称"
        varchar icon "图标"
        bigint parent_id FK "父分类ID"
        tinyint level "层级"
        int sort_order "排序"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    PLAYLISTS {
        bigint id PK "播放列表ID"
        varchar name "播放列表名称"
        text description "描述"
        varchar cover "封面图"
        bigint user_id FK "创建用户ID"
        tinyint is_public "是否公开"
        int song_count "歌曲数"
        bigint play_count "播放次数"
        tinyint type "类型"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    SONG_ARTISTS {
        bigint id PK "ID"
        bigint song_id FK "歌曲ID"
        bigint artist_id FK "歌手ID"
        timestamp created_at "创建时间"
    }

    PLAYLIST_SONGS {
        bigint id PK "ID"
        bigint playlist_id FK "播放列表ID"
        bigint song_id FK "歌曲ID"
        int position "位置"
        timestamp created_at "创建时间"
    }

    PLAYLIST_FAVORITES {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint playlist_id FK "播放列表ID"
        timestamp created_at "创建时间"
    }

    PLAY_HISTORY {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint song_id FK "歌曲ID"
        timestamp played_at "播放时间"
        int play_duration "播放时长(秒)"
        int progress "播放进度"
    }

    USER_FAVORITES {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        tinyint target_type "收藏类型"
        bigint target_id "目标ID"
        timestamp created_at "创建时间"
        tinyint status "状态"
    }

    USER_FOLLOWS {
        bigint id PK "ID"
        bigint follower_id FK "关注者ID"
        bigint followed_id FK "被关注者ID"
        timestamp created_at "创建时间"
    }

    ARTIST_FOLLOWS {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint artist_id FK "歌手ID"
        varchar artist_name "歌手名称"
        varchar artist_avatar "歌手头像"
        timestamp created_at "创建时间"
    }

    USER_BEHAVIOR {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint song_id FK "歌曲ID"
        tinyint behavior_type "行为类型"
        int weight "权重"
        timestamp created_at "创建时间"
    }

    USER_BEHAVIORS {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint song_id FK "歌曲ID"
        varchar behavior_type "行为类型"
        double weight "权重"
        timestamp created_at "创建时间"
    }

    USER_INTEREST_PROFILE {
        bigint id PK "ID"
        bigint user_id UK "用户ID"
        json interest_data "兴趣数据"
        timestamp last_updated "最后更新时间"
    }

    USER_INTEREST_PROFILES {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        json interest_data "兴趣数据"
        timestamp last_updated "最后更新时间"
    }

    POSTS {
        bigint id PK "帖子ID"
        bigint user_id FK "用户ID"
        text content "内容"
        bigint song_id FK "关联歌曲ID"
        varchar images "图片"
        int likes "点赞数"
        int comments_count "评论数"
        int shares_count "分享数"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    POST_COMMENTS {
        bigint id PK "评论ID"
        bigint user_id FK "用户ID"
        bigint post_id FK "帖子ID"
        text content "内容"
        bigint parent_id FK "父评论ID"
        bigint reply_to_user_id FK "回复用户ID"
        int likes "点赞数"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    POST_LIKES {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        bigint post_id FK "帖子ID"
        timestamp created_at "创建时间"
    }

    COMMENTS {
        bigint id PK "评论ID"
        bigint user_id FK "用户ID"
        tinyint target_type "评论类型"
        bigint target_id "目标ID"
        text content "内容"
        bigint parent_id FK "父评论ID"
        int likes "点赞数"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    HOT_RANKING {
        bigint id PK "ID"
        varchar ranking_type "榜单类型"
        bigint category_id FK "分类ID"
        bigint song_id FK "歌曲ID"
        int rank "排名"
        decimal hot_score "热度分数"
        timestamp created_at "创建时间"
    }

    HOT_RANKINGS {
        bigint id PK "ID"
        bigint song_id UK "歌曲ID"
        bigint play_count "播放次数"
        int rank_position "排名位置"
        timestamp calculated_at "计算时间"
    }

    SONG_HOT_SCORE {
        bigint id PK "ID"
        bigint song_id UK "歌曲ID"
        int play_score "播放分数"
        int favorite_score "收藏分数"
        int share_score "分享分数"
        int download_score "下载分数"
        int comment_score "评论分数"
        bigint total_weighted_score "加权总分"
        decimal hot_score "热度分数"
        int rank "排名"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    SONG_HOT_SCORES {
        bigint id PK "ID"
        bigint song_id UK "歌曲ID"
        double play_score "播放分数"
        double favorite_score "收藏分数"
        double share_score "分享分数"
        double recent_score "近期分数"
        double total_score "总分数"
        double download_score "下载分数"
        double comment_score "评论分数"
        bigint total_weighted_score "加权总分"
        timestamp calculated_at "计算时间"
        timestamp updated_at "更新时间"
    }

    RADAR_PLAYLIST {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        json song_ids "歌曲ID列表"
        json interest_summary "兴趣摘要"
        timestamp created_at "创建时间"
        timestamp expires_at "过期时间"
    }

    RADAR_PLAYLISTS {
        bigint id PK "ID"
        bigint playlist_id "播放列表ID"
        varchar playlist_name "播放列表名称"
        text description "描述"
        varchar cover "封面图"
        int song_count "歌曲数"
        bigint play_count "播放次数"
        tinyint status "状态"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    LISTENING_REPORT {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        int report_year "报告年份"
        int total_songs "听歌总数"
        decimal total_hours "听歌小时数"
        int total_days "听歌天数"
        bigint total_plays "播放总次数"
        json category_distribution "分类分布"
        json top_songs "Top歌曲"
        json top_artists "Top歌手"
        json tag_distribution "标签分布"
        json time_distribution "时间分布"
        timestamp created_at "创建时间"
    }

    AI_MUSIC_GENERATIONS {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        text prompt "生成提示词"
        varchar music_url "音乐URL"
        varchar cover "封面图"
        int duration "时长(秒)"
        varchar genre "风格"
        varchar mood "心情"
        tinyint status "状态"
        text error_message "错误信息"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    SYSTEM_CONFIG {
        bigint id PK "ID"
        varchar config_key UK "配置键"
        text config_value "配置值"
        varchar description "描述"
        timestamp created_at "创建时间"
        timestamp updated_at "更新时间"
    }

    SYSTEM_LOGS {
        bigint id PK "ID"
        bigint user_id FK "用户ID"
        varchar username "用户名"
        varchar action "操作"
        varchar target_type "目标类型"
        bigint target_id "目标ID"
        varchar method "请求方法"
        varchar ip_address "IP地址"
        varchar user_agent "用户代理"
        text details "详情"
        tinyint status "状态"
        text error_message "错误信息"
        timestamp created_at "创建时间"
    }

    %% 关系
    USERS ||--o{ SONGS : "上传"
    USERS ||--o{ PLAYLISTS : "创建"
    USERS ||--o{ PLAY_HISTORY : "播放"
    USERS ||--o{ USER_FAVORITES : "收藏"
    USERS ||--o{ USER_FOLLOWS : "关注"
    USERS ||--o{ ARTIST_FOLLOWS : "关注歌手"
    USERS ||--o{ USER_BEHAVIOR : "行为"
    USERS ||--o{ USER_BEHAVIORS : "行为"
    USERS ||--o{ USER_INTEREST_PROFILE : "兴趣"
    USERS ||--o{ USER_INTEREST_PROFILES : "兴趣"
    USERS ||--o{ POSTS : "发帖"
    USERS ||--o{ POST_COMMENTS : "评论"
    USERS ||--o{ POST_LIKES : "点赞"
    USERS ||--o{ COMMENTS : "评论"
    USERS ||--o{ PLAYLIST_FAVORITES : "收藏"
    USERS ||--o{ AI_MUSIC_GENERATIONS : "生成"
    USERS ||--o{ LISTENING_REPORT : "报告"
    USERS ||--o{ RADAR_PLAYLIST : "雷达歌单"

    SONGS ||--o{ PLAY_HISTORY : "播放历史"
    SONGS ||--o{ USER_FAVORITES : "收藏"
    SONGS ||--o{ USER_BEHAVIOR : "行为"
    SONGS ||--o{ USER_BEHAVIORS : "行为"
    SONGS ||--o{ HOT_RANKING : "热榜"
    SONGS ||--o{ HOT_RANKINGS : "热榜"
    SONGS ||--o{ SONG_HOT_SCORE : "热度"
    SONGS ||--o{ SONG_HOT_SCORES : "热度"
    SONGS ||--o{ COMMENTS : "评论"
    SONGS ||--o{ POSTS : "分享"

    ALBUMS ||--o{ SONGS : "包含"

    ARTISTS ||--o{ ALBUMS : "发行"
    ARTISTS ||--o{ SONG_ARTISTS : "演唱"
    ARTISTS ||--o{ ARTIST_FOLLOWS : "被关注"

    PLAYLISTS ||--o{ PLAYLIST_SONGS : "包含"
    PLAYLISTS ||--o{ PLAYLIST_FAVORITES : "被收藏"

    CATEGORIES ||--o{ SONGS : "分类"
    CATEGORIES ||o{ CATEGORIES : "父分类"

    POSTS ||--o{ POST_COMMENTS : "评论"
    POSTS ||--o{ POST_LIKES : "点赞"
```

## 表说明

### 1. 用户相关表

| 表名 | 说明 |
|-----|------|
| users | 用户表 |

### 2. 音乐内容表

| 表名 | 说明 |
|-----|------|
| songs | 歌曲表 |
| albums | 专辑表 |
| artists | 歌手表 |
| categories | 分类表 |
| song_artists | 歌曲-歌手关联表 |

### 3. 播放列表表

| 表名 | 说明 |
|-----|------|
| playlists | 播放列表表 |
| playlist_songs | 播放列表-歌曲关联表 |
| playlist_favorites | 播放列表收藏表 |
| radar_playlists | 雷达播放列表表 |

### 4. 用户行为表

| 表名 | 说明 |
|-----|------|
| play_history | 播放历史表 |
| user_favorites | 用户收藏表 |
| user_follows | 用户关注表 |
| artist_follows | 歌手关注表 |
| user_behavior | 用户行为表 |
| user_interest_profile | 用户兴趣画像表 |

### 5. 社交相关表

| 表名 | 说明 |
|-----|------|
| posts | 帖子表 |
| post_comments | 帖子评论表 |
| post_likes | 帖子点赞表 |
| comments | 评论表 |

### 6. 热榜相关表

| 表名 | 说明 |
|-----|------|
| hot_ranking | 热榜表 |
| hot_rankings | 热榜表(新) |
| song_hot_score | 歌曲热度分数表 |
| song_hot_scores | 歌曲热度分数表(新) |

### 7. 推荐相关表

| 表名 | 说明 |
|-----|------|
| listening_report | 听歌报告表 |
| user_interest_profile | 用户兴趣画像表 |
| radar_playlist | 雷达歌单表 |

### 8. AI音乐相关表

| 表名 | 说明 |
|-----|------|
| ai_music_generations | AI音乐生成记录表 |

### 9. 系统相关表

| 表名 | 说明 |
|-----|------|
| system_config | 系统配置表 |
| system_logs | 系统日志表 |

## 索引说明

### 主键索引
- 所有表都有 `id` 作为主键

### 外键索引
- users: role, status
- songs: album_id, category_id, play_count, status, user_id
- artists: name, status
- albums: title, artist_id, release_date, status
- categories: name, parent_id, status
- playlists: name, user_id, play_count, type, status

## 数据量统计

| 表名 | 数据量 |
|-----|--------|
| users | 50 |
| songs | 492 |
| albums | 202 |
| artists | 151 |
| categories | 12 |
| playlists | 31 |
| user_favorites | 1008 |
| posts | 51 |
