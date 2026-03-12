-- =============================================
-- 热门榜单功能数据库脚本
-- 创建时间: 2026-03-12
-- =============================================

-- 1. 歌曲热度统计表
-- 存储每首歌曲的热度得分和各维度得分
CREATE TABLE IF NOT EXISTS `song_hot_score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `song_id` BIGINT NOT NULL UNIQUE COMMENT '歌曲ID（唯一）',
    `play_score` INT DEFAULT 0 COMMENT '播放得分',
    `favorite_score` INT DEFAULT 0 COMMENT '收藏得分',
    `share_score` INT DEFAULT 0 COMMENT '分享得分',
    `download_score` INT DEFAULT 0 COMMENT '下载得分',
    `comment_score` INT DEFAULT 0 COMMENT '评论得分',
    `total_weighted_score` BIGINT DEFAULT 0 COMMENT '加权总分',
    `hot_score` DECIMAL(10,2) DEFAULT 0 COMMENT '热度得分(衰减后)',
    `rank` INT DEFAULT 0 COMMENT '当前排名',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_hot_score` (`hot_score` DESC),
    INDEX `idx_song` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲热度统计表';

-- 2. 热门榜单表
-- 存储各类热门榜单
CREATE TABLE IF NOT EXISTS `hot_ranking` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `ranking_type` VARCHAR(50) NOT NULL COMMENT '榜单类型: global-全局热门榜, category-分类热门榜, new-新歌榜, soaring-飙升榜',
    `category_id` BIGINT COMMENT '分类ID(分类榜单用)',
    `song_id` BIGINT NOT NULL COMMENT '歌曲ID',
    `rank` INT NOT NULL COMMENT '排名',
    `hot_score` DECIMAL(10,2) NOT NULL COMMENT '热度得分',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_ranking_type` (`ranking_type`, `category_id`, `rank`),
    INDEX `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门榜单表';

-- 3. 为歌曲表添加分享数字段
ALTER TABLE `songs` ADD COLUMN IF NOT EXISTS `share_count` INT DEFAULT 0 COMMENT '分享次数' AFTER `download_count`;

-- 4. 添加索引优化查询性能
ALTER TABLE `songs` ADD INDEX IF NOT EXISTS `idx_play_count` (`play_count` DESC);
ALTER TABLE `songs` ADD INDEX IF NOT EXISTS `idx_created` (`created_at` DESC);

-- =============================================
-- 验证脚本
-- =============================================
SELECT '热门榜单相关表创建成功!' AS status;

-- 查看表结构
-- DESCRIBE song_hot_score;
-- DESCRIBE hot_ranking;

-- 查看歌曲表结构
-- DESCRIBE songs;
