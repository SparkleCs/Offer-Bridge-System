from __future__ import annotations

import os
from pathlib import Path
from typing import Any

try:
    import joblib
except Exception:  # pragma: no cover - optional model dependency for fallback mode
    joblib = None

from feature_engineering import build_features, decimal_float, normalized_gpa
from schemas import ProgramCandidate


MODEL_PATH = Path(__file__).parent / "models" / "admission_lr.pkl"


class AdmissionPredictor:
    def __init__(self) -> None:
        self.strict_model = os.getenv("STRICT_MODEL", "true").lower() == "true"
        self.init_error: str | None = None
        self.model: Any | None = None
        if joblib is not None and MODEL_PATH.exists():
            try:
                self.model = joblib.load(MODEL_PATH)
            except Exception:
                self.model = None
        if self.strict_model and self.model is None:
            reason = "joblib not installed" if joblib is None else f"model not loadable at {MODEL_PATH}"
            self.init_error = f"strict model mode enabled, {reason}"

    @property
    def model_version(self) -> str:
        return "admission-lr-v1"

    @property
    def model_loaded(self) -> bool:
        return self.model is not None

    def predict_probability(self, profile: dict[str, Any], program: ProgramCandidate) -> float:
        if self.init_error:
            raise RuntimeError(self.init_error)
        if self.strict_model and self.model is None:
            raise RuntimeError("strict model mode enabled, model unavailable")
        features = build_features(profile, program)
        if self.model is not None:
            try:
                return float(self.model.predict_proba([features])[0][1])
            except Exception:
                if self.strict_model:
                    raise RuntimeError("strict model mode enabled, model prediction failed")
        if self.strict_model:
            raise RuntimeError("strict model mode enabled, model prediction unavailable")
        return self._fallback_probability(profile, program)

    def _fallback_probability(self, profile: dict[str, Any], program: ProgramCandidate) -> float:
        rule = max(0.0, min(1.0, (program.ruleMatchScore or 0) / 100.0))
        gpa = normalized_gpa(profile)
        gpa_min = decimal_float(program.gpaMinRecommend, 3.0)
        gpa_signal = max(0.0, min(1.0, 0.5 + (gpa - gpa_min) / 1.2))
        background = profile.get("backgroundScore") or {}
        background_signal = max(0.0, min(1.0, decimal_float(background.get("overallAcademicScore"), 50.0) / 100.0))
        probability = rule * 0.55 + gpa_signal * 0.25 + background_signal * 0.20
        return max(0.05, min(0.95, probability))


predictor = AdmissionPredictor()
