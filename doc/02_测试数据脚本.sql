-- ==========================================
-- Fly Music 测试数据脚本
-- 说明: 为系统各表插入测试数据
-- ==========================================

USE fly_music;

-- ==========================================
-- 1. 插入用户测试数据
-- ==========================================
INSERT INTO users (username, password, email, phone, avatar, nickname, gender, birthdate, role, status, vip_expire_at) VALUES
('admin', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'admin@flymusic.com', '13800000000', 'https://example.com/avatar/admin.png', '管理员', 1, '1990-01-01', 2, 1, '2027-12-31'),
('zhangsan', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'zhangsan@flymusic.com', '13800000001', 'https://example.com/avatar/zhangsan.png', '张三', 1, '1995-05-15', 0, 1, NULL),
('lisi', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'lisi@flymusic.com', '13800000002', 'https://example.com/avatar/lisi.png', '李四', 2, '1998-08-20', 0, 1, '2026-06-30'),
('wangwu', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'wangwu@flymusic.com', '13800000003', 'https://example.com/avatar/wangwu.png', '王五', 1, '2000-03-10', 1, 1, '2026-12-31'),
('zhaoliu', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'zhaoliu@flymusic.com', '13800000004', 'https://example.com/avatar/zhaoliu.png', '赵六', 2, '1992-11-25', 0, 1, NULL),
('sunqi', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'sunqi@flymusic.com', '13800000005', 'https://example.com/avatar/sunqi.png', '孙七', 1, '1996-07-08', 0, 0, NULL),
('testuser', '$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', 'test@flymusic.com', '13800000006', 'https://example.com/avatar/test.png', '测试用户', 1, '2002-01-01', 0, 1, NULL);

-- ==========================================
-- 2. 插入艺术家测试数据
-- ==========================================
INSERT INTO artists (name, avatar, cover, description, gender, birthdate, region, fan_count, song_count, album_count, status) VALUES
('周杰伦', 'https://example.com/artist/jay.png', 'https://example.com/artist/jay_cover.png', '华语流行天王', 1, '1979-01-18', '台湾', 1000000, 300, 20, 1),
('林俊杰', 'https://example.com/artist/jj.png', 'https://example.com/artist/jj_cover.png', '华语流行歌手', 1, '1981-03-27', '新加坡', 800000, 200, 15, 1),
('邓紫棋', 'https://example.com/artist/gem.png', 'https://example.com/artist/gem_cover.png', '香港歌手', 2, '1991-08-16', '香港', 600000, 150, 10, 1),
('陈奕迅', 'https://example.com/artist/eason.png', 'https://example.com/artist/eason_cover.png', '香港流行歌手', 1, '1974-07-27', '香港', 900000, 250, 18, 1),
('蔡依林', 'https://example.com/artist/jolin.png', 'https://example.com/artist/jolin_cover.png', '华语流行歌手', 2, '1980-09-15', '台湾', 700000, 180, 14, 1),
('薛之谦', 'https://example.com/artist/xzq.png', 'https://example.com/artist/xzq_cover.png', '华语流行歌手', 1, '1983-07-17', '上海', 500000, 120, 8, 1),
('Taylor Swift', 'https://example.com/artist/taylor.png', 'https://example.com/artist/taylor_cover.png', '美国流行歌手', 2, '1989-12-13', '美国', 2000000, 200, 12, 1),
('Ed Sheeran', 'https://example.com/artist/edsheeran.png', 'https://example.com/artist/edsheeran_cover.png', '英国流行歌手', 1, '1991-02-17', '英国', 1800000, 150, 10, 1);

