ALTER TABLE login_audit_log
  MODIFY COLUMN login_method VARCHAR(32) NOT NULL;
