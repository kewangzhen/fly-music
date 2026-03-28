-- ==========================================
-- Fly Music 数据库建表脚本
-- 数据库名: fly_music
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS fly_music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fly_music;

-- ==========================================
-- 1. 用户表
-- ==========================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    gender TINYINT DEFAULT NULL COMMENT '性别: 0未知, 1男, 2女',
    birthdate DATE DEFAULT NULL COMMENT '生日',
    role TINYINT DEFAULT 0 COMMENT '角色: 0普通用户, 1VIP, 2管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    vip_expire_at TIMESTAMP NULL DEFAULT NULL COMMENT 'VIP过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    reset_token VARCHAR(255) DEFAULT NULL COMMENT '密码重置令牌',
    reset_token_expire_at TIMESTAMP NULL DEFAULT NULL COMMENT '令牌过期时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==========================================
-- 2. 艺术家表
-- ==========================================
DROP TABLE IF EXISTS artists;
CREATE TABLE artists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '艺术家ID',
    name VARCHAR(100) NOT NULL COMMENT '艺术家名称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    description TEXT DEFAULT NULL COMMENT '描述',
    gender TINYINT DEFAULT NULL COMMENT '性别: 0未知, 1男, 2女',
    birthdate DATE DEFAULT NULL COMMENT '生日',
    region VARCHAR(50) DEFAULT NULL COMMENT '地区',
    fan_count INT DEFAULT 0 COMMENT '粉丝数',
    song_count INT DEFAULT 0 COMMENT '歌曲数',
    album_count INT DEFAULT 0 COMMENT '专辑数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='艺术家表';