-- ==========================================
-- 3. 插入专辑测试数据
-- ==========================================
INSERT INTO albums (title, cover, release_date, description, artist_id, song_count, play_count, status) VALUES
('七里香', 'https://example.com/album/qilixiang.jpg', '2004-08-03', '周杰伦第七张专辑', 1, 10, 5000000, 1),
('依然范特西', 'https://example.com/album/fantexi.jpg', '2006-09-05', '周杰伦第八张专辑', 1, 12, 4500000, 1),
('她说', 'https://example.com/album/tashuo.jpg', '2010-12-08', '林俊杰概念专辑', 2, 11, 3000000, 1),
('启示录', 'https://example.com/album/revelation.jpg', '2022-08-21', '邓紫棋最新专辑', 3, 15, 2000000, 1),
('U87', 'https://example.com/album/u87.jpg', '2005-06-07', '陈奕迅经典专辑', 4, 10, 3500000, 1),
('Play', 'https://example.com/album/play.jpg', '2014-11-14', '蔡依林专辑', 5, 11, 2800000, 1),
('几个薛之谦', 'https://example.com/album/jigexzq.jpg', '2023-11-24', '薛之谦2023专辑', 6, 10, 1500000, 1),
('1989', 'https://example.com/album/1989.jpg', '2014-10-27', 'Taylor Swift专辑', 7, 13, 8000000, 1),
('÷', 'https://example.com/album/divide.jpg', '2017-03-03', 'Ed Sheeran专辑', 8, 16, 6000000, 1);

-- ==========================================
-- 4. 插入分类测试数据
-- ==========================================
INSERT INTO categories (name, icon, parent_id, level, sort_order, status) VALUES
('流行', 'icon-pop', NULL, 1, 1, 1),
('摇滚', 'icon-rock', NULL, 1, 2, 1),
('电子', 'icon-electronic', NULL, 1, 3, 1),
('民谣', 'icon-folk', NULL, 1, 4, 1),
('说唱', 'icon-rap', NULL, 1, 5, 1),
('古典', 'icon-classical', NULL, 1, 6, 1),
('爵士', 'icon-jazz', NULL, 1, 7, 1),
('轻音乐', 'icon-light', NULL, 1, 8, 1),
('华语', 'icon-chinese', NULL, 1, 9, 1),
('欧美', 'icon-western', NULL, 1, 10, 1),
('日语', 'icon-japanese', NULL, 1, 11, 1),
('韩语', 'icon-korean', NULL, 1, 12, 1);

