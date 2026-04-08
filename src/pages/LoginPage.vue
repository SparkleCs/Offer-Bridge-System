<template>
  <section class="page-card fade-up auth-page">
    <h2 class="section-title">登录</h2>
    <p class="section-desc">本迭代仅演示前端流程，不接后端接口。</p>
    <el-form label-position="top" class="form">
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.name" placeholder="请输入昵称" />
      </el-form-item>
      <el-button type="primary" round @click="submit">登录（演示）</el-button>
    </el-form>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../session'

const router = useRouter()
const form = reactive({
  email: '',
  name: ''
})

function submit() {
  login(form.name || '留学生')
  router.push('/me')
}
</script>

<style scoped>
.auth-page {
  max-width: 520px;
  margin: 0 auto;
}

.form {
  margin-top: 20px;
}
</style>