-- ==========================================
-- 3. 专辑表
-- ==========================================
DROP TABLE IF EXISTS albums;
CREATE TABLE albums (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '专辑ID',
    title VARCHAR(100) NOT NULL COMMENT '专辑标题',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    release_date DATE DEFAULT NULL COMMENT '发行日期',
    description TEXT DEFAULT NULL COMMENT '专辑描述',
    artist_id BIGINT DEFAULT NULL COMMENT '艺术家ID',
    song_count INT DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_title (title),
    INDEX idx_artist (artist_id),
    INDEX idx_release (release_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专辑表';

-- ==========================================
-- 4. 分类表
-- ==========================================
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    parent_id BIGINT DEFAULT NULL COMMENT '父分类ID',
    level TINYINT DEFAULT 1 COMMENT '层级深度',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_parent (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ==========================================
-- 5. 歌曲表
-- ==========================================
DROP TABLE IF EXISTS songs;
CREATE TABLE songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌曲ID',
    title VARCHAR(100) NOT NULL COMMENT '歌曲标题',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    url VARCHAR(255) NOT NULL COMMENT '歌曲URL',
    lyrics TEXT DEFAULT NULL COMMENT '歌词',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    album_id BIGINT DEFAULT NULL COMMENT '专辑ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0待审核, 1已发布, 2已下架',
    is_original TINYINT DEFAULT 0 COMMENT '是否原创: 0否, 1是',
    user_id BIGINT DEFAULT NULL COMMENT '上传用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_title (title),
    INDEX idx_album (album_id),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_play_count (play_count),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲表';

-- ==========================================
-- 6. 歌曲艺术家关联表
-- ==========================================
DROP TABLE IF EXISTS song_artists;
CREATE TABLE song_artists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    artist_id BIGINT NOT NULL COMMENT '艺术家ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_song_artist (song_id, artist_id),
    INDEX idx_song (song_id),
    INDEX idx_artist (artist_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲艺术家关联表';

-- ==========================================
-- 7. 播放列表表
-- ==========================================
DROP TABLE IF EXISTS playlists;
CREATE TABLE playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '播放列表ID',
    name VARCHAR(100) NOT NULL COMMENT '播放列表名称',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    user_id BIGINT DEFAULT NULL COMMENT '创建用户ID',
    is_public BOOLEAN DEFAULT TRUE COMMENT '是否公开',
    song_count INT DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    type TINYINT DEFAULT 0 COMMENT '类型: 0用户自建, 1心动歌单, 2雷达榜单, 3官方推荐',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_user (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_play_count (play_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表表';

-- ==========================================
-- 8. 播放列表歌曲关联表
-- ==========================================
DROP TABLE IF EXISTS playlist_songs;
CREATE TABLE playlist_songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL COMMENT '播放列表ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    position INT DEFAULT 0 COMMENT '位置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_playlist_song (playlist_id, song_id),
    INDEX idx_playlist (playlist_id),
    INDEX idx_song (song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表歌曲关联表';

-- ==========================================
-- 9. 播放历史表
-- ==========================================
DROP TABLE IF EXISTS play_history;
CREATE TABLE play_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    play_duration INT DEFAULT 0 COMMENT '播放时长(秒)',
    progress INT DEFAULT 0 COMMENT '播放进度(秒)',
    INDEX idx_user (user_id),
    INDEX idx_song (song_id),
    INDEX idx_played (played_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放历史表';

-- ==========================================
-- 10. 用户收藏表
-- ==========================================
DROP TABLE IF EXISTS user_favorites;
CREATE TABLE user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type TINYINT NOT NULL COMMENT '目标类型: 1歌曲, 2歌单, 3专辑, 4艺术家',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ==========================================
-- 11. 用户关注表
-- ==========================================
DROP TABLE IF EXISTS user_follows;
CREATE TABLE user_follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    followed_id BIGINT NOT NULL COMMENT '被关注者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    UNIQUE KEY uk_follow (follower_id, followed_id),
    INDEX idx_follower (follower_id),
    INDEX idx_followed (followed_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- ==========================================
-- 12. 动态帖子表
-- ==========================================
DROP TABLE IF EXISTS posts;
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '内容',
    song_id BIGINT DEFAULT NULL COMMENT '关联歌曲ID',
    images VARCHAR(1000) DEFAULT NULL COMMENT '图片JSON',
    likes INT DEFAULT 0 COMMENT '点赞数',
    comments_count INT DEFAULT 0 COMMENT '评论数',
    shares_count INT DEFAULT 0 COMMENT '分享数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0删除, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态帖子表';

-- ==========================================
-- 13. 评论表
-- ==========================================
DROP TABLE IF EXISTS comments;
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type TINYINT NOT NULL COMMENT '目标类型: 1歌曲, 2歌单, 3帖子',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0删除, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ==========================================
-- 14. AI音乐生成表
-- ==========================================
DROP TABLE IF EXISTS ai_music_generations;
CREATE TABLE ai_music_generations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    prompt TEXT NOT NULL COMMENT '生成提示词',
    music_url VARCHAR(255) DEFAULT NULL COMMENT '生成的音乐URL',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    genre VARCHAR(50) DEFAULT NULL COMMENT '音乐风格',
    mood VARCHAR(50) DEFAULT NULL COMMENT '情绪',
    status TINYINT DEFAULT 0 COMMENT '状态: 0处理中, 1成功, 2失败',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI音乐生成表';

-- ==========================================
-- 15. 系统日志表
-- ==========================================
DROP TABLE IF EXISTS system_logs;
CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '操作用户ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) DEFAULT NULL COMMENT '目标类型',
    target_id BIGINT DEFAULT NULL COMMENT '目标ID',
    method VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    details TEXT DEFAULT NULL COMMENT '详情',
    status TINYINT DEFAULT 1 COMMENT '状态: 0失败, 1成功',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- ==========================================
-- 16. 用户兴趣画像表
-- ==========================================
DROP TABLE IF EXISTS user_interest_profiles;
CREATE TABLE user_interest_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    genre VARCHAR(50) DEFAULT NULL COMMENT '风格',
    mood VARCHAR(50) DEFAULT NULL COMMENT '情绪',
    artist_id BIGINT DEFAULT NULL COMMENT '艺术家ID',
    weight DOUBLE DEFAULT 0.0 COMMENT '兴趣权重',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    INDEX idx_user (user_id),
    INDEX idx_weight (weight)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兴趣画像表';

-- ==========================================
-- 17. 用户行为记录表
-- ==========================================
DROP TABLE IF EXISTS user_behaviors;
CREATE TABLE user_behaviors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    behavior_type VARCHAR(20) NOT NULL COMMENT '行为类型: play/favorite/search/skip/share',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: song/playlist/album/artist',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    duration INT DEFAULT NULL COMMENT '停留时长(秒)',
    completed BOOLEAN DEFAULT TRUE COMMENT '是否完整播放',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
    INDEX idx_user (user_id),
    INDEX idx_behavior (behavior_type),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为记录表';

-- ==========================================
-- 18. 歌曲热度分数表
-- ==========================================
DROP TABLE IF EXISTS song_hot_scores;
CREATE TABLE song_hot_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    play_score DOUBLE DEFAULT 0 COMMENT '播放得分',
    favorite_score DOUBLE DEFAULT 0 COMMENT '收藏得分',
    share_score DOUBLE DEFAULT 0 COMMENT '分享得分',
    recent_score DOUBLE DEFAULT 0 COMMENT '近期热度得分',
    total_score DOUBLE DEFAULT 0 COMMENT '综合热度分数',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    UNIQUE KEY uk_song (song_id),
    INDEX idx_total (total_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲热度分数表';

-- ==========================================
-- 19. 热门榜单表
-- ==========================================
DROP TABLE IF EXISTS hot_rankings;
CREATE TABLE hot_rankings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    rank_position INT DEFAULT 0 COMMENT '排名',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    UNIQUE KEY uk_song (song_id),
    INDEX idx_rank (rank_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门榜单表';

-- ==========================================
-- 20. 雷达歌单表
-- ==========================================
DROP TABLE IF EXISTS radar_playlists;
CREATE TABLE radar_playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL COMMENT '播放列表ID',
    playlist_name VARCHAR(100) NOT NULL COMMENT '歌单名称',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    song_count INT DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_play (play_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='雷达歌单表';