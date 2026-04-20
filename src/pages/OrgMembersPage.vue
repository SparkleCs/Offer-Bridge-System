<template>
  <section class="page-card fade-up">
    <div class="head">
      <div>
        <h2 class="section-title">员工管理</h2>
        <p class="section-desc">创建、编辑机构员工账号，员工用手机号验证码直接登录工作台。</p>
      </div>
      <el-button type="primary" @click="openCreate">新增员工</el-button>
    </div>

    <el-table :data="members" v-loading="loading">
      <el-table-column prop="displayName" label="姓名" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="jobTitle" label="岗位" />
      <el-table-column label="角色">
        <template #default="scope">{{ (scope.row.roleCodes || []).join(' / ') }}</template>
      </el-table-column>
      <el-table-column prop="accountStatus" label="账号状态" width="100" />
      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button text @click="openEdit(scope.row)">编辑</el-button>
          <el-button text @click="toggleStatus(scope.row)">{{ scope.row.accountStatus === 'ACTIVE' ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑员工' : '新增员工'" width="700px">
      <el-form label-position="top">
        <el-row :gutter="12">
          <el-col v-if="!editingId" :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="展示名"><el-input v-model="form.displayName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="岗位"><el-input v-model="form.jobTitle" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学历"><el-input v-model="form.educationLevel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="毕业院校"><el-input v-model="form.graduatedSchool" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="从业年限"><el-input-number v-model="form.yearsOfExperience" :min="0" :max="60" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="擅长国家"><el-input v-model="form.specialCountries" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="擅长方向"><el-input v-model="form.specialDirections" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="简介"><el-input type="textarea" :rows="3" v-model="form.bio" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="角色（主角色放在第一位）"><el-select v-model="roleCodes" multiple allow-create filterable style="width: 100%" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import {
  createOrgMember,
  listOrgMembers,
  updateOrgMember,
  updateOrgMemberRoles,
  updateOrgMemberStatus
} from '../services/agency'
import type { OrgMemberItem } from '../types/agency'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const members = ref<OrgMemberItem[]>([])
const roleCodes = ref<string[]>(['CONSULTANT'])

const form = reactive({
  phone: '',
  displayName: '',
  jobTitle: '咨询顾问',
  educationLevel: '本科',
  graduatedSchool: '',
  major: '',
  yearsOfExperience: 0,
  specialCountries: '待完善',
  specialDirections: '待完善',
  bio: '该成员尚未完善简介',
  serviceStyleTags: '',
  publicStatus: 'PRIVATE' as 'PUBLIC' | 'PRIVATE'
})

async function load() {
  loading.value = true
  try {
    members.value = await listOrgMembers()
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.phone = ''
  form.displayName = ''
  form.jobTitle = '咨询顾问'
  form.educationLevel = '本科'
  form.graduatedSchool = ''
  form.major = ''
  form.yearsOfExperience = 0
  form.specialCountries = '待完善'
  form.specialDirections = '待完善'
  form.bio = '该成员尚未完善简介'
  form.serviceStyleTags = ''
  form.publicStatus = 'PRIVATE'
  roleCodes.value = ['CONSULTANT']
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(item: OrgMemberItem) {
  editingId.value = item.memberId
  form.displayName = item.displayName
  form.jobTitle = item.jobTitle
  form.educationLevel = item.educationLevel
  form.graduatedSchool = item.graduatedSchool
  form.yearsOfExperience = item.yearsOfExperience
  form.specialCountries = '待完善'
  form.specialDirections = '待完善'
  form.bio = '已由管理员维护'
  roleCodes.value = item.roleCodes?.length ? [...item.roleCodes] : ['CONSULTANT']
  dialogVisible.value = true
}

async function toggleStatus(item: OrgMemberItem) {
  const next = item.accountStatus === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  await updateOrgMemberStatus(item.memberId, { status: next })
  ElMessage.success(`员工账号已${next === 'ACTIVE' ? '启用' : '停用'}`)
  await load()
}

async function submit() {
  const roles = roleCodes.value.length ? roleCodes.value : ['CONSULTANT']
  const rolePayload = { roles: roles.map((role, index) => ({ roleCode: role, isPrimary: index === 0 })) }

  saving.value = true
  try {
    if (!editingId.value) {
      await createOrgMember({
        ...form,
        roles: rolePayload.roles,
        permissions: ['CAN_CHAT_STUDENT']
      })
      ElMessage.success('员工已创建')
    } else {
      await updateOrgMember(editingId.value, form)
      await updateOrgMemberRoles(editingId.value, rolePayload)
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

onMounted(load)
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 14px;
}
</style>
