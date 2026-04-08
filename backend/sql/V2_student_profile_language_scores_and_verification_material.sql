USE offer_bridge;

CREATE TABLE IF NOT EXISTS student_profile_bak_20260406_v3 AS
SELECT * FROM student_profile;

ALTER TABLE student_profile
  ADD COLUMN IF NOT EXISTS gpa_value DECIMAL(5,2) NULL AFTER target_country,
  ADD COLUMN IF NOT EXISTS gpa_scale ENUM('FOUR_POINT','PERCENTAGE') NULL AFTER gpa_value;

ALTER TABLE student_profile
  DROP COLUMN IF EXISTS language_scores_json;

CREATE TABLE IF NOT EXISTS student_language_score (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  language_type ENUM('CET4','CET6','TOEFL','IELTS','PTE') NOT NULL,
  score DECIMAL(6,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_student_language_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_language_type (user_id, language_type),
  INDEX idx_student_language_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_verification_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  real_name VARCHAR(100) NOT NULL,
  id_no_masked VARCHAR(64) NOT NULL,
  id_card_image_url VARCHAR(500) NOT NULL,
  student_card_image_url VARCHAR(500) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_verification_material_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
