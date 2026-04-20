USE offer_bridge;

CREATE TABLE IF NOT EXISTS agency_member_permission_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  permission_code VARCHAR(80) NOT NULL,
  permission_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_permission (member_id, permission_code),
  INDEX idx_member_permission_member (member_id, permission_status),
  CONSTRAINT fk_member_permission_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

UPDATE agency_org
SET verification_status = 'PENDING'
WHERE verification_status IS NULL OR verification_status = '';

INSERT INTO agency_member_permission_rel(member_id, permission_code, permission_status)
SELECT m.id, 'CAN_CHAT_STUDENT', 'ACTIVE'
FROM agency_member_profile m
LEFT JOIN agency_member_permission_rel p
  ON p.member_id = m.id AND p.permission_code = 'CAN_CHAT_STUDENT'
WHERE p.id IS NULL;
