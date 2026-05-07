from __future__ import annotations

from decimal import Decimal
from typing import Any

from feature_engineering import best_language_score, decimal_float, normalized_gpa
from ml_predictor import predictor
from schemas import GapAnalysisItem, ImprovementSuggestionItem, ProgramCandidate, RecommendationItem


def tier_for_probability(probability: float) -> str:
    if probability >= 0.75:
        return "保底"
    if probability >= 0.45:
        return "匹配"
    return "冲刺"


def confidence_for(program: ProgramCandidate, probability: float) -> str:
    if program.ruleMatchScore >= 70 and 0.35 <= probability <= 0.9:
        return "medium"
    if program.ruleMatchScore >= 82:
        return "high"
    return "low"


def top_factors(profile: dict[str, Any], program: ProgramCandidate) -> list[str]:
    factors = list(program.ruleReasonTags or [])[:3]
    gpa_gap = normalized_gpa(profile) - decimal_float(program.gpaMinRecommend, 3.0)
    if gpa_gap >= 0 and "GPA达到建议线" not in factors:
        factors.append("GPA达到建议线")
    lang = best_language_score(profile, program.languageType)
    if lang is not None and program.languageMinScore is not None and lang >= decimal_float(program.languageMinScore):
        factors.append("语言成绩达标")
    return factors[:4] or ["基于学生画像与项目要求生成申请竞争力评估"]


def build_recommendation(profile: dict[str, Any], program: ProgramCandidate) -> RecommendationItem:
    probability = predictor.predict_probability(profile, program)
    ml_score = int(round(probability * 100))
    tier = tier_for_probability(probability)
    factors = top_factors(profile, program)
    summary = f"{program.schoolName or ''}{program.programName or '该项目'}当前为{tier}选择，AI匹配度约{ml_score}分。"
    return RecommendationItem(
        programId=program.programId,
        schoolName=program.schoolName,
        programName=program.programName,
        countryName=program.countryName,
        directionName=program.directionName,
        qsRank=program.qsRank,
        usnewsRank=program.usnewsRank,
        rankingSource=program.rankingSource,
        primaryRank=program.primaryRank,
        ruleMatchScore=program.ruleMatchScore,
        mlScore=ml_score,
        admissionProbabilityEstimate=Decimal(str(round(probability, 4))),
        matchTier=tier,
        confidenceLevel=confidence_for(program, probability),
        reasonTags=factors,
        aiSummary=summary,
    )


def build_gap_analysis(profile: dict[str, Any], programs: list[ProgramCandidate]) -> list[GapAnalysisItem]:
    gaps: list[GapAnalysisItem] = []
    gpa = normalized_gpa(profile)
    gpa_targets = [decimal_float(item.gpaMinRecommend) for item in programs if item.gpaMinRecommend is not None]
    if gpa_targets:
        target = max(gpa_targets)
        gaps.append(GapAnalysisItem(
            dimension="GPA",
            current=f"{gpa:.2f}/4.0",
            target=f"目标项目建议线最高约 {target:.2f}/4.0",
            priority="高" if gpa + 0.2 < target else "中",
        ))
    language_targets = [item for item in programs if item.languageType and item.languageMinScore is not None]
    if language_targets:
        target = max(language_targets, key=lambda item: decimal_float(item.languageMinScore))
        current = best_language_score(profile, target.languageType)
        gaps.append(GapAnalysisItem(
            dimension="语言成绩",
            current=f"{target.languageType} {current if current is not None else '未填写'}",
            target=f"{target.languageType} {target.languageMinScore}",
            priority="高" if current is None or current < decimal_float(target.languageMinScore) else "中",
        ))
    return gaps


def build_suggestions(gaps: list[GapAnalysisItem]) -> list[ImprovementSuggestionItem]:
    suggestions: list[ImprovementSuggestionItem] = []
    for gap in gaps:
        if gap.dimension == "语言成绩" and gap.priority == "高":
            suggestions.append(ImprovementSuggestionItem(priority="高", action="优先提升语言成绩", reason="多个目标项目对语言成绩有明确门槛要求。"))
        if gap.dimension == "GPA" and gap.priority == "高":
            suggestions.append(ImprovementSuggestionItem(priority="高", action="补充能证明学术能力的科研或课程项目", reason="GPA与部分目标项目建议线存在差距。"))
    if not suggestions:
        suggestions.append(ImprovementSuggestionItem(priority="中", action="完善科研、实习和论文经历", reason="更完整的背景材料有助于提升申请竞争力评估稳定性。"))
    return suggestions
