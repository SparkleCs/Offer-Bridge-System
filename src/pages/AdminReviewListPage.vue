<template>
  <section class="page-card fade-up">
    <div class="head">
      <h2 class="section-title">{{ title }}</h2>
      <div class="toolbar">
        <el-input v-model="keyword" :placeholder="keywordPlaceholder" clearable class="kw" @keyup.enter="load" />
        <el-select v-model="status" clearable placeholder="状态" class="st" @change="load">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="load" :loading="loading">搜索</el-button>
      </div>
    </div>

    <el-table :data="rows" v-loading="loading" border>
      <el-table-column prop="subjectName" label="对象" min-width="130" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column v-if="!isStudentReview" prop="orgName" label="机构" min-width="120" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="submittedAt" label="提交时间" min-width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="approve(row)">通过</el-button>
          <el-button size="small" type="danger" @click="reject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        layout="total, sizes, prev, pager, next"
        :total="total"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="load"
        @size-change="load"
      />
    </div>

    <el-dialog v-model="detailVisible" title="审核详情" fullscreen>
      <div v-if="detail" class="detail-content">
        <p><b>对象：</b>{{ detail.subjectName }}（{{ detail.phone }}）</p>
        <p><b>状态：</b>{{ detail.status }}</p>
        <p><b>驳回原因：</b>{{ detail.rejectReason || '无' }}</p>
        <p><b>提交时间：</b>{{ detail.submittedAt || '-' }}</p>
        <div class="payload-block">
          <p class="payload-title"><b>提交内容：</b></p>
          <pre class="payload-json">{{ detail.payloadJson || '' }}</pre>
        </div>
      </div>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ApiError } from '../services/http'
import {
  approveAdminReview,
  getAdminReviewDetail,
  listAdminMemberReviews,
  listAdminOrgReviews,
  listAdminStudentReviews,
  rejectAdminReview
} from '../services/admin'
import type { AdminReviewDetail, AdminReviewListItem, ReviewSubjectType } from '../types/admin'

const props = defineProps<{
  subjectType: ReviewSubjectType
  title: string
}>()

const isStudentReview = computed(() => props.subjectType === 'STUDENT')
const keywordPlaceholder = computed(() => (isStudentReview.value ? '搜索姓名/手机号' : '搜索姓名/手机号/机构'))

const loading = ref(false)
const rows = ref<AdminReviewListItem[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const status = ref('PENDING')
const keyword = ref('')

const detailVisible = ref(false)
const detail = ref<AdminReviewDetail | null>(null)

async function load() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value, status: status.value, keyword: keyword.value }
    const result = props.subjectType === 'ORG'
      ? await listAdminOrgReviews(params)
      : props.subjectType === 'MEMBER'
        ? await listAdminMemberReviews(params)
        : await listAdminStudentReviews(params)
    rows.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

async function openDetail(row: AdminReviewListItem) {
  try {
    detail.value = await getAdminReviewDetail(props.subjectType, row.userId)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '详情加载失败')
  }
}

async function approve(row: AdminReviewListItem) {
  try {
    await approveAdminReview(props.subjectType, row.userId)
    ElMessage.success('已通过')
    load()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function reject(row: AdminReviewListItem) {
  try {
    const reason = await ElMessageBox.prompt('请输入驳回原因', '驳回审核', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '驳回原因不能为空'
    }) as { value: string }
    await rejectAdminReview(props.subjectType, row.userId, reason.value)
    ElMessage.success('已驳回')
    load()
  } catch {
    // cancelled
  }
}

onMounted(load)
</script>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
}

.kw {
  width: 240px;
}

.st {
  width: 140px;
}

.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  max-height: calc(100vh - 180px);
  overflow: auto;
}

.payload-block {
  margin-top: 10px;
}

.payload-title {
  margin-bottom: 8px;
}

.payload-json {
  margin: 0;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background: #f8fafc;
  color: #303133;
  font-size: 13px;
  line-height: 1.5;
  max-height: 55vh;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  user-select: text;
}

@media (max-width: 980px) {
  .head {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar {
    width: 100%;
    flex-wrap: wrap;
  }
}
</style>
