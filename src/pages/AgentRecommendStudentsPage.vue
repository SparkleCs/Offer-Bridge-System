<template>
  <section class="agent-recommend-page fade-up">
    <header class="recommend-head">
      <div>
        <h2 class="section-title">推荐学生</h2>
        <p class="section-desc">选择一个已发布套餐，系统会按服务国家、方向和价格区间推荐匹配学生。</p>
      </div>
      <el-button text @click="router.push('/agent-workbench/search')">进入学生库</el-button>
    </header>

    <el-alert v-if="blockedReason" :title="blockedReason" type="warning" :closable="false" show-icon class="gate-alert" />

    <template v-else>
      <section class="selector-panel">
        <el-select v-model="selectedTeamId" class="team-select" placeholder="请选择已发布套餐" @change="loadRecommendations(1)">
          <el-option v-for="team in publishedTeams" :key="team.teamId" :label="team.teamName" :value="team.teamId" />
        </el-select>
        <span v-if="activeTeam" class="team-meta">{{ formatPrice(activeTeam.priceMin, activeTeam.priceMax) }}</span>
      </section>

      <el-empty v-if="publishedTeams.length === 0" description="暂无已发布套餐，请先到团队产品管理发布套餐" />

      <div v-else class="student-list" v-loading="loading">
        <el-empty v-if="students.length === 0 && !loading" description="暂无推荐学生" />
        <article v-for="student in students" :key="student.studentUserId" class="student-card">
          <el-button class="chat-btn" type="primary" plain :loading="startingStudentId === student.studentUserId" @click="openChat(student)">沟通</el-button>
          <div class="avatar">{{ student.displayName.slice(0, 1) || '学' }}</div>
          <div class="student-main">
            <div class="student-title">
              <h3>{{ student.displayName }}</h3>
              <span class="recommend-badge">推荐</span>
            </div>
            <p class="student-sub">{{ student.schoolName || '学校待完善' }} · {{ student.major || '专业待完善' }}</p>
            <div class="info-grid">
              <span>GPA：{{ formatGpa(student) }}</span>
              <span>语言：{{ student.languageSummary || '待完善' }}</span>
              <span>目标国家：{{ student.targetCountries || '待完善' }}</span>
              <span>目标专业：{{ student.targetMajorText || '待完善' }}</span>
              <span>入学季：{{ student.intakeTerm || '待完善' }}</span>
            </div>
          </div>
        </article>
      </div>

      <div v-if="total > pageSize" class="pager-wrap">
        <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="loadRecommendations" />
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyWorkbenchAccess, listTeamProducts } from '../services/agency'
import { agentStartChat } from '../services/message'
import { listAgencyTeamStudentRecommendations } from '../services/recommendation'
import type { MemberWorkbenchAccess, TeamProductSummaryItem } from '../types/agency'
import type { AgencyTeamStudentRecommendationItem } from '../types/recommendation'
import { formatCompactPrice } from '../utils/price'

const router = useRouter()
const pageSize = 12

