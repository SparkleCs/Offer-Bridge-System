# Offer Bridge 毕业答辩学习指南

这份文档按“完整业务闭环”梳理项目，适合用来准备答辩讲解和回看源码。阅读代码时不要从所有文件逐个扫起，而是沿着“页面 -> 前端 service -> 后端 Controller -> Service -> Mapper/Repository -> 数据表/外部服务”的链路追踪。

## 1. 项目总览

Offer Bridge 是一个留学服务撮合系统，核心参与方有学生、机构管理员、机构顾问和平台管理员。

- 前端：Vue 3 + TypeScript + Vite，使用 Vue Router 做页面入口和角色跳转，Pinia 保存登录态，Element Plus 提供 UI 组件。
- 后端：Spring Boot 3，Controller 暴露 REST API，Service 编排业务规则，MyBatis Mapper 访问 MySQL，Redis/MongoDB/WebSocket 支撑验证码、论坛、聊天等扩展能力。
- AI 服务：`ai-service` 是独立 FastAPI 服务，后端通过 `AiClient` 调用它生成择校/项目分析报告。

答辩时可以先讲整体架构：前端负责交互，后端负责身份校验和业务规则，AI 服务负责模型预测和报告文本生成，数据库保存学生资料、机构团队、订单阶段、评价和 AI 报告。

## 2. 推荐学习路线

第一步，看前端入口。

- `src/router/index.ts`：理解普通学生、机构管理员、机构顾问、平台管理员分别进入哪些页面。
- `src/services/http.ts`：理解所有接口如何统一带 token、如何自动刷新 token、如何抛出业务错误。
- `src/stores/auth.ts`：理解登录后的 token、角色、资料完成状态如何保存在 Pinia 和 localStorage。

第二步，看后端基础设施。

- `backend/src/main/java/com/offerbridge/backend/common/ApiResponse.java`：统一响应结构。
- `backend/src/main/java/com/offerbridge/backend/exception/GlobalExceptionHandler.java`：统一异常处理和 traceId。
- `backend/src/main/java/com/offerbridge/backend/config/SecurityConfig.java` 与 `security/AuthInterceptor.java`：哪些接口公开，哪些接口必须登录，后端如何从 JWT 中解析用户身份。

第三步，按完整业务闭环学习。

- 登录注册：`AuthController -> AuthServiceImpl -> JwtService`。
- 学生画像：`StudentController -> StudentServiceImpl -> StudentProfileMapper`，包括基本信息、学术背景、科研/竞赛/工作/交换经历、背景分和学生认证。
- 院校浏览：`UniversityController -> UniversityServiceImpl`，公开读取院校和项目数据。
- 机构发现：`AgencyController` 的 discovery 接口，学生浏览顾问/团队，登录后可收藏团队。
- 沟通咨询：`MessageController -> MessageServiceImpl`，学生与顾问建立会话、发送消息、交换联系方式。
- 服务订单：`OrderController -> OrderServiceImpl`，学生下单、支付、顾问报价/提交阶段、学生确认/驳回阶段。
- 评价反馈：`ReviewController -> ReviewServiceImpl`，订单完成后评价顾问/团队，沉淀机构口碑。
- 审核管理：`AdminController -> AdminReviewServiceImpl`，平台管理员审核机构、顾问、学生资料。

第四步，看机构侧。

- 机构管理员负责公司资料、机构认证、成员邀请、成员权限、团队产品发布。
- 机构顾问负责个人资料认证、学生搜索、沟通、服务订单履约。
- 路由上分别对应 `/org-admin` 和 `/agent-workbench`。

第五步，看 AI 亮点。

- 前端入口：`src/pages/AiReportPage.vue` 与 `src/services/ai.ts`。
- 后端入口：`AiController -> AiServiceImpl -> AiClient`。
- Python 服务入口：`ai-service/main.py`。
- 核心逻辑：后端汇总学生背景分、目标国家、候选院校和美国院校难度画像，调用 FastAPI 预测录取概率，随后保存 AI 报告和推荐结果。

## 3. 答辩讲解主线

可以用这条 5 分钟主线讲项目：

1. 学生注册登录后补全个人画像，包括学术成绩、语言成绩、科研竞赛等背景信息。
2. 系统基于学生画像计算背景分，并提供院校浏览、机构团队浏览和 AI 择校报告。
3. 学生可以收藏团队、发起咨询，会话系统记录沟通过程和联系方式交换。
4. 学生选择团队后创建服务订单，顾问报价并按阶段提交服务成果，学生确认阶段或提出驳回。
5. 订单完成后学生评价，平台管理员审核机构/顾问/学生认证，形成可信撮合闭环。

## 4. 核心接口与页面映射

| 业务 | 前端页面/服务 | 后端入口 | 答辩要点 |
| --- | --- | --- | --- |
| 登录与权限 | `AuthPage.vue`, `AdminAuthPage.vue`, `services/auth.ts` | `AuthController` | JWT + refresh token，角色决定页面入口 |
| 学生资料 | `MePage.vue`, `services/student.ts` | `StudentController` | 学生画像是推荐、审核、沟通的基础数据 |
| 院校浏览 | `UniversitiesPage.vue`, `services/university.ts` | `UniversityController` | 公开数据读取，支持学校/项目筛选 |
| 机构发现 | `AgenciesPage.vue`, `services/agency.ts` | `AgencyController` | discovery 接口允许游客浏览，登录后增强收藏状态 |
| 消息沟通 | `MessagesPage.vue`, `ChatPanel.vue` | `MessageController` | 会话连接学生和顾问，支持动作消息和联系方式交换 |
| 订单履约 | `OrdersPage.vue`, `StudentStageDetailPage.vue`, `AgentStageSubmitPage.vue` | `OrderController` | 服务从下单、支付、报价、阶段提交到确认 |
| 评价口碑 | `AgencyMemberReviewsPage.vue`, `services/review.ts` | `ReviewController` | 评价回流到机构展示和顾问口碑 |
| 平台审核 | `Admin*ReviewsPage.vue` | `AdminController` | 管理员控制认证质量和平台可信度 |
| AI 推荐 | `AiReportPage.vue`, `services/ai.ts` | `AiController`, `AiServiceImpl`, `AiClient`, `ai-service/main.py` | 学生画像 + 模型预测 + 报告保存 |

## 5. 数据与安全答辩点

- 统一响应：后端所有接口用 `ApiResponse` 返回 `code/message/data`，前端统一在 `http.ts` 解析。
- 统一鉴权：前端路由守卫控制页面访问，后端拦截器控制 API 访问，不能只依赖前端判断。
- 密码安全：项目使用 BCrypt 保存密码哈希，验证码和 refresh token 使用摘要保存。
- 角色隔离：学生、机构管理员、机构顾问、平台管理员有不同页面和接口职责。
- AI 风险控制：AI 报告保留“仅供参考”的风险提示，不承诺真实录取结果。
- 配置改进点：本地配置里出现第三方支付密钥时，答辩可说明生产环境应迁移到环境变量或密钥管理服务。

## 6. 代码批注阅读方法

源码里的新增批注主要分三类：

- “学习入口”：帮助你知道这个文件在系统中的位置。
- “流程注释”：解释一个方法在完整业务闭环中负责哪一步。
- “答辩提示”：提醒这个设计在答辩中可以如何表达。

建议阅读顺序是：先读本文档，再读前端入口文件，最后按学生、机构、订单、AI 四条链路深入后端 Service 实现。
