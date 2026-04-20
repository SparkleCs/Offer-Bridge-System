<template>
  <section class="page-card fade-up">
    <h2 class="section-title">公司认证</h2>
    <p class="section-desc">提交认证材料后，状态会变为待审核。审核通过前，员工端核心能力受限。</p>

    <el-alert :title="statusText" :type="statusType" show-icon :closable="false" class="status" />

    <el-form label-position="top" class="form">
      <el-form-item label="营业执照号">
        <el-input v-model="form.licenseNo" />
      </el-form-item>
      <el-form-item label="法人姓名">
        <el-input v-model="form.legalPersonName" />
      </el-form-item>
      <el-form-item label="营业执照图片 URL">
        <el-input v-model="form.licenseImageUrl" />
      </el-form-item>
      <el-form-item label="法人证件图片 URL">
        <el-input v-model="form.legalPersonIdImageUrl" />
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
import { ApiError } from '../services/http'
import { getOrgVerification, submitOrgVerification } from '../services/agency'
import type { OrgVerificationStatus } from '../types/agency'

const saving = ref(false)
const verificationStatus = ref<OrgVerificationStatus>('PENDING')

const form = reactive({
  licenseNo: '',
  legalPersonName: '',
  licenseImageUrl: '',
  legalPersonIdImageUrl: '',
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
  max-width: 700px;
}
</style>
