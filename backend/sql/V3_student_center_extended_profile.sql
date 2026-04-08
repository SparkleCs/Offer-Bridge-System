USE offer_bridge;

ALTER TABLE student_profile
  ADD COLUMN IF NOT EXISTS rank_value INT NULL AFTER gpa_scale,
  ADD COLUMN IF NOT EXISTS rank_total INT NULL AFTER rank_value,
  ADD COLUMN IF NOT EXISTS target_major_text VARCHAR(255) NULL AFTER rank_total,
  ADD COLUMN IF NOT EXISTS intake_term VARCHAR(50) NULL AFTER target_major_text,
  ADD COLUMN IF NOT EXISTS budget_currency VARCHAR(10) NULL AFTER intake_term,
  ADD COLUMN IF NOT EXISTS budget_min DECIMAL(10,2) NULL AFTER budget_currency,
  ADD COLUMN IF NOT EXISTS budget_max DECIMAL(10,2) NULL AFTER budget_min,
  ADD COLUMN IF NOT EXISTS budget_note VARCHAR(255) NULL AFTER budget_max;

CREATE TABLE IF NOT EXISTS student_target_country (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  country_name VARCHAR(100) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_target_country_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  INDEX idx_target_country_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_research_experience (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  project_name VARCHAR(200) NULL,
  role_name VARCHAR(100) NULL,
  start_date VARCHAR(20) NULL,
  end_date VARCHAR(20) NULL,
  content_summary TEXT NULL,
  has_publication TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_research_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  INDEX idx_research_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_publication (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  research_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  author_role VARCHAR(100) NULL,
  journal_name VARCHAR(255) NULL,
  published_year INT NULL,
  indexed_info VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_publication_research
    FOREIGN KEY (research_id) REFERENCES student_research_experience(id) ON DELETE CASCADE,
  INDEX idx_publication_research (research_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_competition_experience (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  competition_name VARCHAR(200) NULL,
  competition_level VARCHAR(100) NULL,
  award VARCHAR(200) NULL,
  role_desc VARCHAR(255) NULL,
  event_date VARCHAR(20) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_competition_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  INDEX idx_competition_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_work_experience (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  company_name VARCHAR(200) NULL,
  position_name VARCHAR(100) NULL,
  start_date VARCHAR(20) NULL,
  end_date VARCHAR(20) NULL,
  keywords VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_work_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  INDEX idx_work_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_work_responsibility (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  work_id BIGINT NOT NULL,
  sort_index INT NOT NULL DEFAULT 0,
  content VARCHAR(500) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_work_resp_work
    FOREIGN KEY (work_id) REFERENCES student_work_experience(id) ON DELETE CASCADE,
  INDEX idx_work_resp_work (work_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
