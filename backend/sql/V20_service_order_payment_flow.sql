USE offer_bridge;

CREATE TABLE IF NOT EXISTS service_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(40) NOT NULL,
  student_user_id BIGINT NOT NULL,
  org_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  assigned_member_id BIGINT NULL,
  team_name_snapshot VARCHAR(160) NOT NULL,
  org_name_snapshot VARCHAR(200) NOT NULL,
  service_title VARCHAR(160) NULL,
  quote_desc TEXT NULL,
  final_amount DECIMAL(12,2) NULL,
  currency VARCHAR(10) NOT NULL DEFAULT 'CNY',
  order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_QUOTE',
  payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
  paid_at DATETIME NULL,
  closed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_service_order_no (order_no),
  INDEX idx_service_order_student (student_user_id, order_status),
  INDEX idx_service_order_org (org_id, order_status),
  INDEX idx_service_order_team (team_id),
  CONSTRAINT fk_service_order_student FOREIGN KEY (student_user_id) REFERENCES user_account(id) ON DELETE CASCADE,
  CONSTRAINT fk_service_order_org FOREIGN KEY (org_id) REFERENCES agency_org(id) ON DELETE CASCADE,
  CONSTRAINT fk_service_order_team FOREIGN KEY (team_id) REFERENCES agency_team(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  payment_no VARCHAR(40) NOT NULL,
  channel VARCHAR(30) NOT NULL DEFAULT 'ALIPAY_SANDBOX',
  status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
  amount DECIMAL(12,2) NOT NULL,
  gateway_trade_no VARCHAR(80) NULL,
  notify_payload TEXT NULL,
  verified TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  paid_at DATETIME NULL,
  UNIQUE KEY uk_payment_no (payment_no),
  INDEX idx_payment_order (order_id, status),
  CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES service_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_service_case (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  org_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  assigned_member_id BIGINT NULL,
  case_status VARCHAR(30) NOT NULL DEFAULT 'IN_SERVICE',
  started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME NULL,
  UNIQUE KEY uk_case_order (order_id),
  INDEX idx_case_student (student_user_id, case_status),
  INDEX idx_case_org (org_id, case_status),
  CONSTRAINT fk_case_order FOREIGN KEY (order_id) REFERENCES service_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS service_stage (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  stage_key VARCHAR(50) NOT NULL,
  stage_name VARCHAR(80) NOT NULL,
  stage_order INT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
  assigned_member_id BIGINT NULL,
  deliverable_text TEXT NULL,
  deliverable_url VARCHAR(500) NULL,
  student_feedback TEXT NULL,
  started_at DATETIME NULL,
  submitted_at DATETIME NULL,
  completed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_case_stage_key (case_id, stage_key),
  INDEX idx_stage_case_order (case_id, stage_order),
  CONSTRAINT fk_stage_case FOREIGN KEY (case_id) REFERENCES student_service_case(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS service_todo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  case_id BIGINT NOT NULL,
  stage_id BIGINT NULL,
  title VARCHAR(160) NOT NULL,
  description VARCHAR(500) NULL,
  owner_role VARCHAR(30) NOT NULL DEFAULT 'STUDENT',
  status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
  due_at DATETIME NULL,
  completed_at DATETIME NULL,
  confirmed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_todo_case (case_id, status),
  CONSTRAINT fk_todo_case FOREIGN KEY (case_id) REFERENCES student_service_case(id) ON DELETE CASCADE,
  CONSTRAINT fk_todo_stage FOREIGN KEY (stage_id) REFERENCES service_stage(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS refund_request (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  requested_by BIGINT NOT NULL,
  refund_amount DECIMAL(12,2) NULL,
  reason TEXT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
  handler_note TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_refund_order (order_id, status),
  CONSTRAINT fk_refund_order FOREIGN KEY (order_id) REFERENCES service_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