const access = ref<MemberWorkbenchAccess | null>(null)
const teams = ref<TeamProductSummaryItem[]>([])
const selectedTeamId = ref<number | null>(null)
const students = ref<AgencyTeamStudentRecommendationItem[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const startingStudentId = ref<number | null>(null)

const publishedTeams = computed(() => teams.value.filter((team) => team.publishStatus === 'PUBLISHED'))
const activeTeam = computed(() => publishedTeams.value.find((team) => team.teamId === selectedTeamId.value) || null)
const blockedReason = computed(() => {
  if (!access.value) return '正在检查权限...'
  if (access.value.blockedReason) return access.value.blockedReason
  if (!access.value.canChatStudent) return '你当前没有沟通学生权限，请联系机构管理员开通。'
  return ''
})

onMounted(async () => {
  await loadAccessAndTeams()
  if (!blockedReason.value && publishedTeams.value.length > 0) {
    selectedTeamId.value = publishedTeams.value[0].teamId
    await loadRecommendations(1)
  }
})

async function loadAccessAndTeams() {
  try {
    const [nextAccess, productList] = await Promise.all([
      getMyWorkbenchAccess(),
      listTeamProducts()
    ])
    access.value = nextAccess
    teams.value = productList
  } catch (error: any) {
    ElMessage.error(error?.message || '工作台信息加载失败')
    access.value = {
      orgVerificationStatus: 'PENDING',
      memberVerificationStatus: 'UNVERIFIED',
      permissions: [],
      canChatStudent: false,
      canPublishPackage: false,
      canDoCoreActions: false,
      blockedReason: '工作台信息加载失败'
    }
  }
}

async function loadRecommendations(nextPage = page.value) {
  if (!selectedTeamId.value) return
  page.value = nextPage
  loading.value = true
  try {
    const data = await listAgencyTeamStudentRecommendations({
      teamId: selectedTeamId.value,
      page: page.value,
      pageSize
    })
    students.value = data.records
    total.value = data.total
  } catch (error: any) {
    ElMessage.error(error?.message || '推荐学生加载失败')
  } finally {
    loading.value = false
  }
}

async function openChat(student: AgencyTeamStudentRecommendationItem) {
  if (!access.value?.canChatStudent) {
    ElMessage.warning('你当前没有沟通学生权限，请联系机构管理员开通。')
    return
  }
  if (!selectedTeamId.value) {
    ElMessage.warning('请选择团队产品')
    return
  }
  startingStudentId.value = student.studentUserId
  try {
    const data = await agentStartChat({
      studentUserId: student.studentUserId,
      teamId: selectedTeamId.value
    })
    await router.push({ path: '/agent-workbench/communication', query: { conversationId: data.conversation.conversationId } })
  } catch (error: any) {
    ElMessage.error(error?.message || '发起沟通失败')
  } finally {
    startingStudentId.value = null
  }
}

function formatPrice(min?: number | null, max?: number | null) {
  return formatCompactPrice(min || 0, max || 0)
}

function formatGpa(student: AgencyTeamStudentRecommendationItem) {
  if (student.gpaValue == null) return '待完善'
  const suffix = student.gpaScale === 'PERCENTAGE' ? '/100' : '/4.0'
  return `${student.gpaValue}${suffix}`
}

</script>

<style scoped>
.agent-recommend-page {
  min-height: calc(100vh - 40px);
}

.recommend-head,
.selector-panel,
.student-card {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 12px 28px rgba(16, 34, 63, 0.06);
}

.recommend-head {
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.selector-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  margin-bottom: 14px;
}

.team-select {
  width: 360px;
}

.team-meta {
  color: #ef6a57;
  font-weight: 750;
}

.gate-alert {
  margin-top: 12px;
}

.student-list {
  display: grid;
  gap: 12px;
}

.student-card {
  position: relative;
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: 14px;
  padding: 16px 142px 16px 16px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #e6f6fb;
  color: #2183a0;
  font-weight: 800;
}

.chat-btn {
  position: absolute;
  top: 16px;
  right: 16px;
}

.student-title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.student-title h3 {
  margin: 0;
  font-size: 18px;
}

.student-sub {
  margin: 8px 0;
  color: #657789;
}

.recommend-badge {
  border-radius: 999px;
  padding: 4px 10px;
  background: #e4f8ef;
  color: #178254;
  font-size: 12px;
  font-weight: 750;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 14px;
  color: #4c5e70;
  font-size: 13px;
}

.pager-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

@media (max-width: 760px) {
  .recommend-head,
  .selector-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .team-select {
    width: 100%;
  }

  .student-card {
    grid-template-columns: 44px minmax(0, 1fr);
    padding-right: 16px;
  }

  .chat-btn {
    position: static;
    grid-column: 2;
    justify-self: start;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
