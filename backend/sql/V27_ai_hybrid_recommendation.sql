USE offer_bridge;

CREATE TABLE IF NOT EXISTS ai_analysis_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  report_type VARCHAR(50) NOT NULL,
  input_snapshot_json LONGTEXT NULL,
  result_json LONGTEXT NULL,
  model_provider VARCHAR(50) NULL,
  model_version VARCHAR(80) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ai_report_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  INDEX idx_ai_report_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ai_program_recommendation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_id BIGINT NOT NULL,
  program_id BIGINT NOT NULL,
  match_score INT NOT NULL DEFAULT 0,
  match_tier VARCHAR(20) NOT NULL,
  probability_estimate DECIMAL(6,4) NOT NULL DEFAULT 0,
  confidence_level VARCHAR(20) NULL,
  reason_tags_json TEXT NULL,
  ai_summary TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ai_program_report FOREIGN KEY (report_id) REFERENCES ai_analysis_report(id) ON DELETE CASCADE,
  CONSTRAINT fk_ai_program_program FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
  INDEX idx_ai_program_report (report_id),
  INDEX idx_ai_program_program (program_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
