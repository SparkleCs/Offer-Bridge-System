USE offer_bridge;

INSERT INTO subject_category (category_code, category_name, sort_order)
VALUES
  ('ENG', '工科', 1),
  ('BUS', '商科', 2),
  ('SCI', '理科', 3),
  ('HUM', '文社科', 4)
ON DUPLICATE KEY UPDATE
  category_name = VALUES(category_name),
  sort_order = VALUES(sort_order);

INSERT INTO major_direction (subject_category_code, direction_code, direction_name, sort_order)
VALUES
  ('ENG', 'CS', '计算机科学', 1),
  ('ENG', 'SE', '软件工程', 2),
  ('ENG', 'DS', '数据科学', 3),
  ('ENG', 'AI', '人工智能', 4),
  ('ENG', 'EE', '电子电气', 5),
  ('BUS', 'BA', '商业分析', 6),
  ('BUS', 'FIN', '金融', 7),
  ('BUS', 'MGT', '管理', 8),
  ('SCI', 'MAT', '材料', 9),
  ('ENG', 'ME', '机械', 10)
ON DUPLICATE KEY UPDATE
  subject_category_code = VALUES(subject_category_code),
  direction_name = VALUES(direction_name),
  sort_order = VALUES(sort_order);

INSERT INTO school (
  school_name_cn, school_name_en, country_code, country_name, city_name, qs_rank, ranking_year,
  school_summary, tuition_min, tuition_max, tuition_currency, duration_min_month, duration_max_month,
  language_requirement_range, advantage_subjects
) VALUES
  ('麻省理工学院', 'Massachusetts Institute of Technology', 'US', '美国', 'Cambridge', 1, 2026,
   '工程与计算机学科顶尖。', 55000, 72000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '计算机,人工智能,工程'),
  ('斯坦福大学', 'Stanford University', 'US', '美国', 'Stanford', 4, 2026,
   '科研与产业联系紧密。', 55000, 75000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '计算机,商科,人工智能'),
  ('牛津大学', 'University of Oxford', 'UK', '英国', 'Oxford', 3, 2026,
   '综合研究型大学。', 33000, 50000, 'GBP', 12, 24, 'IELTS 7.0+ / TOEFL 100+', '文社科,计算机,商科'),
  ('墨尔本大学', 'The University of Melbourne', 'AU', '澳洲', 'Melbourne', 13, 2026,
   '综合排名稳定。', 38000, 56000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '商科,工程,理科')
ON DUPLICATE KEY UPDATE
  country_code = VALUES(country_code),
  country_name = VALUES(country_name),
  city_name = VALUES(city_name),
  qs_rank = VALUES(qs_rank),
  ranking_year = VALUES(ranking_year),
  school_summary = VALUES(school_summary),
  tuition_min = VALUES(tuition_min),
  tuition_max = VALUES(tuition_max),
  tuition_currency = VALUES(tuition_currency),
  duration_min_month = VALUES(duration_min_month),
  duration_max_month = VALUES(duration_max_month),
  language_requirement_range = VALUES(language_requirement_range),
  advantage_subjects = VALUES(advantage_subjects);

INSERT INTO program (
  school_id, program_name, college_name, degree_type, subject_category_code, subject_category_name,
  direction_code, direction_name, study_mode, duration_months, tuition_amount, tuition_currency,
  language_type, language_min_score, gpa_min_recommend, requires_gre, requires_gmat,
  background_preference, application_rounds_overview, suitable_tags, intake_term, program_summary
)
SELECT
  s.id,
  CONCAT(s.school_name_en, ' MSc Computer Science'),
  'School of Engineering',
  'MASTER', 'ENG', '工科', 'CS', '计算机科学',
  'TAUGHT',
  CASE WHEN s.country_code = 'US' THEN 18 ELSE 12 END,
  ROUND((s.tuition_min + s.tuition_max)/2, 2),
  s.tuition_currency,
  CASE WHEN s.country_code='US' THEN 'TOEFL' ELSE 'IELTS' END,
  CASE WHEN s.country_code='US' THEN 90 ELSE 6.5 END,
  CASE WHEN s.qs_rank<=20 THEN 3.4 ELSE 3.1 END,
  CASE WHEN s.country_code='US' THEN 1 ELSE 0 END,
  0,
  '偏好理工背景',
  'Round1: 9-11月; Round2: 12-2月',
  '计算机基础,项目经历',
  'Fall',
  '硕士项目示例'
FROM school s
WHERE s.school_name_en IN (
  'Massachusetts Institute of Technology',
  'Stanford University',
  'University of Oxford',
  'The University of Melbourne'
)
AND NOT EXISTS (
  SELECT 1 FROM program p
  WHERE p.school_id = s.id AND p.program_name = CONCAT(s.school_name_en, ' MSc Computer Science')
);

DELETE FROM school_subject_rel;
INSERT INTO school_subject_rel (school_id, subject_category_code)
SELECT DISTINCT school_id, subject_category_code FROM program;
