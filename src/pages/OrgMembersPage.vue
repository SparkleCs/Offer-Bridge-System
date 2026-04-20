<template>
  <section class="page-card fade-up org-panel">
    <div class="head">
      <div>
        <h2 class="section-title">员工管理</h2>
        <p class="section-desc">管理员工账号与岗位，详细职业档案由员工在员工端填写并提交审核。</p>
      </div>
      <div class="head-actions">
        <el-tag :type="verificationTagType">认证状态：{{ verificationStatus }}</el-tag>
        <el-tooltip v-if="!canCreate" content="机构认证通过后才可新增员工" placement="top">
          <el-button type="primary" :disabled="!canCreate" @click="openCreate">新增员工</el-button>
        </el-tooltip>
        <el-button v-else type="primary" @click="openCreate">新增员工</el-button>
      </div>
    </div>

    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索姓名/手机号/岗位" clearable @clear="onSearch" @keyup.enter="onSearch">
        <template #append>
          <el-button @click="onSearch">搜索</el-button>
        </template>
      </el-input>
      <el-select v-model="statusFilter" placeholder="状态" @change="onSearch" style="width: 160px">
        <el-option label="全部状态" value="" />
        <el-option label="ACTIVE" value="ACTIVE" />
        <el-option label="DISABLED" value="DISABLED" />
      </el-select>
    </div>

    <el-table :data="members" v-loading="loading" row-key="memberId" class="table-fade">
      <el-table-column prop="displayName" label="姓名" min-width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="jobTitle" label="岗位" width="130" />
      <el-table-column prop="profileAuditStatus" label="资料审核" width="120" />
      <el-table-column prop="accountStatus" label="账号状态" width="110" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button class="action-btn edit" @click="openEdit(scope.row)">编辑</el-button>
          <el-button class="action-btn delete" @click="softDelete(scope.row)">删除</el-button>
        </template>
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑员工基础信息' : '新增员工'" width="560px">
      <el-form label-position="top">
        <el-form-item v-if="!editingId" label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="姓名（展示名）">
          <el-input v-model="form.displayName" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="form.jobTitle" style="width: 100%">
            <el-option v-for="job in jobOptions" :key="job" :label="job" :value="job" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ApiError } from '../services/http'
import {
  createOrgMember,
  getOrgVerification,
  listOrgMembers,
  softDeleteOrgMember,
  updateOrgMember
} from '../services/agency'
import type { OrgMemberItem, OrgVerificationStatus } from '../types/agency'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const members = ref<OrgMemberItem[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const statusFilter = ref('')
const verificationStatus = ref<OrgVerificationStatus>('PENDING')

const jobOptions = ['咨询顾问', '文书顾问', '规划顾问', '签证专员', '申请专员', '后续服务']

const form = reactive({
  phone: '',
  displayName: '',
  jobTitle: '咨询顾问'
})

const canCreate = computed(() => verificationStatus.value === 'APPROVED')
const verificationTagType = computed(() => {
  if (verificationStatus.value === 'APPROVED') return 'success'
  if (verificationStatus.value === 'REJECTED') return 'danger'
  return 'warning'
})

function resetForm() {
  form.phone = ''
  form.displayName = ''
  form.jobTitle = '咨询顾问'
}

async function loadVerification() {
  const result = await getOrgVerification().catch(() => null)
  verificationStatus.value = (result?.verificationStatus || 'PENDING') as OrgVerificationStatus
}

async function load() {
  loading.value = true
  try {
    const data = await listOrgMembers({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      status: statusFilter.value
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

function openCreate() {
  if (!canCreate.value) return
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(item: OrgMemberItem) {
  editingId.value = item.memberId
  form.displayName = item.displayName
  form.jobTitle = item.jobTitle
  dialogVisible.value = true
}

async function softDelete(item: OrgMemberItem) {
  await ElMessageBox.confirm(`确认软删除员工 ${item.displayName} 吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  })
  await softDeleteOrgMember(item.memberId)
  ElMessage.success('员工已删除')
  await load()
}

async function submit() {
  saving.value = true
  try {
    if (!editingId.value) {
      await createOrgMember({
        phone: form.phone,
        displayName: form.displayName,
        jobTitle: form.jobTitle,
        permissions: ['CAN_CHAT_STUDENT']
      })
      ElMessage.success('员工已创建')
    } else {
      await updateOrgMember(editingId.value, {
        displayName: form.displayName,
        jobTitle: form.jobTitle,
        educationLevel: 'UNKNOWN',
        graduatedSchool: '待完善',
        yearsOfExperience: 0,
        specialCountries: '待完善',
        specialDirections: '待完善',
        bio: '由员工端自行维护',
        publicStatus: 'PRIVATE'
      })
      ElMessage.success('员工已更新')
    }
    dialogVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadVerification()
  await load()
})
</script>

<style scoped>
.org-panel {
  animation: page-enter 0.2s ease;
}

@keyframes page-enter {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.head-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.toolbar {
  display: grid;
  grid-template-columns: 1fr 160px;
  gap: 12px;
  margin-bottom: 14px;
}

.table-fade {
  transition: opacity 0.18s ease;
}

.pager {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 8px;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.action-btn.edit {
  background: #eef3fb;
  color: #2d4a6f;
  border: 1px solid #d5e2f3;
}

.action-btn.delete {
  background: #fde8ea;
  color: #b8384a;
  border: 1px solid #f8c6cf;
}

@media (max-width: 900px) {
  .head {
    flex-direction: column;
    gap: 10px;
  }

  .toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
