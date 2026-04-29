<template>
  <section class="page-card fade-up agent-profile-page">
    <header class="page-head">
      <h2 class="section-title">个人信息</h2>
      <p class="section-desc">维护成员档案与角色，并提交员工认证材料。</p>
    </header>

    <section class="glass-panel info-panel">
      <el-form label-position="top">
        <el-row :gutter="14">
          <el-col :span="12"><el-form-item label="展示名"><el-input v-model="profile.displayName" placeholder="请输入展示名称" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="岗位名"><el-input v-model="profile.jobTitle" placeholder="例如：留学咨询顾问" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="微信号"><el-input v-model="profile.wechatId" placeholder="用于同意互换微信后展示" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="学历">
              <el-select v-model="profile.educationLevel" placeholder="请选择学历" class="full-select">
                <el-option v-for="item in educationOptions" :key="item" :label="item" :value="item" />
              </el-select>
              <div class="field-tip">只填写最高学历</div>
            </el-form-item>
          </el-col>
          <el-col :span="8"><el-form-item label="毕业院校"><el-input v-model="profile.graduatedSchool" placeholder="请输入毕业院校" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="从业年限"><el-input-number v-model="profile.yearsOfExperience" :min="0" :max="60" class="full-number" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="擅长国家">
              <el-select
                v-model="specialCountryList"
                class="full-select"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="请选择擅长国家"
              >
                <el-option v-for="item in countryOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="擅长方向"><el-input v-model="profile.specialDirections" placeholder="例如：商科申请 / 理工科申请" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="个人简介"><el-input type="textarea" :rows="3" v-model="profile.bio" placeholder="请简要介绍你的服务经验与风格" /></el-form-item></el-col>
        </el-row>
      </el-form>

      <el-form class="role-form" label-position="top">
        <div class="role-grid">
          <el-form-item label="主角色" class="role-item">
            <el-select v-model="primaryRole" placeholder="请选择主角色" style="width: 220px">
              <el-option v-for="role in roleOptions" :key="role" :label="roleLabelMap[role] || role" :value="role" />
            </el-select>
          </el-form-item>
          <el-form-item label="附加角色" class="role-item role-extra">
            <el-select v-model="extraRoles" multiple placeholder="可补充多个角色" style="width: 420px">
              <el-option v-for="role in roleOptions" :key="role" :label="roleLabelMap[role] || role" :value="role" />
            </el-select>
            <div class="field-tip">可选，用于补充擅长职责</div>
          </el-form-item>
        </div>
      </el-form>

      <div class="actions">
        <el-button type="primary" :loading="saving" @click="save">保存个人信息</el-button>
      </div>
    </section>

    <AccountSecurityPanel class="security-panel" />

    <section class="glass-panel verify-panel">
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
import AccountSecurityPanel from '../components/AccountSecurityPanel.vue'
import { ApiError } from '../services/http'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'
import {
  getMyAgencyProfile,
  getMyMemberVerificationStatus,
  submitMyMemberVerification,
  updateMyAgencyProfile,
  updateMyAgencyRoles,
  uploadFile
} from '../services/agency'

const saving = ref(false)
const submittingVerification = ref(false)
const roleOptions = ['CONSULTANT', 'PLANNER', 'WRITER', 'APPLY_SPECIALIST', 'VISA_SPECIALIST', 'AFTERCARE', 'TEAM_LEADER']
const roleLabelMap: Record<string, string> = {
  CONSULTANT: '咨询顾问',
  PLANNER: '方案规划',
  WRITER: '文书导师',
  APPLY_SPECIALIST: '申请专员',
  VISA_SPECIALIST: '签证专员',
  AFTERCARE: '后续服务',
  TEAM_LEADER: '团队负责人'
}
const educationOptions = ['本科', '硕士', '博士']
const countryOptions = ['英国', '美国', '澳大利亚', '新西兰']
const countrySet = new Set(countryOptions)
const primaryRole = ref('CONSULTANT')
const extraRoles = ref<string[]>([])
const specialCountryList = ref<string[]>([])

