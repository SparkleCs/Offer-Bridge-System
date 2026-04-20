<template>
  <section class="page-card fade-up">
    <h2 class="section-title">沟通</h2>
    <p class="section-desc">机构认证通过且具备沟通权限后，可在此处理学生会话。</p>

    <el-alert v-if="blockedReason" :title="blockedReason" type="warning" :closable="false" show-icon />

    <template v-else>
      <div class="chat-shell">
        <div class="chat-list">
          <div class="chat-item" v-for="item in mockChats" :key="item.id">
            <div class="name">{{ item.name }}</div>
            <div class="last">{{ item.last }}</div>
          </div>
        </div>
        <div class="chat-main">
          <h3>会话窗口（占位）</h3>
          <p>后续接入真实 IM 消息流、未读计数与会话分配。</p>
        </div>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getMyWorkbenchAccess } from '../services/agency'
import type { MemberWorkbenchAccess } from '../types/agency'

const access = ref<MemberWorkbenchAccess | null>(null)

const mockChats = [
  { id: 1, name: '张同学', last: '老师您好，我想问一下文书修改。' },
  { id: 2, name: '李同学', last: '我可以申请英港联申吗？' }
]

const blockedReason = computed(() => {
  if (!access.value) return '正在检查权限...'
  if (access.value.orgVerificationStatus !== 'APPROVED') return '当前机构尚未通过认证，暂不可使用沟通功能。'
  if (!access.value.canChatStudent) return '你当前没有沟通学生权限，请联系机构管理员开通。'
  return ''
})

onMounted(async () => {
  access.value = await getMyWorkbenchAccess().catch(() => null)
})
</script>

<style scoped>
.chat-shell {
  margin-top: 14px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 12px;
}

.chat-list,
.chat-main {
  background: #fff;
  border: 1px solid #dbe7f2;
  border-radius: 12px;
  padding: 12px;
}

.chat-item {
  padding: 10px;
  border-bottom: 1px dashed #dbe7f2;
}

.chat-item:last-child {
  border-bottom: none;
}

.name {
  font-weight: 700;
}

.last {
  color: #6b7e92;
  margin-top: 4px;
}

@media (max-width: 900px) {
  .chat-shell {
    grid-template-columns: 1fr;
  }
}
</style>
