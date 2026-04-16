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
  school_summary, tuition_min, tuition_max, tuition_currency,
  duration_min_month, duration_max_month, language_requirement_range, advantage_subjects
)
VALUES
  ('帝国理工学院', 'Imperial College London', 'UK', '英国', 'London', 2, 2026, '理工与医学强校，科研与产业连接紧密。', 32000, 46000, 'GBP', 12, 24, 'IELTS 6.5-7.0 / TOEFL 90+', '工科,计算机,数据科学'),
  ('牛津大学', 'University of Oxford', 'UK', '英国', 'Oxford', 3, 2026, '综合研究型大学，学术资源与全球声誉突出。', 33000, 50000, 'GBP', 12, 24, 'IELTS 7.0+ / TOEFL 100+', '文社科,计算机,商科'),
  ('剑桥大学', 'University of Cambridge', 'UK', '英国', 'Cambridge', 5, 2026, '学院制研究型大学，理工与基础学科优势明显。', 33000, 50000, 'GBP', 12, 24, 'IELTS 7.0+ / TOEFL 100+', '理科,工科,商科'),
  ('伦敦大学学院', 'UCL', 'UK', '英国', 'London', 9, 2026, '课程覆盖广，跨学科项目丰富。', 30000, 43000, 'GBP', 12, 24, 'IELTS 6.5-7.0 / TOEFL 90+', '工科,商科,文社科'),
  ('爱丁堡大学', 'University of Edinburgh', 'UK', '英国', 'Edinburgh', 22, 2026, 'AI与信息学方向口碑高，国际生友好。', 28000, 40000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 90+', '人工智能,计算机,商科'),
  ('曼彻斯特大学', 'University of Manchester', 'UK', '英国', 'Manchester', 34, 2026, '工程与管理学科体系完善，项目选择多。', 27000, 38000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '工科,商科,材料'),
  ('伦敦国王学院', "King's College London", 'UK', '英国', 'London', 40, 2026, '医学与社科见长，商科与数据项目增长快。', 28000, 39000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 90+', '商科,文社科,数据科学'),
  ('伦敦政治经济学院', 'LSE', 'UK', '英国', 'London', 45, 2026, '社会科学和金融方向影响力强。', 29000, 42000, 'GBP', 12, 24, 'IELTS 7.0+ / TOEFL 100+', '金融,管理,文社科'),
  ('布里斯托大学', 'University of Bristol', 'UK', '英国', 'Bristol', 54, 2026, '理工与工程稳健，科研产出优秀。', 26000, 37000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '工科,理科,计算机'),
  ('华威大学', 'University of Warwick', 'UK', '英国', 'Coventry', 69, 2026, '商学院认可度高，数理与工程基础强。', 28000, 39000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 92+', '商科,工科,数据科学'),
  ('格拉斯哥大学', 'University of Glasgow', 'UK', '英国', 'Glasgow', 74, 2026, '历史悠久，工程与管理方向成熟。', 25000, 36000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '工程,管理,理科'),
  ('伯明翰大学', 'University of Birmingham', 'UK', '英国', 'Birmingham', 80, 2026, '课程体系完整，校企合作资源较多。', 25000, 35000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '工科,商科,机械'),
  ('南安普顿大学', 'University of Southampton', 'UK', '英国', 'Southampton', 81, 2026, '电子与计算机方向传统优势明显。', 25000, 36000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '电子电气,计算机,工程'),
  ('利兹大学', 'University of Leeds', 'UK', '英国', 'Leeds', 86, 2026, '多学科并进，国际学生支持体系较完善。', 24500, 35500, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '商科,工科,材料'),
  ('谢菲尔德大学', 'University of Sheffield', 'UK', '英国', 'Sheffield', 98, 2026, '工程制造领域认可度较高。', 24000, 34000, 'GBP', 12, 24, 'IELTS 6.5+ / TOEFL 88+', '工程,机械,材料'),

  ('麻省理工学院', 'Massachusetts Institute of Technology', 'US', '美国', 'Cambridge', 1, 2026, '工程与计算机学科顶尖，科研密度极高。', 55000, 72000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '计算机,人工智能,工程'),
  ('斯坦福大学', 'Stanford University', 'US', '美国', 'Stanford', 4, 2026, '硅谷生态优势显著，创业与AI资源丰富。', 55000, 75000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '计算机,商科,人工智能'),
  ('哈佛大学', 'Harvard University', 'US', '美国', 'Cambridge', 6, 2026, '综合学术资源顶级，管理与公共政策优势强。', 54000, 73000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '管理,金融,理科'),
  ('加州理工学院', 'California Institute of Technology', 'US', '美国', 'Pasadena', 7, 2026, '理工科研导向，项目规模小而精。', 53000, 71000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '理科,工程,人工智能'),
  ('芝加哥大学', 'University of Chicago', 'US', '美国', 'Chicago', 11, 2026, '经济与数据方法论见长，学术氛围严谨。', 52000, 70000, 'USD', 12, 24, 'TOEFL 104+ / IELTS 7.0+', '金融,商业分析,理科'),
  ('宾夕法尼亚大学', 'University of Pennsylvania', 'US', '美国', 'Philadelphia', 12, 2026, '商学院和工程学院合作密切。', 52000, 70000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '商科,工程,管理'),
  ('耶鲁大学', 'Yale University', 'US', '美国', 'New Haven', 14, 2026, '综合实力强，学术与职业资源平衡。', 50000, 68000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '管理,文社科,理科'),
  ('普林斯顿大学', 'Princeton University', 'US', '美国', 'Princeton', 15, 2026, '基础科研和量化学科突出。', 50000, 68000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '理科,工程,计算机'),
  ('哥伦比亚大学', 'Columbia University', 'US', '美国', 'New York', 16, 2026, '地处纽约，项目与行业连接紧密。', 52000, 72000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '商科,计算机,数据科学'),
  ('康奈尔大学', 'Cornell University', 'US', '美国', 'Ithaca', 17, 2026, '工程、酒店与管理方向口碑稳定。', 50000, 69000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '工程,管理,计算机'),
  ('加州大学伯克利分校', 'University of California, Berkeley', 'US', '美国', 'Berkeley', 18, 2026, '工科与CS影响力高，科研与就业强势。', 50000, 70000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '计算机,工程,数据科学'),
  ('加州大学洛杉矶分校', 'University of California, Los Angeles', 'US', '美国', 'Los Angeles', 20, 2026, '学科全面，项目选择丰富。', 49000, 68000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '商科,工程,理科'),
  ('纽约大学', 'New York University', 'US', '美国', 'New York', 25, 2026, '商业分析与金融项目就业导向明确。', 50000, 69000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '金融,商业分析,管理'),
  ('密歇根大学安娜堡分校', 'University of Michigan-Ann Arbor', 'US', '美国', 'Ann Arbor', 28, 2026, '工科与管理学科稳定，申请热度高。', 48000, 67000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 6.5+', '工程,管理,数据科学'),
  ('卡内基梅隆大学', 'Carnegie Mellon University', 'US', '美国', 'Pittsburgh', 30, 2026, '计算机与AI方向全球领先。', 52000, 73000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '计算机,人工智能,软件工程'),
  ('杜克大学', 'Duke University', 'US', '美国', 'Durham', 32, 2026, '综合实力强，跨学院项目较多。', 50000, 68000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '商科,理科,工程'),
  ('西北大学', 'Northwestern University', 'US', '美国', 'Evanston', 35, 2026, '工程与传媒并重，研究型氛围强。', 50000, 69000, 'USD', 12, 24, 'TOEFL 100+ / IELTS 7.0+', '工程,管理,文社科'),
  ('南加州大学', 'University of Southern California', 'US', '美国', 'Los Angeles', 43, 2026, '就业导向强，工科与商科项目热门。', 50000, 70000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 6.5+', '工程,商科,数据科学'),
  ('德州大学奥斯汀分校', 'The University of Texas at Austin', 'US', '美国', 'Austin', 58, 2026, '计算机与工程实力稳健，性价比较高。', 40000, 60000, 'USD', 12, 24, 'TOEFL 79+ / IELTS 6.5+', '计算机,工程,商业分析'),
  ('佐治亚理工学院', 'Georgia Institute of Technology', 'US', '美国', 'Atlanta', 61, 2026, '工程和计算机应用导向明显。', 42000, 62000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '工程,计算机,电子电气'),

  ('墨尔本大学', 'The University of Melbourne', 'AU', '澳洲', 'Melbourne', 13, 2026, '综合排名稳定，商科与工程申请热度高。', 38000, 56000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '商科,工程,理科'),
  ('悉尼大学', 'The University of Sydney', 'AU', '澳洲', 'Sydney', 18, 2026, '城市资源丰富，课程设置应用导向。', 37000, 55000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 85+', '商科,工程,计算机'),
  ('新南威尔士大学', 'UNSW Sydney', 'AU', '澳洲', 'Sydney', 19, 2026, '工程与计算机口碑稳健。', 38000, 56000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 90+', '工程,计算机,商业分析'),
  ('澳大利亚国立大学', 'Australian National University', 'AU', '澳洲', 'Canberra', 30, 2026, '研究导向明显，理科与政策学科强。', 36000, 53000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 80+', '理科,管理,工程'),
  ('蒙纳士大学', 'Monash University', 'AU', '澳洲', 'Melbourne', 37, 2026, '课程规模大，跨学科项目丰富。', 36000, 54000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '药学,工程,数据科学'),
  ('昆士兰大学', 'The University of Queensland', 'AU', '澳洲', 'Brisbane', 40, 2026, '研究资源充足，理工与商科并重。', 35000, 52000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '理科,工程,商科'),
  ('西澳大学', 'The University of Western Australia', 'AU', '澳洲', 'Perth', 46, 2026, '工程矿业与海洋相关学科较强。', 34000, 50000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 82+', '工程,理科,管理'),
  ('阿德莱德大学', 'The University of Adelaide', 'AU', '澳洲', 'Adelaide', 82, 2026, '工科与农业相关方向特色明显。', 33000, 49000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '工程,理科,商业分析'),
  ('悉尼科技大学', 'University of Technology Sydney', 'AU', '澳洲', 'Sydney', 88, 2026, '应用型项目和就业连接较强。', 33000, 50000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '商业分析,软件工程,管理'),
  ('皇家墨尔本理工大学', 'RMIT University', 'AU', '澳洲', 'Melbourne', 100, 2026, '设计与工程实践导向鲜明。', 32000, 48000, 'AUD', 12, 24, 'IELTS 6.5+ / TOEFL 79+', '工程,数据科学,管理'),

  ('奥克兰大学', 'The University of Auckland', 'NZ', '新西兰', 'Auckland', 65, 2026, '新西兰综合实力领先，课程设置稳健。', 32000, 46000, 'NZD', 12, 24, 'IELTS 6.5+ / TOEFL 90+', '工程,商科,理科'),
  ('奥塔哥大学', 'University of Otago', 'NZ', '新西兰', 'Dunedin', 85, 2026, '研究传统强，生命科学与商科稳健。', 30000, 44000, 'NZD', 12, 24, 'IELTS 6.5+ / TOEFL 80+', '理科,商科,管理'),
  ('惠灵顿维多利亚大学', 'Victoria University of Wellington', 'NZ', '新西兰', 'Wellington', 90, 2026, '政策与商业方向具地理优势。', 30000, 43000, 'NZD', 12, 24, 'IELTS 6.5+ / TOEFL 80+', '商科,文社科,数据科学'),
  ('坎特伯雷大学', 'University of Canterbury', 'NZ', '新西兰', 'Christchurch', 94, 2026, '工程学科稳健，项目实践性强。', 29500, 42000, 'NZD', 12, 24, 'IELTS 6.5+ / TOEFL 80+', '工程,机械,理科'),
  ('怀卡托大学', 'University of Waikato', 'NZ', '新西兰', 'Hamilton', 99, 2026, '管理和信息系统方向发展稳定。', 28500, 41000, 'NZD', 12, 24, 'IELTS 6.5+ / TOEFL 80+', '管理,计算机,商业分析')
ON DUPLICATE KEY UPDATE
  school_name_cn = VALUES(school_name_cn),
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

DELETE FROM program;

INSERT INTO program (
  school_id, program_name, college_name, degree_type, subject_category_code, subject_category_name,
  direction_code, direction_name, study_mode, duration_months, tuition_amount, tuition_currency,
  language_type, language_min_score, gpa_min_recommend, requires_gre, requires_gmat,
  background_preference, application_rounds_overview, suitable_tags, intake_term, program_summary
)
SELECT
  s.id,
  CONCAT(s.school_name_en, ' MSc ',
    CASE MOD(s.id, 5)
      WHEN 0 THEN 'Computer Science'
      WHEN 1 THEN 'Data Science'
      WHEN 2 THEN 'Artificial Intelligence'
      WHEN 3 THEN 'Electrical Engineering'
      ELSE 'Mechanical Engineering'
    END
  ) AS program_name,
  'School of Engineering',
  'MASTER',
  'ENG',
  '工科',
  CASE MOD(s.id, 5)
    WHEN 0 THEN 'CS'
    WHEN 1 THEN 'DS'
    WHEN 2 THEN 'AI'
    WHEN 3 THEN 'EE'
    ELSE 'ME'
  END AS direction_code,
  CASE MOD(s.id, 5)
    WHEN 0 THEN '计算机科学'
    WHEN 1 THEN '数据科学'
    WHEN 2 THEN '人工智能'
    WHEN 3 THEN '电子电气'
    ELSE '机械'
  END AS direction_name,
  'TAUGHT',
  CASE WHEN s.country_code = 'US' THEN 18 ELSE 12 END,
  ROUND((s.tuition_min + s.tuition_max) / 2, 2),
  s.tuition_currency,
  CASE WHEN s.country_code = 'US' THEN 'TOEFL' ELSE 'IELTS' END,
  CASE WHEN s.country_code = 'US' THEN 90 ELSE 6.5 END,
  CASE WHEN s.qs_rank <= 20 THEN 3.5 WHEN s.qs_rank <= 50 THEN 3.3 ELSE 3.1 END,
  CASE WHEN s.country_code = 'US' AND s.qs_rank <= 30 THEN 1 ELSE 0 END,
  0,
  CASE WHEN s.qs_rank <= 30 THEN '偏好985/211或同等院校' ELSE '接受多元本科背景，重视实践能力' END,
  'Round1: 9-11月; Round2: 12-2月; Round3: 3-5月',
  '工科基础好,有项目经历,目标就业导向',
  'Fall',
  '聚焦工程与计算能力培养，强调实践与行业应用。'
FROM school s;

INSERT INTO program (
  school_id, program_name, college_name, degree_type, subject_category_code, subject_category_name,
  direction_code, direction_name, study_mode, duration_months, tuition_amount, tuition_currency,
  language_type, language_min_score, gpa_min_recommend, requires_gre, requires_gmat,
  background_preference, application_rounds_overview, suitable_tags, intake_term, program_summary
)
SELECT
  s.id,
  CONCAT(s.school_name_en, ' MSc ',
    CASE MOD(s.id, 3)
      WHEN 0 THEN 'Business Analytics'
      WHEN 1 THEN 'Finance'
      ELSE 'Management'
    END
  ) AS program_name,
  'Business School',
  'MASTER',
  'BUS',
  '商科',
  CASE MOD(s.id, 3)
    WHEN 0 THEN 'BA'
    WHEN 1 THEN 'FIN'
    ELSE 'MGT'
  END AS direction_code,
  CASE MOD(s.id, 3)
    WHEN 0 THEN '商业分析'
    WHEN 1 THEN '金融'
    ELSE '管理'
  END AS direction_name,
  'TAUGHT',
  CASE WHEN s.country_code = 'US' THEN 18 ELSE 12 END,
  ROUND((s.tuition_min + s.tuition_max) / 2 + 2000, 2),
  s.tuition_currency,
  CASE WHEN s.country_code = 'US' THEN 'TOEFL' ELSE 'IELTS' END,
  CASE WHEN s.country_code = 'US' THEN 95 ELSE 6.5 END,
  CASE WHEN s.qs_rank <= 20 THEN 3.4 WHEN s.qs_rank <= 50 THEN 3.2 ELSE 3.0 END,
  0,
  CASE WHEN MOD(s.id, 3) = 1 THEN 1 ELSE 0 END,
  '偏好商科或量化背景，接受转专业申请',
  'Round1: 9-11月; Round2: 12-2月; Round3: 3-5月',
  '有实习经历,目标商科就业,重视数据能力',
  'Fall',
  '强调商业理解和数据分析能力，面向就业与职业发展。'
FROM school s;

DELETE FROM school_subject_rel;
INSERT INTO school_subject_rel (school_id, subject_category_code)
SELECT DISTINCT school_id, subject_category_code
FROM program;

INSERT INTO agency_profile (agency_name, service_countries, service_summary, success_rate, reputation_score)
VALUES
  ('启航留学', '英国,美国', '擅长英美工科与商科申请策略规划。', 86.00, 4.60),
  ('远见申研', '美国,澳洲', '擅长美国选校定位与澳洲快速申请。', 83.00, 4.40),
  ('南十字顾问', '澳洲,新西兰', '擅长澳新项目规划与申请节奏管理。', 81.00, 4.30),
  ('北洋智申', '英国,新西兰', '擅长英联邦申请与材料打磨。', 79.00, 4.20),
  ('博雅OfferLab', '英国,美国,澳洲,新西兰', '覆盖四国热门方向，提供全流程跟进。', 88.00, 4.70)
ON DUPLICATE KEY UPDATE
  service_countries = VALUES(service_countries),
  service_summary = VALUES(service_summary),
  success_rate = VALUES(success_rate),
  reputation_score = VALUES(reputation_score);

DELETE FROM agency_direction_rel;
INSERT INTO agency_direction_rel (agency_id, direction_code, direction_name, country_code, country_name)
SELECT id, 'CS', '计算机科学', 'UK', '英国' FROM agency_profile WHERE agency_name IN ('启航留学', '博雅OfferLab')
UNION ALL
SELECT id, 'DS', '数据科学', 'US', '美国' FROM agency_profile WHERE agency_name IN ('远见申研', '博雅OfferLab')
UNION ALL
SELECT id, 'AI', '人工智能', 'US', '美国' FROM agency_profile WHERE agency_name IN ('启航留学', '远见申研', '博雅OfferLab')
UNION ALL
SELECT id, 'EE', '电子电气', 'UK', '英国' FROM agency_profile WHERE agency_name IN ('启航留学', '北洋智申')
UNION ALL
SELECT id, 'BA', '商业分析', 'AU', '澳洲' FROM agency_profile WHERE agency_name IN ('远见申研', '南十字顾问', '博雅OfferLab')
UNION ALL
SELECT id, 'FIN', '金融', 'UK', '英国' FROM agency_profile WHERE agency_name IN ('启航留学', '北洋智申', '博雅OfferLab')
UNION ALL
SELECT id, 'MGT', '管理', 'NZ', '新西兰' FROM agency_profile WHERE agency_name IN ('南十字顾问', '北洋智申', '博雅OfferLab')
UNION ALL
SELECT id, 'ME', '机械', 'AU', '澳洲' FROM agency_profile WHERE agency_name IN ('南十字顾问', '博雅OfferLab');
