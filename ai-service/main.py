from __future__ import annotations

from pathlib import Path

from fastapi import FastAPI
from fastapi import HTTPException
from fastapi import Response

try:
    from dotenv import load_dotenv
except ImportError:  # pragma: no cover - dependency is installed through requirements.txt
    load_dotenv = None

if load_dotenv is not None:
    load_dotenv(Path(__file__).resolve().parent / ".env")

from llm_client import DeepSeekUnavailable, deepseek_configured, generate_explanation
from ml_predictor import predictor, us_school_predictor
from report_builder import build_gap_analysis, build_recommendation, build_suggestions, build_us_school_gap_analysis, build_us_school_recommendation
from schemas import GapAnalysisItem, ImprovementSuggestionItem, RecommendationRequest, RecommendationResponse, UsSchoolRecommendationRequest, UsSchoolRecommendationResponse


app = FastAPI(title="Offer Bridge AI Service", version="0.1.0")


@app.get("/")
def root() -> dict[str, str]:
    return {
        "service": "Offer Bridge AI Service",
        "health": "/health",
        "usSchoolRecommendations": "/ai/us-school-recommendations",
    }


@app.get("/favicon.ico", include_in_schema=False)
def favicon() -> Response:
    return Response(status_code=204)


@app.get("/health")
def health() -> dict[str, str | bool]:
    return {
        "status": "ok",
        "modelVersion": predictor.model_version,
        "modelLoaded": predictor.model_loaded,
        "usSchoolModelVersion": us_school_predictor.model_version,
        "usSchoolModelLoaded": us_school_predictor.model_loaded,
        "usSchoolCalibratorLoaded": us_school_predictor.calibrator_loaded,
        "deepseekConfigured": deepseek_configured(),
    }


def apply_llm_report(response: RecommendationResponse | UsSchoolRecommendationResponse, llm_data: dict) -> None:
    response.overallSummary = llm_data.get("overallSummary") or response.overallSummary
    response.riskWarnings = [str(item) for item in llm_data.get("riskWarnings", []) if str(item).strip()] or response.riskWarnings
    response.gapAnalysis = [GapAnalysisItem.model_validate(item) for item in llm_data.get("gapAnalysis", [])]
    response.improvementSuggestions = [
        ImprovementSuggestionItem.model_validate(item)
        for item in llm_data.get("improvementSuggestions", [])
    ]


@app.post("/ai/recommendations", response_model=RecommendationResponse)
async def recommendations(request: RecommendationRequest) -> RecommendationResponse:
    return await build_response(request, "已生成 AI 择校报告。")


@app.post("/ai/program-analysis", response_model=RecommendationResponse)
async def program_analysis(request: RecommendationRequest) -> RecommendationResponse:
    return await build_response(request, "已生成单项目 AI 申请竞争力分析。")


@app.post("/ai/us-school-recommendations", response_model=UsSchoolRecommendationResponse)
async def us_school_recommendations(request: UsSchoolRecommendationRequest) -> UsSchoolRecommendationResponse:
    return await build_us_school_response(request)


async def build_response(request: RecommendationRequest, default_summary: str) -> RecommendationResponse:
    try:
        recs = [build_recommendation(request.studentProfile, program) for program in request.programs]
    except RuntimeError as ex:
        raise HTTPException(status_code=503, detail="AI预测暂不可用，请稍后重试") from ex
    recs.sort(key=lambda item: item.admissionProbabilityEstimate, reverse=True)
    gaps = build_gap_analysis(request.studentProfile, request.programs)
    suggestions = build_suggestions(gaps)
    response = RecommendationResponse(
        status="SUCCESS",
        overallSummary=default_summary,
        modelProvider="fastapi-deepseek",
        modelVersion=predictor.model_version,
        recommendations=recs,
        gapAnalysis=gaps,
        improvementSuggestions=suggestions,
    )

    llm_data = await generate_explanation({
        "studentProfile": request.studentProfile,
        "recommendations": [item.model_dump(mode="json") for item in recs[:12]],
        "gapAnalysis": [item.model_dump(mode="json") for item in gaps],
    }, strict=False)
    if llm_data:
        apply_llm_report(response, llm_data)
    return response


async def build_us_school_response(request: UsSchoolRecommendationRequest) -> UsSchoolRecommendationResponse:
    try:
        recs = [build_us_school_recommendation(request.studentFeatures, school) for school in request.schools]
    except RuntimeError as ex:
        raise HTTPException(status_code=503, detail="AI预测暂不可用，请稍后重试") from ex
    recs.sort(key=lambda item: item.admissionProbabilityEstimate, reverse=True)
    gaps = build_us_school_gap_analysis(request.studentFeatures)
    suggestions = build_suggestions(gaps)
    response = UsSchoolRecommendationResponse(
        status="SUCCESS",
        overallSummary="已生成美国院校级 AI 择校报告。",
        modelProvider="fastapi-lightgbm-deepseek",
        modelVersion=us_school_predictor.model_version,
        schoolRecommendations=recs,
        gapAnalysis=gaps,
        improvementSuggestions=suggestions,
    )

    try:
        llm_data = await generate_explanation({
            "task": "生成美国院校级申请背景评分报告",
            "studentFeatures": request.studentFeatures,
            "schoolRecommendations": [item.model_dump(mode="json") for item in recs[:12]],
            "gapAnalysis": [item.model_dump(mode="json") for item in gaps],
            "rules": [
                "只解释 LightGBM 输出的概率和分层，不重新计算概率",
                "不编造学校官方录取率，不承诺真实录取结果",
                "用中文输出面向学生的背景评分报告",
            ],
        }, strict=True)
    except DeepSeekUnavailable as ex:
        raise HTTPException(status_code=503, detail="AI预测暂不可用，请稍后重试") from ex
    apply_llm_report(response, llm_data)
    return response
