<template>
  <section class="products-page fade-up">
    <header class="header-card">
      <div>
        <div class="title-row">
          <h2 class="section-title">团队产品管理</h2>
          <span class="mode-pill" :class="`mode-${editorMode}`">{{ editorModeLabel }}</span>
        </div>
        <p class="section-desc">创建、编辑并发布团队套餐，发布前可实时预览前台展示效果。</p>
      </div>
      <el-button type="primary" class="new-btn" @click="onCreateProduct" :disabled="isBlocked">新建套餐</el-button>
    </header>

    <el-alert v-if="blockedReason" :title="blockedReason" type="warning" :closable="false" show-icon class="gate-alert" />

    <div v-else class="workspace">
      <aside class="left-panel glass-panel" v-loading="loadingList">
        <el-empty v-if="products.length === 0" description="暂无套餐，点击右上角创建" />
        <button
          v-for="item in products"
          :key="item.teamId"
          class="product-item"
          :class="{ active: selectedTeamId === item.teamId }"
          @click="onSelectProduct(item.teamId)"
        >
          <div class="row-top">
            <strong class="product-title">{{ item.teamName }}</strong>
            <el-tag :type="item.publishStatus === 'PUBLISHED' ? 'success' : 'info'" size="small">{{ item.publishStatus === 'PUBLISHED' ? '已发布' : '草稿' }}</el-tag>
          </div>
          <p class="type-line"><span class="type-pill">{{ item.teamType || '未设置类型' }}</span></p>
          <div class="row-meta">
            <span class="meta-cell">
              <em class="meta-label">价格</em>
              <strong class="meta-value price-value">{{ formatPrice(item.priceMin, item.priceMax) }}</strong>
            </span>
            <span class="meta-cell">
              <em class="meta-label">更新时间</em>
              <strong class="meta-value">{{ item.updatedAt || '-' }}</strong>
            </span>
          </div>
        </button>
      </aside>

      <main class="main-panel glass-panel" v-loading="loadingDetail">
        <el-empty v-if="editorMode === 'view'" description="请选择或创建一个套餐" />

        <template v-else>
          <el-form ref="formRef" :model="form" label-position="top" class="editor-form">
            <el-row :gutter="14">
              <el-col :span="12">
                <el-form-item label="套餐名称" required>
                  <el-input v-model="form.teamName" maxlength="120" show-word-limit />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="套餐类型">
                  <el-input v-model="form.teamType" placeholder="如：综合组/冲刺组" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="服务国家" required>
                  <el-input v-model="form.serviceCountryScope" placeholder="如：美国,英国" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="服务方向" required>
                  <el-input v-model="form.serviceMajorScope" placeholder="如：计算机,商科" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="最低价（CNY）" required>
                  <el-input-number v-model="form.priceMin" :min="0" :precision="2" :step="500" :controls="false" class="full" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最高价（CNY）" required>
                  <el-input-number v-model="form.priceMax" :min="0" :precision="2" :step="500" :controls="false" class="full" />
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="套餐介绍">
                  <el-input v-model="form.teamIntro" type="textarea" :rows="4" maxlength="500" show-word-limit />
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="负责成员（用于前台展示）" required>
                  <div class="member-picker">
                    <div class="member-tags">
                      <el-tag v-for="member in selectedMemberObjects" :key="member.memberId" closable @close="removeMember(member.memberId)">
                        {{ member.displayName }}
                      </el-tag>
                      <span v-if="selectedMemberObjects.length === 0" class="placeholder">未选择负责成员</span>
                    </div>
                    <el-button @click="memberDialogVisible = true">选择成员</el-button>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>

          <div class="editor-actions">
            <el-button @click="cancelEditing">{{ editorMode === 'creating' ? '取消新建' : '撤销修改' }}</el-button>
            <el-button type="primary" @click="saveProduct" :loading="saving">保存草稿</el-button>
            <el-button type="success" @click="publishProduct" :loading="publishing">发布套餐</el-button>
          </div>

          <section class="preview-card">
            <div class="preview-head">
              <h3>{{ form.teamName || '未命名套餐' }}</h3>
              <span class="price">{{ formatPrice(form.priceMin, form.priceMax) }}</span>
            </div>
            <div class="preview-tags">
              <span class="tag">{{ form.serviceCountryScope || '未填写国家' }}</span>
              <span class="tag">{{ form.serviceMajorScope || '未填写方向' }}</span>
              <span class="tag">中介套餐预览</span>
            </div>
            <p class="preview-intro">{{ form.teamIntro || '该套餐暂无详细介绍。' }}</p>
            <div class="preview-members">
              <article v-for="member in selectedMemberObjects" :key="member.memberId" class="preview-member">
                <strong>{{ member.displayName }}</strong>
                <span>{{ roleLabel(member.roleCode) }}</span>
                <small v-if="displayJobTitle(member)">{{ displayJobTitle(member) }}</small>
              </article>
              <p v-if="selectedMemberObjects.length === 0" class="placeholder">发布前请至少选择 1 位负责成员。</p>
            </div>
          </section>
        </template>
      </main>
    </div>

    <el-dialog
      v-model="memberDialogVisible"
      title="选择负责成员"
      width="720px"
      :before-close="handleMemberDialogBeforeClose"
      @closed="handleMemberDialogClosed"
    >
      <div class="dialog-toolbar">
        <el-input v-model="memberKeyword" placeholder="按姓名/岗位搜索" clearable @keyup.enter="onMemberSearch" />
        <el-button @click="onMemberSearch" :loading="loadingMembers">搜索</el-button>
      </div>
      <el-table
        :data="orgMembers"
        :empty-text="memberEmptyText"
        :row-key="(row: TeamProductOrgMemberItem) => row.memberId"
        v-loading="loadingMembers"
        max-height="420"
        @selection-change="onMemberSelectionChange"
      >
        <el-table-column type="selection" width="52" :reserve-selection="true" :selectable="() => true" />
        <el-table-column prop="displayName" label="成员" min-width="150" />
        <el-table-column label="岗位" min-width="170">
          <template #default="scope">{{ displayJobTitle(scope.row) || '-' }}</template>
        </el-table-column>
        <el-table-column prop="roleCode" label="服务角色" min-width="140">
          <template #default="scope">{{ roleLabel(scope.row.roleCode) }}</template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="closeMemberDialog">取消</el-button>
        <el-button type="primary" @click="confirmMemberSelection">确定</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { formatCompactPrice } from '../utils/price'
