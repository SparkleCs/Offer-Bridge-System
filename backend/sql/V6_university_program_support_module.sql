USE offer_bridge;

CREATE TABLE IF NOT EXISTS subject_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_code VARCHAR(50) NOT NULL,
  category_name VARCHAR(100) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_subject_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS major_direction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  subject_category_code VARCHAR(50) NOT NULL,
  direction_code VARCHAR(50) NOT NULL,
  direction_name VARCHAR(100) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_major_direction_code (direction_code),
  INDEX idx_major_direction_subject (subject_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS school (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_name_cn VARCHAR(200) NOT NULL,
  school_name_en VARCHAR(255) NOT NULL,
  country_code VARCHAR(10) NOT NULL,
  country_name VARCHAR(50) NOT NULL,
  city_name VARCHAR(100) NULL,
  qs_rank INT NOT NULL,
  ranking_year INT NOT NULL DEFAULT 2026,
  school_summary TEXT NULL,
  tuition_min DECIMAL(12,2) NULL,
  tuition_max DECIMAL(12,2) NULL,
  tuition_currency VARCHAR(10) NULL,
  duration_min_month INT NULL,
  duration_max_month INT NULL,
  language_requirement_range VARCHAR(255) NULL,
  advantage_subjects VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_school_name_en (school_name_en),
  INDEX idx_school_country_rank (country_code, qs_rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS school_subject_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_id BIGINT NOT NULL,
  subject_category_code VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_school_subject (school_id, subject_category_code),
  CONSTRAINT fk_school_subject_school
    FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE,
  INDEX idx_school_subject_code (subject_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS program (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_id BIGINT NOT NULL,
  program_name VARCHAR(255) NOT NULL,
  college_name VARCHAR(255) NULL,
  degree_type VARCHAR(50) NOT NULL DEFAULT 'MASTER',
  subject_category_code VARCHAR(50) NOT NULL,
  subject_category_name VARCHAR(100) NOT NULL,
  direction_code VARCHAR(50) NOT NULL,
  direction_name VARCHAR(100) NOT NULL,
  study_mode VARCHAR(50) NOT NULL,
  duration_months INT NULL,
  tuition_amount DECIMAL(12,2) NULL,
  tuition_currency VARCHAR(10) NULL,
  language_type VARCHAR(30) NULL,
  language_min_score DECIMAL(6,2) NULL,
  gpa_min_recommend DECIMAL(5,2) NULL,
  requires_gre TINYINT(1) NOT NULL DEFAULT 0,
  requires_gmat TINYINT(1) NOT NULL DEFAULT 0,
  background_preference VARCHAR(255) NULL,
  application_rounds_overview VARCHAR(255) NULL,
  suitable_tags VARCHAR(255) NULL,
  intake_term VARCHAR(50) NULL,
  program_summary TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_program_school
    FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE,
  INDEX idx_program_filter (subject_category_code, direction_code, study_mode),
  INDEX idx_program_school (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_application_list (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  program_id BIGINT NOT NULL,
  status_code VARCHAR(30) NOT NULL DEFAULT 'COLLECTED',
  note_text VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_student_program (user_id, program_id),
  CONSTRAINT fk_application_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  CONSTRAINT fk_application_program
    FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
  INDEX idx_application_user_status (user_id, status_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_program_match_result (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  program_id BIGINT NOT NULL,
  match_score INT NOT NULL,
  match_tier VARCHAR(20) NOT NULL,
  reason_tags_json TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_match_user_program (user_id, program_id),
  CONSTRAINT fk_match_user
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  CONSTRAINT fk_match_program
    FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
  INDEX idx_match_user_score (user_id, match_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  agency_name VARCHAR(200) NOT NULL,
  service_countries VARCHAR(255) NULL,
  service_summary VARCHAR(500) NULL,
  success_rate DECIMAL(5,2) NULL,
  reputation_score DECIMAL(5,2) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_agency_name (agency_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_direction_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  agency_id BIGINT NOT NULL,
  direction_code VARCHAR(50) NOT NULL,
  direction_name VARCHAR(100) NOT NULL,
  country_code VARCHAR(10) NOT NULL,
  country_name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_agency_direction_country (agency_id, direction_code, country_code),
  CONSTRAINT fk_agency_direction_agency
    FOREIGN KEY (agency_id) REFERENCES agency_profile(id) ON DELETE CASCADE,
  INDEX idx_agency_direction_query (direction_code, country_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
