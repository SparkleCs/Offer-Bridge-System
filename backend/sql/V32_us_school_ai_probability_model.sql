USE offer_bridge;

CREATE TABLE IF NOT EXISTS us_school_difficulty_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_id BIGINT NOT NULL,
  school_selectivity_score DECIMAL(5,2) NOT NULL,
  admission_bar_score DECIMAL(5,2) NOT NULL,
  school_heat_score DECIMAL(5,2) NOT NULL,
  difficulty_version VARCHAR(40) NOT NULL DEFAULT 'US_SCHOOL_DIFFICULTY_V1',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_us_school_difficulty_school (school_id),
  CONSTRAINT fk_us_school_difficulty_school FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ai_school_recommendation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_id BIGINT NOT NULL,
  school_id BIGINT NOT NULL,
  match_score INT NOT NULL DEFAULT 0,
  match_tier VARCHAR(20) NOT NULL,
  probability_estimate DECIMAL(6,4) NOT NULL DEFAULT 0,
  confidence_level VARCHAR(20) NULL,
  gpa_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  language_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  soft_background_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  school_selectivity_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  admission_bar_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  school_heat_score DECIMAL(5,2) NOT NULL DEFAULT 0,
  reason_tags_json TEXT NULL,
  ai_summary TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ai_school_report FOREIGN KEY (report_id) REFERENCES ai_analysis_report(id) ON DELETE CASCADE,
  CONSTRAINT fk_ai_school_school FOREIGN KEY (school_id) REFERENCES school(id) ON DELETE CASCADE,
  INDEX idx_ai_school_report (report_id),
  INDEX idx_ai_school_school (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO us_school_difficulty_profile (
  school_id, school_selectivity_score, admission_bar_score, school_heat_score, difficulty_version
)
SELECT
  s.id,
  CASE
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 10 THEN 100
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 30 THEN 90
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 60 THEN 80
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 100 THEN 70
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 150 THEN 60
    ELSE 50
  END,
  LEAST(100, GREATEST(45,
    COALESCE(AVG(p.gpa_min_recommend), 3.0) / 4.0 * 45
    + COALESCE(MAX(CASE
        WHEN p.language_type = 'TOEFL' THEN p.language_min_score / 120.0 * 35
        WHEN p.language_type = 'IELTS' THEN p.language_min_score / 9.0 * 35
        ELSE 20
      END), 20)
    + CASE WHEN MAX(CASE WHEN p.requires_gre = 1 OR p.requires_gmat = 1 THEN 1 ELSE 0 END) = 1 THEN 20 ELSE 8 END
  )),
  CASE
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 20 THEN 95
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 50 THEN 85
    WHEN COALESCE(s.usnews_rank, s.qs_rank) <= 100 THEN 75
    ELSE 60
  END,
  'US_SCHOOL_DIFFICULTY_V1'
FROM school s
LEFT JOIN program p ON p.school_id = s.id
WHERE s.country_code = 'US'
GROUP BY s.id, s.usnews_rank, s.qs_rank
ON DUPLICATE KEY UPDATE
  school_selectivity_score = VALUES(school_selectivity_score),
  admission_bar_score = VALUES(admission_bar_score),
  school_heat_score = VALUES(school_heat_score),
  difficulty_version = VALUES(difficulty_version);
