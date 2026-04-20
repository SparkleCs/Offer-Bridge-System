USE offer_bridge;
START TRANSACTION;

-- 兼容旧库：verification_record.verify_type 可能不包含 AGENT_ORG。
SET @verify_type_column := (
  SELECT COLUMN_TYPE
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'verification_record'
    AND COLUMN_NAME = 'verify_type'
  LIMIT 1
);

SET @need_expand_verify_type := IF(
  @verify_type_column LIKE '%AGENT_ORG%',
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

-- 选一个可用管理员作为 reviewed_by（若无则回落为 NULL）。
SET @admin_id := (
  SELECT id
  FROM user_account
  WHERE role = 'ADMIN' AND status = 'ACTIVE'
  ORDER BY id
  LIMIT 1
);

-- 仅为缺少 AGENT_ORG 申请记录的公司补录；已存在的记录不改动。
INSERT INTO verification_record (user_id, verify_type, status, payload_json, submitted_at, reject_reason, reviewed_by, reviewed_at)
SELECT
  o.admin_user_id,
  'AGENT_ORG',
  CASE
    WHEN o.verification_status = 'APPROVED' THEN 'APPROVED'
    WHEN o.verification_status = 'REJECTED' THEN 'REJECTED'
    ELSE 'PENDING'
  END AS status,
  NULL AS payload_json,
  COALESCE(o.verified_at, o.updated_at, o.created_at, NOW()) AS submitted_at,
  NULL AS reject_reason,
  CASE
    WHEN o.verification_status IN ('APPROVED', 'REJECTED') THEN @admin_id
    ELSE NULL
  END AS reviewed_by,
  CASE
    WHEN o.verification_status IN ('APPROVED', 'REJECTED') THEN COALESCE(o.verified_at, o.updated_at, NOW())
    ELSE NULL
  END AS reviewed_at
FROM agency_org o
LEFT JOIN verification_record vr
  ON vr.user_id = o.admin_user_id
 AND vr.verify_type = 'AGENT_ORG'
WHERE vr.id IS NULL;

COMMIT;
