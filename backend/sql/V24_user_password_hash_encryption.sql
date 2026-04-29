SET @ddl = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE user_account ADD COLUMN password_hash VARCHAR(100) NULL AFTER phone',
    'ALTER TABLE user_account MODIFY COLUMN password_hash VARCHAR(100) NULL'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_account'
    AND COLUMN_NAME = 'password_hash'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
