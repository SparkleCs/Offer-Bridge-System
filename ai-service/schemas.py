from __future__ import annotations

from datetime import datetime
from decimal import Decimal
from typing import Any

from pydantic import BaseModel, Field


class ProgramCandidate(BaseModel):
    programId: int
    schoolName: str | None = None
    programName: str | None = None
    countryName: str | None = None
    directionName: str | None = None
    qsRank: int | None = None
    usnewsRank: int | None = None
    rankingSource: str | None = None
    primaryRank: int | None = None
    gpaMinRecommend: Decimal | None = None
    languageType: str | None = None
    languageMinScore: Decimal | None = None
    tuitionAmount: Decimal | None = None
    backgroundPreference: str | None = None
    ruleMatchScore: int = 0
    ruleMatchTier: str | None = None
    ruleReasonTags: list[str] = Field(default_factory=list)


class RecommendationRequest(BaseModel):
    studentProfile: dict[str, Any]
    programs: list[ProgramCandidate] = Field(default_factory=list)


class GapAnalysisItem(BaseModel):
    dimension: str
    current: str
    target: str
    priority: str


class ImprovementSuggestionItem(BaseModel):
    priority: str
    action: str
    reason: str


class RecommendationItem(BaseModel):
    programId: int
    schoolName: str | None = None
    programName: str | None = None
    countryName: str | None = None
    directionName: str | None = None
    qsRank: int | None = None
    usnewsRank: int | None = None
    rankingSource: str | None = None
    primaryRank: int | None = None
    ruleMatchScore: int = 0
    mlScore: int = 0
    admissionProbabilityEstimate: Decimal = Decimal("0")
    matchTier: str
    confidenceLevel: str
    reasonTags: list[str] = Field(default_factory=list)
    aiSummary: str


class RecommendationResponse(BaseModel):
    status: str = "SUCCESS"
    overallSummary: str
    generatedAt: str = Field(default_factory=lambda: datetime.now().isoformat(timespec="seconds"))
    modelProvider: str = "fastapi-deepseek"
    modelVersion: str = "hybrid-mvp-v1"
    recommendations: list[RecommendationItem] = Field(default_factory=list)
    gapAnalysis: list[GapAnalysisItem] = Field(default_factory=list)
    improvementSuggestions: list[ImprovementSuggestionItem] = Field(default_factory=list)
    riskWarnings: list[str] = Field(default_factory=lambda: ["结果仅供申请规划参考，最终录取以学校审核为准。"])
