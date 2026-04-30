USE offer_bridge;

ALTER TABLE student_profile
  ADD COLUMN IF NOT EXISTS undergraduate_school_tier VARCHAR(50) NULL AFTER school_name;

CREATE TABLE IF NOT EXISTS school_tier_dictionary (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_name VARCHAR(200) NOT NULL,
  school_alias VARCHAR(200) NULL,
  tier_code VARCHAR(50) NOT NULL,
  tier_score DECIMAL(5,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_school_tier_name (school_name),
  INDEX idx_school_tier_alias (school_alias),
  INDEX idx_school_tier_code (tier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_background_score (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  gpa_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  language_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  publication_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  research_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  internship_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  exchange_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  competition_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  undergraduate_school_score DECIMAL(5,2) NOT NULL DEFAULT 50,
  overall_academic_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  score_version VARCHAR(30) NOT NULL DEFAULT 'BACKGROUND_SCORE_V1',
  score_detail_json TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_background_score_user (user_id),
  CONSTRAINT fk_background_score_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE student_publication
  ADD COLUMN IF NOT EXISTS author_order VARCHAR(50) NULL AFTER author_role,
  ADD COLUMN IF NOT EXISTS publication_level VARCHAR(50) NULL AFTER journal_name,
  ADD COLUMN IF NOT EXISTS journal_partition VARCHAR(50) NULL AFTER publication_level;

ALTER TABLE student_research_experience
  ADD COLUMN IF NOT EXISTS role_level VARCHAR(50) NULL AFTER role_name,
  ADD COLUMN IF NOT EXISTS relevance_level VARCHAR(50) NULL AFTER role_level,
  ADD COLUMN IF NOT EXISTS duration_months INT NULL AFTER end_date;

ALTER TABLE student_work_experience
  ADD COLUMN IF NOT EXISTS company_tier VARCHAR(50) NULL AFTER company_name,
  ADD COLUMN IF NOT EXISTS relevance_level VARCHAR(50) NULL AFTER position_name,
  ADD COLUMN IF NOT EXISTS title_level VARCHAR(50) NULL AFTER relevance_level,
  ADD COLUMN IF NOT EXISTS duration_months INT NULL AFTER end_date;

ALTER TABLE student_competition_experience
  ADD COLUMN IF NOT EXISTS award_level VARCHAR(50) NULL AFTER award,
  ADD COLUMN IF NOT EXISTS relevance_level VARCHAR(50) NULL AFTER award_level;

ALTER TABLE student_exchange_experience
  ADD COLUMN IF NOT EXISTS school_tier VARCHAR(50) NULL AFTER university_name,
  ADD COLUMN IF NOT EXISTS relevance_level VARCHAR(50) NULL AFTER major_courses,
  ADD COLUMN IF NOT EXISTS duration_months INT NULL AFTER end_date;

INSERT INTO school_tier_dictionary (school_name, school_alias, tier_code, tier_score)
VALUES
  ('C9', NULL, 'C9', 100),
  ('985', NULL, '985', 90),
  ('211', NULL, '211', 80),
  ('双一流', 'DOUBLE_FIRST_CLASS', 'DOUBLE_FIRST_CLASS', 80),
  ('普通本科', NULL, 'NORMAL_UNDERGRAD', 60),
  ('大专', NULL, 'JUNIOR_COLLEGE', 35),
  ('海外本科', NULL, 'OVERSEAS', 75),
  ('未知', NULL, 'UNKNOWN', 50)
ON DUPLICATE KEY UPDATE
  school_alias = VALUES(school_alias),
  tier_code = VALUES(tier_code),
  tier_score = VALUES(tier_score);
