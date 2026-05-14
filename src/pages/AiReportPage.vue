<template>
  <section class="ai-report-page fade-up">
    <div class="report-hero">
      <p class="crumb">首页 / AI择校报告</p>
      <div class="hero-row">
        <div>
          <h2>AI择校报告</h2>
        </div>
        <el-button v-if="report" type="primary" :loading="generating" @click="generateReport">重新生成报告</el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated class="report-card" />

    <el-empty v-else-if="!report" class="report-card" description="暂无AI择校报告">
      <el-button type="primary" :loading="generating" @click="generateReport">生成AI择校报告</el-button>
    </el-empty>

    <template v-else>
      <section class="report-card summary-card">
        <div>
          <span class="eyebrow">{{ report.status }} · {{ formatDate(report.generatedAt) }}</span>
          <h3>申请竞争力总览</h3>
          <p>{{ report.overallSummary }}</p>
        </div>
        <div class="model-pill">{{ report.modelVersion || 'admission-us-school-lgbm-v1' }}</div>
      </section>

      <section class="report-grid">
        <article v-for="item in topSchools" :key="item.schoolId" class="school-card">
          <div class="school-card-head">
            <div>
              <h3>{{ item.schoolName }}</h3>
              <p>{{ formatRank(item) }}</p>
            </div>
            <span class="tier" :class="tierClass(item.matchTier)">{{ item.matchTier }}</span>
          </div>
          <div class="probability">{{ probabilityText(item.admissionProbabilityEstimate) }}</div>
          <p class="summary">{{ item.aiSummary }}</p>
          <div class="score-row">
            <span>GPA {{ formatScore(item.gpaScore) }}</span>
            <span>语言 {{ formatScore(item.languageScore) }}</span>
            <span>软背景 {{ formatScore(item.softBackgroundScore) }}</span>
          </div>
          <div class="tag-row">
            <span v-for="tag in item.reasonTags" :key="`${item.schoolId}-${tag}`">{{ tag }}</span>
          </div>
        </article>
      </section>

      <section class="two-column">
        <div class="report-card">
          <h3>差距分析</h3>
          <el-empty v-if="!report.gapAnalysis.length" description="暂无明显短板" :image-size="72" />
          <div v-for="item in report.gapAnalysis" :key="`${item.dimension}-${item.target}`" class="analysis-row">
            <strong>{{ item.dimension }} · {{ item.priority }}</strong>
            <span>当前：{{ item.current }}</span>
            <span>目标：{{ item.target }}</span>
          </div>
        </div>

        <div class="report-card">
          <h3>提升建议</h3>
          <el-empty v-if="!report.improvementSuggestions.length" description="暂无提升建议" :image-size="72" />
          <div v-for="item in report.improvementSuggestions" :key="`${item.priority}-${item.action}`" class="analysis-row">
            <strong>{{ item.priority }} · {{ item.action }}</strong>
            <span>{{ item.reason }}</span>
          </div>
        </div>
      </section>

      <section v-if="report.riskWarnings.length" class="report-card warning-card">
        <h3>风险提示</h3>
        <p v-for="item in report.riskWarnings" :key="item">{{ item }}</p>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'
import { generateAiRecommendations, getLatestAiReport } from '../services/ai'
import type { AiReportView } from '../types/ai'

const loading = ref(false)
const generating = ref(false)
const report = ref<AiReportView | null>(null)

const topSchools = computed(() => report.value?.schoolRecommendations?.slice(0, 12) || [])

function readError(error: unknown) {
  return error instanceof ApiError ? error.message : 'AI预测暂不可用，请稍后重试'
}

function probabilityText(value?: number | null) {
  if (value === undefined || value === null) return '-'
  return `${Math.round(Number(value) * 100)}%`
}

function formatDate(value?: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

function formatRank(item: AiReportView['schoolRecommendations'][number]) {
  if (item.rankingSource === 'USNEWS' && item.usnewsRank) return `USNews ${item.usnewsRank}`
  if (item.qsRank) return `QS ${item.qsRank}`
  return '美国院校'
}

function formatScore(value?: number | null) {
  if (value === undefined || value === null) return '-'
  return Math.round(Number(value))
}

function tierClass(tier?: string | null) {
  if (tier === '保底') return 'safe'
  if (tier === '匹配') return 'match'
  return 'reach'
}

async function loadLatest() {
  loading.value = true
  try {
    report.value = await getLatestAiReport()
  } catch (error) {
    report.value = null
    ElMessage.error(readError(error))
  } finally {
    loading.value = false
  }
}

async function generateReport() {
  generating.value = true
  try {
    report.value = await generateAiRecommendations()
    ElMessage.success('AI择校报告已生成')
  } catch (error) {
    ElMessage.error(readError(error))
  } finally {
    generating.value = false
  }
}

onMounted(loadLatest)
</script>

<style scoped>
.ai-report-page {
  --ink: #182433;
  --muted: #66758a;
  --line: #dce5ef;
  --blue: #0f4c81;
  --mint: #e7f4ef;
  padding: 24px;
  color: var(--ink);
}

.report-hero,
.report-card,
.school-card {
  border: 1px solid rgba(15, 76, 129, 0.13);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 50px rgba(31, 47, 70, 0.08);
}

.report-hero {
  border-radius: 28px;
  padding: 28px;
  background: linear-gradient(135deg, #f7fbff, #eef7f1 58%, #fff8ed);
  margin-bottom: 18px;
}

.crumb,
.eyebrow {
  color: var(--muted);
  font-size: 13px;
  margin: 0 0 8px;
}

.hero-row,
.school-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.hero-row h2,
.summary-card h3,
.school-card h3,
.report-card h3 {
  margin: 0 0 8px;
}

.hero-row p,
.summary-card p,
.school-card p,
.warning-card p {
  color: var(--muted);
  margin: 0;
  line-height: 1.7;
}

.report-card {
  border-radius: 24px;
  padding: 22px;
  margin-bottom: 18px;
}

.summary-card {
  display: flex;
  justify-content: space-between;
  gap: 18px;
}

.model-pill,
.tier,
.tag-row span {
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 12px;
  white-space: nowrap;
}

.model-pill {
  color: var(--blue);
  background: #eaf3fb;
  height: fit-content;
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.school-card {
  border-radius: 24px;
  padding: 18px;
}

.school-card-head p,
.summary {
  color: var(--muted);
}

.tier.safe { background: #e6f5ec; color: #26734d; }
.tier.match { background: #e8f1fb; color: #0f4c81; }
.tier.reach { background: #fff1df; color: #a15b13; }

.probability {
  font-size: 40px;
  font-weight: 800;
  color: var(--blue);
  margin: 12px 0;
}

.score-row,
.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.score-row span {
  color: var(--muted);
  font-size: 13px;
}

.tag-row span {
  background: #f3f7fb;
  color: #385069;
}

.two-column {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.analysis-row {
  display: grid;
  gap: 6px;
  padding: 14px 0;
  border-top: 1px solid var(--line);
}

.analysis-row span {
  color: var(--muted);
  line-height: 1.6;
}

.warning-card {
  background: #fff8ed;
}

@media (max-width: 900px) {
  .ai-report-page { padding: 14px; }
  .hero-row,
  .summary-card { flex-direction: column; }
  .report-grid,
  .two-column { grid-template-columns: 1fr; }
}
</style>
