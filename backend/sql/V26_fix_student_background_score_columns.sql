USE offer_bridge;

SET @indexed_info_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'student_publication'
    AND column_name = 'indexed_info'
);
SET @indexed_info_sql := IF(
  @indexed_info_exists = 0,
  'ALTER TABLE student_publication ADD COLUMN indexed_info VARCHAR(255) NULL AFTER published_year',
  'SELECT 1'
);
PREPARE indexed_info_stmt FROM @indexed_info_sql;
EXECUTE indexed_info_stmt;
DEALLOCATE PREPARE indexed_info_stmt;

SET @score_version_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'student_background_score'
    AND column_name = 'score_version'
);
SET @score_version_sql := IF(
  @score_version_exists = 0,
  'ALTER TABLE student_background_score ADD COLUMN score_version VARCHAR(30) NOT NULL DEFAULT ''BACKGROUND_SCORE_V1'' AFTER overall_academic_score',
  'SELECT 1'
);
PREPARE score_version_stmt FROM @score_version_sql;
EXECUTE score_version_stmt;
DEALLOCATE PREPARE score_version_stmt;
