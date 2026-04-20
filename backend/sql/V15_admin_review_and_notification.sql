USE offer_bridge;

CREATE TABLE IF NOT EXISTS agency_member_verification_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  id_card_image_url VARCHAR(500) NOT NULL,
  employment_proof_image_url VARCHAR(500) NOT NULL,
  education_proof_image_url VARCHAR(500) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_member_verification_material_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS system_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(50) NOT NULL,
  title VARCHAR(150) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
  related_type VARCHAR(50) NULL,
  related_id VARCHAR(64) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at DATETIME NULL,
  INDEX idx_system_notification_user_status_created (user_id, status, created_at),
  INDEX idx_system_notification_created (created_at),
  CONSTRAINT fk_system_notification_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

