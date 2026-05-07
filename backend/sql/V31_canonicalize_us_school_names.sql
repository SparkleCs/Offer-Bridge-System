USE offer_bridge;

START TRANSACTION;

DELETE s
FROM school s
JOIN school old_school ON old_school.school_name_en = 'University of California Berkeley'
LEFT JOIN program p ON p.school_id = s.id
WHERE s.school_name_en = 'University of California, Berkeley'
  AND p.id IS NULL;

UPDATE school
SET school_name_en = 'University of California, Berkeley',
    usnews_rank = 15,
    usnews_ranking_year = 2026,
    updated_at = CURRENT_TIMESTAMP
WHERE school_name_en = 'University of California Berkeley';

DELETE s
FROM school s
JOIN school old_school ON old_school.school_name_en = 'University of California Los Angeles'
LEFT JOIN program p ON p.school_id = s.id
WHERE s.school_name_en = 'University of California, Los Angeles'
  AND p.id IS NULL;

UPDATE school
SET school_name_en = 'University of California, Los Angeles',
    usnews_rank = 17,
    usnews_ranking_year = 2026,
    updated_at = CURRENT_TIMESTAMP
WHERE school_name_en = 'University of California Los Angeles';

DELETE s
FROM school s
JOIN school old_school ON old_school.school_name_en = 'University of Michigan Ann Arbor'
LEFT JOIN program p ON p.school_id = s.id
WHERE s.school_name_en = 'University of Michigan-Ann Arbor'
  AND p.id IS NULL;

UPDATE school
SET school_name_en = 'University of Michigan-Ann Arbor',
    usnews_rank = 20,
    usnews_ranking_year = 2026,
    updated_at = CURRENT_TIMESTAMP
WHERE school_name_en = 'University of Michigan Ann Arbor';

DELETE s
FROM school s
JOIN school old_school ON old_school.school_name_en = 'University of Texas at Austin'
LEFT JOIN program p ON p.school_id = s.id
WHERE s.school_name_en = 'The University of Texas at Austin'
  AND p.id IS NULL;

UPDATE school
SET school_name_cn = '德州大学奥斯汀分校',
    school_name_en = 'The University of Texas at Austin',
    usnews_rank = 30,
    usnews_ranking_year = 2026,
    updated_at = CURRENT_TIMESTAMP
WHERE school_name_en = 'University of Texas at Austin';

DELETE s
FROM school s
JOIN school old_school ON old_school.school_name_en = 'Purdue University'
LEFT JOIN program p ON p.school_id = s.id
WHERE s.school_name_en = 'Purdue University--Main Campus'
  AND p.id IS NULL;

UPDATE school
SET school_name_cn = '普渡大学主校区',
    school_name_en = 'Purdue University--Main Campus',
    city_name = 'West Lafayette',
    usnews_rank = 46,
    usnews_ranking_year = 2026,
    updated_at = CURRENT_TIMESTAMP
WHERE school_name_en = 'Purdue University';

COMMIT;