import {
  createTeamProduct,
  getMyWorkbenchAccess,
  getTeamProduct,
  listTeamProductOrgMembers,
  listTeamProducts,
  publishTeamProduct,
  updateTeamProduct
} from '../services/agency'
import type {
  MemberWorkbenchAccess,
  TeamProductDetailView,
  TeamProductOrgMemberItem,
  TeamProductSummaryItem,
  TeamProductUpsertPayload
} from '../types/agency'

type EditorMode = 'view' | 'edit-existing' | 'creating'

interface DraftSnapshot {
  selectedTeamId: number | null
  editingTeamId: number | null
  form: TeamProductUpsertPayload
}

const access = ref<MemberWorkbenchAccess | null>(null)
const loadingList = ref(false)
const loadingDetail = ref(false)
const saving = ref(false)
const publishing = ref(false)

const products = ref<TeamProductSummaryItem[]>([])
const selectedTeamId = ref<number | null>(null)
const editingTeamId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const editorMode = ref<EditorMode>('view')
const draftSnapshot = ref<DraftSnapshot | null>(null)

const form = reactive<TeamProductUpsertPayload>({
  teamName: '',
  teamType: '',
  teamIntro: '',
  serviceCountryScope: '',
  serviceMajorScope: '',
  priceMin: 0,
  priceMax: 0,
  publisherMemberIds: []
})

const orgMembers = ref<TeamProductOrgMemberItem[]>([])
const allOrgMembers = ref<TeamProductOrgMemberItem[]>([])
const memberLookup = ref<Record<number, TeamProductOrgMemberItem>>({})
const memberKeyword = ref('')
const loadingMembers = ref(false)
const memberDialogVisible = ref(false)
const pendingMemberIds = ref<number[]>([])
const membersLoadedOnce = ref(false)
const memberLoadRequestId = ref(0)
const activeMemberLoadRequestId = ref(0)
const MEMBER_LOAD_TIMEOUT_MS = 10000

const memberEmptyText = computed(() => {
  if (loadingMembers.value) return '加载中...'
  return memberKeyword.value.trim() ? '未匹配到已认证员工' : '暂无已认证员工'
})

