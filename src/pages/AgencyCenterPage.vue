<template>
  <section class="page-card fade-up">
    <h2 class="section-title">中介端信息录入</h2>
    <p class="section-desc">按步骤完成机构、团队、邀请、员工资料录入。</p>

    <el-steps :active="step" finish-status="success" align-center class="steps">
      <el-step title="机构信息" />
      <el-step title="团队" />
      <el-step title="邀请员工" />
      <el-step title="员工资料" />
    </el-steps>

    <div v-show="step === 0" class="panel">
      <el-form label-position="top">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="机构名称"><el-input v-model="orgForm.orgName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌名"><el-input v-model="orgForm.brandName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="国家"><el-input v-model="orgForm.countryCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="省/州"><el-input v-model="orgForm.provinceOrState" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="城市"><el-input v-model="orgForm.city" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="详细地址"><el-input v-model="orgForm.addressLine" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系电话"><el-input v-model="orgForm.contactPhone" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系邮箱"><el-input v-model="orgForm.contactEmail" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="服务方式"><el-select v-model="orgForm.serviceMode"><el-option label="线上" value="ONLINE"/><el-option label="线下" value="OFFLINE"/><el-option label="混合" value="HYBRID"/></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="成立年份"><el-input-number v-model="orgForm.foundedYear" :min="1900" :max="2100" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="团队规模"><el-input v-model="orgForm.teamSizeRange" placeholder="如：20-50人" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="Logo URL"><el-input v-model="orgForm.logoUrl" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="封面图 URL"><el-input v-model="orgForm.coverImageUrl" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="办公环境图 URL（逗号分隔）"><el-input v-model="orgForm.officeEnvironmentImages" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="品牌介绍"><el-input type="textarea" :rows="4" v-model="orgForm.orgIntro" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div class="actions"><el-button type="primary" @click="saveOrg" :loading="saving">保存并下一步</el-button></div>
    </div>

    <div v-show="step === 1" class="panel">
      <el-form inline>
        <el-form-item label="团队名"><el-input v-model="teamForm.teamName" /></el-form-item>
        <el-form-item label="团队类型"><el-input v-model="teamForm.teamType" placeholder="咨询组/文书组" /></el-form-item>
        <el-form-item label="服务国家"><el-input v-model="teamForm.serviceCountryScope" /></el-form-item>
        <el-form-item label="服务方向"><el-input v-model="teamForm.serviceMajorScope" /></el-form-item>
      </el-form>
      <el-form-item label="团队介绍"><el-input type="textarea" :rows="2" v-model="teamForm.teamIntro" /></el-form-item>
      <div class="actions"><el-button type="primary" @click="addTeam" :loading="saving">创建团队并下一步</el-button></div>
      <el-table :data="teams" style="margin-top: 12px"><el-table-column prop="id" label="ID" width="70"/><el-table-column prop="teamName" label="团队"/><el-table-column prop="teamType" label="类型"/></el-table>
    </div>

    <div v-show="step === 2" class="panel">
      <el-form inline>
        <el-form-item label="团队"><el-select v-model="inviteForm.teamId" style="width:180px"><el-option v-for="t in teams" :key="t.id" :label="`${t.teamName}#${t.id}`" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="inviteForm.email" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="inviteForm.inviteeName" /></el-form-item>
        <el-form-item label="角色Hint"><el-input v-model="inviteForm.roleHint" placeholder="CONSULTANT" /></el-form-item>
      </el-form>
      <div class="actions"><el-button type="primary" @click="sendInvite" :loading="saving">发送邀请并下一步</el-button></div>
      <el-alert v-if="inviteToken" :title="`邀请Token：${inviteToken}`" type="success" show-icon :closable="false" />
      <p class="tips">提示：V1 可把 token 发给员工登录后在本页底部完成接受邀请。</p>
    </div>

    <div v-show="step === 3" class="panel">
      <el-form label-position="top">
        <el-form-item label="接受邀请 Token"><el-input v-model="acceptToken" /></el-form-item>
        <el-button @click="acceptInvite" :loading="saving">接受邀请（员工账号）</el-button>

        <hr class="line" />
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="展示名"><el-input v-model="memberForm.displayName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="岗位名"><el-input v-model="memberForm.jobTitle" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="学历"><el-input v-model="memberForm.educationLevel" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="毕业院校"><el-input v-model="memberForm.graduatedSchool" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="从业年限"><el-input-number v-model="memberForm.yearsOfExperience" :min="0" :max="60" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="擅长国家"><el-input v-model="memberForm.specialCountries" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="擅长方向"><el-input v-model="memberForm.specialDirections" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="个人简介"><el-input type="textarea" :rows="3" v-model="memberForm.bio" /></el-form-item></el-col>
        </el-row>
      </el-form>

      <el-form inline>
        <el-form-item label="主角色"><el-select v-model="primaryRole"><el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" /></el-select></el-form-item>
        <el-form-item label="附加角色"><el-select v-model="extraRoles" multiple style="width: 420px"><el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" /></el-select></el-form-item>
      </el-form>

      <el-form inline>
        <el-form-item label="案例数"><el-input-number v-model="metricsForm.caseCount" :min="0" /></el-form-item>
        <el-form-item label="成功率"><el-input-number v-model="metricsForm.successRate" :min="0" :max="100" :step="0.1" /></el-form-item>
        <el-form-item label="评分"><el-input-number v-model="metricsForm.avgRating" :min="0" :max="5" :step="0.1" /></el-form-item>
        <el-form-item label="响应效率"><el-input-number v-model="metricsForm.responseEfficiencyScore" :min="0" :max="5" :step="0.1" /></el-form-item>
      </el-form>

      <div class="actions">
        <el-button type="primary" @click="saveMemberAll" :loading="saving">保存员工资料与能力指标</el-button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  acceptAgencyInvitation,
  createAgencyInvitation,
  createAgencyOrgProfile,
  createAgencyTeam,
  getAgencyOrgProfile,
  listAgencyTeams,
  updateAgencyOrgProfile,
  updateMyAgencyMetrics,
  updateMyAgencyProfile,
  updateMyAgencyRoles
} from '../services/agency'
import type { AgencyOrgProfile, AgencyTeam } from '../types/agency'

