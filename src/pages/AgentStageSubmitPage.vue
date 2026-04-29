<template>
  <section class="stage-submit-page fade-up" v-loading="loading">
    <header class="page-head">
      <div>
        <span class="eyebrow">{{ detail?.order.orderNo || '-' }}</span>
        <h2 class="section-title">提交阶段成果</h2>
        <p class="section-desc">上传图片或文件材料，提交后等待学生审核确认。</p>
      </div>
      <el-button @click="backToStudents">返回学生服务</el-button>
    </header>

    <el-empty v-if="!loading && (!detail || !stage)" description="阶段不存在或无权查看">
      <el-button type="primary" @click="backToStudents">返回学生服务</el-button>
    </el-empty>

    <template v-else-if="detail && stage">
      <section class="summary-panel">
        <el-descriptions title="基础信息" :column="2" border>
          <el-descriptions-item label="学生">{{ studentName }}</el-descriptions-item>
          <el-descriptions-item label="服务">{{ detail.order.serviceTitle || detail.order.teamNameSnapshot }}</el-descriptions-item>
          <el-descriptions-item label="阶段">{{ stage.stageOrder }}. {{ stage.stageName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="stageTagType(stage.status)">{{ stageLabel(stage.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学生退回意见" :span="2">{{ stage.studentFeedback || '无' }}</el-descriptions-item>
        </el-descriptions>
      </section>

      <section class="form-panel">
        <el-form label-position="top">
          <el-form-item label="阶段成果说明">
            <el-input v-model="stageForm.deliverableText" type="textarea" :rows="5" maxlength="1000" show-word-limit />
          </el-form-item>
          <el-form-item label="材料链接（可选）">
            <el-input v-model="stageForm.deliverableUrl" placeholder="如有外部网盘、在线文档链接可填写" />
          </el-form-item>
        </el-form>

        <div class="upload-panel">
          <div class="panel-head">
            <div>
              <h3>附件材料</h3>
              <p>支持图片、PDF、Word 等文件，单个文件不超过 10MB。</p>
            </div>
            <div class="upload-actions">
              <el-button :icon="Picture" :loading="stageUploading" @click="stageImageInputRef?.click()">上传图片</el-button>
              <el-button :icon="Document" :loading="stageUploading" @click="stageFileInputRef?.click()">上传文件</el-button>
              <input ref="stageImageInputRef" class="hidden-input" type="file" accept="image/*" @change="onPickStageImage" />
              <input ref="stageFileInputRef" class="hidden-input" type="file" @change="onPickStageFile" />
            </div>
          </div>

          <el-empty v-if="stageForm.attachments.length === 0" description="暂无附件" />
          <div v-else class="attachment-list">
            <article v-for="(attachment, index) in stageForm.attachments" :key="attachment.fileUrl" class="attachment-row">
              <a :href="attachment.fileUrl" target="_blank" rel="noreferrer">
                <el-icon><Picture v-if="attachment.contentType === 'IMAGE'" /><Document v-else /></el-icon>
                <span>{{ attachment.fileName }}</span>
                <small>{{ formatFileSize(attachment.sizeBytes) }}</small>
              </a>
              <el-button :icon="Delete" circle text @click="removeStageAttachment(index)" />
            </article>
          </div>
        </div>
      </section>

      <section class="action-panel">
        <el-button @click="backToStudents">取消</el-button>
        <el-button type="primary" :loading="stageSubmitting" @click="submitStageResult">提交给学生确认</el-button>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Document, Picture } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { uploadFile } from '../services/agency'
import { getAgentServiceOrder, submitStage } from '../services/order'
import type { AgentOrderSummary, OrderDetail, StageAttachment, StageItem } from '../types/order'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'

const route = useRoute()
const router = useRouter()

const orderId = computed(() => Number(route.params.orderId || 0))
const stageId = computed(() => Number(route.params.stageId || 0))
const loading = ref(false)
const stageUploading = ref(false)
const stageSubmitting = ref(false)
const detail = ref<OrderDetail | null>(null)
const stage = ref<StageItem | null>(null)
const stageImageInputRef = ref<HTMLInputElement | null>(null)
const stageFileInputRef = ref<HTMLInputElement | null>(null)

const stageForm = reactive<{
  deliverableText: string
  deliverableUrl: string
  attachments: StageAttachment[]
}>({
  deliverableText: '',
  deliverableUrl: '',
  attachments: []
})

const studentName = computed(() => {
  const order = detail.value?.order as AgentOrderSummary | undefined
  return order?.studentName || order?.studentPhone || (order?.studentUserId ? `学生 #${order.studentUserId}` : '-')
})

function stageLabel(status: string) {
  const dict: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    WAITING_AGENT: '待中介处理',
    WAITING_CONFIRMATION: '待学生确认',
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

function syncStage(next: OrderDetail) {
  detail.value = next
  stage.value = next.stages.find((item) => item.id === stageId.value) || null
  stageForm.deliverableText = stage.value?.deliverableText || ''
  stageForm.deliverableUrl = stage.value?.deliverableUrl || ''
  stageForm.attachments = [...(stage.value?.attachments || [])]
}

async function loadDetail() {
  if (!orderId.value || !stageId.value) return
  loading.value = true
  try {
    syncStage(await getAgentServiceOrder(orderId.value))
  } catch (error: any) {
    ElMessage.error(error?.message || '阶段详情加载失败')
  } finally {
    loading.value = false
  }
}

async function onPickStageImage(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  await uploadStageAttachment(file, 'IMAGE')
}

async function onPickStageFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  await uploadStageAttachment(file, file.type.startsWith('image/') ? 'IMAGE' : 'FILE')
}

async function uploadStageAttachment(file: File, contentType: 'IMAGE' | 'FILE') {
  if (!validateUploadFileSize(file)) return
  stageUploading.value = true
  try {
    const data = await uploadFile(file, 'service-stage')
    stageForm.attachments.push({
      fileName: file.name,
      fileUrl: data.url,
      contentType,
      mimeType: file.type || null,
      sizeBytes: file.size
    })
    ElMessage.success('附件已上传')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error, '附件上传失败'))
  } finally {
    stageUploading.value = false
  }
}

function removeStageAttachment(index: number) {
  stageForm.attachments.splice(index, 1)
}

async function submitStageResult() {
  if (!detail.value || !stage.value || !stageForm.deliverableText.trim()) {
    ElMessage.warning('请填写阶段成果')
    return
  }
  stageSubmitting.value = true
  try {
    await submitStage(detail.value.order.id, stage.value.id, {
      deliverableText: stageForm.deliverableText.trim(),
      deliverableUrl: stageForm.deliverableUrl.trim(),
      attachments: stageForm.attachments
    })
    ElMessage.success('已提交给学生确认')
    backToStudents()
  } catch (error: any) {
    ElMessage.error(error?.message || '提交阶段成果失败')
  } finally {
    stageSubmitting.value = false
  }
}

function formatFileSize(size?: number | null) {
  if (!size) return ''
  if (size < 1024) return `${size}B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
  return `${(size / 1024 / 1024).toFixed(1)}MB`
}

function backToStudents() {
  router.push({ path: '/agent-workbench/students', query: orderId.value ? { orderId: orderId.value } : undefined })
}

loadDetail()
</script>

<style scoped>
.stage-submit-page {
  max-width: 1120px;
  margin: 0 auto;
}

.page-head,
.panel-head,
.upload-actions,
.action-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.eyebrow {
  font-size: 12px;
  color: #7b8b96;
}

.summary-panel,
.form-panel,
.upload-panel,
.action-panel {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
  padding: 16px;
  margin-top: 14px;
}

.upload-panel {
  margin-top: 0;
}

.panel-head h3 {
  margin: 0;
}

.panel-head p {
  margin: 6px 0 0;
  color: #7b8b96;
}

.attachment-list {
  display: grid;
  gap: 8px;
  margin-top: 12px;
}

.attachment-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  border: 1px solid #dfeaf1;
  border-radius: 8px;
  padding: 8px 10px;
  background: #fbfdff;
}

.attachment-row a {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #285c7a;
  text-decoration: none;
}

.attachment-row span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attachment-row small {
  color: #7b8b96;
  white-space: nowrap;
}

.hidden-input {
  display: none;
}

.action-panel {
  justify-content: flex-end;
}

@media (max-width: 760px) {
  .page-head,
  .panel-head,
  .action-panel {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
