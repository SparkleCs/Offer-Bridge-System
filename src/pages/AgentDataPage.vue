<template>
  <section class="data-page fade-up" v-loading="loading">
    <header class="data-hero">
      <div>
        <span>RATING INTELLIGENCE</span>
        <h2>服务评分与 Offer 表现</h2>
        <p>以学生评价、录取结果、履约过程和平台可信度构成的中介服务看板。</p>
      </div>
      <el-button class="refresh-btn" @click="loadDashboard" :loading="loading">刷新</el-button>
    </header>

    <div class="score-grid">
      <article class="score-tile primary">
        <label>机构综合评分</label>
        <strong>{{ scoreText(dashboard?.orgSummary.totalScore) }}</strong>
        <span>{{ dashboard?.orgSummary.confidenceLabel || '暂无套餐数据' }}</span>
      </article>
      <article class="score-tile">
        <label>学生评价</label>
        <strong>{{ scoreText(dashboard?.orgSummary.studentReviewScore) }}</strong>
        <span>{{ dashboard?.orgSummary.reviewCount || 0 }} 条评价</span>
      </article>
      <article class="score-tile">
        <label>Offer 结果</label>
        <strong>{{ scoreText(dashboard?.orgSummary.offerOutcomeScore) }}</strong>
        <span>转化与质量综合</span>
      </article>
      <article class="score-tile">
        <label>履约过程</label>
        <strong>{{ scoreText(dashboard?.orgSummary.processPerformanceScore) }}</strong>
        <span>阶段推进表现</span>
      </article>
    </div>

    <div class="data-layout">
      <section class="panel">
        <div class="panel-head">
          <h3>老师评价排行</h3>
          <span>按真实学生评价聚合</span>
        </div>
        <el-empty v-if="!dashboard?.memberRankings.length" description="暂无老师评价数据" />
        <div v-else class="ranking-list">
          <article v-for="member in dashboard.memberRankings" :key="member.memberId" class="ranking-item">
            <div class="avatar">{{ member.displayName.slice(0, 1) }}</div>
            <div class="ranking-main">
              <strong>{{ member.displayName }}</strong>
              <span>{{ roleLabel(member.roleCode) }} · {{ member.reviewCount }} 条评价</span>
              <p>{{ member.keywordSummary }}</p>
            </div>
            <div class="ranking-score">{{ scoreText(member.ratingScore) }}</div>
          </article>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h3>套餐评分拆解</h3>
          <span>学生评价 50% / Offer 结果 35%</span>
        </div>
        <el-empty v-if="!dashboard?.teamSummaries.length" description="暂无已发布套餐评分" />
        <div v-else class="team-score-list">
          <article v-for="team in dashboard.teamSummaries" :key="team.teamId || 0" class="team-score-item">
            <div class="team-score-top">
              <strong>套餐 #{{ team.teamId }}</strong>
              <span>{{ scoreText(team.totalScore) }}</span>
            </div>
            <div class="bars">
              <label>学生评价 <el-progress :percentage="Number(team.studentReviewScore || 0)" :show-text="false" /></label>
              <label>Offer结果 <el-progress :percentage="Number(team.offerOutcomeScore || 0)" :show-text="false" /></label>
              <label>履约过程 <el-progress :percentage="Number(team.processPerformanceScore || 0)" :show-text="false" /></label>
              <label>平台可信 <el-progress :percentage="Number(team.platformTrustScore || 0)" :show-text="false" /></label>
            </div>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAgencyReviewDashboard } from '../services/review'
import type { AgencyDashboard } from '../types/review'

const loading = ref(false)
const dashboard = ref<AgencyDashboard | null>(null)

function scoreText(value?: number | null) {
  if (value == null || Number.isNaN(Number(value))) return '--'
  return Number(value).toFixed(0)
}

function roleLabel(code?: string | null) {
  const dict: Record<string, string> = {
    CONSULTANT: '咨询顾问',
    PLANNER: '规划顾问',
    WRITER: '文书顾问',
    APPLY_SPECIALIST: '申请专员',
    VISA_SPECIALIST: '签证专员',
    AFTERCARE: '后续服务'
  }
  return code ? dict[code] || code : '服务老师'
}

async function loadDashboard() {
  loading.value = true
  try {
    dashboard.value = await getAgencyReviewDashboard()
  } catch (error: any) {
    ElMessage.error(error?.message || '评分看板加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<style scoped>
.data-page {
  display: grid;
  gap: 16px;
}

.data-hero {
  border-radius: 8px;
  padding: 22px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(13, 32, 49, 0.98), rgba(22, 83, 99, 0.96)),
    #102b46;
  box-shadow: 0 18px 38px rgba(12, 44, 68, 0.18);
}

.data-hero span {
  font-size: 12px;
  color: #9ed8e5;
  letter-spacing: 0.08em;
  font-weight: 800;
}

.data-hero h2 {
  margin: 6px 0;
  font-size: 28px;
}

.data-hero p {
  margin: 0;
  color: #c7d8e5;
}

.refresh-btn {
  border: 1px solid rgba(255, 255, 255, 0.28);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.score-tile,
.panel {
  border: 1px solid #d9e6ef;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 12px 26px rgba(20, 65, 92, 0.08);
}

.score-tile {
  padding: 16px;
}

.score-tile label {
  display: block;
  color: #6b7f92;
}

.score-tile strong {
  display: block;
  margin-top: 8px;
  font-size: 34px;
  color: #122d44;
}

.score-tile span {
  color: #7b8fa2;
}

.score-tile.primary {
  background: #102b46;
}

.score-tile.primary label,
.score-tile.primary span {
  color: #bdd1df;
}

.score-tile.primary strong {
  color: #fff;
}

.data-layout {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 16px;
}

.panel {
  padding: 16px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 14px;
}

.panel-head h3 {
  margin: 0;
}

.panel-head span {
  color: #7a8fa3;
}

.ranking-list,
.team-score-list {
  display: grid;
  gap: 10px;
}

.ranking-item {
  display: grid;
  grid-template-columns: 42px 1fr 54px;
  gap: 12px;
  align-items: center;
  border: 1px solid #edf2f6;
  border-radius: 8px;
  padding: 12px;
  background: #fbfdff;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #fff;
  background: #173b57;
  font-weight: 800;
}

.ranking-main strong {
  color: #1b3348;
}

.ranking-main span,
.ranking-main p {
  display: block;
  margin: 4px 0 0;
  color: #73879a;
}

.ranking-main p {
  font-size: 13px;
}

.ranking-score {
  font-size: 26px;
  font-weight: 900;
  color: #153852;
  text-align: right;
}

.team-score-item {
  border: 1px solid #edf2f6;
  border-radius: 8px;
  padding: 12px;
}

.team-score-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.team-score-top span {
  font-size: 26px;
  font-weight: 900;
  color: #153852;
}

.bars {
  display: grid;
  gap: 8px;
  margin-top: 10px;
}

.bars label {
  display: grid;
  grid-template-columns: 90px 1fr;
  gap: 10px;
  align-items: center;
  color: #60758a;
}

@media (max-width: 1000px) {
  .score-grid,
  .data-layout {
    grid-template-columns: 1fr;
  }
}
</style>
