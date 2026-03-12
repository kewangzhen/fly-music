-- =============================================
-- 雷达歌单功能数据库脚本
-- 创建时间: 2026-03-12
-- =============================================

-- 1. 用户行为记录表
-- 记录用户对歌曲的各种行为（播放、收藏、切歌等）
CREATE TABLE IF NOT EXISTS `user_behavior` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `song_id` BIGINT NOT NULL COMMENT '歌曲ID',
    `behavior_type` TINYINT NOT NULL COMMENT '行为类型: 1-播放 2-完整收听 3-收藏 4-分享 5-切歌 6-不喜欢',
    `weight` INT DEFAULT 1 COMMENT '权重分数',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_created` (`user_id`, `created_at`),
    INDEX `idx_song` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为记录表';

-- 2. 用户兴趣画像表
-- 存储用户对各音乐标签的偏好分数
CREATE TABLE IF NOT EXISTS `user_interest_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID（唯一）',
    `interest_data` JSON NOT NULL COMMENT '兴趣数据JSON，如: {"摇滚": 45, "民谣": 22, "流行": 4}',
    `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兴趣画像表';

-- 3. 雷达歌单表
-- 存储每日生成的个性化推荐歌单
CREATE TABLE IF NOT EXISTS `radar_playlist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `song_ids` JSON NOT NULL COMMENT '推荐的歌曲ID列表，如: [1,2,3,4,5]',
    `interest_summary` JSON NOT NULL COMMENT '兴趣摘要，如: {"top_tag": "摇滚", "scores": {"摇滚":45,"民谣":22}}',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expires_at` TIMESTAMP NOT NULL COMMENT '过期时间（次日）',
    PRIMARY KEY (`id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='雷达歌单表';

-- =============================================
-- 初始化音乐分类数据（用于测试）
-- =============================================
INSERT INTO `categories` (`name`, `icon`, `parent_id`, `level`, `sort_order`, `status`, `created_at`) VALUES
('流行', 'music', NULL, 1, 1, 1, NOW()),
('摇滚', 'guitar', NULL, 1, 2, 1, NOW()),
('民谣', 'folk', NULL, 1, 3, 1, NOW()),
('电子', 'electronic', NULL, 1, 4, 1, NOW()),
('古典', 'classical', NULL, 1, 5, 1, NOW()),
('爵士', 'jazz', NULL, 1, 6, 1, NOW()),
('嘻哈', 'hiphop', NULL, 1, 7, 1, NOW()),
('R&B', 'rnb', NULL, 1, 8, 1, NOW()),
('说唱', 'rap', NULL, 1, 9, 1, NOW()),
('轻音乐', 'light', NULL, 1, 10, 1, NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- =============================================
-- 测试数据：添加一些歌曲和播放历史
-- =============================================

-- 插入测试歌曲（带分类）
INSERT INTO `songs` (`title`, `duration`, `url`, `cover`, `category_id`, `play_count`, `status`, `created_at`) VALUES
('孤勇者', 240, 'https://example.com/song1.mp3', 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400', 2, 100000, 1, NOW()),
('起风了', 260, 'https://example.com/song2.mp3', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400', 1, 80000, 1, NOW()),
('海阔天空', 300, 'https://example.com/song3.mp3', 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400', 2, 75000, 1, NOW()),
('平凡之路', 280, 'https://example.com/song4.mp3', 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400', 3, 60000, 1, NOW()),
('晴天', 265, 'https://example.com/song5.mp3', 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=400', 1, 55000, 1, NOW()),
('稻香', 220, 'https://example.com/song6.mp3', 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400', 1, 50000, 1, NOW()),
('夜曲', 265, 'https://example.com/song7.mp3', 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=400', 2, 45000, 1, NOW()),
('青花瓷', 240, 'https://example.com/song8.mp3', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400', 1, 40000, 1, NOW()),
('修炼爱情', 250, 'https://example.com/song9.mp3', 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400', 1, 35000, 1, NOW()),
('不为谁而作的歌', 280, 'https://example.com/song10.mp3', 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=400', 1, 30000, 1, NOW()),
('光年之外', 250, 'https://example.com/song11.mp3', 'https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=400', 4, 28000, 1, NOW()),
('泡沫', 270, 'https://example.com/song12.mp3', 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=400', 4, 25000, 1, NOW())
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- =============================================
-- 验证脚本
-- =============================================
SELECT 'Tables created successfully!' AS status;

-- 查看表结构
-- DESCRIBE user_behavior;
-- DESCRIBE user_interest_profile;
-- DESCRIBE radar_playlist;

-- 查看分类数据
-- SELECT * FROM categories;
