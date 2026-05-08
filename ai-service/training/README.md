# US school admission model training

Default input path:

```text
ai-service/training/data/us_school_admission_history.csv
```

Required columns:

```text
gpa_score
language_score
soft_background_score
school_selectivity_score
admission_bar_score
school_heat_score
admit_label
```

`school_id` can exist in the CSV for traceability, but it is not used as a model feature.

Run:

```bash
python ai-service/training/train_us_school_model.py
```

Optional override:

```bash
US_SCHOOL_TRAIN_DATA_PATH=/path/to/us_school_admission_history.csv python ai-service/training/train_us_school_model.py
```
