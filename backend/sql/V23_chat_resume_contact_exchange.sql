USE offer_bridge;

ALTER TABLE student_profile
  ADD COLUMN wechat_id VARCHAR(100) NULL AFTER email;

ALTER TABLE agency_member_profile
  ADD COLUMN wechat_id VARCHAR(100) NULL AFTER real_name;
