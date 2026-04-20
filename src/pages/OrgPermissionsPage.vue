<template>
  <section class="page-card fade-up org-panel">
    <h2 class="section-title">权限管理</h2>
    <p class="section-desc">设置员工功能权限（沟通学生、发布套餐），支持搜索和分页管理。</p>

    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索姓名/手机号/岗位" clearable @clear="onSearch" @keyup.enter="onSearch">
        <template #append>
          <el-button @click="onSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="members" v-loading="loading" row-key="memberId">
      <el-table-column prop="displayName" label="员工" min-width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="jobTitle" label="岗位" width="120" />
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

    <div class="pager">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="load"
        @size-change="onSizeChange"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import { listPermissionMembers, updateOrgMemberPermissions } from '../services/agency'
import type { AgentPermissionCode, OrgMemberItem } from '../types/agency'

const loading = ref(false)
const members = ref<OrgMemberItem[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')

function hasPermission(permissions: AgentPermissionCode[] | undefined, code: AgentPermissionCode) {
  return (permissions || []).includes(code)
}

async function load() {
  loading.value = true
  try {
    const data = await listPermissionMembers({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    })
    members.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.value = 1
  load()
}

function onSizeChange() {
  page.value = 1
  load()
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

<style scoped>
.org-panel {
  animation: page-enter 0.2s ease;
}

@keyframes page-enter {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.toolbar {
  margin-bottom: 14px;
}

.pager {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
</style>