-- ==========================================
-- 5. 插入歌曲测试数据
-- ==========================================
INSERT INTO songs (title, duration, url, lyrics, cover, album_id, category_id, play_count, download_count, status, is_original, user_id) VALUES
('晴天', 270, 'https://music.example.com/qingtian.mp3', '[00:00.00]晴天 - 周杰伦\n[00:04.00]词：周杰伦\n[00:08.00]曲：周杰伦\n[00:12.00]故事的小黄花\n[00:16.00]从出生那年就飘着', 'https://example.com/cover/qingtian.jpg', 1, 9, 10000000, 500000, 1, 0, NULL),
('七里香', 320, 'https://music.example.com/qilixiang.mp3', '[00:00.00]七里香 - 周杰伦\n[00:04.00]窗外的麻雀在电线杆上站成一行', 'https://example.com/cover/qilixiang.jpg', 1, 9, 8000000, 400000, 1, 0, NULL),
('稻香', 220, 'https://music.example.com/daoxiang.mp3', '[00:00.00]稻香 - 周杰伦\n[00:04.00]对这个世界如果你有太多的抱怨', 'https://example.com/cover/daoxiang.jpg', 1, 9, 7500000, 380000, 1, 0, NULL),
('夜曲', 280, 'https://music.example.com/yequ.mp3', '[00:00.00]夜曲 - 周杰伦\n[00:04.00]一群嗜血的蚂蚁被腐肉所吸引', 'https://example.com/cover/yequ.jpg', 2, 9, 6000000, 300000, 1, 0, NULL),
('她说', 250, 'https://music.example.com/tashuo.mp3', '[00:00.00]她说 - 林俊杰\n[00:04.00]他用疲惫的耳朵听', 'https://example.com/cover/tashuo.jpg', 3, 9, 5000000, 250000, 1, 0, NULL),
('江南', 260, 'https://music.example.com/jiangnan.mp3', '[00:00.00]江南 - 林俊杰\n[00:04.00]雨到江南 风吹一下', 'https://example.com/cover/jiangnan.jpg', 3, 9, 5500000, 280000, 1, 0, NULL),
('泡沫', 270, 'https://music.example.com/paomo.mp3', '[00:00.00]泡沫 - 邓紫棋\n[00:04.00]阳光下的泡沫 是彩色的', 'https://example.com/cover/paomo.jpg', 4, 9, 4500000, 220000, 1, 0, NULL),
('光年之外', 280, 'https://music.example.com/gnzw.mp3', '[00:00.00]光年之外 - 邓紫棋\n[00:04.00]感受停在我发端的指尖', 'https://example.com/cover/gnzw.jpg', 4, 9, 4200000, 200000, 1, 0, NULL),
('十年', 230, 'https://music.example.com/shinian.mp3', '[00:00.00]十年 - 陈奕迅\n[00:04.00]如果那两个字没有颤抖', 'https://example.com/cover/shinian.jpg', 5, 9, 7000000, 350000, 1, 0, NULL),
('浮夸', 290, 'https://music.example.com/fukua.mp3', '[00:00.00]浮夸 - 陈奕迅\n[00:04.00]有人问我我就会讲', 'https://example.com/cover/fukua.jpg', 5, 9, 6500000, 320000, 1, 0, NULL),
('爱情36计', 240, 'https://music.example.com/aq36j.mp3', '[00:00.00]爱情36计 - 蔡依林\n[00:04.00]是谁说的漂亮女生没大脑', 'https://example.com/cover/aq36j.jpg', 6, 9, 3800000, 180000, 1, 0, NULL),
('天后', 260, 'https://music.example.com/tianhou.mp3', '[00:00.00]天后 - 薛之谦\n[00:04.00]终于找到借口', 'https://example.com/cover/tianhou.jpg', 6, 9, 3200000, 150000, 1, 0, NULL),
('Red', 200, 'https://music.example.com/red.mp3', '[00:00.00]Red - Taylor Swift\n[00:04.00]Loving him is like driving a new Maserati', 'https://example.com/cover/red.jpg', 8, 10, 5500000, 280000, 1, 0, NULL),
('Shape of You', 240, 'https://music.example.com/shape.mp3', '[00:00.00]Shape of You - Ed Sheeran\n[00:04.00]The club isn''t the best place to find a lover', 'https://example.com/cover/shape.jpg', 9, 10, 6200000, 310000, 1, 0, NULL),
('Perfect', 260, 'https://music.example.com/perfect.mp3', '[00:00.00]Perfect - Ed Sheeran\n[00:04.00]Baby I''m dancing in the dark', 'https://example.com/cover/perfect.jpg', 9, 10, 5800000, 290000, 1, 0, NULL);

-- ==========================================
-- 6. 插入歌曲艺术家关联数据
-- ==========================================
INSERT INTO song_artists (song_id, artist_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1),
(5, 2), (6, 2),
(7, 3), (8, 3),
(9, 4), (10, 4),
(11, 5),
(12, 6),
(13, 7),
(14, 8), (15, 8);

-- ==========================================
-- 7. 插入播放列表测试数据
-- ==========================================
INSERT INTO playlists (name, description, cover, user_id, is_public, song_count, play_count, type, status) VALUES
('我的收藏', '我喜欢的歌曲', 'https://example.com/playlist/favorite.jpg', 2, TRUE, 10, 50000, 0, 1),
('每日推荐', '每日推荐歌曲', 'https://example.com/playlist/daily.jpg', NULL, TRUE, 20, 100000, 3, 1),
('热门歌曲榜', '最热门的歌曲', 'https://example.com/playlist/hot.jpg', NULL, TRUE, 50, 200000, 2, 1),
('华语经典', '经典华语歌曲', 'https://example.com/playlist/chinese.jpg', 2, TRUE, 30, 80000, 0, 1),
('欧美精选', '优质欧美歌曲', 'https://example.com/playlist/western.jpg', 3, TRUE, 25, 60000, 0, 1),
('跑步歌单', '运动时听的歌', 'https://example.com/playlist/run.jpg', 4, TRUE, 15, 30000, 0, 1),
('睡前音乐', '助眠轻音乐', 'https://example.com/playlist/sleep.jpg', 5, TRUE, 20, 25000, 0, 1);

