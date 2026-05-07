USE offer_bridge;

SET @column_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND COLUMN_NAME = 'usnews_rank'
);
SET @ddl = IF(@column_exists = 0,
  'ALTER TABLE school ADD COLUMN usnews_rank INT NULL AFTER ranking_year',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND COLUMN_NAME = 'usnews_ranking_year'
);
SET @ddl = IF(@column_exists = 0,
  'ALTER TABLE school ADD COLUMN usnews_ranking_year INT NULL AFTER usnews_rank',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND INDEX_NAME = 'idx_school_country_usnews_rank'
);
SET @ddl = IF(@index_exists = 0,
  'ALTER TABLE school ADD INDEX idx_school_country_usnews_rank (country_code, usnews_rank)',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
