from __future__ import annotations

import csv
import json
import os
from pathlib import Path

import joblib
import lightgbm as lgb
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import average_precision_score, brier_score_loss, roc_auc_score
from sklearn.model_selection import train_test_split


FEATURES = [
    "gpa_score",
    "language_score",
    "soft_background_score",
    "school_selectivity_score",
    "admission_bar_score",
    "school_heat_score",
]
LABEL = "admit_label"
DEFAULT_DATA_PATH = Path(__file__).parent / "data" / "us_school_admission_history.csv"
MODEL_DIR = Path(__file__).resolve().parents[1] / "models"


def read_dataset(path: Path) -> tuple[list[list[float]], list[int]]:
    if not path.exists():
        raise FileNotFoundError(f"Training data not found: {path}")
    with path.open("r", encoding="utf-8-sig", newline="") as handle:
        reader = csv.DictReader(handle)
        missing = [name for name in [*FEATURES, LABEL] if name not in (reader.fieldnames or [])]
        if missing:
            raise ValueError(f"Missing required columns: {', '.join(missing)}")
        x: list[list[float]] = []
        y: list[int] = []
        for row in reader:
            x.append([float(row[name]) for name in FEATURES])
            y.append(int(row[LABEL]))
    if len(set(y)) < 2:
        raise ValueError("Training data must include both admitted and rejected samples")
    return x, y


def main() -> None:
    data_path = Path(os.getenv("US_SCHOOL_TRAIN_DATA_PATH", str(DEFAULT_DATA_PATH)))
    x, y = read_dataset(data_path)
    x_train, x_temp, y_train, y_temp = train_test_split(x, y, test_size=0.3, random_state=42, stratify=y)
    x_valid, x_test, y_valid, y_test = train_test_split(x_temp, y_temp, test_size=0.5, random_state=42, stratify=y_temp)

    model = lgb.LGBMClassifier(
        objective="binary",
        n_estimators=180,
        learning_rate=0.04,
        num_leaves=15,
        min_child_samples=10,
        subsample=0.85,
        colsample_bytree=0.85,
        random_state=42,
    )
    model.fit(x_train, y_train)

    valid_prob = model.predict_proba(x_valid)[:, 1]
    calibrator = LogisticRegression(random_state=42)
    calibrator.fit([[float(value)] for value in valid_prob], y_valid)

    raw_test_prob = model.predict_proba(x_test)[:, 1]
    calibrated_test_prob = calibrator.predict_proba([[float(value)] for value in raw_test_prob])[:, 1]
    metrics = {
        "feature_schema_version": "US_SCHOOL_FEATURES_V1",
        "features": FEATURES,
        "train_size": len(x_train),
        "valid_size": len(x_valid),
        "test_size": len(x_test),
        "auc": roc_auc_score(y_test, calibrated_test_prob),
        "pr_auc": average_precision_score(y_test, calibrated_test_prob),
        "brier": brier_score_loss(y_test, calibrated_test_prob),
    }

    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, MODEL_DIR / "admission_us_school_lgbm_v1.pkl")
    joblib.dump(calibrator, MODEL_DIR / "admission_us_school_calibrator_v1.pkl")
    (MODEL_DIR / "admission_us_school_feature_schema_v1.json").write_text(
        json.dumps({"features": FEATURES, "label": LABEL}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    (MODEL_DIR / "admission_us_school_metrics_v1.json").write_text(
        json.dumps(metrics, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(json.dumps(metrics, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