const step = ref(0)
const saving = ref(false)
const teams = ref<AgencyTeam[]>([])
const inviteToken = ref('')
const acceptToken = ref('')

const orgForm = reactive<AgencyOrgProfile>({
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

const teamForm = reactive({
  teamName: '',
  teamType: '',
  teamIntro: '',
  serviceCountryScope: '',
  serviceMajorScope: ''
})

const inviteForm = reactive({
  teamId: undefined as number | undefined,
  email: '',
  inviteeName: '',
  roleHint: 'CONSULTANT'
})

const memberForm = reactive({
  displayName: '',
  jobTitle: '',
  educationLevel: '',
  graduatedSchool: '',
  major: '',
  yearsOfExperience: 0,
  specialCountries: '',
  specialDirections: '',
  bio: '',
  serviceStyleTags: '',
  publicStatus: 'PUBLIC' as 'PUBLIC' | 'PRIVATE'
})

const roleOptions = ['CONSULTANT', 'PLANNER', 'WRITER', 'APPLY_SPECIALIST', 'VISA_SPECIALIST', 'AFTERCARE', 'TEAM_LEADER']
const primaryRole = ref('CONSULTANT')
const extraRoles = ref<string[]>([])

const metricsForm = reactive({
  caseCount: 0,
  successRate: 0,
  avgRating: 0,
  responseEfficiencyScore: 0,
  serviceTags: '',
  budgetTags: ''
})

async function loadOrg() {
  try {
    const org = await getAgencyOrgProfile()
    if (org) Object.assign(orgForm, org)
    teams.value = await listAgencyTeams()
  } catch {
    // ignore init errors
  }
}

async function saveOrg() {
  saving.value = true
  try {
    const current = await getAgencyOrgProfile()
    if (current?.id) {
      await updateAgencyOrgProfile(orgForm)
    } else {
      await createAgencyOrgProfile(orgForm)
    }
    teams.value = await listAgencyTeams()
    step.value = 1
    ElMessage.success('机构信息已保存')
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function addTeam() {
  saving.value = true
  try {
    const team = await createAgencyTeam(teamForm)
    teams.value = [team, ...teams.value]
    inviteForm.teamId = team.id
    step.value = 2
    ElMessage.success('团队已创建')
  } catch (error: any) {
    ElMessage.error(error?.message || '创建失败')
  } finally {
    saving.value = false
  }
}

async function sendInvite() {
  if (!inviteForm.teamId) {
    ElMessage.warning('请选择团队')
    return
  }
  saving.value = true
  try {
    const result = await createAgencyInvitation({
      teamId: inviteForm.teamId,
      email: inviteForm.email,
      inviteeName: inviteForm.inviteeName,
      roleHint: inviteForm.roleHint
    })
    inviteToken.value = result.token
    acceptToken.value = result.token
    step.value = 3
    ElMessage.success('邀请已创建')
  } catch (error: any) {
    ElMessage.error(error?.message || '邀请失败')
  } finally {
    saving.value = false
  }
}

async function acceptInvite() {
  if (!acceptToken.value) {
    ElMessage.warning('请输入邀请 token')
    return
  }
  saving.value = true
  try {
    await acceptAgencyInvitation(acceptToken.value)
    ElMessage.success('已接受邀请')
  } catch (error: any) {
    ElMessage.error(error?.message || '接受邀请失败')
  } finally {
    saving.value = false
  }
}

async function saveMemberAll() {
  saving.value = true
  try {
    await updateMyAgencyProfile(memberForm)

    const roleSet = new Set<string>([primaryRole.value, ...extraRoles.value])
    await updateMyAgencyRoles({ roles: Array.from(roleSet).map((role) => ({ roleCode: role, isPrimary: role === primaryRole.value })) })

    await updateMyAgencyMetrics(metricsForm)
    ElMessage.success('员工信息已保存')
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

loadOrg()
</script>

<style scoped>
.steps {
  margin: 16px 0 18px;
}

.panel {
  border: 1px solid #d8e7f2;
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}

.actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.tips {
  margin-top: 10px;
  color: #6d8095;
}

.line {
  border: none;
  border-top: 1px dashed #cad9e5;
  margin: 16px 0;
}
</style>
