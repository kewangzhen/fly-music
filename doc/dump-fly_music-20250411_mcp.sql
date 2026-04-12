-- Fly Music 数据库备份
-- 备份时间: 2025-04-11
-- 通过 MCP MySQL 工具生成

SET FOREIGN_KEY_CHECKS=0;

-- Table: users
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(255) NOT NULL,
  email varchar(100) DEFAULT NULL,
  phone varchar(20) DEFAULT NULL,
  avatar varchar(255) DEFAULT NULL,
  nickname varchar(50) DEFAULT NULL,
  gender tinyint(4) DEFAULT NULL,
  birthdate date DEFAULT NULL,
  role tinyint(4) DEFAULT 0,
  status tinyint(4) DEFAULT 1,
  vip_expire_at timestamp NULL DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  last_login_at timestamp NULL DEFAULT NULL,
  reset_token varchar(255) DEFAULT NULL,
  reset_token_expire_at timestamp NULL DEFAULT NULL,
  description text DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  UNIQUE KEY uk_email (email),
  UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (id, username, password, email, phone, avatar, nickname, gender, birthdate, role, status, vip_expire_at, created_at, updated_at, last_login_at, reset_token, reset_token_expire_at, description) VALUES
(1,'admin','$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z','admin@flymusic.com','13800000000','https://example.com/avatar/admin.png','管理员',1,'1990-01-01',2,1,'2027-12-31 00:00:00','2026-04-01 14:39:02','2026-04-07 15:53:17',NULL,NULL,NULL,NULL),
(2,'zhangsan','$2a$10$XQ5KJZxSxU7IxO8p4R0mVeuZ1Y5Z0Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z','zhangsan@flymusic.com','13800000001','https://example.com/avatar/zhangsan.png','张三',1,'1995-05-15',0,1,NULL,'2026-04-01 14:39:02','2026-04-01 14:39:02',NULL,NULL,NULL,NULL),
(8,'kwz','$2a$10$sybfbdqNulmabi5F1Jz34.Dk5jduDnM/qyEULkgKjXM4LUJp4uJ9m','123@qq.com','123',NULL,NULL,NULL,NULL,1,1,'2026-05-02 17:31:50','2026-04-02 15:31:56','2026-04-02 17:31:50','2026-04-07 15:08:08','e8a17dc8-a149-48c1-a30a-0c7242718306','2026-04-02 17:35:26',NULL);

-- 继续其他表的数据...
-- (由于数据量较大，建议使用 mysqldump 命令进行完整备份)
