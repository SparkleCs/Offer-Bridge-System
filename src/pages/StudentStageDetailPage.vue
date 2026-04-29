<template>
  <section class="stage-detail-page fade-up" v-loading="loading">
    <header class="page-head">
      <div>
        <span class="eyebrow">ORDER {{ detail?.order.orderNo || '-' }}</span>
        <h2 class="section-title">阶段材料详情</h2>
        <p class="section-desc">查看中介提交的阶段成果，确认无误后通过，或填写意见退回修改。</p>
      </div>
      <el-button @click="backToOrders">返回订单</el-button>
    </header>

    <el-empty v-if="!loading && (!detail || !stage)" description="阶段不存在或无权查看">
      <el-button type="primary" @click="backToOrders">返回订单</el-button>
    </el-empty>

    <template v-else-if="detail && stage">
      <section class="summary-panel">
        <el-descriptions title="基础信息" :column="2" border>
          <el-descriptions-item label="服务">{{ detail.order.serviceTitle || detail.order.teamNameSnapshot }}</el-descriptions-item>
          <el-descriptions-item label="机构">{{ detail.order.orgNameSnapshot }}</el-descriptions-item>
          <el-descriptions-item label="阶段">{{ stage.stageOrder }}. {{ stage.stageName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="stageTagType(stage.status)">{{ stageLabel(stage.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ stage.submittedAt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ stage.completedAt || '-' }}</el-descriptions-item>
        </el-descriptions>
      </section>

      <section class="content-panel">
        <div class="panel-head">
          <h3>成果说明</h3>
        </div>
        <p class="deliverable">{{ stage.deliverableText || '中介尚未提交成果说明。' }}</p>
        <a v-if="stage.deliverableUrl" :href="stage.deliverableUrl" target="_blank" rel="noreferrer" class="legacy-link">
          打开材料链接
        </a>
        <p v-if="stage.studentFeedback" class="feedback">上次退回意见：{{ stage.studentFeedback }}</p>
      </section>

      <section class="content-panel">
        <div class="panel-head">
          <h3>提交材料</h3>
          <span>{{ stage.attachments?.length || 0 }} 个文件</span>
        </div>
        <el-empty v-if="!stage.attachments?.length" description="暂无附件材料" />
        <div v-else class="material-grid">
          <article
            v-for="attachment in stage.attachments"
            :key="attachment.id || attachment.fileUrl"
            class="material-card"
            :class="{ image: attachment.contentType === 'IMAGE' }"
          >
            <el-image
              v-if="attachment.contentType === 'IMAGE'"
              class="material-image"
              :src="attachment.fileUrl"
              :preview-src-list="stageImageUrls"
              fit="cover"
              preview-teleported
            />
            <a v-else class="file-preview" :href="attachment.fileUrl" target="_blank" rel="noreferrer">
              <el-icon><Document /></el-icon>
            </a>
            <div class="material-meta">
              <a :href="attachment.fileUrl" target="_blank" rel="noreferrer">{{ attachment.fileName }}</a>
              <span>{{ formatFileSize(attachment.sizeBytes) }}</span>
            </div>
          </article>
        </div>
      </section>

      <section v-if="stage.status === 'WAITING_CONFIRMATION'" class="action-panel">
        <div>
          <h3>审核操作</h3>
          <p>确认前请先查看全部材料；退回时请写清楚需要调整的地方。</p>
        </div>
        <el-input
          v-model="rejectFeedback"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="退回修改意见"
        />
        <div class="action-row">
          <el-button type="success" :loading="confirming" @click="confirmCurrentStage">确认通过</el-button>
          <el-button type="warning" :loading="rejecting" @click="rejectCurrentStage">退回修改</el-button>
        </div>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmStage, getMyOrder, rejectStage } from '../services/order'
import type { OrderDetail, StageItem } from '../types/order'

const route = useRoute()
const router = useRouter()

const orderId = computed(() => Number(route.params.orderId || 0))
const stageId = computed(() => Number(route.params.stageId || 0))
const loading = ref(false)
const confirming = ref(false)
const rejecting = ref(false)
const detail = ref<OrderDetail | null>(null)
const stage = ref<StageItem | null>(null)
const rejectFeedback = ref('')

const stageImageUrls = computed(() => (stage.value?.attachments || [])
  .filter((attachment) => attachment.contentType === 'IMAGE')
  .map((attachment) => attachment.fileUrl))

function stageLabel(status: string) {
  const dict: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    WAITING_STUDENT: '等待学生',
    WAITING_AGENT: '等待中介',
    WAITING_CONFIRMATION: '待确认',
    COMPLETED: '已完成'
  }
  return dict[status] || status
}

function stageTagType(status: string) {
  if (status === 'COMPLETED') return 'success'
  if (status === 'WAITING_CONFIRMATION') return 'warning'
  if (status === 'WAITING_AGENT') return 'danger'
  return 'info'
}

function formatFileSize(size?: number | null) {
  if (!size) return ''
  if (size < 1024) return `${size}B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
  return `${(size / 1024 / 1024).toFixed(1)}MB`
}

function syncStage(next: OrderDetail) {
  detail.value = next
  stage.value = next.stages.find((item) => item.id === stageId.value) || null
}

async function loadDetail() {
  if (!orderId.value || !stageId.value) return
  loading.value = true
  try {
    syncStage(await getMyOrder(orderId.value))
  } catch (error: any) {
    ElMessage.error(error?.message || '阶段详情加载失败')
  } finally {
    loading.value = false
  }
}

async function confirmCurrentStage() {
  if (!detail.value || !stage.value) return
  confirming.value = true
  try {
    syncStage(await confirmStage(detail.value.order.id, stage.value.id))
    ElMessage.success('阶段已确认')
    backToOrders()
  } catch (error: any) {
    ElMessage.error(error?.message || '确认失败')
  } finally {
    confirming.value = false
  }
}

async function rejectCurrentStage() {
  if (!detail.value || !stage.value || !rejectFeedback.value.trim()) {
    ElMessage.warning('请填写退回意见')
    return
  }
  rejecting.value = true
  try {
    syncStage(await rejectStage(detail.value.order.id, stage.value.id, rejectFeedback.value.trim()))
    ElMessage.success('已退回中介修改')
    backToOrders()
  } catch (error: any) {
    ElMessage.error(error?.message || '退回失败')
  } finally {
    rejecting.value = false
  }
}

function backToOrders() {
  router.push({ path: '/orders', query: orderId.value ? { orderId: orderId.value } : undefined })
}

loadDetail()
</script>

<style scoped>
.stage-detail-page {
  max-width: 1120px;
  margin: 0 auto;
}

.page-head,
.panel-head,
.action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.eyebrow,
.panel-head span {
  font-size: 12px;
  color: #7b8b96;
}

.summary-panel,
.content-panel,
.action-panel {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
  padding: 16px;
  margin-top: 14px;
}

.panel-head h3,
.action-panel h3 {
  margin: 0;
}

.deliverable {
  color: #506579;
  line-height: 1.7;
}

.legacy-link {
  color: #0f70c8;
}

.feedback {
  margin-top: 10px;
  color: #b14b38;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.material-card {
  min-width: 0;
  border: 1px solid #dfeaf1;
  border-radius: 8px;
  background: #fbfdff;
  padding: 10px;
}

.material-image {
  width: 100%;
  height: 150px;
  border-radius: 6px;
  display: block;
  background: #edf4f8;
}

.file-preview {
  height: 110px;
  border-radius: 6px;
  display: grid;
  place-items: center;
  background: #eef6fb;
  color: #285c7a;
  font-size: 34px;
  text-decoration: none;
}

.material-meta {
  min-width: 0;
  margin-top: 8px;
  display: grid;
  gap: 3px;
}

.material-meta a {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #0f70c8;
  text-decoration: none;
}

.material-meta span,
.action-panel p {
  color: #7b8b96;
}

.action-panel {
  display: grid;
  gap: 12px;
}

.action-row {
  justify-content: flex-end;
}

@media (max-width: 760px) {
  .page-head,
  .panel-head {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
