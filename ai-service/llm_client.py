from __future__ import annotations

import json
import os
from typing import Any

import httpx


DEEPSEEK_URL = os.getenv("DEEPSEEK_API_URL", "https://api.deepseek.com/chat/completions")
DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")


async def generate_explanation(payload: dict[str, Any]) -> dict[str, Any] | None:
    api_key = os.getenv("DEEPSEEK_API_KEY")
    if not api_key:
        return None

    prompt = (
        "你是留学申请规划助手。只根据输入的结构化学生画像、项目推荐和模型结果生成中文 JSON，"
        "不要输出 markdown，不要承诺真实录取结果。字段必须包含 overallSummary、gapAnalysis、"
        "improvementSuggestions、riskWarnings。"
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
        if not isinstance(data, dict):
            return None
        return data
    except Exception:
        return None
