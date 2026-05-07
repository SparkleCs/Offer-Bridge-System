USE offer_bridge;

SET @column_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND COLUMN_NAME = 'usnews_rank'
);
SET @ddl = IF(@column_exists = 0,
  'ALTER TABLE school ADD COLUMN usnews_rank INT NULL AFTER ranking_year',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND COLUMN_NAME = 'usnews_ranking_year'
);
SET @ddl = IF(@column_exists = 0,
  'ALTER TABLE school ADD COLUMN usnews_ranking_year INT NULL AFTER usnews_rank',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'school'
    AND INDEX_NAME = 'idx_school_country_usnews_rank'
);
SET @ddl = IF(@index_exists = 0,
  'ALTER TABLE school ADD INDEX idx_school_country_usnews_rank (country_code, usnews_rank)',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO school (
  school_name_cn, school_name_en, country_code, country_name, city_name,
  qs_rank, ranking_year, usnews_rank, usnews_ranking_year,
  school_summary, tuition_min, tuition_max, tuition_currency,
  duration_min_month, duration_max_month, language_requirement_range, advantage_subjects
) VALUES
  ('普林斯顿大学', 'Princeton University', 'US', '美国', 'Princeton', 999, 2026, 1, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('麻省理工学院', 'Massachusetts Institute of Technology', 'US', '美国', 'Cambridge', 999, 2026, 2, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('哈佛大学', 'Harvard University', 'US', '美国', 'Cambridge', 999, 2026, 3, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('斯坦福大学', 'Stanford University', 'US', '美国', 'Stanford', 999, 2026, 4, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('耶鲁大学', 'Yale University', 'US', '美国', 'New Haven', 999, 2026, 4, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('芝加哥大学', 'University of Chicago', 'US', '美国', 'Chicago', 999, 2026, 6, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('杜克大学', 'Duke University', 'US', '美国', 'Durham', 999, 2026, 7, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('约翰斯·霍普金斯大学', 'Johns Hopkins University', 'US', '美国', 'Baltimore', 999, 2026, 7, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('西北大学', 'Northwestern University', 'US', '美国', 'Evanston', 999, 2026, 7, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('宾夕法尼亚大学', 'University of Pennsylvania', 'US', '美国', 'Philadelphia', 999, 2026, 7, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州理工学院', 'California Institute of Technology', 'US', '美国', 'Pasadena', 999, 2026, 11, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('康奈尔大学', 'Cornell University', 'US', '美国', 'Ithaca', 999, 2026, 12, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('布朗大学', 'Brown University', 'US', '美国', 'Providence', 999, 2026, 13, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('达特茅斯学院', 'Dartmouth College', 'US', '美国', 'Hanover', 999, 2026, 13, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('哥伦比亚大学', 'Columbia University', 'US', '美国', 'New York', 999, 2026, 15, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学伯克利分校', 'University of California, Berkeley', 'US', '美国', 'Berkeley', 999, 2026, 15, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('莱斯大学', 'Rice University', 'US', '美国', 'Houston', 999, 2026, 17, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学洛杉矶分校', 'University of California, Los Angeles', 'US', '美国', 'Los Angeles', 999, 2026, 17, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('范德堡大学', 'Vanderbilt University', 'US', '美国', 'Nashville', 999, 2026, 17, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('卡内基梅隆大学', 'Carnegie Mellon University', 'US', '美国', 'Pittsburgh', 999, 2026, 20, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('密歇根大学安娜堡分校', 'University of Michigan-Ann Arbor', 'US', '美国', 'Ann Arbor', 999, 2026, 20, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('圣母大学', 'University of Notre Dame', 'US', '美国', 'Notre Dame', 999, 2026, 20, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('圣路易斯华盛顿大学', 'Washington University in St. Louis', 'US', '美国', 'St. Louis', 999, 2026, 20, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('埃默里大学', 'Emory University', 'US', '美国', 'Atlanta', 999, 2026, 24, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('乔治城大学', 'Georgetown University', 'US', '美国', 'Washington', 999, 2026, 24, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('北卡罗来纳大学教堂山分校', 'University of North Carolina--Chapel Hill', 'US', '美国', 'Chapel Hill', 999, 2026, 26, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('弗吉尼亚大学', 'University of Virginia', 'US', '美国', 'Charlottesville', 999, 2026, 26, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('南加州大学', 'University of Southern California', 'US', '美国', 'Los Angeles', 999, 2026, 28, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学圣地亚哥分校', 'University of California San Diego', 'US', '美国', 'San Diego', 999, 2026, 29, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('佛罗里达大学', 'University of Florida', 'US', '美国', 'Gainesville', 999, 2026, 30, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('德州大学奥斯汀分校', 'The University of Texas at Austin', 'US', '美国', 'Austin', 999, 2026, 30, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('佐治亚理工学院', 'Georgia Institute of Technology', 'US', '美国', 'Atlanta', 999, 2026, 32, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('纽约大学', 'New York University', 'US', '美国', 'New York', 999, 2026, 32, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学戴维斯分校', 'University of California, Davis', 'US', '美国', 'Davis', 999, 2026, 32, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学欧文分校', 'University of California, Irvine', 'US', '美国', 'Irvine', 999, 2026, 32, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('波士顿学院', 'Boston College', 'US', '美国', 'Chestnut Hill', 999, 2026, 36, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('塔夫茨大学', 'Tufts University', 'US', '美国', 'Medford', 999, 2026, 36, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('伊利诺伊大学厄巴纳-香槟分校', 'University of Illinois Urbana-Champaign', 'US', '美国', 'Urbana-Champaign', 999, 2026, 36, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('威斯康星大学麦迪逊分校', 'University of Wisconsin--Madison', 'US', '美国', 'Madison', 999, 2026, 36, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('加州大学圣塔芭芭拉分校', 'University of California, Santa Barbara', 'US', '美国', 'Santa Barbara', 999, 2026, 41, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('俄亥俄州立大学', 'The Ohio State University', 'US', '美国', 'Columbus', 999, 2026, 41, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('波士顿大学', 'Boston University', 'US', '美国', 'Boston', 999, 2026, 42, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('罗格斯大学新不伦瑞克分校', 'Rutgers University--New Brunswick', 'US', '美国', 'New Brunswick', 999, 2026, 42, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('马里兰大学帕克分校', 'University of Maryland, College Park', 'US', '美国', 'College Park', 999, 2026, 42, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('华盛顿大学', 'University of Washington', 'US', '美国', 'Seattle', 999, 2026, 42, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('里海大学', 'Lehigh University', 'US', '美国', 'Bethlehem', 999, 2026, 46, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('东北大学', 'Northeastern University', 'US', '美国', 'Boston', 999, 2026, 46, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('普渡大学主校区', 'Purdue University--Main Campus', 'US', '美国', 'West Lafayette', 999, 2026, 46, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('佐治亚大学', 'University of Georgia', 'US', '美国', 'Athens', 999, 2026, 46, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('罗切斯特大学', 'University of Rochester', 'US', '美国', 'Rochester', 999, 2026, 46, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('凯斯西储大学', 'Case Western Reserve University', 'US', '美国', 'Cleveland', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('佛罗里达州立大学', 'Florida State University', 'US', '美国', 'Tallahassee', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('德州农工大学', 'Texas A&M University', 'US', '美国', 'College Station', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('弗吉尼亚理工大学', 'Virginia Tech', 'US', '美国', 'Blacksburg', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('维克森林大学', 'Wake Forest University', 'US', '美国', 'Winston-Salem', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科'),
  ('威廉玛丽学院', 'William & Mary', 'US', '美国', 'Williamsburg', 999, 2026, 51, 2026, '美国综合研究型大学，学术资源与科研实力突出。', 50000, 72000, 'USD', 12, 24, 'TOEFL 90+ / IELTS 7.0+', '综合研究,理工,商科')
ON DUPLICATE KEY UPDATE
  usnews_rank = VALUES(usnews_rank),
  usnews_ranking_year = VALUES(usnews_ranking_year),
  updated_at = CURRENT_TIMESTAMP;
