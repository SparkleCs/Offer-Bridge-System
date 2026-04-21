USE offer_bridge;

SET @has_price_min := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND COLUMN_NAME = 'price_min'
);
SET @sql := IF(@has_price_min = 0,
  'ALTER TABLE agency_team ADD COLUMN price_min DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER service_major_scope',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_price_max := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND COLUMN_NAME = 'price_max'
);
SET @sql := IF(@has_price_max = 0,
  'ALTER TABLE agency_team ADD COLUMN price_max DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER price_min',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_publish_status := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND COLUMN_NAME = 'publish_status'
);
SET @sql := IF(@has_publish_status = 0,
  "ALTER TABLE agency_team ADD COLUMN publish_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' AFTER price_max",
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_published_at := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND COLUMN_NAME = 'published_at'
);
SET @sql := IF(@has_published_at = 0,
  'ALTER TABLE agency_team ADD COLUMN published_at DATETIME NULL AFTER publish_status',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_published_by := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND COLUMN_NAME = 'published_by'
);
SET @sql := IF(@has_published_by = 0,
  'ALTER TABLE agency_team ADD COLUMN published_by BIGINT NULL AFTER published_at',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_idx_publish_front := (
  SELECT COUNT(1) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND INDEX_NAME = 'idx_team_publish_front'
);
SET @sql := IF(@has_idx_publish_front = 0,
  'ALTER TABLE agency_team ADD INDEX idx_team_publish_front (publish_status, is_front_visible, status)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_idx_team_price := (
  SELECT COUNT(1) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team' AND INDEX_NAME = 'idx_team_price'
);
SET @sql := IF(@has_idx_team_price = 0,
  'ALTER TABLE agency_team ADD INDEX idx_team_price (price_min, price_max)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS agency_team_publisher_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  role_code VARCHAR(50) NOT NULL DEFAULT 'CONSULTANT',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_team_publisher (team_id, member_id, role_code),
  INDEX idx_team_publisher_team (team_id, status),
  INDEX idx_team_publisher_member (member_id, status),
  CONSTRAINT fk_team_publisher_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_publisher_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @has_uk_member_single_team := (
  SELECT COUNT(1) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team_member_rel' AND INDEX_NAME = 'uk_member_single_team'
);
SET @has_fk_team_member_member := (
  SELECT COUNT(1) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'agency_team_member_rel'
    AND CONSTRAINT_NAME = 'fk_team_member_member'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(@has_fk_team_member_member > 0 AND @has_uk_member_single_team > 0,
  'ALTER TABLE agency_team_member_rel DROP FOREIGN KEY fk_team_member_member',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@has_uk_member_single_team > 0,
  'ALTER TABLE agency_team_member_rel DROP INDEX uk_member_single_team',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_idx_team_member_member := (
  SELECT COUNT(1) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team_member_rel' AND INDEX_NAME = 'idx_team_member_member'
);
SET @sql := IF(@has_idx_team_member_member = 0,
  'ALTER TABLE agency_team_member_rel ADD INDEX idx_team_member_member (member_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_uk_team_member_unique := (
  SELECT COUNT(1) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agency_team_member_rel' AND INDEX_NAME = 'uk_team_member_unique'
);
SET @sql := IF(@has_uk_team_member_unique = 0,
  'ALTER TABLE agency_team_member_rel ADD UNIQUE KEY uk_team_member_unique (team_id, member_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_fk_team_member_member := (
  SELECT COUNT(1) FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'agency_team_member_rel'
    AND CONSTRAINT_NAME = 'fk_team_member_member'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(@has_fk_team_member_member = 0,
  'ALTER TABLE agency_team_member_rel ADD CONSTRAINT fk_team_member_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
