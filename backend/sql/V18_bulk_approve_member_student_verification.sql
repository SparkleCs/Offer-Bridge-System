USE offer_bridge;
START TRANSACTION;

-- 兼容旧库：verification_record.verify_type 可能是旧枚举，
-- 先扩展到同时支持历史值与当前代码所需值，避免 AGENT_MEMBER_CERT/AGENT_ORG 截断报错。
SET @verify_type_column := (
  SELECT COLUMN_TYPE
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'verification_record'
    AND COLUMN_NAME = 'verify_type'
  LIMIT 1
);

SET @need_expand_verify_type := IF(
  @verify_type_column LIKE '%AGENT_MEMBER_CERT%'
  AND @verify_type_column LIKE '%AGENT_ORG%',
  0,
  1
);

SET @ddl_expand_verify_type := IF(
  @need_expand_verify_type = 1,
  'ALTER TABLE verification_record
     MODIFY COLUMN verify_type ENUM(
       ''REAL_NAME'',''EDUCATION'',''FACE'',''ORG_LICENSE'',''ORG_QUALIFICATION'',
       ''AGENT_ORG'',''AGENT_MEMBER_CERT'',''AGENT_REAL_NAME'',''AGENT_EDUCATION''
     ) NOT NULL',
  'SELECT 1'
);

PREPARE stmt_expand_verify_type FROM @ddl_expand_verify_type;
EXECUTE stmt_expand_verify_type;
DEALLOCATE PREPARE stmt_expand_verify_type;

-- 选一个可用管理员作为 reviewed_by（若无则回落为 NULL）
SET @admin_id := (
  SELECT id FROM user_account
  WHERE role = 'ADMIN' AND status = 'ACTIVE'
  ORDER BY id
  LIMIT 1
);

-- 员工：补齐/更新 AGENT_MEMBER_CERT
INSERT INTO verification_record (user_id, verify_type, status, payload_json, submitted_at, reject_reason, reviewed_by, reviewed_at)
SELECT ua.id, 'AGENT_MEMBER_CERT', 'APPROVED', NULL, NOW(), NULL, @admin_id, NOW()
FROM user_account ua
WHERE ua.role = 'AGENT_MEMBER' AND ua.status = 'ACTIVE'
ON DUPLICATE KEY UPDATE
  status = 'APPROVED',
  reject_reason = NULL,
  reviewed_by = VALUES(reviewed_by),
  reviewed_at = VALUES(reviewed_at),
  submitted_at = COALESCE(verification_record.submitted_at, VALUES(submitted_at));

-- 学生：补齐/更新 REAL_NAME
INSERT INTO verification_record (user_id, verify_type, status, payload_json, submitted_at, reject_reason, reviewed_by, reviewed_at)
SELECT ua.id, 'REAL_NAME', 'APPROVED', NULL, NOW(), NULL, @admin_id, NOW()
FROM user_account ua
WHERE ua.role = 'STUDENT' AND ua.status = 'ACTIVE'
ON DUPLICATE KEY UPDATE
  status = 'APPROVED',
  reject_reason = NULL,
  reviewed_by = VALUES(reviewed_by),
  reviewed_at = VALUES(reviewed_at),
  submitted_at = COALESCE(verification_record.submitted_at, VALUES(submitted_at));

-- 学生：补齐/更新 EDUCATION
INSERT INTO verification_record (user_id, verify_type, status, payload_json, submitted_at, reject_reason, reviewed_by, reviewed_at)
SELECT ua.id, 'EDUCATION', 'APPROVED', NULL, NOW(), NULL, @admin_id, NOW()
FROM user_account ua
WHERE ua.role = 'STUDENT' AND ua.status = 'ACTIVE'
ON DUPLICATE KEY UPDATE
  status = 'APPROVED',
  reject_reason = NULL,
  reviewed_by = VALUES(reviewed_by),
  reviewed_at = VALUES(reviewed_at),
  submitted_at = COALESCE(verification_record.submitted_at, VALUES(submitted_at));

-- 员工资料审核状态一并置通过（便于前端一致显示）
UPDATE agency_member_profile mp
JOIN user_account ua ON ua.id = mp.user_id
SET mp.profile_audit_status = 'APPROVED',
    mp.verified_badge_status = 'APPROVED'
WHERE ua.role = 'AGENT_MEMBER' AND ua.status = 'ACTIVE';

COMMIT;
