<template>
  <section class="page-card fade-up">
    <h2 class="section-title">通知记录</h2>
    <p class="section-desc">展示平台审核产生的系统通知。</p>

    <el-table :data="rows" v-loading="loading" border>
      <el-table-column prop="userId" label="接收用户" width="110" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>

    <div class="pager">
      <el-pagination
        layout="total, sizes, prev, pager, next"
        :total="total"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[20, 50, 100]"
        @current-change="load"
        @size-change="load"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listAdminNotifications } from '../services/admin'
import { ApiError } from '../services/http'
import type { SystemNotificationItem } from '../types/message'

const loading = ref(false)
const rows = ref<SystemNotificationItem[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

async function load() {
  loading.value = true
  try {
    const res = await listAdminNotifications({ page: page.value, pageSize: pageSize.value })
    rows.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
