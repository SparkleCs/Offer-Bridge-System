from __future__ import annotations

import json
import os
from typing import Any

import httpx


DEEPSEEK_URL = os.getenv("DEEPSEEK_API_URL", "https://api.deepseek.com/chat/completions")
DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")
REQUIRED_RESPONSE_FIELDS = ("overallSummary", "gapAnalysis", "improvementSuggestions", "riskWarnings")


class DeepSeekUnavailable(RuntimeError):
    """Raised when the explanation layer cannot produce a valid report."""


def deepseek_configured() -> bool:
    return bool(os.getenv("DEEPSEEK_API_KEY"))


def _invalid(strict: bool, message: str) -> None:
    if strict:
        raise DeepSeekUnavailable(message)


def _normalize_gap_item(item: Any) -> dict[str, str] | None:
    if isinstance(item, dict):
        return {
            "dimension": str(item.get("dimension") or "综合背景"),
            "current": str(item.get("current") or "已根据当前资料评估"),
            "target": str(item.get("target") or "建议继续完善申请竞争力"),
            "priority": str(item.get("priority") or "中"),
        }
    if isinstance(item, str) and item.strip():
        return {
            "dimension": "综合背景",
            "current": "当前背景存在可提升空间",
            "target": item.strip(),
            "priority": "中",
        }
    return None


def _normalize_suggestion_item(item: Any) -> dict[str, str] | None:
    if isinstance(item, dict):
        return {
            "priority": str(item.get("priority") or "中"),
            "action": str(item.get("action") or item.get("suggestion") or "完善申请背景"),
            "reason": str(item.get("reason") or item.get("detail") or "用于提升整体申请竞争力。"),
        }
    if isinstance(item, str) and item.strip():
        return {
            "priority": "中",
            "action": item.strip(),
            "reason": "用于提升整体申请竞争力。",
        }
    return None


def _validate_response(data: Any, strict: bool) -> dict[str, Any] | None:
    if not isinstance(data, dict):
        _invalid(strict, "DeepSeek response is not a JSON object")
        return None

    missing = [field for field in REQUIRED_RESPONSE_FIELDS if field not in data]
    if missing:
        _invalid(strict, "DeepSeek response missing fields: " + ", ".join(missing))
        return None

    if not isinstance(data.get("overallSummary"), str):
        _invalid(strict, "DeepSeek overallSummary must be a string")
        return None
    if not isinstance(data.get("gapAnalysis"), list):
        _invalid(strict, "DeepSeek gapAnalysis must be a list")
        return None
    if not isinstance(data.get("improvementSuggestions"), list):
        _invalid(strict, "DeepSeek improvementSuggestions must be a list")
        return None
    if not isinstance(data.get("riskWarnings"), list):
        _invalid(strict, "DeepSeek riskWarnings must be a list")
        return None

    gap_analysis = [_normalize_gap_item(item) for item in data["gapAnalysis"]]
    suggestions = [_normalize_suggestion_item(item) for item in data["improvementSuggestions"]]
    risk_warnings = [str(item) for item in data["riskWarnings"] if str(item).strip()]
    data["gapAnalysis"] = [item for item in gap_analysis if item is not None]
    data["improvementSuggestions"] = [item for item in suggestions if item is not None]
    data["riskWarnings"] = risk_warnings or ["结果仅供申请规划参考，最终录取以学校审核为准。"]
    return data


async def generate_explanation(payload: dict[str, Any], *, strict: bool = False) -> dict[str, Any] | None:
    api_key = os.getenv("DEEPSEEK_API_KEY")
    if not api_key:
        _invalid(strict, "DeepSeek API key is not configured")
        return None

    prompt = (
        "你是留学申请规划助手。只根据输入的结构化学生背景分、院校级模型概率和差距分析生成中文 JSON，"
        "不要输出 markdown，不要重新计算概率，不要编造学校官方录取率，不要承诺真实录取结果。"
        "字段必须包含 overallSummary、gapAnalysis、improvementSuggestions、riskWarnings。"
    )
    body = {
        "model": DEEPSEEK_MODEL,
        "messages": [
            {"role": "system", "content": prompt},
            {"role": "user", "content": json.dumps(payload, ensure_ascii=False)},
        ],
        "temperature": 0.2,
        "response_format": {"type": "json_object"},
    }
    try:
        async with httpx.AsyncClient(timeout=12) as client:
            response = await client.post(
                DEEPSEEK_URL,
                headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"},
                json=body,
            )
            response.raise_for_status()
        content = response.json()["choices"][0]["message"]["content"]
        data = json.loads(content)
        return _validate_response(data, strict)
    except DeepSeekUnavailable:
        raise
    except Exception:
        _invalid(strict, "DeepSeek explanation generation failed")
        return None
