<template>
  <section class="page-card fade-up">
    <h2 class="section-title">个人信息</h2>
    <p class="section-desc">维护成员档案、角色和能力指标，并提交员工认证材料。</p>

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
      <el-button @click="submitAudit">提交资料审核</el-button>
    </div>

    <el-divider />

    <section class="verify-block">
      <div class="verify-head">
        <h3>员工认证材料</h3>
        <el-tag :type="statusTagType">{{ verifyStatus.status }}</el-tag>
      </div>
      <p class="muted" v-if="verifyStatus.rejectReason">驳回原因：{{ verifyStatus.rejectReason }}</p>

      <el-form label-position="top">
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="身份证">
              <el-upload :show-file-list="false" :http-request="onUploadIdCard" accept="image/*">
                <el-button :loading="uploading.idCard">上传图片</el-button>
              </el-upload>
              <div v-if="verifyForm.idCardImageUrl" class="upload-result">{{ verifyForm.idCardImageUrl }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="在职证明">
              <el-upload :show-file-list="false" :http-request="onUploadEmployment" accept="image/*">
                <el-button :loading="uploading.employment">上传图片</el-button>
              </el-upload>
              <div v-if="verifyForm.employmentProofImageUrl" class="upload-result">{{ verifyForm.employmentProofImageUrl }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学历证明">
              <el-upload :show-file-list="false" :http-request="onUploadEducation" accept="image/*">
                <el-button :loading="uploading.education">上传图片</el-button>
              </el-upload>
              <div v-if="verifyForm.educationProofImageUrl" class="upload-result">{{ verifyForm.educationProofImageUrl }}</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-button type="primary" :loading="submittingVerification" @click="submitVerification">提交员工认证</el-button>
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type UploadRequestOptions } from 'element-plus'
import { ApiError } from '../services/http'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'
import {
  getMyMemberVerificationStatus,
  submitMyMemberVerification,
  submitMyProfileForAudit,
  updateMyAgencyMetrics,
  updateMyAgencyProfile,
  updateMyAgencyRoles,
  uploadFile
} from '../services/agency'

const saving = ref(false)
const submittingVerification = ref(false)
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

const verifyForm = reactive({
  idCardImageUrl: '',
  employmentProofImageUrl: '',
  educationProofImageUrl: ''
})

const verifyStatus = reactive({
  status: 'UNVERIFIED',
  rejectReason: ''
})

const uploading = reactive({
  idCard: false,
  employment: false,
  education: false
})

const statusTagType = computed(() => {
  if (verifyStatus.status === 'APPROVED') return 'success'
  if (verifyStatus.status === 'REJECTED') return 'danger'
  if (verifyStatus.status === 'PENDING') return 'warning'
  return 'info'
})

async function loadVerificationStatus() {
  try {
    const res = await getMyMemberVerificationStatus()
    verifyStatus.status = res.status || 'UNVERIFIED'
    verifyStatus.rejectReason = res.rejectReason || ''
    if (res.payloadJson) {
      const payload = JSON.parse(res.payloadJson)
      verifyForm.idCardImageUrl = payload.idCardImageUrl || verifyForm.idCardImageUrl
      verifyForm.employmentProofImageUrl = payload.employmentProofImageUrl || verifyForm.employmentProofImageUrl
      verifyForm.educationProofImageUrl = payload.educationProofImageUrl || verifyForm.educationProofImageUrl
    }
  } catch {
    // ignore
  }
}

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
    ElMessage.success('已提交资料审核，等待平台管理员处理')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '提交审核失败')
  }
}

async function uploadWith(field: 'idCardImageUrl' | 'employmentProofImageUrl' | 'educationProofImageUrl', key: 'idCard' | 'employment' | 'education', options: UploadRequestOptions) {
  const file = options.file as File
  if (!validateUploadFileSize(file)) {
    return
  }
  uploading[key] = true
  try {
    const result = await uploadFile(file, 'member-verification')
    verifyForm[field] = result.url
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error))
  } finally {
    uploading[key] = false
  }
}

function onUploadIdCard(options: UploadRequestOptions) {
  return uploadWith('idCardImageUrl', 'idCard', options)
}

function onUploadEmployment(options: UploadRequestOptions) {
  return uploadWith('employmentProofImageUrl', 'employment', options)
}

function onUploadEducation(options: UploadRequestOptions) {
  return uploadWith('educationProofImageUrl', 'education', options)
}

async function submitVerification() {
  if (!verifyForm.idCardImageUrl || !verifyForm.employmentProofImageUrl || !verifyForm.educationProofImageUrl) {
    ElMessage.warning('请上传身份证、在职证明和学历证明')
    return
  }
  submittingVerification.value = true
  try {
    await submitMyMemberVerification({ ...verifyForm })
    await loadVerificationStatus()
    ElMessage.success('员工认证已提交，等待管理员审核')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '提交失败')
  } finally {
    submittingVerification.value = false
  }
}

onMounted(loadVerificationStatus)
</script>

<style scoped>
.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.verify-block {
  margin-top: 10px;
}

.verify-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.verify-head h3 {
  margin: 0;
}

.muted {
  color: #70859a;
  margin: 6px 0 12px;
}

.upload-result {
  margin-top: 8px;
  font-size: 12px;
  color: #4f6581;
  word-break: break-all;
}
</style>