const profile = reactive({
  displayName: '',
  wechatId: '',
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

async function loadProfile() {
  try {
    const res = await getMyAgencyProfile()
    profile.displayName = res.displayName || ''
    profile.wechatId = res.wechatId || ''
    profile.jobTitle = res.jobTitle || ''
    profile.educationLevel = res.educationLevel || ''
    profile.graduatedSchool = res.graduatedSchool || ''
    profile.major = res.major || ''
    profile.yearsOfExperience = res.yearsOfExperience ?? 0
    specialCountryList.value = parseSpecialCountries(res.specialCountries)
    profile.specialCountries = specialCountryList.value.join(',')
    profile.specialDirections = res.specialDirections || ''
    profile.bio = res.bio || ''
    profile.serviceStyleTags = res.serviceStyleTags || ''
    profile.publicStatus = res.publicStatus === 'PRIVATE' ? 'PRIVATE' : 'PUBLIC'

    const availableRoleCodes = new Set(roleOptions)
    const selectedRoles = (res.roleCodes || []).filter((role) => availableRoleCodes.has(role))
    if (selectedRoles.length > 0) {
      primaryRole.value = selectedRoles[0]
      extraRoles.value = selectedRoles.slice(1).filter((role) => role !== primaryRole.value)
    } else {
      primaryRole.value = 'CONSULTANT'
      extraRoles.value = []
    }
  } catch {
    // keep defaults
  }
}

function parseSpecialCountries(raw: string | undefined | null) {
  if (!raw) return []
  const parts = raw
    .split(/[,，、/|\s]+/g)
    .map((item) => item.trim())
    .filter(Boolean)
  const matched = Array.from(new Set(parts.filter((item) => countrySet.has(item))))
  if (matched.length) return matched
  const fallback = countryOptions.filter((item) => raw.includes(item))
  return Array.from(new Set(fallback))
}

async function save() {
  if (!specialCountryList.value.length) {
    ElMessage.warning('请至少选择一个擅长国家')
    return
  }
  saving.value = true
  try {
    profile.specialCountries = specialCountryList.value.join(',')
    await updateMyAgencyProfile(profile)
    const roleSet = new Set<string>([primaryRole.value, ...extraRoles.value])
    await updateMyAgencyRoles({ roles: Array.from(roleSet).map((role) => ({ roleCode: role, isPrimary: role === primaryRole.value })) })
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    saving.value = false
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

onMounted(async () => {
  await Promise.all([loadProfile(), loadVerificationStatus()])
})
</script>

<style scoped>
.agent-profile-page {
  position: relative;
  overflow: hidden;
  background: linear-gradient(165deg, #f4f9ff 0%, #eef5ff 42%, #f7fbff 100%);
  border-color: rgba(159, 191, 242, 0.35);
}

.agent-profile-page::before,
.agent-profile-page::after {
  content: '';
  position: absolute;
  border-radius: 999px;
  filter: blur(2px);
  pointer-events: none;
}

.agent-profile-page::before {
  width: 460px;
  height: 460px;
  top: -210px;
  right: -150px;
  background: radial-gradient(circle, rgba(77, 144, 255, 0.32) 0%, rgba(77, 144, 255, 0) 72%);
}

.agent-profile-page::after {
  width: 420px;
  height: 420px;
  left: -180px;
  bottom: -230px;
  background: radial-gradient(circle, rgba(89, 205, 255, 0.24) 0%, rgba(89, 205, 255, 0) 70%);
}

.page-head,
.glass-panel {
  position: relative;
  z-index: 1;
}

.page-head {
  margin-bottom: 14px;
}

.glass-panel {
  border: 1px solid rgba(165, 194, 239, 0.36);
  border-radius: 18px;
  padding: 16px;
  background: linear-gradient(155deg, rgba(255, 255, 255, 0.9), rgba(241, 248, 255, 0.76));
  box-shadow: 0 16px 36px rgba(31, 73, 141, 0.14), inset 0 1px 0 rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(4px);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.glass-panel:hover {
  transform: translateY(-2px);
  box-shadow: 0 22px 42px rgba(21, 62, 127, 0.18), inset 0 1px 0 rgba(255, 255, 255, 0.78);
}

.verify-panel {
  margin-top: 14px;
}

.security-panel {
  position: relative;
  z-index: 1;
  margin-top: 14px;
  background: linear-gradient(155deg, rgba(255, 255, 255, 0.92), rgba(241, 248, 255, 0.8));
  box-shadow: 0 16px 36px rgba(31, 73, 141, 0.12);
}

.field-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #5e7ea8;
}

.full-select,
.full-number {
  width: 100%;
}

.role-form {
  margin-top: 6px;
}

.role-grid {
  display: grid;
  grid-template-columns: 220px minmax(320px, 1fr);
  gap: 14px;
}

.role-item {
  margin-bottom: 0;
}

.role-extra {
  min-width: 0;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.actions :deep(.el-button--primary) {
  border: none;
  min-width: 148px;
  height: 40px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #2b79ff, #4f8eff 52%, #2aa4ff);
  box-shadow: 0 12px 24px rgba(43, 121, 255, 0.3);
}

.actions :deep(.el-button--primary:hover) {
  filter: saturate(1.08) brightness(1.02);
  transform: translateY(-1px);
}

.agent-profile-page :deep(.el-input__wrapper),
.agent-profile-page :deep(.el-select__wrapper),
.agent-profile-page :deep(.el-textarea__inner),
.agent-profile-page :deep(.el-input-number__decrease),
.agent-profile-page :deep(.el-input-number__increase) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(158, 185, 226, 0.5) inset;
}

.agent-profile-page :deep(.el-input__wrapper.is-focus),
.agent-profile-page :deep(.el-select__wrapper.is-focused),
.agent-profile-page :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px rgba(57, 126, 255, 0.9) inset, 0 0 0 4px rgba(57, 126, 255, 0.12);
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

@media (max-width: 860px) {
  .role-grid {
    grid-template-columns: 1fr;
  }

  .role-item :deep(.el-select) {
    width: 100% !important;
  }
}
</style>
