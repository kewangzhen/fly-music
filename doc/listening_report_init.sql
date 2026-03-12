-- =============================================
-- 听歌报告功能数据库脚本
-- 创建时间: 2026-03-12
-- =============================================

-- 听歌报告表（用于缓存年度听歌报告）
CREATE TABLE IF NOT EXISTS `listening_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `report_year` INT NOT NULL COMMENT '报告年份',
    `total_songs` INT DEFAULT 0 COMMENT '听歌曲数',
    `total_hours` DECIMAL(10,2) DEFAULT 0 COMMENT '听歌时长(小时)',
    `total_days` INT DEFAULT 0 COMMENT '听歌天数',
    `total_plays` BIGINT DEFAULT 0 COMMENT '播放次数',
    `category_distribution` JSON COMMENT '分类分布JSON',
    `top_songs` JSON COMMENT 'TOP歌曲JSON',
    `top_artists` JSON COMMENT 'TOP歌手JSON',
    `tag_distribution` JSON COMMENT '标签分布JSON',
    `time_distribution` JSON COMMENT '时间分布JSON',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_year` (`user_id`, `report_year`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='听歌报告表';

-- =============================================
-- 验证脚本
-- =============================================
SELECT '听歌报告表创建成功!' AS status;

-- 查看表结构
-- DESCRIBE listening_report;
