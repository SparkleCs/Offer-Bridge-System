<template>
  <section class="page-card fade-up">
    <h2 class="section-title">个人信息</h2>
    <p class="section-desc">维护成员档案、角色和能力指标（已接通现有接口）。</p>

    <el-form label-position="top">
      <el-row :gutter="12">
        <el-col :span="12"><el-form-item label="展示名"><el-input v-model="profile.displayName" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="岗位名"><el-input v-model="profile.jobTitle" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="学历"><el-input v-model="profile.educationLevel" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="毕业院校"><el-input v-model="profile.graduatedSchool" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="从业年限"><el-input-number v-model="profile.yearsOfExperience" :min="0" :max="60" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="擅长国家"><el-input v-model="profile.specialCountries" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="擅长方向"><el-input v-model="profile.specialDirections" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="个人简介"><el-input type="textarea" :rows="3" v-model="profile.bio" /></el-form-item></el-col>
      </el-row>
    </el-form>

    <el-form inline>
      <el-form-item label="主角色"><el-select v-model="primaryRole"><el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" /></el-select></el-form-item>
      <el-form-item label="附加角色"><el-select v-model="extraRoles" multiple style="width: 420px"><el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" /></el-select></el-form-item>
    </el-form>

    <el-form inline>
      <el-form-item label="案例数"><el-input-number v-model="metrics.caseCount" :min="0" /></el-form-item>
      <el-form-item label="成功率"><el-input-number v-model="metrics.successRate" :min="0" :max="100" :step="0.1" /></el-form-item>
      <el-form-item label="评分"><el-input-number v-model="metrics.avgRating" :min="0" :max="5" :step="0.1" /></el-form-item>
      <el-form-item label="响应效率"><el-input-number v-model="metrics.responseEfficiencyScore" :min="0" :max="5" :step="0.1" /></el-form-item>
    </el-form>

    <div class="actions">
      <el-button type="primary" :loading="saving" @click="save">保存个人信息</el-button>
      <el-button @click="submitAudit">提交审核</el-button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import { submitMyProfileForAudit, updateMyAgencyMetrics, updateMyAgencyProfile, updateMyAgencyRoles } from '../services/agency'

const saving = ref(false)
const roleOptions = ['CONSULTANT', 'PLANNER', 'WRITER', 'APPLY_SPECIALIST', 'VISA_SPECIALIST', 'AFTERCARE', 'TEAM_LEADER']
const primaryRole = ref('CONSULTANT')
const extraRoles = ref<string[]>([])

const profile = reactive({
  displayName: '',
  jobTitle: '咨询顾问',
  educationLevel: '本科',
  graduatedSchool: '',
  major: '',
  yearsOfExperience: 0,
  specialCountries: '待完善',
  specialDirections: '待完善',
  bio: '待完善',
  serviceStyleTags: '',
  publicStatus: 'PUBLIC' as 'PUBLIC' | 'PRIVATE'
})

const metrics = reactive({
  caseCount: 0,
  successRate: 0,
  avgRating: 0,
  responseEfficiencyScore: 0,
  serviceTags: '',
  budgetTags: ''
})

async function save() {
  saving.value = true
  try {
    await updateMyAgencyProfile(profile)
    const roleSet = new Set<string>([primaryRole.value, ...extraRoles.value])
    await updateMyAgencyRoles({ roles: Array.from(roleSet).map((role) => ({ roleCode: role, isPrimary: role === primaryRole.value })) })
    await updateMyAgencyMetrics(metrics)
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function submitAudit() {
  try {
    await submitMyProfileForAudit()
    ElMessage.success('已提交审核，等待平台管理员处理')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '提交审核失败')
  }
}
</script>

<style scoped>
.actions {
  display: flex;
  gap: 10px;
}
</style>
