USE offer_bridge;

-- 1) 团队支持产品化字段
ALTER TABLE agency_team
  ADD COLUMN IF NOT EXISTS direction_code VARCHAR(30) NOT NULL DEFAULT 'OTHER' AFTER service_major_scope,
  ADD COLUMN IF NOT EXISTS price_min DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER direction_code,
  ADD COLUMN IF NOT EXISTS price_max DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER price_min,
  ADD COLUMN IF NOT EXISTS price_currency VARCHAR(10) NOT NULL DEFAULT 'CNY' AFTER price_max,
  ADD COLUMN IF NOT EXISTS publish_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' AFTER price_currency,
  ADD COLUMN IF NOT EXISTS published_at DATETIME NULL AFTER publish_status,
  ADD COLUMN IF NOT EXISTS published_by BIGINT NULL AFTER published_at;

ALTER TABLE agency_team
  ADD INDEX IF NOT EXISTS idx_team_publish_front (publish_status, is_front_visible, status),
  ADD INDEX IF NOT EXISTS idx_team_direction (direction_code),
  ADD INDEX IF NOT EXISTS idx_team_price (price_min, price_max);

-- 2) 方向字典
CREATE TABLE IF NOT EXISTS agency_direction_dict (
  code VARCHAR(30) PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO agency_direction_dict(code, name, status)
VALUES
  ('ENGINEERING', '工科', 'ACTIVE'),
  ('BUSINESS', '商科', 'ACTIVE'),
  ('SCIENCE', '理科', 'ACTIVE'),
  ('HUMANITIES_SOCSCI', '文社科', 'ACTIVE'),
  ('OTHER', '其他', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status);

-- 3) 团队发布者关系
CREATE TABLE IF NOT EXISTS agency_team_publisher_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  role_code VARCHAR(50) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_team_publisher (team_id, member_id, role_code),
  INDEX idx_team_publisher_team (team_id, status),
  INDEX idx_team_publisher_member (member_id, status),
  CONSTRAINT fk_team_publisher_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_publisher_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) 成员与团队关系改为多对多（移除单成员唯一团队限制）
ALTER TABLE agency_team_member_rel
  DROP INDEX uk_member_single_team;

ALTER TABLE agency_team_member_rel
  ADD UNIQUE KEY uk_team_member_unique (team_id, member_id);

-- 5) 可选方向外键（若历史脏数据可能导致失败，可先注释后手动清洗）
-- ALTER TABLE agency_team
--   ADD CONSTRAINT fk_team_direction_code
--   FOREIGN KEY (direction_code) REFERENCES agency_direction_dict(code);
