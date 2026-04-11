-- Fly Music 数据库建表脚本
-- 创建日期: 2024-04-11
-- 数据库版本: MySQL 8.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS fly_music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fly_music;

-- =====================================================
-- 用户相关表
-- =====================================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    gender TINYINT(4) DEFAULT NULL COMMENT '性别: 0-未知, 1-男, 2-女',
    birthdate DATE DEFAULT NULL COMMENT '生日',
    role TINYINT(4) DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    vip_expire_at TIMESTAMP NULL DEFAULT NULL COMMENT 'VIP过期时间',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    reset_token VARCHAR(255) DEFAULT NULL COMMENT '重置密码Token',
    reset_token_expire_at TIMESTAMP NULL DEFAULT NULL COMMENT '重置密码Token过期时间',
    description TEXT DEFAULT NULL COMMENT '个人简介',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    KEY idx_status (status),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 音乐内容相关表
-- =====================================================

-- 歌手表
CREATE TABLE IF NOT EXISTS artists (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '歌手ID',
    name VARCHAR(100) NOT NULL COMMENT '歌手名称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    description TEXT DEFAULT NULL COMMENT '简介',
    gender TINYINT(4) DEFAULT NULL COMMENT '性别: 0-未知, 1-男, 2-女',
    birthdate DATE DEFAULT NULL COMMENT '生日',
    region VARCHAR(50) DEFAULT NULL COMMENT '地区',
    fan_count INT(11) DEFAULT 0 COMMENT '粉丝数',
    song_count INT(11) DEFAULT 0 COMMENT '歌曲数',
    album_count INT(11) DEFAULT 0 COMMENT '专辑数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_name (name),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌手表';

-- 专辑表
CREATE TABLE IF NOT EXISTS albums (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '专辑ID',
    title VARCHAR(100) NOT NULL COMMENT '专辑标题',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    release_date DATE DEFAULT NULL COMMENT '发行日期',
    description TEXT DEFAULT NULL COMMENT '简介',
    artist_id BIGINT(20) DEFAULT NULL COMMENT '艺术家ID',
    song_count INT(11) DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT(20) DEFAULT 0 COMMENT '播放次数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_title (title),
    KEY idx_artist_id (artist_id),
    KEY idx_release_date (release_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专辑表';

-- 歌曲表
CREATE TABLE IF NOT EXISTS songs (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '歌曲ID',
    title VARCHAR(100) NOT NULL COMMENT '歌曲标题',
    duration INT(11) DEFAULT 0 COMMENT '时长(秒)',
    url VARCHAR(255) NOT NULL COMMENT '音频URL',
    lyrics TEXT DEFAULT NULL COMMENT '歌词',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    album_id BIGINT(20) DEFAULT NULL COMMENT '专辑ID',
    category_id BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    play_count BIGINT(20) DEFAULT 0 COMMENT '播放次数',
    download_count INT(11) DEFAULT 0 COMMENT '下载次数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    is_original TINYINT(4) DEFAULT 0 COMMENT '是否原创: 0-否, 1-是',
    user_id BIGINT(20) DEFAULT NULL COMMENT '上传用户ID',
    user_upload TINYINT(4) DEFAULT 0 COMMENT '是否用户上传: 0-否, 1-是',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    share_count INT(11) DEFAULT 0 COMMENT '分享次数',
    PRIMARY KEY (id),
    KEY idx_title (title),
    KEY idx_album_id (album_id),
    KEY idx_category_id (category_id),
    KEY idx_play_count (play_count),
    KEY idx_status (status),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲表';

-- 歌曲-歌手关联表
CREATE TABLE IF NOT EXISTS song_artists (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    artist_id BIGINT(20) NOT NULL COMMENT '歌手ID',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_song_id (song_id),
    KEY idx_artist_id (artist_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲歌手关联表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) DEFAULT NULL COMMENT '图标',
    parent_id BIGINT(20) DEFAULT NULL COMMENT '父分类ID',
    level TINYINT(4) DEFAULT 1 COMMENT '层级',
    sort_order INT(11) DEFAULT 0 COMMENT '排序',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_name (name),
    KEY idx_parent_id (parent_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- =====================================================
-- 播放列表相关表
-- =====================================================

-- 播放列表表
CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '播放列表ID',
    name VARCHAR(100) NOT NULL COMMENT '播放列表名称',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    user_id BIGINT(20) DEFAULT NULL COMMENT '创建用户ID',
    is_public TINYINT(1) DEFAULT 1 COMMENT '是否公开: 0-私有, 1-公开',
    song_count INT(11) DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT(20) DEFAULT 0 COMMENT '播放次数',
    type TINYINT(4) DEFAULT 0 COMMENT '类型: 0-普通, 1-心动, 2-雷达',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_name (name),
    KEY idx_user_id (user_id),
    KEY idx_play_count (play_count),
    KEY idx_type (type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放列表表';

-- 播放列表-歌曲关联表
CREATE TABLE IF NOT EXISTS playlist_songs (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    playlist_id BIGINT(20) NOT NULL COMMENT '播放列表ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    position INT(11) DEFAULT 0 COMMENT '位置',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_playlist_id (playlist_id),
    KEY idx_song_id (song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放列表歌曲关联表';

-- 播放列表收藏表
CREATE TABLE IF NOT EXISTS playlist_favorites (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    playlist_id BIGINT(20) NOT NULL COMMENT '播放列表ID',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_playlist_id (playlist_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放列表收藏表';

-- 雷达播放列表表
CREATE TABLE IF NOT EXISTS radar_playlists (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    playlist_id BIGINT(20) NOT NULL COMMENT '播放列表ID',
    playlist_name VARCHAR(100) NOT NULL COMMENT '播放列表名称',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    song_count INT(11) DEFAULT 0 COMMENT '歌曲数',
    play_count BIGINT(20) DEFAULT 0 COMMENT '播放次数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_play_count (play_count),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雷达播放列表表';

-- =====================================================
-- 用户行为相关表
-- =====================================================

-- 播放历史表
CREATE TABLE IF NOT EXISTS play_history (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    played_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    play_duration INT(11) DEFAULT 0 COMMENT '播放时长(秒)',
    progress INT(11) DEFAULT 0 COMMENT '播放进度(秒)',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_song_id (song_id),
    KEY idx_played_at (played_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放历史表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    target_type TINYINT(4) NOT NULL COMMENT '收藏类型: 1-歌曲, 2-专辑, 3-歌手, 4-播放列表',
    target_id BIGINT(20) NOT NULL COMMENT '目标ID',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-取消, 1-收藏',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    follower_id BIGINT(20) NOT NULL COMMENT '关注者ID',
    followed_id BIGINT(20) NOT NULL COMMENT '被关注者ID',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_follower_id (follower_id),
    KEY idx_followed_id (followed_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 歌手关注表
CREATE TABLE IF NOT EXISTS artist_follows (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    artist_id BIGINT(20) NOT NULL COMMENT '歌手ID',
    artist_name VARCHAR(100) DEFAULT NULL COMMENT '歌手名称',
    artist_avatar VARCHAR(255) DEFAULT NULL COMMENT '歌手头像',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌手关注表';

-- =====================================================
-- 社交相关表
-- =====================================================

-- 帖子表
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '内容',
    song_id BIGINT(20) DEFAULT NULL COMMENT '关联歌曲ID',
    images VARCHAR(1000) DEFAULT NULL COMMENT '图片(逗号分隔)',
    likes INT(11) DEFAULT 0 COMMENT '点赞数',
    comments_count INT(11) DEFAULT 0 COMMENT '评论数',
    shares_count INT(11) DEFAULT 0 COMMENT '分享数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 帖子评论表
CREATE TABLE IF NOT EXISTS post_comments (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    post_id BIGINT(20) NOT NULL COMMENT '帖子ID',
    content TEXT NOT NULL COMMENT '内容',
    parent_id BIGINT(20) DEFAULT NULL COMMENT '父评论ID',
    reply_to_user_id BIGINT(20) DEFAULT NULL COMMENT '回复用户ID',
    likes INT(11) DEFAULT 0 COMMENT '点赞数',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_post_id (post_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS post_likes (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    post_id BIGINT(20) NOT NULL COMMENT '帖子ID',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    target_type TINYINT(4) NOT NULL COMMENT '评论类型: 1-歌曲, 2-专辑',
    target_id BIGINT(20) NOT NULL COMMENT '目标ID',
    content TEXT NOT NULL COMMENT '内容',
    parent_id BIGINT(20) DEFAULT NULL COMMENT '父评论ID',
    likes INT(11) DEFAULT 0 COMMENT '点赞数',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-删除, 1-正常',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_target (target_type, target_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- =====================================================
-- 推荐相关表
-- =====================================================

-- 听歌报告表
CREATE TABLE IF NOT EXISTS listening_report (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    report_year INT(11) NOT NULL COMMENT '报告年份',
    total_songs INT(11) DEFAULT 0 COMMENT '听歌总数',
    total_hours DECIMAL(10,2) DEFAULT 0.00 COMMENT '听歌小时数',
    total_days INT(11) DEFAULT 0 COMMENT '听歌天数',
    total_plays BIGINT(20) DEFAULT 0 COMMENT '播放总次数',
    category_distribution JSON DEFAULT NULL COMMENT '分类分布',
    top_songs JSON DEFAULT NULL COMMENT 'Top歌曲',
    top_artists JSON DEFAULT NULL COMMENT 'Top歌手',
    tag_distribution JSON DEFAULT NULL COMMENT '标签分布',
    time_distribution JSON DEFAULT NULL COMMENT '时间分布',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听歌报告表';

-- 用户兴趣画像表
CREATE TABLE IF NOT EXISTS user_interest_profile (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    interest_data JSON NOT NULL COMMENT '兴趣数据',
    last_updated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣画像表';

-- 用户行为表
CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    behavior_type TINYINT(4) NOT NULL COMMENT '行为类型: 1-播放, 2-收藏, 3-下载, 4-分享',
    weight INT(11) DEFAULT 1 COMMENT '权重',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_song_id (song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- 雷达歌单表
CREATE TABLE IF NOT EXISTS radar_playlist (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    song_ids JSON NOT NULL COMMENT '歌曲ID列表',
    interest_summary JSON NOT NULL COMMENT '兴趣摘要',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雷达歌单表';

-- =====================================================
-- 热榜相关表
-- =====================================================

-- 热榜表
CREATE TABLE IF NOT EXISTS hot_rankings (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    play_count BIGINT(20) DEFAULT 0 COMMENT '播放次数',
    rank_position INT(11) DEFAULT 0 COMMENT '排名位置',
    calculated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_song_id (song_id),
    KEY idx_rank_position (rank_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热榜表';

-- 歌曲热度分数表
CREATE TABLE IF NOT EXISTS song_hot_scores (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    play_score DOUBLE DEFAULT 0 COMMENT '播放分数',
    favorite_score DOUBLE DEFAULT 0 COMMENT '收藏分数',
    share_score DOUBLE DEFAULT 0 COMMENT '分享分数',
    recent_score DOUBLE DEFAULT 0 COMMENT '近期分数',
    total_score DOUBLE DEFAULT 0 COMMENT '总分数',
    calculated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    download_score DOUBLE DEFAULT 0 COMMENT '下载分数',
    comment_score DOUBLE DEFAULT 0 COMMENT '评论分数',
    total_weighted_score BIGINT(20) DEFAULT 0 COMMENT '加权总分',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_song_id (song_id),
    KEY idx_total_score (total_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲热度分数表';

-- =====================================================
-- AI音乐相关表
-- =====================================================

-- AI音乐生成记录表
CREATE TABLE IF NOT EXISTS ai_music_generations (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    prompt TEXT NOT NULL COMMENT '生成提示词',
    music_url VARCHAR(255) DEFAULT NULL COMMENT '生成的音乐URL',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    duration INT(11) DEFAULT 0 COMMENT '时长(秒)',
    genre VARCHAR(50) DEFAULT NULL COMMENT '风格',
    mood VARCHAR(50) DEFAULT NULL COMMENT '心情',
    status TINYINT(4) DEFAULT 0 COMMENT '状态: 0-处理中, 1-成功, 2-失败',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI音乐生成记录表';

-- =====================================================
-- 系统相关表
-- =====================================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT DEFAULT NULL COMMENT '配置值',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) DEFAULT NULL COMMENT '用户ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    action VARCHAR(100) NOT NULL COMMENT '操作',
    target_type VARCHAR(50) DEFAULT NULL COMMENT '目标类型',
    target_id BIGINT(20) DEFAULT NULL COMMENT '目标ID',
    method VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    details TEXT DEFAULT NULL COMMENT '详情',
    status TINYINT(4) DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_action (action),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- =====================================================
-- 其他备用表 (用于兼容或扩展)
-- =====================================================

-- 旧版用户行为表
CREATE TABLE IF NOT EXISTS user_behaviors (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    behavior_type VARCHAR(20) NOT NULL COMMENT '行为类型',
    weight DOUBLE DEFAULT 0 COMMENT '权重',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_song_id (song_id),
    KEY idx_behavior_type (behavior_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表(旧)';

-- 旧版用户兴趣画像表
CREATE TABLE IF NOT EXISTS user_interest_profiles (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    interest_data JSON DEFAULT NULL COMMENT '兴趣数据',
    last_updated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣画像表(旧)';

-- 旧版歌曲热度分数表
CREATE TABLE IF NOT EXISTS song_hot_score (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    play_score INT(11) DEFAULT 0 COMMENT '播放分数',
    favorite_score INT(11) DEFAULT 0 COMMENT '收藏分数',
    share_score INT(11) DEFAULT 0 COMMENT '分享分数',
    download_score INT(11) DEFAULT 0 COMMENT '下载分数',
    comment_score INT(11) DEFAULT 0 COMMENT '评论分数',
    total_weighted_score BIGINT(20) DEFAULT 0 COMMENT '加权总分',
    hot_score DECIMAL(10,2) DEFAULT 0.00 COMMENT '热度分数',
    rank INT(11) DEFAULT 0 COMMENT '排名',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_song_id (song_id),
    KEY idx_hot_score (hot_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲热度分数表(旧)';

-- 旧版热榜表
CREATE TABLE IF NOT EXISTS hot_ranking (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    ranking_type VARCHAR(50) NOT NULL COMMENT '榜单类型',
    category_id BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    song_id BIGINT(20) NOT NULL COMMENT '歌曲ID',
    rank INT(11) NOT NULL COMMENT '排名',
    hot_score DECIMAL(10,2) NOT NULL COMMENT '热度分数',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_ranking_type (ranking_type),
    KEY idx_song_id (song_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热榜表(旧)';
