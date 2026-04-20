## 登录审计字段规范（login_audit_log.login_method）

### 当前定义
- 字段：`login_audit_log.login_method`
- 推荐数据库类型：`VARCHAR(32)`（避免新增登录方式时被 ENUM 卡死）
- 当前应用层允许值：
  - `SMS_CODE`
  - `PASSWORD`

### 写入规范
- 普通短信登录写入：`SMS_CODE`
- 管理员短信登录写入：`SMS_CODE`
  - 管理员身份通过 `user_id -> user_account.role = 'ADMIN'` 区分

### 新增登录方式流程
1. 先在代码中新增允许值校验（应用层白名单）。
2. 如数据库为 VARCHAR，无需改库即可兼容。
3. 若历史环境仍为 ENUM，先执行迁移脚本：
   - `backend/sql/V17_login_audit_log_method_to_varchar.sql`

### 兼容策略
- 旧值保留，不做破坏性替换。
- 新值上线前先完成代码校验与灰度验证，避免脏数据写入。
