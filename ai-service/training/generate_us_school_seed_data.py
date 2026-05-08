from __future__ import annotations

import argparse
import csv
import math
import os
import random
import re
import subprocess
from pathlib import Path


ROOT_DIR = Path(__file__).resolve().parents[2]
DEFAULT_OUTPUT = Path(__file__).parent / "data" / "us_school_admission_history.csv"
US_SCHOOL_SEED_SQL = ROOT_DIR / "backend" / "sql" / "V30_usnews_us_school_seed.sql"
def mysql_cmd() -> list[str]:
    mysql_cli = os.getenv("MYSQL_CLI", "mysql")
    host = os.getenv("MYSQL_HOST", "127.0.0.1")
    user = os.getenv("MYSQL_USER", "root")
    password = os.getenv("MYSQL_PASSWORD", "121380hxy")
    database = os.getenv("MYSQL_DATABASE", "offer_bridge")
    return [
        mysql_cli,
        f"-h{host}",
        f"-u{user}",
        f"-p{password}",
        "-D",
        database,
        "-N",
        "-B",
        "-e",
    ]


def clamp(value: float, low: float = 0.0, high: float = 100.0) -> float:
    return max(low, min(high, value))


def sigmoid(value: float) -> float:
    return 1.0 / (1.0 + math.exp(-value))


def mysql_query(sql: str) -> list[dict[str, float | int]]:
    result = subprocess.run(
        [*mysql_cmd(), sql],
        cwd=ROOT_DIR,
        check=True,
        text=True,
        capture_output=True,
    )
    rows: list[dict[str, float | int]] = []
    for line in result.stdout.splitlines():
        if not line.strip():
            continue
        school_id, selectivity, bar, heat = line.split("\t")
        rows.append({
            "school_id": int(school_id),
            "school_selectivity_score": float(selectivity),
            "admission_bar_score": float(bar),
            "school_heat_score": float(heat),
        })
    return rows


def load_school_profiles(allow_sql_seed_fallback: bool) -> list[dict[str, float | int]]:
    sql = """
      SELECT school_id, school_selectivity_score, admission_bar_score, school_heat_score
      FROM us_school_difficulty_profile
      ORDER BY school_id ASC
    """
    try:
        return mysql_query(sql)
    except (subprocess.CalledProcessError, FileNotFoundError) as ex:
        if allow_sql_seed_fallback:
            return load_school_profiles_from_sql_seed()
        raise RuntimeError(
            "Cannot connect to MySQL us_school_difficulty_profile. "
            "Start MySQL and rerun, or pass --allow-sql-seed-fallback for local smoke testing only."
        ) from ex


def score_from_rank(rank: int) -> tuple[float, float, float]:
    if rank <= 10:
        selectivity = 100.0
    elif rank <= 30:
        selectivity = 90.0
    elif rank <= 60:
        selectivity = 80.0
    elif rank <= 100:
        selectivity = 70.0
    elif rank <= 150:
        selectivity = 60.0
    else:
        selectivity = 50.0
    admission_bar = clamp(50.0 + selectivity * 0.42)
    heat = 95.0 if rank <= 20 else 85.0 if rank <= 50 else 75.0 if rank <= 100 else 60.0
    return selectivity, admission_bar, heat


def load_school_profiles_from_sql_seed() -> list[dict[str, float | int]]:
    if not US_SCHOOL_SEED_SQL.exists():
        return []
    text = US_SCHOOL_SEED_SQL.read_text(encoding="utf-8")
    pattern = re.compile(
        r"\('([^']*)',\s*'([^']*)',\s*'US',\s*'美国',\s*'[^']*',\s*(\d+),\s*2026,\s*(\d+),",
        re.MULTILINE,
    )
    profiles: list[dict[str, float | int]] = []
    for index, match in enumerate(pattern.finditer(text), start=1):
        qs_rank = int(match.group(3))
        usnews_rank = int(match.group(4))
        primary_rank = usnews_rank if usnews_rank < 999 else qs_rank
        selectivity, admission_bar, heat = score_from_rank(primary_rank)
        profiles.append({
            "school_id": index,
            "school_selectivity_score": selectivity,
            "admission_bar_score": admission_bar,
            "school_heat_score": heat,
        })
    return profiles


def sample_student(rng: random.Random) -> tuple[float, float, float]:
    # A broad but plausible development distribution; this is synthetic data only.
    gpa = clamp(rng.gauss(73, 14))
    language = clamp(rng.gauss(70, 15))
    soft = clamp(rng.gauss(58, 18))
    if rng.random() < 0.18:
        gpa = clamp(gpa + rng.uniform(8, 18))
        language = clamp(language + rng.uniform(5, 15))
    if rng.random() < 0.22:
        soft = clamp(soft + rng.uniform(10, 25))
    return round(gpa, 2), round(language, 2), round(soft, 2)


def make_row(profile: dict[str, float | int], rng: random.Random) -> dict[str, float | int]:
    gpa, language, soft = sample_student(rng)
    selectivity = float(profile["school_selectivity_score"])
    bar = float(profile["admission_bar_score"])
    heat = float(profile["school_heat_score"])
    student_strength = 0.45 * gpa + 0.30 * language + 0.25 * soft
    school_difficulty = 0.45 * selectivity + 0.35 * bar + 0.20 * heat
    admit_probability = sigmoid((student_strength - school_difficulty) / 10.0)
    admit_label = 1 if rng.random() < admit_probability else 0
    return {
        "school_id": int(profile["school_id"]),
        "gpa_score": gpa,
        "language_score": language,
        "soft_background_score": soft,
        "school_selectivity_score": round(selectivity, 2),
        "admission_bar_score": round(bar, 2),
        "school_heat_score": round(heat, 2),
        "admit_label": admit_label,
    }


def write_csv(rows: list[dict[str, float | int]], output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    fieldnames = [
        "school_id",
        "gpa_score",
        "language_score",
        "soft_background_score",
        "school_selectivity_score",
        "admission_bar_score",
        "school_heat_score",
        "admit_label",
    ]
    with output.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def main() -> None:
    parser = argparse.ArgumentParser(description="Generate synthetic US school admission seed data.")
    parser.add_argument("--rows", type=int, default=600)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--allow-sql-seed-fallback", action="store_true")
    args = parser.parse_args()

    profiles = load_school_profiles(args.allow_sql_seed_fallback)
    if not profiles:
        raise RuntimeError("No US school difficulty profiles found. Run backend/sql/V32_us_school_ai_probability_model.sql first.")

    rng = random.Random(args.seed)
    rows = [make_row(rng.choice(profiles), rng) for _ in range(args.rows)]
    labels = {int(row["admit_label"]) for row in rows}
    if labels != {0, 1}:
        raise RuntimeError("Synthetic data generation failed to create both admitted and rejected samples.")
    write_csv(rows, args.output)

    admitted = sum(int(row["admit_label"]) for row in rows)
    print(f"Wrote {len(rows)} rows to {args.output}")
    print(f"admitted={admitted}, rejected={len(rows) - admitted}")


if __name__ == "__main__":
    main()
