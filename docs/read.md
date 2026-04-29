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

## 加密与敏感数据保护

### 密码存储
- 用户密码不保存明文，也不使用可逆加密。
- 系统使用 `BCryptPasswordEncoder(12)` 对密码做单向哈希后写入 `user_account.password_hash`。
- BCrypt 哈希自带随机盐，同一个密码每次生成的哈希值也不同，数据库泄露时无法直接还原用户密码。
- 密码规则由前后端共同校验，后端兜底要求：8-32 位，至少包含字母和数字。
- 密码初始设置与修改需要用户登录后调用账号安全接口完成；已有密码时必须先校验当前密码。

### 验证码与令牌
- 短信验证码入库前使用 SHA-256 摘要，校验时对用户输入再次摘要后比对。
- refresh token 不以明文保存，数据库仅保存 SHA-256 摘要；退出登录或刷新时按摘要吊销旧令牌。

### 传输加密
- 生产环境应在 Nginx、云负载均衡或 HTTPS 网关层配置 TLS/HTTPS。
- 应用代码不硬编码证书；前端生产环境的 `VITE_API_BASE_URL` 应配置为 `https://...` 地址。
- HTTPS 用于保护手机号、验证码、密码、个人资料、聊天内容等数据在传输过程中的机密性。
