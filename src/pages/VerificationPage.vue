<template>
  <section class="page-card fade-up verify-page">
    <h2 class="section-title">资质审核中</h2>
    <p class="section-desc">
      你的账号已登录成功，但当前认证状态未通过审核。请前往个人中心补充资料，审核通过后自动开放全部功能。
    </p>
    <div class="actions">
      <el-button type="primary" round @click="$router.push('/me')">前往个人中心</el-button>
      <el-button round @click="$router.push('/')">返回首页</el-button>
    </div>
  </section>
</template>

<style scoped>
.verify-page {
  max-width: 780px;
  margin: 0 auto;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>