-- ==========================================
-- 8. 插入播放列表歌曲关联数据
-- ==========================================
INSERT INTO playlist_songs (playlist_id, song_id, position) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 5, 4), (1, 6, 5),
(1, 9, 6), (1, 10, 7), (1, 14, 8), (1, 15, 9), (1, 13, 10),
(2, 1, 1), (2, 2, 2), (2, 3, 3), (2, 4, 4), (2, 5, 5),
(3, 1, 1), (3, 2, 2), (3, 3, 3), (3, 4, 4), (3, 5, 5),
(3, 6, 6), (3, 7, 7), (3, 8, 8), (3, 9, 9), (3, 10, 10),
(4, 1, 1), (4, 2, 2), (4, 3, 3), (4, 5, 4), (4, 6, 5),
(4, 9, 6), (4, 10, 7), (4, 12, 8),
(5, 13, 1), (5, 14, 2), (5, 15, 3),
(6, 3, 1), (6, 14, 2), (6, 15, 3),
(7, 6, 1), (7, 15, 2);

-- 更新播放列表歌曲数
UPDATE playlists SET song_count = (SELECT COUNT(*) FROM playlist_songs WHERE playlist_id = playlists.id);

-- ==========================================
-- 9. 插入播放历史测试数据
-- ==========================================
INSERT INTO play_history (user_id, song_id, played_at, play_duration, progress) VALUES
(2, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR), 270, 270),
(2, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR), 320, 320),
(2, 3, DATE_SUB(NOW(), INTERVAL 3 HOUR), 220, 220),
(2, 5, DATE_SUB(NOW(), INTERVAL 4 HOUR), 250, 250),
(2, 9, DATE_SUB(NOW(), INTERVAL 5 HOUR), 230, 230),
(3, 6, DATE_SUB(NOW(), INTERVAL 1 HOUR), 260, 260),
(3, 7, DATE_SUB(NOW(), INTERVAL 6 HOUR), 270, 270),
(3, 14, DATE_SUB(NOW(), INTERVAL 7 HOUR), 240, 240),
(4, 8, DATE_SUB(NOW(), INTERVAL 2 HOUR), 280, 280),
(4, 10, DATE_SUB(NOW(), INTERVAL 8 HOUR), 290, 290),
(5, 13, DATE_SUB(NOW(), INTERVAL 3 HOUR), 200, 200),
(5, 15, DATE_SUB(NOW(), INTERVAL 9 HOUR), 260, 260),
(2, 14, DATE_SUB(NOW(), INTERVAL 10 HOUR), 240, 240),
(3, 1, DATE_SUB(NOW(), INTERVAL 11 HOUR), 270, 270),
(4, 2, DATE_SUB(NOW(), INTERVAL 12 HOUR), 320, 320);

-- ==========================================
-- 10. 插入用户收藏测试数据
-- ==========================================
INSERT INTO user_favorites (user_id, target_type, target_id) VALUES
(2, 1, 1), (2, 1, 2), (2, 1, 3), (2, 1, 5), (2, 1, 9),
(2, 2, 1), (2, 2, 4),
(3, 1, 6), (3, 1, 7), (3, 1, 14),
(3, 2, 2), (3, 2, 5),
(4, 1, 8), (4, 1, 10), (4, 1, 12),
(4, 3, 1),
(5, 1, 13), (5, 1, 15),
(5, 2, 3);

-- ==========================================
-- 11. 插入用户关注测试数据
-- ==========================================
INSERT INTO user_follows (follower_id, followed_id) VALUES
(2, 3), (2, 4), (2, 5),
(3, 2), (3, 4), (3, 6),
(4, 2), (4, 3), (4, 5),
(5, 2), (5, 3), (5, 4);

