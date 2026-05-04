USE offer_bridge;

CREATE TABLE IF NOT EXISTS student_favorite_agency_team (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_student_fav_team_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  CONSTRAINT fk_student_fav_team_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE,
  UNIQUE KEY uk_student_fav_team (user_id, team_id),
  INDEX idx_student_fav_user_created (user_id, created_at),
  INDEX idx_student_fav_team (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
