<template>
  <section class="agency-page fade-up">
    <header class="agency-filter-bar">
      <div class="search-line">
        <el-input
          v-model="filters.keyword"
          class="search-input"
          size="large"
          placeholder="搜索团队名、机构名、介绍关键词"
          clearable
          @keyup.enter="loadTeams"
        >
          <template #prefix>
            <span class="icon">🔎</span>
          </template>
        </el-input>
        <el-select v-model="filters.sort" class="sort-select" @change="loadTeams">
          <el-option label="综合相关" value="relevance" />
          <el-option label="评分优先" value="rating_desc" />
          <el-option label="最新上架" value="published_desc" />
        </el-select>
        <el-button type="primary" size="large" class="search-btn" @click="loadTeams" :loading="loadingList">搜索</el-button>
      </div>

      <div class="chip-row">
        <el-select v-model="filters.country" clearable placeholder="国家" class="chip" @change="loadTeams">
          <el-option label="美国" value="美国" />
          <el-option label="英国" value="英国" />
          <el-option label="澳大利亚" value="澳大利亚" />
          <el-option label="新西兰" value="新西兰" />
        </el-select>

        <el-select v-model="filters.direction" clearable placeholder="方向" class="chip" @change="loadTeams">
          <el-option v-for="item in directionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>

        <el-select v-model="filters.minRating" clearable placeholder="评分" class="chip" @change="loadTeams">
          <el-option label="4.0 分及以上" :value="4" />
          <el-option label="4.3 分及以上" :value="4.3" />
          <el-option label="4.5 分及以上" :value="4.5" />
        </el-select>

        <el-select v-model="filters.budgetBucket" clearable placeholder="预算" class="chip" @change="loadTeams">
          <el-option label="3万以下" value="LT_30000" />
          <el-option label="3万-5万" value="30000_50000" />
          <el-option label="5万-8万" value="50000_80000" />
          <el-option label="8万以上" value="80000_PLUS" />
        </el-select>

        <el-input v-model="filters.city" clearable placeholder="城市" class="chip" @keyup.enter="loadTeams" />

        <el-button text class="clear-btn" @click="clearFilters">清空</el-button>
      </div>
    </header>

    <div class="agency-body">
      <aside class="team-list" v-loading="loadingList">
        <el-empty v-if="!loadingList && teams.length === 0" description="暂无符合条件的团队" />

        <article
          v-for="(team, index) in teams"
          :key="team.teamId"
          class="team-card"
          :class="{ active: team.teamId === selectedTeamId }"
          :style="{ animationDelay: `${index * 50}ms` }"
          @click="selectTeam(team.teamId)"
        >
          <div class="team-top">
            <h3>{{ team.teamName }}</h3>
            <span class="price">{{ team.priceTextPlaceholder || '价格待上线' }}</span>
          </div>

          <div class="tags">
            <span class="tag">{{ team.serviceCountryScope }}</span>
            <span class="tag">{{ team.serviceMajorScope || '未标注方向' }}</span>
            <span class="tag">{{ team.city }}</span>
          </div>

          <p class="org-line">{{ team.orgName }}</p>

          <div class="mini-metrics">
            <span>案例 {{ team.caseCount || 0 }}</span>
            <span>评分 {{ Number(team.avgRating || 0).toFixed(1) }}</span>
          </div>

          <div class="card-actions">
            <el-button size="small" @click.stop="openGreetingDialog(team)">沟通</el-button>
          </div>
        </article>
      </aside>

      <main class="team-detail-wrap">
        <el-empty v-if="!loadingDetail && !detail" description="请选择左侧团队查看详情" />

        <transition name="detail-fade" mode="out-in">
          <section v-if="detail" :key="detail.teamId" class="team-detail" v-loading="loadingDetail">
            <div class="detail-head">
              <div>
                <h2>{{ detail.teamName }}</h2>
                <p class="sub">{{ detail.orgName }} · {{ detail.city }}</p>
              </div>
              <div class="head-actions">
                <el-button size="large" class="ghost-btn">收藏</el-button>
                <el-button size="large" type="primary" @click="openGreetingDialog(detail)">立即沟通</el-button>
              </div>
            </div>

            <div class="detail-meta">
              <span>国家：{{ detail.serviceCountryScope }}</span>
              <span>方向：{{ detail.serviceMajorScope || '未标注方向' }}</span>
              <span>价格：{{ detail.priceTextPlaceholder || '价格待上线' }}</span>
            </div>

            <div class="metric-panel">
              <div class="metric-item"><label>案例数</label><strong>{{ detail.caseCount || 0 }}</strong></div>
              <div class="metric-item"><label>成功率</label><strong>{{ Number(detail.successRate || 0).toFixed(1) }}%</strong></div>
              <div class="metric-item"><label>评分</label><strong>{{ Number(detail.avgRating || 0).toFixed(1) }}</strong></div>
              <div class="metric-item"><label>响应</label><strong>{{ Number(detail.responseEfficiencyScore || 0).toFixed(1) }}</strong></div>
            </div>

            <section class="block">
              <h3>团队介绍</h3>
              <p>{{ detail.teamIntro || '该团队暂无详细介绍。' }}</p>
            </section>

            <section class="block">
              <h3>团队成员（按角色）</h3>
              <div class="member-grid">
                <article v-for="member in detail.members" :key="member.memberId" class="member-card">
                  <div class="member-top">
                    <strong>{{ roleLabel(member.roleCode) }}</strong>
                    <span>{{ member.displayName }}</span>
                  </div>
                  <p class="member-job">{{ member.jobTitle }}</p>
                  <p class="member-desc">{{ member.bio || '暂无简介' }}</p>
                  <div class="tags">
                    <span class="tag">{{ member.educationLevel }}</span>
                    <span class="tag">{{ member.yearsOfExperience }}年</span>
                  </div>
                </article>
              </div>
            </section>

            <section class="block package-block">
              <h3>{{ detail.packagePlaceholderTitle || '套餐内容（开发中）' }}</h3>
              <p>{{ detail.packagePlaceholderDesc || '后续将接入服务产品与分档价格。' }}</p>
            </section>
          </section>
        </transition>
      </main>
    </div>

    <el-dialog v-model="greetingDialogVisible" width="460px" class="greeting-dialog" :show-close="false">
      <template #header>
        <div class="greeting-head">
          <strong>已向中介发送消息</strong>
          <el-button text @click="greetingDialogVisible = false">×</el-button>
        </div>
      </template>
      <div class="greeting-preview">{{ greetingText }}</div>
      <p class="greeting-tip">如需修改打招呼内容，请在消息页面继续编辑发送。</p>
      <template #footer>
        <el-button size="large" @click="greetingDialogVisible = false">留在此页</el-button>
        <el-button size="large" type="primary" :loading="startingChat" @click="continueChat">继续沟通</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getDiscoveryTeamDetail, listDiscoveryTeams } from '../services/agency'
