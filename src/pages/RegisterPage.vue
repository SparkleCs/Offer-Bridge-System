<template>
  <section class="page-card fade-up auth-page">
    <h2 class="section-title">注册</h2>
    <p class="section-desc">填写基础信息后即可进入系统（静态演示页面）。</p>
    <el-form label-position="top" class="form">
      <el-form-item label="姓名">
        <el-input placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="目标国家">
        <el-select placeholder="请选择目标国家">
          <el-option label="美国" value="us" />
          <el-option label="英国" value="uk" />
          <el-option label="澳大利亚" value="au" />
        </el-select>
      </el-form-item>
      <el-button type="primary" round @click="$router.push('/login')">提交并去登录</el-button>
    </el-form>
  </section>
</template>

<style scoped>
.auth-page {
  max-width: 520px;
  margin: 0 auto;
}

.form {
  margin-top: 20px;
}
</style>
