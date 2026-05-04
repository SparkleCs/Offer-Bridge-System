<template>
  <section class="agency-favorites-page fade-up">
    <header class="favorites-head">
      <div>
        <h2 class="section-title">我的中介收藏</h2>
        <p class="section-desc">集中管理感兴趣的中介团队，随时发起沟通。</p>
      </div>
      <el-button type="primary" plain @click="router.push('/agencies')">继续筛选中介</el-button>
    </header>

    <div class="favorite-list" v-loading="loading">
      <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏中介">
        <el-button type="primary" @click="router.push('/agencies')">去收藏中介</el-button>
      </el-empty>

      <article v-for="team in pagedFavorites" :key="team.teamId" class="favorite-card">
        <div class="team-side">
          <div class="team-avatar">{{ team.teamName.slice(0, 1) || '中' }}</div>
          <div class="team-identity">
            <div class="team-name-row">
              <strong>{{ team.teamName }}</strong>
              <span>{{ team.city || '城市待完善' }}</span>
            </div>
            <h3>{{ team.serviceCountryScope || '服务国家待完善' }} · {{ team.serviceMajorScope || '方向待完善' }}</h3>
            <div class="team-tags">
              <span class="price">{{ team.priceTextPlaceholder || '价格待沟通' }}</span>
              <span>{{ team.caseCount || 0 }} 个案例</span>
              <span>综合 {{ scoreText(teamRatingMap[team.teamId]?.totalScore) }}</span>
              <span>成功率 {{ successRateText(team.successRate) }}</span>
            </div>
          </div>
        </div>

        <div class="org-side">
          <div class="org-mark">
            <img v-if="team.logoUrl" :src="team.logoUrl" :alt="team.orgName" />
            <span v-else>{{ orgInitial(team.orgName) }}</span>
          </div>
          <div class="org-text">
            <strong>{{ team.orgName }}</strong>
            <div class="org-tags">
              <span>{{ team.serviceCountryScope || '国家待完善' }}</span>
              <span>{{ team.serviceMajorScope || '方向待完善' }}</span>
            </div>
          </div>
        </div>

        <div class="card-actions">
          <el-button class="action-primary" :loading="startingTeamId === team.teamId" @click="startFavoriteChat(team)">立即沟通</el-button>
          <el-button class="action-secondary" :loading="removingTeamId === team.teamId" @click="removeFavorite(team)">取消感兴趣</el-button>
          <el-button text class="detail-link" @click="router.push({ path: '/agencies', query: { teamId: team.teamId } })">查看详情</el-button>
        </div>
      </article>

      <div v-if="total > pageSize" class="favorites-pager">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="page"
          :page-size="pageSize"
          :total="total"
          @current-change="changePage"
        />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { listFavoriteDiscoveryTeams, unfavoriteDiscoveryTeam } from '../services/agency'
import { ApiError } from '../services/http'
import { startChat } from '../services/message'
import { getTeamRatingSummaries } from '../services/review'
import { getStudentVerificationStatus } from '../services/student'
import { useAuthStore } from '../stores/auth'
import type { DiscoveryTeamItem } from '../types/agency'
import type { RatingSummary } from '../types/review'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const favorites = ref<DiscoveryTeamItem[]>([])
const teamRatingMap = ref<Record<number, RatingSummary>>({})
const startingTeamId = ref<number | null>(null)
const removingTeamId = ref<number | null>(null)
const page = ref(1)
const pageSize = 6
const total = computed(() => favorites.value.length)
const pagedFavorites = computed(() => {
  const start = (page.value - 1) * pageSize
  return favorites.value.slice(start, start + pageSize)
})

onMounted(loadFavorites)

async function loadFavorites() {
  loading.value = true
  try {
    favorites.value = await listFavoriteDiscoveryTeams()
    page.value = 1
    await loadRatings()
  } catch (error: any) {
    if (error instanceof ApiError && error.code === 'BIZ_UNAUTHORIZED') {
      authStore.doLogout()
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/auth')
      return
    }
    ElMessage.error(error?.message || '收藏中介加载失败')
  } finally {
    loading.value = false
  }
}

async function loadRatings() {
  const summaries = await getTeamRatingSummaries(favorites.value.map((item) => item.teamId)).catch(() => [])
  const next: Record<number, RatingSummary> = {}
  summaries.forEach((item) => {
    if (item.teamId) next[item.teamId] = item
  })
  teamRatingMap.value = next
}

async function startFavoriteChat(team: DiscoveryTeamItem) {
  startingTeamId.value = team.teamId
  try {
    const verify = await getStudentVerificationStatus()
    if (!verify.verificationCompleted) {
      ElMessage.warning('请先完成学生认证，认证通过后才能主动沟通中介')
      router.push('/me')
      return
    }
    const data = await startChat({
      teamId: team.teamId,
      greeting: '您好，我收藏了你们的服务团队，想进一步咨询留学申请相关问题。'
    })
    router.push({ path: '/messages', query: { conversationId: data.conversation.conversationId } })
  } catch (error: any) {
    ElMessage.error(error?.message || '发起沟通失败')
  } finally {
    startingTeamId.value = null
  }
}

