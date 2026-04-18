<template>
  <section class="page-card fade-up message-shell">
    <div class="message-head">
      <h2 class="section-title">消息</h2>
      <p class="section-desc">会话中心（开发中），后续将接入团队沟通、系统通知与进度提醒。</p>
    </div>

    <div class="message-card">
      <h3>当前上下文</h3>
      <p>团队ID：{{ teamId || '未指定' }}</p>
      <p>团队名称：{{ teamName || '未指定' }}</p>
      <el-alert title="消息模块正在建设中，本版本仅保留入口和参数传递。" type="info" :closable="false" show-icon />
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const teamId = computed(() => String(route.query.teamId || ''))
const teamName = computed(() => String(route.query.teamName || ''))
</script>

<style scoped>
.message-shell {
  max-width: 920px;
  margin: 0 auto;
}

.message-card {
  margin-top: 16px;
  border: 1px solid #dbe6f1;
  border-radius: 14px;
  padding: 16px;
  background: linear-gradient(160deg, #ffffff, #f8fbff);
}

.message-card h3 {
  margin: 0 0 8px;
}

.message-card p {
  margin: 4px 0;
  color: #5b738b;
}
</style>
