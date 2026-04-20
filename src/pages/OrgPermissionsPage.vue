<template>
  <section class="page-card fade-up">
    <h2 class="section-title">权限管理</h2>
    <p class="section-desc">控制员工是否可以沟通学生、发布套餐。</p>

    <el-table :data="members" v-loading="loading">
      <el-table-column prop="displayName" label="员工" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="沟通学生" width="140">
        <template #default="scope">
          <el-switch :model-value="hasPermission(scope.row.permissions, 'CAN_CHAT_STUDENT')" @change="(val: boolean) => onToggle(scope.row, 'CAN_CHAT_STUDENT', val)" />
        </template>
      </el-table-column>
      <el-table-column label="发布套餐" width="140">
        <template #default="scope">
          <el-switch :model-value="hasPermission(scope.row.permissions, 'CAN_PUBLISH_PACKAGE')" @change="(val: boolean) => onToggle(scope.row, 'CAN_PUBLISH_PACKAGE', val)" />
        </template>
      </el-table-column>
      <el-table-column label="当前权限">
        <template #default="scope">{{ (scope.row.permissions || []).join(' / ') || '-' }}</template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import { listOrgMembers, updateOrgMemberPermissions } from '../services/agency'
import type { AgentPermissionCode, OrgMemberItem } from '../types/agency'

const loading = ref(false)
const members = ref<OrgMemberItem[]>([])

function hasPermission(permissions: AgentPermissionCode[] | undefined, code: AgentPermissionCode) {
  return (permissions || []).includes(code)
}

async function load() {
  loading.value = true
  try {
    members.value = await listOrgMembers()
  } finally {
    loading.value = false
  }
}

async function onToggle(member: OrgMemberItem, code: AgentPermissionCode, enabled: boolean) {
  const set = new Set(member.permissions || [])
  if (enabled) set.add(code)
  else set.delete(code)

  try {
    await updateOrgMemberPermissions(member.memberId, { permissions: Array.from(set) })
    member.permissions = Array.from(set)
    ElMessage.success('权限已更新')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '权限更新失败')
    await load()
  }
}

onMounted(load)
</script>
