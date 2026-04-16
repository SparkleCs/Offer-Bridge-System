# 院校信息支撑模块说明

## 1. 模块范围
- 场景：硕士申请
- 国家：英国 / 美国 / 澳洲 / 新西兰
- 数据：QS前50学校试运行（可扩展到前100）
- 目标：支撑学校浏览、项目筛选、匹配展示、申请清单与中介入口

## 2. 数据库迁移
- `backend/sql/V6_university_program_support_module.sql`
  - `subject_category` 学科大类
  - `major_direction` 专业方向
  - `school` 学校信息
  - `school_subject_rel` 学校-学科关系
  - `program` 项目信息
  - `student_application_list` 学生申请清单
  - `student_program_match_result` 学生项目匹配结果
  - `agency_profile` 中介基础信息
  - `agency_direction_rel` 中介擅长方向关系
- `backend/sql/V7_university_program_seed_data.sql`
  - 字典、学校、项目、学校-学科关系、中介映射等种子数据

## 3. 后端接口
- 学校与项目
  - `GET /api/v1/universities/meta`
  - `GET /api/v1/universities/schools`
  - `GET /api/v1/universities/schools/{schoolId}`
  - `GET /api/v1/universities/programs`
  - `GET /api/v1/universities/programs/{programId}`
  - `GET /api/v1/universities/programs/{programId}/agencies`
- 申请清单
  - `GET /api/v1/student/application-list`
  - `POST /api/v1/student/application-list`
  - `PUT /api/v1/student/application-list/{applicationId}/status`
  - `DELETE /api/v1/student/application-list/{applicationId}`

## 4. 匹配规则 V1
- 评分维度：目标国家、目标专业方向、GPA、语言成绩、预算、GRE/GMAT要求
- 输出：
  - `matchScore`（0-100）
  - `matchTier`（冲刺校/匹配校/保底校）
  - `reasonTags`（解释标签）
- 匹配结果会写入 `student_program_match_result`，供项目详情和申请清单复用。

## 5. 前端交付
- `UniversitiesPage.vue`
  - 学校筛选与列表
  - 学校详情抽屉
  - 项目列表
  - 项目详情（匹配结果 + 推荐中介）
  - 加入申请清单
- `MePage.vue`
  - 新增“申请清单”区块
  - 状态流转：`COLLECTED -> TO_EVALUATE -> PREPARING -> APPLYING -> SUBMITTED -> ADMITTED/REJECTED`
  - 可移除清单项目
  - 与进度计算联动

## 6. 演示脚本（建议）
1. 登录学生账号，进入“世界大学库”。
2. 按国家+方向筛选学校，打开学校详情。
3. 打开项目详情，查看匹配分与解释标签。
4. 点击“加入申请清单”。
5. 进入“个人中心”，在“申请清单”区块更新状态并观察进度变化。
6. 返回项目详情，查看推荐中介入口。