-- ==========================================
-- 12. 插入动态帖子测试数据
-- ==========================================
INSERT INTO posts (user_id, content, song_id, images, likes, comments_count, shares_count, status) VALUES
(2, '今天天气真好，听一首晴天心情更好！', 1, '["https://example.com/post/1.jpg"]', 52, 10, 5, 1),
(3, '强烈推荐林俊杰的这首歌，太好听了！', 6, NULL, 38, 8, 3, 1),
(4, '晚上跑步必备歌单，分享给大家！', NULL, '["https://example.com/post/2.jpg","https://example.com/post/3.jpg"]', 120, 25, 15, 1),
(5, '睡前听这首歌，太治愈了！', 15, NULL, 45, 12, 8, 1),
(2, '发现一首好歌，果断收藏！', 13, NULL, 28, 6, 2, 1),
(3, '周末在家听歌，太惬意了！', 10, '["https://example.com/post/4.jpg"]', 65, 15, 10, 1);

-- ==========================================
-- 13. 插入评论测试数据
-- ==========================================
INSERT INTO comments (user_id, target_type, target_id, content, parent_id, likes, status) VALUES
(3, 1, 1, '这首歌太好听了！', NULL, 100, 1),
(4, 1, 1, '经典中的经典！', NULL, 80, 1),
(5, 1, 1, '百听不厌', NULL, 60, 1),
(2, 1, 2, '七里香是我的最爱！', NULL, 90, 1),
(3, 1, 5, 'JJ的声音太迷人了', NULL, 70, 1),
(4, 1, 9, '每次听都有不同的感受', NULL, 55, 1),
(5, 1, 14, 'ED sheeran太有才了！', NULL, 85, 1);

-- ==========================================
-- 14. 插入AI音乐生成测试数据
-- ==========================================
INSERT INTO ai_music_generations (user_id, prompt, music_url, cover, duration, genre, mood, status) VALUES
(2, '轻松愉悦的流行音乐，适合早晨', 'https://music.example.com/ai/1.mp3', 'https://example.com/ai/1.jpg', 180, '流行', '欢快', 1),
(2, '舒缓的钢琴曲，适合睡前', 'https://music.example.com/ai/2.mp3', 'https://example.com/ai/2.jpg', 240, '轻音乐', '平静', 1),
(4, '节奏感强的电子音乐', 'https://music.example.com/ai/3.mp3', 'https://example.com/ai/3.jpg', 200, '电子', '动感', 1),
(5, '浪漫的爵士乐', NULL, NULL, NULL, '爵士', '浪漫', 0);

-- ==========================================
-- 15. 插入系统日志测试数据
-- ==========================================
INSERT INTO system_logs (user_id, username, action, target_type, target_id, method, ip_address, details, status) VALUES
(2, 'zhangsan', 'LOGIN', 'user', 2, 'POST', '192.168.1.100', '用户登录成功', 1),
(2, 'zhangsan', 'PLAY_SONG', 'song', 1, 'POST', '192.168.1.100', '播放歌曲: 晴天', 1),
(3, 'lisi', 'LOGIN', 'user', 3, 'POST', '192.168.1.101', '用户登录成功', 1),
(3, 'lisi', 'CREATE_PLAYLIST', 'playlist', 1, 'POST', '192.168.1.101', '创建播放列表: 我的收藏', 1),
(4, 'wangwu', 'LOGIN', 'user', 4, 'POST', '192.168.1.102', '用户登录成功', 1),
(4, 'wangwu', 'FAVORITE', 'song', 8, 'POST', '192.168.1.102', '收藏歌曲: 光年之外', 1),
(5, 'zhaoliu', 'REGISTER', 'user', 5, 'POST', '192.168.1.103', '用户注册成功', 1),
(1, 'admin', 'UPDATE_CONFIG', 'system', NULL, 'PUT', '192.168.1.1', '更新系统配置', 1),
(1, 'admin', 'AUDIT_SONG', 'song', 1, 'POST', '192.168.1.1', '审核歌曲: 晴天', 1);