const blockedReason = computed(() => {
  if (!access.value) return '正在检查权限...'
  if (access.value.blockedReason) return access.value.blockedReason
  if (!access.value.canPublishPackage) return '你当前没有发布套餐权限，请联系管理员。'
  return ''
})

const isBlocked = computed(() => !!blockedReason.value)
const editorModeLabel = computed(() => {
  if (editorMode.value === 'creating') return '新建模式'
  if (editorMode.value === 'edit-existing') return '编辑模式'
  return '浏览模式'
})

const selectedMemberObjects = computed(() => {
  return form.publisherMemberIds.map((id) => memberLookup.value[id]).filter(Boolean) as TeamProductOrgMemberItem[]
})

watch(memberDialogVisible, (visible) => {
  if (visible) {
    pendingMemberIds.value = [...form.publisherMemberIds]
    if (membersLoadedOnce.value) {
      applyMemberFilter()
      return
    }
    fetchAllOrgMembers()
  }
})

function resetMemberDialogLoadingState() {
  activeMemberLoadRequestId.value = 0
  loadingMembers.value = false
}

function closeMemberDialog() {
  resetMemberDialogLoadingState()
  pendingMemberIds.value = [...form.publisherMemberIds]
  memberDialogVisible.value = false
}

function handleMemberDialogBeforeClose(done: () => void) {
  closeMemberDialog()
  done()
}

function handleMemberDialogClosed() {
  resetMemberDialogLoadingState()
}

function shouldApplyMemberLoadResult(requestId: number) {
  return memberDialogVisible.value && activeMemberLoadRequestId.value === requestId
}

function includesKeyword(source: string | null | undefined, keyword: string) {
  return (source || '').toLowerCase().includes(keyword)
}

function applyMemberFilter() {
  const keyword = memberKeyword.value.trim().toLowerCase()
  if (!keyword) {
    orgMembers.value = [...allOrgMembers.value]
    return
  }
  orgMembers.value = allOrgMembers.value.filter((member) => (
    includesKeyword(member.displayName, keyword) || includesKeyword(member.jobTitle, keyword)
  ))
}

function roleLabel(code?: string | null) {
  if (!code) return '未设置'
  const dict: Record<string, string> = {
    CONSULTANT: '咨询顾问',
    PLANNER: '规划顾问',
    WRITER: '文书顾问',
    APPLY_SPECIALIST: '申请专员',
    VISA_SPECIALIST: '签证专员',
    AFTERCARE: '后续服务'
  }
  return dict[code] || code
}

function normalizeDisplayText(value?: string | null) {
  return (value || '').trim().replace(/\s+/g, '').toLowerCase()
}

function displayJobTitle(member: { jobTitle?: string | null; roleCode?: string | null }) {
  const title = member.jobTitle?.trim()
  if (!title) return ''
  if (normalizeDisplayText(title) === normalizeDisplayText(roleLabel(member.roleCode))) return ''
  return title
}

function formatPrice(min?: number | null, max?: number | null) {
  return formatCompactPrice(min, max)
}

function validateForm() {
  if (!form.teamName.trim()) throw new Error('请填写套餐名称')
  if (!form.serviceCountryScope.trim()) throw new Error('请填写服务国家')
  if (!form.serviceMajorScope.trim()) throw new Error('请填写服务方向')
  if (form.priceMin < 0 || form.priceMax < 0) throw new Error('价格不能小于 0')
  if (form.priceMin > form.priceMax) throw new Error('最低价不能高于最高价')
}

function fillFormFromDetail(detail: TeamProductDetailView) {
  form.teamName = detail.teamName || ''
  form.teamType = detail.teamType || ''
  form.teamIntro = detail.teamIntro || ''
  form.serviceCountryScope = detail.serviceCountryScope || ''
  form.serviceMajorScope = detail.serviceMajorScope || ''
  form.priceMin = Number(detail.priceMin || 0)
  form.priceMax = Number(detail.priceMax || 0)
  form.publisherMemberIds = detail.publisherMembers?.map((m) => m.memberId) || []
  const nextLookup: Record<number, TeamProductOrgMemberItem> = {}
  detail.publisherMembers?.forEach((member) => {
    nextLookup[member.memberId] = {
      memberId: member.memberId,
      displayName: member.displayName,
      avatarUrl: member.avatarUrl,
      jobTitle: member.jobTitle,
      roleCode: member.roleCode,
      verifiedBadgeStatus: ''
    }
  })
  memberLookup.value = { ...memberLookup.value, ...nextLookup }
}

