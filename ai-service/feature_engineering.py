from __future__ import annotations

from decimal import Decimal
from typing import Any

from schemas import ProgramCandidate


def decimal_float(value: Any, default: float = 0.0) -> float:
    if value is None:
        return default
    if isinstance(value, Decimal):
        return float(value)
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def normalized_gpa(profile: dict[str, Any]) -> float:
    gpa = decimal_float(profile.get("gpaValue"))
    if profile.get("gpaScale") == "PERCENTAGE":
        return max(0.0, min(4.0, gpa / 25.0))
    return max(0.0, min(4.0, gpa))


def best_language_score(profile: dict[str, Any], language_type: str | None) -> float | None:
    scores = profile.get("languageScores") or []
    for item in scores:
        if item.get("languageType") == language_type:
            return decimal_float(item.get("score"))
    return None


def build_features(profile: dict[str, Any], program: ProgramCandidate) -> list[float]:
    gpa = normalized_gpa(profile)
    gpa_min = decimal_float(program.gpaMinRecommend, 3.0)
    language = best_language_score(profile, program.languageType)
    language_min = decimal_float(program.languageMinScore, 0.0)
    language_gap = 0.0 if language is None or language_min <= 0 else language - language_min
    background = profile.get("backgroundScore") or {}
    overall_background = decimal_float(background.get("overallAcademicScore"), 50.0)
    primary_rank = float(program.primaryRank or program.qsRank or 150)
    rule_score = float(program.ruleMatchScore or 0)
    return [
        gpa,
        gpa - gpa_min,
        language_gap,
        overall_background / 100.0,
        max(0.0, min(1.0, (150.0 - primary_rank) / 150.0)),
        rule_score / 100.0,
    ]
