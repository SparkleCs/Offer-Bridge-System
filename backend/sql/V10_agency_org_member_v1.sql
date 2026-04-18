USE offer_bridge;

CREATE TABLE IF NOT EXISTS agency_org (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_user_id BIGINT NOT NULL,
  org_name VARCHAR(200) NOT NULL,
  brand_name VARCHAR(200) NULL,
  country_code VARCHAR(20) NOT NULL,
  province_or_state VARCHAR(100) NOT NULL,
  city VARCHAR(100) NOT NULL,
  district VARCHAR(100) NULL,
  address_line VARCHAR(255) NOT NULL,
  logo_url VARCHAR(500) NULL,
  cover_image_url VARCHAR(500) NULL,
  office_environment_images TEXT NULL,
  contact_phone VARCHAR(50) NOT NULL,
  contact_email VARCHAR(120) NULL,
  website_url VARCHAR(255) NULL,
  social_links VARCHAR(500) NULL,
  founded_year INT NULL,
  team_size_range VARCHAR(50) NULL,
  service_mode VARCHAR(30) NOT NULL DEFAULT 'HYBRID',
  org_intro TEXT NULL,
  org_slogan VARCHAR(255) NULL,
  verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  verified_at DATETIME NULL,
  audit_remark VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_agency_org_admin (admin_user_id),
  INDEX idx_agency_org_city (city),
  INDEX idx_agency_org_verified (verification_status),
  CONSTRAINT fk_agency_org_admin_user FOREIGN KEY (admin_user_id) REFERENCES user_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_team (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  team_name VARCHAR(120) NOT NULL,
  team_type VARCHAR(50) NULL,
  team_intro VARCHAR(500) NULL,
  service_country_scope VARCHAR(255) NULL,
  service_major_scope VARCHAR(255) NULL,
  is_front_visible TINYINT(1) NOT NULL DEFAULT 1,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_team_name_in_org (org_id, team_name),
  INDEX idx_team_org_visible (org_id, is_front_visible),
  CONSTRAINT fk_agency_team_org FOREIGN KEY (org_id) REFERENCES agency_org(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_member_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  org_id BIGINT NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  real_name VARCHAR(100) NULL,
  job_title VARCHAR(100) NOT NULL,
  education_level VARCHAR(50) NOT NULL,
  graduated_school VARCHAR(150) NOT NULL,
  major VARCHAR(150) NULL,
  years_of_experience INT NOT NULL DEFAULT 0,
  special_countries VARCHAR(255) NOT NULL,
  special_directions VARCHAR(255) NOT NULL,
  bio VARCHAR(1000) NOT NULL,
  service_style_tags VARCHAR(255) NULL,
  public_status VARCHAR(30) NOT NULL DEFAULT 'PRIVATE',
  verified_badge_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_user (user_id),
  INDEX idx_member_org_public (org_id, public_status),
  CONSTRAINT fk_member_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  CONSTRAINT fk_member_org FOREIGN KEY (org_id) REFERENCES agency_org(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_team_member_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_single_team (member_id),
  INDEX idx_team_member_team (team_id),
  CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_member_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_member_role_rel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  role_code VARCHAR(50) NOT NULL,
  is_primary TINYINT(1) NOT NULL DEFAULT 0,
  role_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_role (member_id, role_code),
  INDEX idx_member_role_primary (member_id, is_primary),
  CONSTRAINT fk_member_role_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_invitation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  email VARCHAR(120) NOT NULL,
  invitee_name VARCHAR(80) NULL,
  role_hint VARCHAR(50) NULL,
  token VARCHAR(64) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  expires_at DATETIME NOT NULL,
  accepted_user_id BIGINT NULL,
  accepted_at DATETIME NULL,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_invite_token (token),
  INDEX idx_invite_org_status (org_id, status),
  CONSTRAINT fk_invite_org FOREIGN KEY (org_id) REFERENCES agency_org(id) ON DELETE CASCADE,
  CONSTRAINT fk_invite_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE,
  CONSTRAINT fk_invite_user FOREIGN KEY (accepted_user_id) REFERENCES user_account(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS agency_member_metrics (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  case_count INT NOT NULL DEFAULT 0,
  success_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
  avg_rating DECIMAL(3,2) NOT NULL DEFAULT 0,
  response_efficiency_score DECIMAL(3,2) NOT NULL DEFAULT 0,
  service_tags VARCHAR(255) NULL,
  budget_tags VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_metrics_member (member_id),
  CONSTRAINT fk_metrics_member FOREIGN KEY (member_id) REFERENCES agency_member_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