function resetForm() {
  form.teamName = ''
  form.teamType = ''
  form.teamIntro = ''
  form.serviceCountryScope = ''
  form.serviceMajorScope = ''
  form.priceMin = 0
  form.priceMax = 0
  form.publisherMemberIds = []
}

function buildPayload(): TeamProductUpsertPayload {
  return {
    ...form,
    teamName: form.teamName.trim(),
    teamType: form.teamType?.trim() || '',
    teamIntro: form.teamIntro?.trim() || '',
    serviceCountryScope: form.serviceCountryScope.trim(),
    serviceMajorScope: form.serviceMajorScope.trim(),
    publisherMemberIds: [...form.publisherMemberIds]
  }
}

function snapshotCurrentContext() {
  draftSnapshot.value = {
    selectedTeamId: selectedTeamId.value,
    editingTeamId: editingTeamId.value,
    form: buildPayload()
  }
}

function restoreSnapshotContext() {
  const snapshot = draftSnapshot.value
  if (!snapshot) {
    selectedTeamId.value = null
    editingTeamId.value = null
    editorMode.value = 'view'
    resetForm()
    return
  }
  selectedTeamId.value = snapshot.selectedTeamId
  editingTeamId.value = snapshot.editingTeamId
  form.teamName = snapshot.form.teamName
  form.teamType = snapshot.form.teamType || ''
  form.teamIntro = snapshot.form.teamIntro || ''
  form.serviceCountryScope = snapshot.form.serviceCountryScope
  form.serviceMajorScope = snapshot.form.serviceMajorScope
  form.priceMin = snapshot.form.priceMin
  form.priceMax = snapshot.form.priceMax
  form.publisherMemberIds = [...snapshot.form.publisherMemberIds]
  editorMode.value = snapshot.editingTeamId ? 'edit-existing' : 'view'
}

async function loadAccessAndList() {
  access.value = await getMyWorkbenchAccess().catch(() => null)
  if (blockedReason.value) return
  await loadProducts()
}

async function loadProducts() {
  loadingList.value = true
  try {
    products.value = await listTeamProducts()
    if (products.value.length === 0) {
      selectedTeamId.value = null
      editingTeamId.value = null
      editorMode.value = 'view'
      return
    }
    const firstId = selectedTeamId.value && products.value.some((it) => it.teamId === selectedTeamId.value)
      ? selectedTeamId.value
      : products.value[0].teamId
    await onSelectProduct(firstId)
  } catch (error: any) {
    ElMessage.error(error?.message || '套餐列表加载失败')
  } finally {
    loadingList.value = false
  }
}

async function fetchAllOrgMembers() {
  const requestId = ++memberLoadRequestId.value
  activeMemberLoadRequestId.value = requestId
  loadingMembers.value = true
  try {
    const loaded = await withTimeout(
      listTeamProductOrgMembers(),
      MEMBER_LOAD_TIMEOUT_MS
    )
    if (!shouldApplyMemberLoadResult(requestId)) return
    allOrgMembers.value = loaded
    membersLoadedOnce.value = true
    const lookup = { ...memberLookup.value }
    loaded.forEach((member) => {
      lookup[member.memberId] = member
    })
    memberLookup.value = lookup
    applyMemberFilter()
  } catch (error: any) {
    if (!shouldApplyMemberLoadResult(requestId)) return
    ElMessage.error(resolveMemberLoadError(error))
  } finally {
    if (activeMemberLoadRequestId.value === requestId) {
      loadingMembers.value = false
    }
  }
}

function onMemberSearch() {
  if (!membersLoadedOnce.value) {
    fetchAllOrgMembers()
    return
  }
  applyMemberFilter()
}

function resolveMemberLoadError(error: any) {
  if (error?.code === 'BIZ_TIMEOUT') {
    return '成员加载超时，请检查网络后重试'
  }
  if (error?.code === 'BIZ_UNAUTHORIZED' || error?.status === 401) {
    return '登录状态已失效，请重新登录后重试'
  }
  if (typeof error?.message === 'string' && error.message.trim()) {
    return `成员加载失败：${error.message.trim()}`
  }
  return '成员加载失败，请稍后重试'
}

