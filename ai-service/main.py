from __future__ import annotations

from fastapi import FastAPI
from fastapi import HTTPException

from llm_client import generate_explanation
from ml_predictor import predictor
from report_builder import build_gap_analysis, build_recommendation, build_suggestions
from schemas import RecommendationRequest, RecommendationResponse


app = FastAPI(title="Offer Bridge AI Service", version="0.1.0")


@app.get("/health")
def health() -> dict[str, str | bool]:
    return {
        "status": "ok",
        "modelVersion": predictor.model_version,
        "modelLoaded": predictor.model_loaded,
    }


@app.post("/ai/recommendations", response_model=RecommendationResponse)
async def recommendations(request: RecommendationRequest) -> RecommendationResponse:
    return await build_response(request, "已生成 AI 择校报告。")


@app.post("/ai/program-analysis", response_model=RecommendationResponse)
async def program_analysis(request: RecommendationRequest) -> RecommendationResponse:
    return await build_response(request, "已生成单项目 AI 申请竞争力分析。")


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
    })
    if llm_data:
        response.overallSummary = llm_data.get("overallSummary") or response.overallSummary
        response.riskWarnings = llm_data.get("riskWarnings") or response.riskWarnings
        if isinstance(llm_data.get("gapAnalysis"), list):
            response.gapAnalysis = llm_data["gapAnalysis"]
        if isinstance(llm_data.get("improvementSuggestions"), list):
            response.improvementSuggestions = llm_data["improvementSuggestions"]
    return response
