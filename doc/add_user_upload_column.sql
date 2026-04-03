-- 为 songs 表添加 user_upload 字段
ALTER TABLE songs ADD COLUMN user_upload TINYINT DEFAULT 0 AFTER user_id;
