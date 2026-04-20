<template>
  <section class="page-card fade-up">
    <h2 class="section-title">团队产品管理</h2>
    <p class="section-desc">后续用于发布套餐与服务产品，本版本先做能力门禁与占位。</p>

    <el-alert v-if="blockedReason" :title="blockedReason" type="warning" :closable="false" show-icon />
    <el-empty v-else description="套餐发布功能即将接入" />
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getMyWorkbenchAccess } from '../services/agency'
import type { MemberWorkbenchAccess } from '../types/agency'

const access = ref<MemberWorkbenchAccess | null>(null)

const blockedReason = computed(() => {
  if (!access.value) return '正在检查权限...'
  if (access.value.blockedReason) return access.value.blockedReason
  if (!access.value.canPublishPackage) return '你当前没有发布套餐权限，请联系管理员。'
  return ''
})

onMounted(async () => {
  access.value = await getMyWorkbenchAccess().catch(() => null)
})
</script>
