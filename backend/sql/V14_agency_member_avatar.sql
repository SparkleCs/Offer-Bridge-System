USE offer_bridge;

ALTER TABLE agency_member_profile
  ADD COLUMN avatar_url VARCHAR(500) NULL AFTER display_name;

