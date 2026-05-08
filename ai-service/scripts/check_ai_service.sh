#!/usr/bin/env bash
set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
AI_DIR="$ROOT_DIR/ai-service"
HEALTH_URL="${AI_HEALTH_URL:-http://127.0.0.1:8001/health}"
MYSQL_BIN="${MYSQL_CLI:-mysql}"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-121380hxy}"
MYSQL_DATABASE="${MYSQL_DATABASE:-offer_bridge}"

ok() { printf '[OK] %s\n' "$1"; }
fail() { printf '[FAIL] %s\n' "$1"; }
info() { printf '[INFO] %s\n' "$1"; }

status=0

if [[ -f "$AI_DIR/.env" ]]; then
  ok "ai-service/.env exists"
  if grep -q '^DEEPSEEK_API_KEY=.' "$AI_DIR/.env"; then
    ok "DEEPSEEK_API_KEY is configured"
  else
    fail "DEEPSEEK_API_KEY is missing in ai-service/.env"
    status=1
  fi
else
  fail "ai-service/.env is missing"
  status=1
fi

for artifact in \
  "$AI_DIR/models/admission_us_school_lgbm_v1.pkl" \
  "$AI_DIR/models/admission_us_school_calibrator_v1.pkl" \
  "$AI_DIR/models/admission_us_school_feature_schema_v1.json"; do
  if [[ -f "$artifact" ]]; then
    ok "model artifact exists: ${artifact#$ROOT_DIR/}"
  else
    fail "model artifact missing: ${artifact#$ROOT_DIR/}"
    status=1
  fi
done

health_body="$(curl -fsS "$HEALTH_URL" 2>/dev/null || true)"
if [[ -n "$health_body" ]]; then
  ok "FastAPI health reachable at $HEALTH_URL"
  printf '%s\n' "$health_body" | "$AI_DIR/.venv/bin/python" -m json.tool 2>/dev/null || printf '%s\n' "$health_body"
  for key in '"usSchoolModelLoaded": true' '"usSchoolCalibratorLoaded": true' '"deepseekConfigured": true'; do
    if printf '%s' "$health_body" | grep -q "$key"; then
      ok "health contains $key"
    else
      fail "health missing $key"
      status=1
    fi
  done
else
  fail "FastAPI health is not reachable at $HEALTH_URL"
  info "Start it with: cd $AI_DIR && source .venv/bin/activate && uvicorn main:app --host 127.0.0.1 --port 8001 --reload"
  status=1
fi

sql="SELECT 'us_schools', COUNT(*) FROM school WHERE country_code='US' UNION ALL SELECT 'difficulty_profiles', COUNT(*) FROM us_school_difficulty_profile UNION ALL SELECT 'joined', COUNT(*) FROM school s JOIN us_school_difficulty_profile p ON p.school_id=s.id WHERE s.country_code='US';"
db_rows="$($MYSQL_BIN -h"$MYSQL_HOST" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -D "$MYSQL_DATABASE" -N -B -e "$sql" 2>/dev/null || true)"
if [[ -n "$db_rows" ]]; then
  ok "MySQL school/profile counts are reachable"
  printf '%s\n' "$db_rows"
  while IFS=$'\t' read -r label count; do
    if [[ "$count" != "57" ]]; then
      fail "$label expected 57, got $count"
      status=1
    fi
  done <<< "$db_rows"
else
  fail "Cannot query MySQL school/profile counts"
  info "Check MySQL and MYSQL_CLI/MYSQL_HOST/MYSQL_USER/MYSQL_PASSWORD/MYSQL_DATABASE"
  status=1
fi

exit "$status"
