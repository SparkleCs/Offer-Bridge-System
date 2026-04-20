<template>
  <section class="page-card fade-up">
    <h2 class="section-title">公司认证</h2>
    <p class="section-desc">请完整上传认证材料。认证通过前，无法新增员工。</p>

    <el-alert :title="statusText" :type="statusType" show-icon :closable="false" class="status" />

    <el-form label-position="top" class="form">
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="营业执照号">
            <el-input v-model="form.licenseNo" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="法人姓名">
            <el-input v-model="form.legalPersonName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="企业对公账户名">
            <el-input v-model="form.corporateAccountName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开户行">
            <el-input v-model="form.corporateBankName" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="企业对公账号">
            <el-input v-model="form.corporateBankAccountNo" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="营业执照图片 URL">
        <div class="upload-line"><el-input v-model="form.licenseImageUrl" /><el-upload :show-file-list="false" :http-request="onUploadLicense"><el-button>上传图片</el-button></el-upload></div>
      </el-form-item>
      <el-form-item label="法人身份证图片 URL">
        <div class="upload-line"><el-input v-model="form.legalPersonIdImageUrl" /><el-upload :show-file-list="false" :http-request="onUploadLegalId"><el-button>上传图片</el-button></el-upload></div>
      </el-form-item>
      <el-form-item label="对公账户凭证图 URL">
        <div class="upload-line"><el-input v-model="form.corporateAccountProofImageUrl" /><el-upload :show-file-list="false" :http-request="onUploadCorporateProof"><el-button>上传图片</el-button></el-upload></div>
      </el-form-item>
      <el-form-item label="办公环境图片 URL（逗号分隔）">
        <div class="upload-line"><el-input v-model="form.officeEnvironmentImageUrls" /><el-upload :show-file-list="false" :http-request="onUploadOfficeImage"><el-button>上传并追加</el-button></el-upload></div>
      </el-form-item>
      <el-form-item label="管理员个人实名认证图片 URL">
        <div class="upload-line"><el-input v-model="form.adminRealNameImageUrl" /><el-upload :show-file-list="false" :http-request="onUploadAdminRealName"><el-button>上传图片</el-button></el-upload></div>
      </el-form-item>
      <el-form-item label="管理员在职证明/授权书 URL">
        <div class="upload-line"><el-input v-model="form.adminEmploymentProofImageUrl" /><el-upload :show-file-list="false" :http-request="onUploadAdminEmploymentProof"><el-button>上传图片</el-button></el-upload></div>
      </el-form-item>

      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
      </el-form-item>
      <el-button type="primary" :loading="saving" @click="submit">提交认证</el-button>
    </el-form>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { ApiError } from '../services/http'
import { getOrgVerification, submitOrgVerification, uploadFile } from '../services/agency'
import type { OrgVerificationPayload, OrgVerificationStatus } from '../types/agency'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'

const saving = ref(false)
const verificationStatus = ref<OrgVerificationStatus>('PENDING')

const form = reactive<OrgVerificationPayload>({
  licenseNo: '',
  legalPersonName: '',
  licenseImageUrl: '',
  legalPersonIdImageUrl: '',
  corporateAccountName: '',
  corporateBankName: '',
  corporateBankAccountNo: '',
  corporateAccountProofImageUrl: '',
  officeEnvironmentImageUrls: '',
  adminRealNameImageUrl: '',
  adminEmploymentProofImageUrl: '',
  remark: ''
})

const statusText = computed(() => {
  if (verificationStatus.value === 'APPROVED') return '认证状态：已通过'
  if (verificationStatus.value === 'REJECTED') return '认证状态：已驳回，请补充后重新提交'
  return '认证状态：待审核'
})

const statusType = computed(() => {
  if (verificationStatus.value === 'APPROVED') return 'success'
  if (verificationStatus.value === 'REJECTED') return 'error'
  return 'warning'
})

async function uploadInto(field: keyof OrgVerificationPayload, file: File) {
  if (!validateUploadFileSize(file)) {
    return
  }
  try {
    const res = await uploadFile(file)
    ;(form[field] as unknown as string) = res.url
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error))
  }
}

async function uploadAppendOfficeImage(file: File) {
  if (!validateUploadFileSize(file)) {
    return
  }
  try {
    const res = await uploadFile(file)
    form.officeEnvironmentImageUrls = form.officeEnvironmentImageUrls
      ? `${form.officeEnvironmentImageUrls},${res.url}`
      : res.url
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error))
  }
}

function onUploadLicense(opt: UploadRequestOptions) { return uploadInto('licenseImageUrl', opt.file as File) }
function onUploadLegalId(opt: UploadRequestOptions) { return uploadInto('legalPersonIdImageUrl', opt.file as File) }
function onUploadCorporateProof(opt: UploadRequestOptions) { return uploadInto('corporateAccountProofImageUrl', opt.file as File) }
function onUploadOfficeImage(opt: UploadRequestOptions) { return uploadAppendOfficeImage(opt.file as File) }
function onUploadAdminRealName(opt: UploadRequestOptions) { return uploadInto('adminRealNameImageUrl', opt.file as File) }
function onUploadAdminEmploymentProof(opt: UploadRequestOptions) { return uploadInto('adminEmploymentProofImageUrl', opt.file as File) }

async function load() {
  try {
    const data = await getOrgVerification()
    verificationStatus.value = data.verificationStatus || 'PENDING'
    if (data.payloadJson) {
      try {
        const payload = JSON.parse(data.payloadJson)
        form.licenseNo = payload.licenseNo || ''
        form.legalPersonName = payload.legalPersonName || ''
        form.licenseImageUrl = payload.licenseImageUrl || ''
        form.legalPersonIdImageUrl = payload.legalPersonIdImageUrl || ''
        form.corporateAccountName = payload.corporateAccountName || ''
        form.corporateBankName = payload.corporateBankName || ''
        form.corporateBankAccountNo = payload.corporateBankAccountNo || ''
        form.corporateAccountProofImageUrl = payload.corporateAccountProofImageUrl || ''
        form.officeEnvironmentImageUrls = payload.officeEnvironmentImageUrls || ''
        form.adminRealNameImageUrl = payload.adminRealNameImageUrl || ''
        form.adminEmploymentProofImageUrl = payload.adminEmploymentProofImageUrl || ''
        form.remark = payload.remark || ''
      } catch {
        // ignore malformed payload
      }
    }
  } catch {
    // ignore initial errors
  }
}

async function submit() {
  saving.value = true
  try {
    const data = await submitOrgVerification(form)
    verificationStatus.value = data.verificationStatus || 'PENDING'
    ElMessage.success('认证材料已提交')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '提交失败')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.status {
  margin: 14px 0 18px;
}

.form {
  max-width: 880px;
}

.upload-line {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}
</style>