-- ==========================================
-- 16. 插入用户兴趣画像测试数据
-- ==========================================
INSERT INTO user_interest_profiles (user_id, category_id, genre, mood, artist_id, weight) VALUES
(2, 1, '流行', '欢快', 1, 0.9),
(2, 9, '华语', '治愈', 2, 0.8),
(2, 1, '流行', '浪漫', 4, 0.7),
(3, 9, '华语', '深情', 2, 0.85),
(3, 10, '欧美', '动感', 7, 0.75),
(4, 3, '电子', '动感', NULL, 0.8),
(4, 5, '说唱', '激烈', NULL, 0.7),
(5, 6, '古典', '平静', NULL, 0.9),
(5, 7, '爵士', '浪漫', NULL, 0.85);

-- ==========================================
-- 17. 插入歌曲热度分数测试数据
-- ==========================================
INSERT INTO song_hot_scores (song_id, play_score, favorite_score, share_score, recent_score, total_score) VALUES
(1, 1000, 500, 200, 300, 2000),
(2, 800, 400, 180, 280, 1660),
(3, 750, 380, 160, 260, 1550),
(4, 600, 300, 140, 220, 1260),
(5, 550, 320, 150, 240, 1260),
(6, 500, 280, 130, 200, 1110),
(7, 450, 250, 120, 190, 1010),
(8, 420, 220, 110, 180, 930),
(9, 700, 350, 170, 260, 1480),
(10, 650, 320, 160, 240, 1370);

-- ==========================================
-- 18. 插入热门榜单测试数据
-- ==========================================
INSERT INTO hot_rankings (song_id, play_count, rank_position) VALUES
(1, 10000000, 1),
(2, 8000000, 2),
(3, 7500000, 3),
(4, 6000000, 4),
(9, 7000000, 5),
(5, 5500000, 6),
(10, 6500000, 7),
(6, 5000000, 8),
(7, 4500000, 9),
(8, 4200000, 10);

-- ==========================================
-- 19. 插入雷达歌单测试数据
-- ==========================================
INSERT INTO radar_playlists (playlist_id, playlist_name, description, cover, song_count, play_count, status) VALUES
(3, '热门雷达', '根据你的听歌口味推荐的热门歌曲', 'https://example.com/radar/hot.jpg', 50, 200000, 1),
(NULL, '怀旧时光', '经典老歌回忆杀', 'https://example.com/radar/nostalgic.jpg', 40, 150000, 1),
(NULL, '节奏控', '节奏感强的歌曲合集', 'https://example.com/radar/beat.jpg', 35, 120000, 1),
(NULL, '治愈系', '温暖治愈的音乐', 'https://example.com/radar/heal.jpg', 30, 100000, 1),
(NULL, '学习工作', '专注工作学习的背景音乐', 'https://example.com/radar/study.jpg', 25, 80000, 1);

-- ==========================================
-- 更新歌曲热度分数
-- ==========================================
UPDATE songs s SET play_count = (
    SELECT COALESCE(SUM(hs.play_count), 0) FROM hot_rankings hs WHERE hs.song_id = s.id
);

-- ==========================================
-- 验证数据插入结果
-- ==========================================
SELECT '用户表' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT '艺术家表', COUNT(*) FROM artists
UNION ALL
SELECT '专辑表', COUNT(*) FROM albums
UNION ALL
SELECT '分类表', COUNT(*) FROM categories
UNION ALL
SELECT '歌曲表', COUNT(*) FROM songs
UNION ALL
SELECT '播放列表表', COUNT(*) FROM playlists
UNION ALL
SELECT '播放历史表', COUNT(*) FROM play_history
UNION ALL
SELECT '用户收藏表', COUNT(*) FROM user_favorites
UNION ALL
SELECT '用户关注表', COUNT(*) FROM user_follows
UNION ALL
SELECT '动态帖子表', COUNT(*) FROM posts
UNION ALL
SELECT '评论表', COUNT(*) FROM comments
UNION ALL
SELECT 'AI生成表', COUNT(*) FROM ai_music_generations
UNION ALL
SELECT '热门榜单', COUNT(*) FROM hot_rankings;

SELECT '测试数据插入完成！' as message;
