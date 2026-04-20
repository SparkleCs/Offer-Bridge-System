<template>
  <section class="page-card fade-up">
    <h2 class="section-title">公司信息管理</h2>
    <p class="section-desc">机构管理员维护对外展示与联系方式。</p>

    <el-form label-position="top">
      <el-row :gutter="12">
        <el-col :span="12"><el-form-item label="机构名称"><el-input v-model="form.orgName" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="品牌名"><el-input v-model="form.brandName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="国家"><el-input v-model="form.countryCode" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="省/州"><el-input v-model="form.provinceOrState" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="城市"><el-input v-model="form.city" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="详细地址"><el-input v-model="form.addressLine" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="联系邮箱"><el-input v-model="form.contactEmail" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="服务方式"><el-select v-model="form.serviceMode"><el-option label="线上" value="ONLINE" /><el-option label="线下" value="OFFLINE" /><el-option label="混合" value="HYBRID" /></el-select></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="机构介绍"><el-input type="textarea" :rows="4" v-model="form.orgIntro" /></el-form-item></el-col>
      </el-row>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </el-form>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import { createAgencyOrgProfile, getAgencyOrgProfile, updateAgencyOrgProfile } from '../services/agency'
import type { AgencyOrgProfile } from '../types/agency'

const saving = ref(false)

const form = reactive<AgencyOrgProfile>({
  orgName: '',
  brandName: '',
  countryCode: 'CN',
  provinceOrState: '',
  city: '',
  district: '',
  addressLine: '',
  logoUrl: '',
  coverImageUrl: '',
  officeEnvironmentImages: '',
  contactPhone: '',
  contactEmail: '',
  websiteUrl: '',
  socialLinks: '',
  foundedYear: undefined,
  teamSizeRange: '',
  serviceMode: 'HYBRID',
  orgIntro: '',
  orgSlogan: ''
})

async function load() {
  try {
    const data = await getAgencyOrgProfile()
    if (data) Object.assign(form, data)
  } catch {
    // ignore
  }
}

async function save() {
  saving.value = true
  try {
    if (form.id) {
      const updated = await updateAgencyOrgProfile(form)
      Object.assign(form, updated)
    } else {
      const created = await createAgencyOrgProfile(form)
      Object.assign(form, created)
    }
    ElMessage.success('公司信息已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
