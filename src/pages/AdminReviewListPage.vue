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

    <el-dialog
      v-model="detailVisible"
      title="审核详情"
      width="calc(100vw - 250px - 48px)"
      class="admin-review-dialog"
      modal-class="admin-review-dialog-modal"
      append-to-body
    >
      <div v-if="detail" class="detail-content">
        <el-descriptions title="基础信息" :column="2" border class="detail-summary">
          <el-descriptions-item label="对象">{{ detail.subjectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.orgName" label="机构">{{ detail.orgName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detail.submittedAt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="驳回原因">{{ detail.rejectReason || '无' }}</el-descriptions-item>
        </el-descriptions>

        <el-alert
          v-if="parsedPayload.error"
          class="parse-alert"
          type="warning"
          show-icon
          :closable="false"
          title="提交内容解析失败，已保留原始 JSON 供排查"
        />

        <el-empty v-if="!detail.payloadJson" description="暂无提交内容" />

        <div v-else-if="!parsedPayload.error" class="review-sections">
          <section v-for="section in reviewSections" :key="section.title" class="review-section">
            <h3>{{ section.title }}</h3>

            <div v-if="section.fields.length" class="field-grid">
              <div v-for="field in section.fields" :key="field.label" class="field-item">
                <span class="field-label">{{ field.label }}</span>
                <span class="field-value">{{ displayText(field.value) }}</span>
              </div>
            </div>

            <div v-if="section.materials.length" class="material-grid">
              <article v-for="material in section.materials" :key="material.label" class="material-card">
                <div class="material-head">
                  <span>{{ material.label }}</span>
                  <span class="material-count">{{ material.urls.length ? `${material.urls.length} 个文件` : '未上传' }}</span>
                </div>

                <div v-if="material.urls.length" class="material-list">
                  <div v-for="url in material.urls" :key="`${material.label}-${url}`" class="material-item">
                    <el-image
                      v-if="isLikelyImageUrl(url)"
                      class="material-image"
                      :src="url"
                      :preview-src-list="material.urls.filter(isLikelyImageUrl)"
                      :initial-index="material.urls.filter(isLikelyImageUrl).indexOf(url)"
                      fit="cover"
                      preview-teleported
                    >
                      <template #error>
                        <a class="image-fallback" :href="url" target="_blank" rel="noreferrer">打开原链接</a>
                      </template>
                    </el-image>
                    <a v-else class="file-link" :href="url" target="_blank" rel="noreferrer">打开原链接</a>
                    <a class="url-link" :href="url" target="_blank" rel="noreferrer">{{ url }}</a>
                  </div>
                </div>
                <div v-else class="empty-material">未上传</div>
              </article>
            </div>
          </section>
        </div>

        <el-collapse v-if="detail.payloadJson" class="raw-collapse">
          <el-collapse-item title="原始提交内容" name="raw-payload">
            <pre class="payload-json">{{ detail.payloadJson || '' }}</pre>
          </el-collapse-item>
        </el-collapse>
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

type ReviewPayload = Record<string, unknown>

interface ReviewField {
  label: string
  value: unknown
}

interface ReviewMaterial {
  label: string
  urls: string[]
}

interface ReviewSection {
  title: string
  fields: ReviewField[]
  materials: ReviewMaterial[]
}

const parsedPayload = computed<{ data: ReviewPayload; error: boolean }>(() => {
  const raw = detail.value?.payloadJson
  if (!raw) {
    return { data: {}, error: false }
  }
  try {
    const parsed = JSON.parse(raw)
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      return { data: parsed as ReviewPayload, error: false }
    }
    return { data: {}, error: true }
  } catch {
    return { data: {}, error: true }
  }
})

const reviewSections = computed<ReviewSection[]>(() => {
  const payload = parsedPayload.value.data
  const subjectType = detail.value?.subjectType || props.subjectType

  if (subjectType === 'ORG') {
    return [
      {
        title: '公司资料',
        fields: [
          field('营业执照号', payload.licenseNo),
          field('法人姓名', payload.legalPersonName),
          field('企业对公账户名', payload.corporateAccountName),
          field('开户行', payload.corporateBankName),
          field('企业对公账号', payload.corporateBankAccountNo),
          field('备注', payload.remark)
        ],
        materials: [
          material('营业执照图片', payload.licenseImageUrl),
          material('法人身份证图片', payload.legalPersonIdImageUrl),
          material('对公账户凭证图', payload.corporateAccountProofImageUrl),
          material('办公环境图片', payload.officeEnvironmentImageUrls),
          material('管理员个人实名认证图片', payload.adminRealNameImageUrl),
          material('管理员在职证明/授权书', payload.adminEmploymentProofImageUrl)
        ]
      }
    ]
  }

  if (subjectType === 'MEMBER') {
    return [
      {
        title: '员工认证材料',
        fields: [],
        materials: [
          material('身份证图片', payload.idCardImageUrl),
          material('在职证明图片', payload.employmentProofImageUrl),
          material('学历证明图片', payload.educationProofImageUrl)
        ]
      }
    ]
  }

  return [
    {
      title: '学生认证资料',
      fields: [
        field('真实姓名', payload.realName),
        field('脱敏身份证号', payload.idNo || payload.idNoMasked)
      ],
      materials: [
        material('身份证图片', payload.idCardImageUrl),
        material('学生证图片', payload.studentCardImageUrl)
      ]
    }
  ]
})

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

function field(label: string, value: unknown): ReviewField {
  return { label, value }
}

function material(label: string, value: unknown): ReviewMaterial {
  return { label, urls: splitUrls(value) }
}

function splitUrls(value: unknown): string[] {
  if (Array.isArray(value)) {
    return value.flatMap(splitUrls)
  }
  if (typeof value !== 'string') {
    return []
  }
  return value
    .split(/[\s,，;；]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

function displayText(value: unknown) {
  if (value === null || value === undefined || value === '') {
    return '未填写'
  }
  if (Array.isArray(value)) {
    return value.length ? value.join('，') : '未填写'
  }
  return String(value)
}

function isLikelyImageUrl(url: string) {
  return /\.(png|jpe?g|webp|gif|bmp|svg)(\?.*)?$/i.test(url)
}

function statusLabel(value: string) {
  const dict: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    UNVERIFIED: '未认证'
  }
  return dict[value] || value || '-'
}

function statusTagType(value: string) {
  if (value === 'APPROVED') return 'success'
  if (value === 'REJECTED') return 'danger'
  if (value === 'PENDING') return 'warning'
  return 'info'
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

.detail-summary {
  margin-bottom: 18px;
}

.parse-alert {
  margin-bottom: 16px;
}

.review-sections {
  display: grid;
  gap: 18px;
}

.review-section {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fff;
}

.review-section h3 {
  margin: 0 0 14px;
  font-size: 17px;
  color: #1f2937;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 16px;
}

.field-item {
  display: grid;
  gap: 4px;
  min-width: 0;
  padding: 10px 12px;
  border: 1px solid #edf0f5;
  border-radius: 8px;
  background: #f8fafc;
}

.field-label {
  color: #6b7280;
  font-size: 13px;
}

.field-value {
  color: #1f2937;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.material-card {
  min-width: 0;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.material-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-bottom: 1px solid #edf0f5;
  color: #1f2937;
  font-weight: 600;
}

.material-count {
  flex: 0 0 auto;
  color: #909399;
  font-size: 12px;
  font-weight: 400;
}

.material-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  padding: 12px;
}

.material-item {
  display: grid;
  gap: 8px;
  min-width: 0;
}

.material-image {
  width: 100%;
  height: 150px;
  border: 1px solid #edf0f5;
  border-radius: 8px;
  overflow: hidden;
  background: #f8fafc;
}

.image-fallback,
.file-link {
  display: grid;
  place-items: center;
  min-height: 96px;
  padding: 12px;
  border: 1px dashed #cfd6e4;
  border-radius: 8px;
  color: #2563eb;
  text-decoration: none;
  background: #f8fafc;
}

.url-link {
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
  word-break: break-all;
}

.empty-material {
  padding: 16px 12px;
  color: #909399;
  font-size: 13px;
}

.raw-collapse {
  margin-top: 18px;
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

  .field-grid,
  .material-grid {
    grid-template-columns: 1fr;
  }
}

:global(.admin-review-dialog-modal) {
  left: 250px;
  right: 0;
  width: auto;
}

:global(.admin-review-dialog-modal .el-overlay-dialog) {
  left: 250px;
  right: 0;
  width: auto;
  overflow-y: auto;
}

:global(.admin-review-dialog) {
  --el-dialog-padding-primary: 28px;
  max-width: calc(100vw - 250px - 48px);
  margin: 24px auto;
  border-radius: 10px;
}

:global(.admin-review-dialog .el-dialog__header) {
  padding: 26px 28px 14px;
  margin-right: 0;
}

:global(.admin-review-dialog .el-dialog__title) {
  font-size: 22px;
  font-weight: 700;
}

:global(.admin-review-dialog .el-dialog__body) {
  padding: 0 28px 28px;
}

@media (max-width: 980px) {
  :global(.admin-review-dialog-modal) {
    left: 0;
    right: 0;
  }

  :global(.admin-review-dialog-modal .el-overlay-dialog) {
    left: 0;
    right: 0;
  }

  :global(.admin-review-dialog) {
    width: calc(100vw - 24px) !important;
    max-width: calc(100vw - 24px);
    margin: 12px auto;
  }

  :global(.admin-review-dialog .el-dialog__body) {
    padding: 0 18px 20px;
  }
}
</style>
