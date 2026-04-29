<template>
  <section class="member-reviews-page fade-up" v-loading="loading">
    <header class="page-head">
      <div>
        <span class="eyebrow">{{ detail?.orgName || 'OfferBridge' }}</span>
        <h2 class="section-title">老师评价</h2>
        <p class="section-desc">查看套餐中该老师的公开学生评价和维度评分。</p>
      </div>
      <el-button @click="backToTeam">返回套餐</el-button>
    </header>

    <el-empty v-if="!loading && (!detail || !member)" description="老师或套餐不存在">
      <el-button type="primary" @click="backToTeam">返回套餐</el-button>
    </el-empty>

    <template v-else-if="detail && member">
      <section class="profile-panel">
        <div class="teacher-main">
          <el-avatar :size="64">{{ member.displayName.slice(0, 1) }}</el-avatar>
          <div>
            <span class="eyebrow">{{ roleLabel(member.roleCode) }}</span>
            <h3>{{ member.displayName }}</h3>
            <p>{{ member.jobTitle || '服务老师' }} · {{ detail.teamName }}</p>
          </div>
        </div>
        <div class="score-summary">
          <strong>{{ scoreText(memberSummary?.ratingScore) }}</strong>
          <span>{{ memberSummary?.reviewCount || 0 }} 条学生评价</span>
        </div>
      </section>

      <section class="summary-panel">
        <el-descriptions title="老师信息" :column="2" border>
          <el-descriptions-item label="所属机构">{{ detail.orgName }}</el-descriptions-item>
          <el-descriptions-item label="套餐">{{ detail.teamName }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ member.educationLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="从业年限">{{ member.yearsOfExperience ?? '-' }} 年</el-descriptions-item>
          <el-descriptions-item label="评价关键词" :span="2">{{ memberSummary?.keywordSummary || '暂无评价关键词' }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ member.bio || '暂无简介' }}</el-descriptions-item>
        </el-descriptions>
      </section>

      <section class="reviews-panel">
        <div class="panel-head">
          <h3>公开评价</h3>
          <span>{{ reviews.length }} 条</span>
        </div>
        <el-empty v-if="reviews.length === 0" description="暂无公开评价" />
        <div v-else class="review-list">
          <article v-for="review in reviews" :key="review.reviewId" class="review-item">
            <div class="review-head">
              <div>
                <strong>{{ review.studentName }}</strong>
                <span>{{ review.createdAt }}</span>
              </div>
              <el-rate :model-value="Number(review.overallRating || 0)" disabled allow-half />
            </div>
            <p>{{ review.commentText || '学生未留下文字评价。' }}</p>
            <div class="review-dimensions">
              <span>专业 {{ review.professionalScore }}</span>
              <span>沟通 {{ review.communicationScore }}</span>
              <span>材料 {{ review.materialScore }}</span>
              <span>透明 {{ review.transparencyScore }}</span>
              <span>责任 {{ review.responsibilityScore }}</span>
            </div>
          </article>
        </div>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getDiscoveryTeamDetail } from '../services/agency'
import { getTeamMemberReviewSummaries, getTeamMemberReviews } from '../services/review'
import type { DiscoveryTeamDetail } from '../types/agency'
import type { MemberReviewItem, TeamMemberReviewSummary } from '../types/review'

const route = useRoute()
const router = useRouter()

const teamId = computed(() => Number(route.params.teamId || 0))
const memberId = computed(() => Number(route.params.memberId || 0))
const loading = ref(false)
const detail = ref<DiscoveryTeamDetail | null>(null)
const summaries = ref<TeamMemberReviewSummary[]>([])
const reviews = ref<MemberReviewItem[]>([])

const member = computed(() => detail.value?.members.find((item) => item.memberId === memberId.value) || null)
const memberSummary = computed(() => summaries.value.find((item) => item.memberId === memberId.value) || null)

const roleOptions = [
  { label: '咨询顾问', value: 'CONSULTANT' },
  { label: '规划顾问', value: 'PLANNER' },
  { label: '文书顾问', value: 'WRITER' },
  { label: '申请专员', value: 'APPLY_SPECIALIST' },
  { label: '签证专员', value: 'VISA_SPECIALIST' },
  { label: '后续服务', value: 'AFTERCARE' }
]

function roleLabel(code: string) {
  const match = roleOptions.find((item) => item.value === code)
  return match?.label || code || '未标注角色'
}

function scoreText(value?: number | null) {
  if (value == null || Number.isNaN(Number(value))) return '--'
  return Number(value).toFixed(0)
}

async function loadReviews() {
  if (!teamId.value || !memberId.value) return
  loading.value = true
  try {
    const [team, memberSummaries, memberReviews] = await Promise.all([
      getDiscoveryTeamDetail(teamId.value),
      getTeamMemberReviewSummaries(teamId.value).catch(() => []),
      getTeamMemberReviews(teamId.value, memberId.value).catch(() => [])
    ])
    detail.value = team
    summaries.value = memberSummaries
    reviews.value = memberReviews
  } catch (error: any) {
    ElMessage.error(error?.message || '评价加载失败')
  } finally {
    loading.value = false
  }
}

function backToTeam() {
  router.push({ path: '/agencies', query: teamId.value ? { teamId: teamId.value } : undefined })
}

loadReviews()
</script>

<style scoped>
.member-reviews-page {
  max-width: 1120px;
  margin: 0 auto;
}

.page-head,
.profile-panel,
.panel-head,
.review-head,
.teacher-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.eyebrow,
.panel-head span {
  font-size: 12px;
  color: #7b8b96;
}

.profile-panel,
.summary-panel,
.reviews-panel {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
  padding: 16px;
  margin-top: 14px;
}

.teacher-main {
  justify-content: flex-start;
}

.teacher-main h3 {
  margin: 2px 0 4px;
  font-size: 24px;
  color: #14293d;
}

.teacher-main p {
  margin: 0;
  color: #6f8194;
}

.score-summary {
  min-width: 170px;
  border-radius: 8px;
  background: linear-gradient(135deg, #102b46, #1a6479);
  color: #fff;
  padding: 14px 16px;
  text-align: right;
}

.score-summary strong {
  display: block;
  font-size: 40px;
  line-height: 1;
}

.score-summary span {
  color: #c6d7e5;
}

.panel-head h3 {
  margin: 0;
}

.review-list {
  display: grid;
  gap: 12px;
  margin-top: 12px;
}

.review-item {
  border: 1px solid #dde8f0;
  border-radius: 8px;
  padding: 12px;
  background: #fbfdff;
}

.review-head > div {
  display: grid;
  gap: 4px;
}

.review-head strong {
  color: #1e3449;
}

.review-head span {
  color: #6f8194;
  font-size: 13px;
}

.review-item p {
  color: #465d73;
  line-height: 1.7;
}

.review-dimensions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.review-dimensions span {
  border-radius: 999px;
  background: #edf5fa;
  color: #456175;
  padding: 3px 8px;
  font-size: 12px;
}

@media (max-width: 760px) {
  .page-head,
  .profile-panel,
  .review-head {
    align-items: stretch;
    flex-direction: column;
  }

  .score-summary {
    text-align: left;
  }
}
</style>
