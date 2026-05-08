-- Refine US school difficulty profiles from coarse buckets to continuous rank-based scores.
-- This gives the probability model more school-level variation while still keeping the
-- model inputs explainable: selectivity, admission bar, and market heat.

INSERT INTO us_school_difficulty_profile (
  school_id, school_selectivity_score, admission_bar_score, school_heat_score, difficulty_version
)
SELECT
  scored.school_id,
  scored.school_selectivity_score,
  ROUND(LEAST(100, GREATEST(45, scored.school_selectivity_score * 0.55 + scored.requirement_score * 0.45)), 2),
  scored.school_heat_score,
  'US_SCHOOL_DIFFICULTY_V2'
FROM (
  SELECT
    s.id AS school_id,
    ROUND(LEAST(100, GREATEST(45,
      102 - SQRT(COALESCE(NULLIF(s.usnews_rank, 999), NULLIF(s.qs_rank, 999), 200)) * 5.80
    )), 2) AS school_selectivity_score,
    ROUND(LEAST(100, GREATEST(45,
      COALESCE(AVG(p.gpa_min_recommend), 3.0) / 4.0 * 45
      + COALESCE(MAX(CASE
          WHEN p.language_type = 'TOEFL' THEN p.language_min_score / 120.0 * 35
          WHEN p.language_type = 'IELTS' THEN p.language_min_score / 9.0 * 35
          ELSE 20
        END), 20)
      + CASE WHEN MAX(CASE WHEN p.requires_gre = 1 OR p.requires_gmat = 1 THEN 1 ELSE 0 END) = 1 THEN 20 ELSE 8 END
    )), 2) AS requirement_score,
    ROUND(LEAST(98, GREATEST(55,
      100 - LN(COALESCE(NULLIF(s.usnews_rank, 999), NULLIF(s.qs_rank, 999), 200) + 1) * 8
    )), 2) AS school_heat_score
  FROM school s
  LEFT JOIN program p ON p.school_id = s.id
  WHERE s.country_code = 'US'
  GROUP BY s.id, s.usnews_rank, s.qs_rank
) scored
ON DUPLICATE KEY UPDATE
  school_selectivity_score = VALUES(school_selectivity_score),
  admission_bar_score = VALUES(admission_bar_score),
  school_heat_score = VALUES(school_heat_score),
  difficulty_version = VALUES(difficulty_version);
