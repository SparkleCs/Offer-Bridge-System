USE offer_bridge;

START TRANSACTION;

-- 0) Backup before destructive changes
CREATE TABLE IF NOT EXISTS backup_student_profile_target_country_20260408 AS
SELECT id, user_id, target_country
FROM student_profile;

CREATE TABLE IF NOT EXISTS backup_student_work_responsibility_20260408 AS
SELECT id, work_id, sort_index, content, created_at, updated_at
FROM student_work_responsibility;

-- 1) Add single text summary field for work content (if not exists)
ALTER TABLE student_work_experience
  ADD COLUMN IF NOT EXISTS content_summary TEXT NULL AFTER keywords;

-- 2) Migrate bullet responsibilities into work content summary
UPDATE student_work_experience w
LEFT JOIN (
  SELECT wr.work_id,
         GROUP_CONCAT(wr.content ORDER BY wr.sort_index ASC, wr.id ASC SEPARATOR '\n') AS merged_content
  FROM student_work_responsibility wr
  GROUP BY wr.work_id
) agg ON agg.work_id = w.id
SET w.content_summary = CASE
  WHEN w.content_summary IS NULL OR TRIM(w.content_summary) = '' THEN agg.merged_content
  ELSE w.content_summary
END;

-- 3) Drop redundant profile single country field
ALTER TABLE student_profile
  DROP COLUMN target_country;

-- 4) Drop old work responsibility table
DROP TABLE IF EXISTS student_work_responsibility;

COMMIT;
