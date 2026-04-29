USE offer_bridge;

CREATE TABLE IF NOT EXISTS service_stage_attachment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stage_id BIGINT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_url VARCHAR(500) NOT NULL,
  content_type VARCHAR(20) NOT NULL,
  mime_type VARCHAR(120) NULL,
  size_bytes BIGINT NULL,
  uploaded_by_user_id BIGINT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_stage_attachment_stage (stage_id, sort_order, id),
  CONSTRAINT fk_stage_attachment_stage FOREIGN KEY (stage_id) REFERENCES service_stage(id) ON DELETE CASCADE,
  CONSTRAINT fk_stage_attachment_uploader FOREIGN KEY (uploaded_by_user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
