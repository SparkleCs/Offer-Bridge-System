<template>
  <section class="page-card fade-up">
    <h2 class="section-title">沟通</h2>
    <p class="section-desc">机构认证通过且具备沟通权限后，可在此处理学生会话。</p>

    <el-alert v-if="blockedReason" :title="blockedReason" type="warning" :closable="false" show-icon />

    <template v-else>
      <ChatPanel mode="agent" />
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ChatPanel from '../components/ChatPanel.vue'
import { getMyWorkbenchAccess } from '../services/agency'
import type { MemberWorkbenchAccess } from '../types/agency'

const access = ref<MemberWorkbenchAccess | null>(null)

const blockedReason = computed(() => {
  if (!access.value) return '正在检查权限...'
  if (access.value.blockedReason) return access.value.blockedReason
  if (!access.value.canChatStudent) return '你当前没有沟通学生权限，请联系机构管理员开通。'
  return ''
})

onMounted(async () => {
  access.value = await getMyWorkbenchAccess().catch(() => null)
})
</script>

<style scoped>
.page-card {
  height: calc(100vh - 116px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.page-card :deep(.chat-panel) {
  flex: 1;
  min-height: 0;
}
</style>
