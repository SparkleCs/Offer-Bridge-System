USE offer_bridge;

ALTER TABLE agency_member_profile
  ADD COLUMN IF NOT EXISTS profile_audit_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' AFTER verified_badge_status;

UPDATE agency_member_profile
SET profile_audit_status = 'DRAFT'
WHERE profile_audit_status IS NULL OR profile_audit_status = '';

CREATE INDEX IF NOT EXISTS idx_member_profile_audit_status ON agency_member_profile(profile_audit_status);
CREATE INDEX IF NOT EXISTS idx_member_status_org ON agency_member_profile(org_id, status);