import { startChat } from '../services/message'
import { getStudentVerificationStatus } from '../services/student'
import type { DiscoveryTeamDetail, DiscoveryTeamItem } from '../types/agency'

const router = useRouter()

const loadingList = ref(false)
const loadingDetail = ref(false)
const teams = ref<DiscoveryTeamItem[]>([])
const selectedTeamId = ref<number | null>(null)
const detail = ref<DiscoveryTeamDetail | null>(null)
const detailCache = new Map<number, DiscoveryTeamDetail>()
const greetingDialogVisible = ref(false)
const greetingText = ref('您好，想咨询一下留学申请相关问题。')
const pendingChatTeam = ref<{ teamId: number; teamName: string } | null>(null)
const startingChat = ref(false)

const roleOptions = [
  { label: '咨询顾问', value: 'CONSULTANT' },
  { label: '规划顾问', value: 'PLANNER' },
  { label: '文书顾问', value: 'WRITER' },
  { label: '申请专员', value: 'APPLY_SPECIALIST' },
  { label: '签证专员', value: 'VISA_SPECIALIST' },
  { label: '后续服务', value: 'AFTERCARE' }
]

const directionOptions = [
  { label: '工科', value: '工科' },
  { label: '商科', value: '商科' },
  { label: '理科', value: '理科' },
  { label: '文社科', value: '文社科' }
]

const filters = reactive({
  keyword: '',
  country: '',
  direction: '',
  minRating: undefined as number | undefined,
  budgetBucket: '',
  city: '',
  sort: 'relevance'
})

function roleLabel(code: string) {
  const match = roleOptions.find((item) => item.value === code)
  return match?.label || code || '未标注角色'
}

async function loadTeams() {
  loadingList.value = true
  try {
    teams.value = await listDiscoveryTeams(filters)
    if (teams.value.length === 0) {
      selectedTeamId.value = null
      detail.value = null
      return
    }

    const nextId = selectedTeamId.value && teams.value.some((item) => item.teamId === selectedTeamId.value)
      ? selectedTeamId.value
      : teams.value[0].teamId

    await selectTeam(nextId)
  } catch (error: any) {
    ElMessage.error(error?.message || '团队加载失败')
  } finally {
    loadingList.value = false
  }
}

async function selectTeam(teamId: number) {
  selectedTeamId.value = teamId
  if (detailCache.has(teamId)) {
    detail.value = detailCache.get(teamId) || null
    return
  }

  loadingDetail.value = true
  try {
    const data = await getDiscoveryTeamDetail(teamId)
    detailCache.set(teamId, data)
    detail.value = data
  } catch (error: any) {
    ElMessage.error(error?.message || '团队详情加载失败')
  } finally {
    loadingDetail.value = false
  }
}

function clearFilters() {
  filters.keyword = ''
  filters.country = ''
  filters.direction = ''
  filters.minRating = undefined
  filters.budgetBucket = ''
  filters.city = ''
  filters.sort = 'relevance'
  loadTeams()
}

async function openGreetingDialog(team: { teamId: number; teamName: string }) {
  try {
    const verify = await getStudentVerificationStatus()
    if (!verify.verificationCompleted) {
      ElMessage.warning('请先完成学生认证，认证通过后才能主动沟通中介')
      router.push('/me')
      return
    }
  } catch {
    ElMessage.warning('请先登录并完成学生认证')
    router.push('/auth')
    return
  }
  pendingChatTeam.value = team
  greetingText.value = '您好，想咨询一下留学申请相关问题。'
  greetingDialogVisible.value = true
}