async function removeFavorite(team: DiscoveryTeamItem) {
  removingTeamId.value = team.teamId
  try {
    await unfavoriteDiscoveryTeam(team.teamId)
    favorites.value = favorites.value.filter((item) => item.teamId !== team.teamId)
    normalizePage()
    ElMessage.success('已取消感兴趣')
  } catch (error: any) {
    ElMessage.error(error?.message || '取消收藏失败')
  } finally {
    removingTeamId.value = null
  }
}

function orgInitial(value?: string | null) {
  return (value || '中介').slice(0, 1)
}

function scoreText(value?: number | null) {
  if (value == null || Number.isNaN(Number(value))) return '--'
  return Number(value).toFixed(0)
}

function successRateText(value?: number | null) {
  if (value == null || Number.isNaN(Number(value))) return '--'
  return `${Number(value).toFixed(1)}%`
}

function changePage(nextPage: number) {
  page.value = nextPage
}

function normalizePage() {
  const maxPage = Math.max(1, Math.ceil(favorites.value.length / pageSize))
  if (page.value > maxPage) {
    page.value = maxPage
  }
}
</script>

<style scoped>
.agency-favorites-page {
  max-width: 1180px;
  margin: 0 auto;
}

.favorites-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
  padding: 14px 16px;
  border-radius: 8px;
  border: 1px solid #dce8ef;
  background: #fff;
  box-shadow: 0 10px 24px rgba(16, 34, 63, 0.05);
}

.favorite-list {
  display: grid;
  gap: 10px;
  align-content: start;
}

.favorite-list:empty,
.favorite-list[aria-busy='true'] {
  min-height: 180px;
}

.favorite-card {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(220px, .65fr) 138px;
  align-items: center;
  align-self: start;
  gap: 16px;
  min-height: 96px;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #dfe8ef;
  background: #fff;
  box-shadow: 0 8px 22px rgba(16, 34, 63, 0.05);
}

.team-side,
.org-side {
  display: flex;
  align-items: center;
  min-width: 0;
}

.team-side {
  gap: 12px;
}

.team-avatar {
  width: 40px;
  height: 40px;
  flex: 0 0 40px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #dff6f4;
  color: #0f817b;
  font-weight: 800;
  font-size: 17px;
}

.team-identity {
  min-width: 0;
}

.team-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #273243;
  font-size: 15px;
}

.team-name-row strong {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.team-name-row span {
  color: #667484;
}

.team-identity h3 {
  margin: 9px 0 8px;
  color: #1f2c3b;
  font-size: 17px;
  line-height: 1.35;
}

.team-tags,
.org-tags {
  display: flex;
  gap: 7px;
  flex-wrap: wrap;
}

.team-tags span,
.org-tags span {
  border-radius: 6px;
  padding: 4px 8px;
  background: #f4f6f8;
  color: #606b78;
  font-size: 13px;
}

.team-tags .price {
  color: #e14c45;
  background: transparent;
  padding-left: 0;
  font-size: 17px;
  font-weight: 750;
}

.org-side {
  gap: 12px;
}

.org-mark {
  width: 58px;
  height: 58px;
  flex: 0 0 58px;
  border-radius: 4px;
  overflow: hidden;
  background: #d7e7e5;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #205b57;
  font-size: 20px;
  font-weight: 800;
}

.org-mark img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.org-text {
  min-width: 0;
}

.org-text strong {
  display: block;
  margin-bottom: 9px;
  color: #273243;
  font-size: 16px;
  line-height: 1.3;
}

.card-actions {
  display: grid;
  gap: 7px;
  align-content: center;
  justify-items: stretch;
}

.card-actions :deep(.el-button) {
  margin-left: 0;
}

.card-actions :deep(.el-button.action-primary),
.card-actions :deep(.el-button.action-secondary) {
  width: 100%;
  height: 34px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
}

.card-actions :deep(.el-button.action-primary) {
  border: 0;
  background: linear-gradient(135deg, #1f334c, #2b4a70);
  color: #fff;
  box-shadow: 0 8px 18px rgba(31, 51, 76, 0.18);
}

.card-actions :deep(.el-button.action-primary:hover),
.card-actions :deep(.el-button.action-primary:focus) {
  background: linear-gradient(135deg, #263d5d, #31547e);
  color: #fff;
}

.card-actions :deep(.el-button.action-secondary) {
  border: 1px solid #d8e1ea;
  background: #fff;
  color: #536173;
  box-shadow: none;
}

.card-actions :deep(.el-button.action-secondary:hover),
.card-actions :deep(.el-button.action-secondary:focus) {
  border-color: #bdcad8;
  background: #f7f9fb;
  color: #35445a;
}

.card-actions :deep(.el-button.detail-link) {
  height: 24px;
  padding: 0;
  color: #68778a;
  font-size: 13px;
  font-weight: 650;
}

.favorites-pager {
  display: flex;
  justify-content: flex-end;
  padding: 10px 2px 0;
}

@media (max-width: 980px) {
  .favorite-card {
    grid-template-columns: 1fr;
  }

  .card-actions {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    align-items: center;
  }
}

@media (max-width: 640px) {
  .favorites-head,
  .team-name-row,
  .org-side {
    align-items: flex-start;
    flex-direction: column;
  }

  .favorite-card {
    padding: 14px;
  }

  .team-side {
    align-items: flex-start;
  }

  .team-identity h3 {
    margin: 9px 0 8px;
    font-size: 16px;
  }

  .card-actions {
    grid-template-columns: 1fr;
  }

  .favorites-pager {
    justify-content: center;
  }
}
</style>