async function withTimeout<T>(promise: Promise<T>, timeoutMs: number): Promise<T> {
  let timer: number | undefined
  try {
    return await Promise.race([
      promise,
      new Promise<T>((_, reject) => {
        timer = window.setTimeout(() => {
          reject({ code: 'BIZ_TIMEOUT' })
        }, timeoutMs)
      })
    ])
  } finally {
    if (timer !== undefined) window.clearTimeout(timer)
  }
}

async function onSelectProduct(teamId: number) {
  selectedTeamId.value = teamId
  editingTeamId.value = teamId
  editorMode.value = 'edit-existing'
  loadingDetail.value = true
  try {
    const detail = await getTeamProduct(teamId)
    fillFormFromDetail(detail)
  } catch (error: any) {
    ElMessage.error(error?.message || '套餐详情加载失败')
  } finally {
    loadingDetail.value = false
  }
}

async function onCreateProduct() {
  if (editorMode.value !== 'creating') {
    snapshotCurrentContext()
  }
  editingTeamId.value = null
  selectedTeamId.value = null
  editorMode.value = 'creating'
  resetForm()
}

async function reloadCurrent() {
  if (!editingTeamId.value) return
  await onSelectProduct(editingTeamId.value)
}

async function cancelEditing() {
  if (editorMode.value === 'creating') {
    restoreSnapshotContext()
    draftSnapshot.value = null
    return
  }
  if (editingTeamId.value) {
    await reloadCurrent()
    return
  }
  editorMode.value = 'view'
}