async function continueChat() {
  if (!pendingChatTeam.value) return
  startingChat.value = true
  try {
    const data = await startChat({
      teamId: pendingChatTeam.value.teamId,
      greeting: greetingText.value
    })
    greetingDialogVisible.value = false
    router.push({ path: '/messages', query: { conversationId: data.conversation.conversationId } })
  } catch (error: any) {
    ElMessage.error(error?.message || '发起沟通失败')
  } finally {
    startingChat.value = false
  }
}

loadTeams()
</script>

<style scoped>
.agency-page {
  max-width: 1240px;
  margin: 0 auto;
  margin-top: -14px;
  padding-bottom: 16px;
}

.agency-filter-bar {
  position: relative;
  z-index: 1;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 14px;
  border: 1px solid rgba(108, 179, 208, 0.45);
  background: linear-gradient(140deg, rgba(18, 31, 49, 0.97), rgba(15, 57, 82, 0.95));
  backdrop-filter: blur(10px);
  box-shadow: 0 12px 28px rgba(10, 24, 40, 0.24);
}

.search-line {
  display: grid;
  grid-template-columns: 1fr 130px 120px;
  gap: 10px;
}

.search-input :deep(.el-input__wrapper),
.sort-select :deep(.el-input__wrapper) {
  border-radius: 999px;
}

.search-btn {
  border-radius: 999px;
}

.chip-row {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.chip {
  width: 140px;
}

.clear-btn {
  color: #d7ecf9;
}

.agency-body {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 14px;
  min-height: 620px;
}

.team-list {
  height: calc(100vh - 270px);
  overflow: auto;
  border-radius: 16px;
  padding: 10px;
  background: linear-gradient(180deg, #eaf1f7, #dde8f1);
  border: 1px solid #cedde9;
}

.team-card {
  border-radius: 14px;
  padding: 14px;
  background: #f7fbff;
  border: 1px solid #d8e7f1;
  margin-bottom: 10px;
  cursor: pointer;
  opacity: 0;
  transform: translateY(10px);
  animation: riseIn .35s ease forwards;
  transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}

.team-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 18px rgba(28, 72, 112, 0.16);
}

.team-card.active {
  border-color: #4eb9df;
  box-shadow: 0 0 0 2px rgba(78, 185, 223, 0.25), 0 12px 22px rgba(37, 106, 157, 0.16);
}

.team-top {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.team-top h3 {
  margin: 0;
  font-size: 18px;
  color: #2c445d;
}

.price {
  font-weight: 700;
  color: #ef6a57;
  white-space: nowrap;
}

.org-line {
  margin: 10px 0 8px;
  color: #617890;
}

.tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag {
  background: #eef5fb;
  color: #47627c;
  border-radius: 999px;
  padding: 3px 9px;
  font-size: 12px;
}

.mini-metrics {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  color: #60768e;
  font-size: 12px;
}

.card-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.team-detail-wrap {
  border-radius: 16px;
  border: 1px solid #d8e3ef;
  background: #fff;
  padding: 14px;
}

.team-detail {
  min-height: 560px;
}

.detail-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.detail-head h2 {
  margin: 0;
}

.sub {
  margin: 6px 0 0;
  color: #70849a;
}

.head-actions {
  display: flex;
  gap: 10px;
}

.ghost-btn {
  border-color: #8bb7d5;
  color: #36739e;
}

.detail-meta {
  margin: 14px 0;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: #526a84;
}

.metric-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.metric-item {
  border-radius: 12px;
  border: 1px solid #e2ecf6;
  padding: 10px;
  background: #f8fbff;
}

.metric-item label {
  display: block;
  color: #7e8ea4;
  font-size: 12px;
}

.metric-item strong {
  font-size: 20px;
}

.block {
  margin-top: 14px;
}

.member-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.member-card {
  border: 1px solid #e4edf6;
  border-radius: 12px;
  padding: 10px;
}

.member-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.member-job {
  margin: 8px 0 6px;
  color: #627991;
}

.member-desc {
  margin: 0 0 8px;
  color: #495f77;
}

.detail-fade-enter-active,
.detail-fade-leave-active {
  transition: all .24s ease;
}

.detail-fade-enter-from,
.detail-fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.greeting-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 24px;
}

.greeting-preview {
  padding: 14px 18px;
  border-radius: 8px;
  background: #f7f7f7;
  color: #2d3339;
  font-size: 18px;
  line-height: 1.7;
}

.greeting-tip {
  margin: 10px 0 0;
  color: #9aa1a8;
  font-size: 15px;
}

@keyframes riseIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 980px) {
  .search-line {
    grid-template-columns: 1fr;
  }

  .agency-body {
    grid-template-columns: 1fr;
  }

  .team-list {
    height: auto;
    max-height: 380px;
  }

  .metric-panel,
  .member-grid {
    grid-template-columns: 1fr;
  }
}
</style>