async function saveProduct() {
  try {
    validateForm()
  } catch (error: any) {
    ElMessage.warning(error?.message || '请检查表单')
    return
  }

  saving.value = true
  try {
    const payload = buildPayload()

    if (editorMode.value === 'edit-existing' && editingTeamId.value) {
      await updateTeamProduct(editingTeamId.value, payload)
      ElMessage.success('草稿已保存')
    } else {
      const created = await createTeamProduct(payload)
      editingTeamId.value = created.teamId
      selectedTeamId.value = created.teamId
      editorMode.value = 'edit-existing'
      draftSnapshot.value = null
      ElMessage.success('套餐草稿已创建')
    }

    await loadProducts()
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function publishProduct() {
  try {
    validateForm()
    if (form.publisherMemberIds.length === 0) {
      ElMessage.warning('发布前请至少选择 1 位负责成员')
      return
    }
  } catch (error: any) {
    ElMessage.warning(error?.message || '请检查表单')
    return
  }

  try {
    await ElMessageBox.confirm('确认发布该套餐？发布后会在前台可见。', '发布确认', { type: 'warning' })
  } catch {
    return
  }

  publishing.value = true
  try {
    let targetTeamId = editingTeamId.value
    const payload = buildPayload()
    if (editorMode.value === 'creating' || !targetTeamId) {
      const created = await createTeamProduct(payload)
      targetTeamId = created.teamId
      editingTeamId.value = targetTeamId
      selectedTeamId.value = targetTeamId
      editorMode.value = 'edit-existing'
      draftSnapshot.value = null
    } else {
      await updateTeamProduct(targetTeamId, payload)
    }
    await publishTeamProduct(targetTeamId)
    ElMessage.success('套餐已发布')
    await loadProducts()
  } catch (error: any) {
    ElMessage.error(error?.message || '发布失败')
  } finally {
    publishing.value = false
  }
}

function removeMember(memberId: number) {
  form.publisherMemberIds = form.publisherMemberIds.filter((id) => id !== memberId)
}

function onMemberSelectionChange(rows: TeamProductOrgMemberItem[]) {
  pendingMemberIds.value = rows.map((row) => row.memberId)
}

function confirmMemberSelection() {
  form.publisherMemberIds = [...pendingMemberIds.value]
  closeMemberDialog()
}

loadAccessAndList()
</script>

<style scoped>
.products-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
  --panel-border: #d6e8f4;
  --panel-shadow: 0 16px 36px rgba(17, 63, 97, 0.1);
}

.header-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--panel-border);
  border-radius: 18px;
  padding: 16px;
  background:
    radial-gradient(circle at 80% 18%, rgba(126, 203, 254, 0.24), rgba(126, 203, 254, 0)),
    linear-gradient(130deg, #f9feff, #edf8ff);
  box-shadow: var(--panel-shadow);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.mode-pill {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.03em;
  border-radius: 999px;
  padding: 4px 10px;
  border: 1px solid transparent;
}

.mode-creating {
  color: #0f6d52;
  background: #e7f8f1;
  border-color: #97dbbe;
}

.mode-edit-existing {
  color: #1660a6;
  background: #ecf6ff;
  border-color: #abd0f5;
}

.mode-view {
  color: #5b6f80;
  background: #f1f5f8;
  border-color: #d2dde7;
}

.new-btn {
  border-radius: 12px;
  box-shadow: 0 10px 20px rgba(28, 117, 193, 0.3);
}

.gate-alert {
  margin-top: 6px;
}

.workspace {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 12px;
}

.left-panel,
.main-panel {
  border: 1px solid var(--panel-border);
  border-radius: 16px;
  background: #ffffff;
  padding: 12px;
}

.glass-panel {
  box-shadow: var(--panel-shadow);
}

.left-panel {
  max-height: calc(100vh - 220px);
  overflow: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-item {
  border: 1px solid #dfebf4;
  border-radius: 12px;
  padding: 12px;
  text-align: left;
  background: linear-gradient(140deg, #ffffff, #f6fbff);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.25s ease, border-color 0.2s ease;
}

.product-item:hover {
  border-color: #9ec9e9;
  transform: translateY(-2px);
  box-shadow: 0 10px 18px rgba(36, 102, 149, 0.13);
}

.product-item.active {
  border-color: #63a8df;
  box-shadow: 0 8px 18px rgba(69, 136, 186, 0.16);
}

.row-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.product-title {
  font-size: 20px;
  line-height: 1.18;
  letter-spacing: 0.01em;
  color: #193449;
}

.type-line {
  margin: 8px 0 10px;
}

.type-pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #c6dff4;
  background: #eef7ff;
  color: #2d5f89;
  font-size: 15px;
  font-weight: 700;
}

.row-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.meta-cell {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.meta-label {
  color: #5f7990;
  font-size: 13px;
  font-style: normal;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.meta-value {
  color: #304f6a;
  font-size: 16px;
  line-height: 1.34;
  font-weight: 700;
}

.price-value {
  color: #d93026;
}

.editor-form {
  border: 1px solid #e5edf4;
  border-radius: 14px;
  padding: 14px;
  background: linear-gradient(150deg, #fcfeff, #f4faff);
}

.editor-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.preview-card {
  margin-top: 14px;
  border: 1px solid #d7e8f2;
  border-radius: 16px;
  padding: 14px;
  background: linear-gradient(160deg, #09212e, #0f3b52);
  color: #ecfbff;
  box-shadow: 0 18px 28px rgba(10, 41, 59, 0.24);
  animation: preview-enter 0.28s ease;
}

.preview-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.preview-head h3 {
  margin: 0;
  font-size: 20px;
}

.price {
  font-weight: 700;
  color: #9ff5ff;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.tag {
  border: 1px solid rgba(176, 236, 245, 0.4);
  border-radius: 999px;
  padding: 3px 10px;
  font-size: 12px;
  color: #d8f7ff;
}

.preview-intro {
  margin-top: 10px;
  color: #d6eef7;
}

.preview-members {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.preview-member {
  border: 1px solid rgba(172, 223, 236, 0.3);
  border-radius: 10px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.04);
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.preview-member small {
  color: rgba(214, 238, 247, 0.78);
}

.member-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.member-tags {
  min-height: 34px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
}

.placeholder {
  color: #8ba1b5;
  font-size: 13px;
}

.dialog-toolbar {
  margin-bottom: 10px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}

@keyframes preview-enter {
  from {
    opacity: 0;
    transform: translateY(6px) scale(0.99);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.full {
  width: 100%;
}

@media (max-width: 1100px) {
  .workspace {
    grid-template-columns: 1fr;
  }

  .left-panel {
    max-height: 320px;
  }

  .preview-members {
    grid-template-columns: 1fr;
  }

  .product-title {
    font-size: 20px;
  }

  .meta-value {
    font-size: 14px;
  }
}
</style>
