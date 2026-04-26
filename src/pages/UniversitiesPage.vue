<template>
  <section ref="pageTopRef" class="page-card fade-up universities-page">
    <div class="page-head">
      <p class="crumb">首页 / 院校</p>
      <h2 class="section-title">院校</h2>
      <p class="section-desc">以筛选为主：国家、学科领域、专业方向、QS 排名，快速定位学校与项目并加入申请清单。</p>
    </div>

    <div class="filter-panel">
      <div class="filter-row">
        <div class="filter-label">国家</div>
        <div class="chip-group">
          <button
            v-for="item in meta.countries"
            :key="item.code"
            class="chip"
            :class="{ active: filters.countryCode === item.code }"
            @click="toggleFilter('countryCode', item.code)"
          >
            {{ item.name }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-label">学科领域</div>
        <div class="chip-group">
          <button
            v-for="item in meta.subjectCategories"
            :key="item.code"
            class="chip"
            :class="{ active: filters.subjectCategoryCode === item.code }"
            @click="onSubjectSelect(item.code)"
          >
            {{ item.name }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-label">专业方向</div>
        <div class="chip-group">
          <button class="chip" :class="{ active: !filters.directionCode }" @click="filters.directionCode = ''">全部</button>
          <button
            v-for="item in directionOptions"
            :key="item.code"
            class="chip"
            :class="{ active: filters.directionCode === item.code }"
            @click="toggleFilter('directionCode', item.code)"
          >
            {{ item.name }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-label">大学排名</div>
        <div class="chip-group">
          <button class="chip" :class="{ active: rankPreset === 'ALL' }" @click="setRankPreset('ALL')">全部</button>
          <button class="chip" :class="{ active: rankPreset === 'TOP20' }" @click="setRankPreset('TOP20')">1-20名</button>
          <button class="chip" :class="{ active: rankPreset === 'TOP50' }" @click="setRankPreset('TOP50')">20-50名</button>
          <button class="chip" :class="{ active: rankPreset === 'TOP100' }" @click="setRankPreset('TOP100')">50-100名</button>
        </div>
        <div class="rank-inputs">
          <el-input-number v-model="filters.rankMin" :min="1" :max="500" controls-position="right" placeholder="最小QS" />
          <span>至</span>
          <el-input-number v-model="filters.rankMax" :min="1" :max="500" controls-position="right" placeholder="最大QS" />
        </div>
      </div>

      <div class="filter-actions">
        <el-input v-model="filters.keyword" placeholder="学校/国家/城市关键词（可选）" clearable />
        <el-button type="primary" :loading="loading" @click="search">筛选</el-button>
        <el-button :loading="loading" @click="resetFilters">清空条件</el-button>
      </div>
    </div>

    <div class="result-list" v-loading="loading">
      <el-empty v-if="totalSchools === 0 && !loading" description="没有符合条件的院校" />
      <transition-group name="school-fade" tag="div" class="school-list-inner">
        <article v-for="school in pagedSchools" :key="school.id" class="school-card">
        <div class="school-main">
          <div class="school-left">
            <div class="school-logo">{{ school.schoolNameEn.slice(0, 3).toUpperCase() }}</div>
          </div>

          <div class="school-center">
            <div class="school-title-row">
              <div>
                <h3>{{ school.schoolNameCn }}</h3>
                <p class="school-en">{{ school.schoolNameEn }}</p>
              </div>
              <div class="rank-badge">QS {{ school.qsRank }}</div>
            </div>
            <p class="school-meta">{{ school.countryName }} / {{ school.cityName }}</p>
            <p class="school-meta">费用区间：{{ formatTuitionRange(school.tuitionMin, school.tuitionMax, school.tuitionCurrency) }}</p>
            <div class="tag-list">
              <span v-for="tag in splitTags(school.advantageSubjects)" :key="`${school.id}-${tag}`" class="tag-item">{{ tag }}</span>
            </div>
          </div>

          <div class="school-actions">
            <el-button link type="primary" @click="toggleExpand(school.id)">
              {{ expandedSchoolIds.has(school.id) ? '收起项目' : '查看项目' }}
            </el-button>
          </div>
        </div>

        <div v-if="expandedSchoolIds.has(school.id)" class="program-list">
          <div v-if="programsBySchool.get(school.id)?.length" class="program-rows">
            <div v-for="program in programsBySchool.get(school.id)" :key="program.id" class="program-row">
              <div class="program-info">
                <div class="program-name">{{ program.programName }}</div>
                <div class="program-sub">
                  方向：{{ program.directionName }} · 类型：{{ displayStudyMode(program.studyMode) }} · 学制：{{ program.durationMonths || '-' }} 月
                </div>
              </div>
              <el-button type="primary" plain size="small" :loading="addingProgramId === program.id" @click="addToList(program.id)">
                加入申请清单
              </el-button>
            </div>
          </div>
          <el-empty v-else description="该校暂无项目数据" :image-size="64" />
        </div>
        </article>
      </transition-group>
      <div v-if="totalSchools > pageSize" class="pager-wrap">
        <el-pagination
          class="school-pagination"
          background
          layout="prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalSchools"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ApiError } from '../services/http'
import { addApplication, getUniversityMeta, listPrograms, listSchools } from '../services/university'
import { useAuthStore } from '../stores/auth'
import type { FilterOption, ProgramListItem, SchoolListItem, UniversityMeta } from '../types/university'
import { confirmLoginRequired } from '../utils/authPrompt'

type RankPreset = 'ALL' | 'TOP20' | 'TOP50' | 'TOP100'

const router = useRouter()
const authStore = useAuthStore()

const meta = reactive<UniversityMeta>({
  countries: [],
  subjectCategories: [],
  directions: [],
  applicationStatuses: []
})

const filters = reactive({
  countryCode: '',
  subjectCategoryCode: '',
  directionCode: '',
  rankMin: undefined as number | undefined,
  rankMax: undefined as number | undefined,
  keyword: ''
})

const rankPreset = ref<RankPreset>('ALL')
const loading = ref(false)
const addingProgramId = ref<number | null>(null)
const schools = ref<SchoolListItem[]>([])
const currentPage = ref(1)
const pageSize = 10
const pageTopRef = ref<HTMLElement | null>(null)
const programsBySchool = ref<Map<number, ProgramListItem[]>>(new Map())
const expandedSchoolIds = ref<Set<number>>(new Set())

const directionOptions = computed<FilterOption[]>(() => {
  if (!filters.subjectCategoryCode) return meta.directions
  return meta.directions.filter((item) => item.parentCode === filters.subjectCategoryCode)
})
const totalSchools = computed(() => schools.value.length)
const pagedSchools = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return schools.value.slice(start, start + pageSize)
})

function readError(error: unknown) {
  if (error instanceof ApiError) return error.message
  return '请求失败'
}

function displayStudyMode(studyMode: string) {
  if (studyMode === 'TAUGHT') return '授课型'
  if (studyMode === 'RESEARCH') return '研究型'
  return studyMode || '-'
}

function formatTuitionRange(min: number | null, max: number | null, currency: string | null) {
  if (min === null && max === null) return '-'
  return `${currency || ''} ${min ?? '-'} - ${max ?? '-'}`
}

function splitTags(text: string | null) {
  if (!text) return []
  return text
    .split(/[，,]/)
    .map((item) => item.trim())
    .filter(Boolean)
    .slice(0, 6)
}

function toggleFilter(field: 'countryCode' | 'directionCode', value: string) {
  filters[field] = filters[field] === value ? '' : value
}

function onSubjectSelect(code: string) {
  const next = filters.subjectCategoryCode === code ? '' : code
  filters.subjectCategoryCode = next
  if (next && filters.directionCode) {
    const stillExists = meta.directions.some((item) => item.parentCode === next && item.code === filters.directionCode)
    if (!stillExists) filters.directionCode = ''
  }
}

function setRankPreset(preset: RankPreset) {
  rankPreset.value = preset
  if (preset === 'ALL') {
    filters.rankMin = undefined
    filters.rankMax = undefined
  } else if (preset === 'TOP20') {
    filters.rankMin = 1
    filters.rankMax = 20
  } else if (preset === 'TOP50') {
    filters.rankMin = 20
    filters.rankMax = 50
  } else if (preset === 'TOP100') {
    filters.rankMin = 50
    filters.rankMax = 100
  }
}

function resetFilters() {
  filters.countryCode = ''
  filters.subjectCategoryCode = ''
  filters.directionCode = ''
  filters.rankMin = undefined
  filters.rankMax = undefined
  filters.keyword = ''
  rankPreset.value = 'ALL'
  currentPage.value = 1
  expandedSchoolIds.value = new Set()
  search().catch(() => {
    // ignored
  })
}

function regroupPrograms(programs: ProgramListItem[]) {
  const grouped = new Map<number, ProgramListItem[]>()
  for (const item of programs) {
    const list = grouped.get(item.schoolId) || []
    list.push(item)
    grouped.set(item.schoolId, list)
  }
  programsBySchool.value = grouped
}

async function search() {
  loading.value = true
  try {
    const schoolList = await listSchools({
      countryCode: filters.countryCode || undefined,
      subjectCategoryCode: filters.subjectCategoryCode || undefined,
      directionCode: filters.directionCode || undefined,
      rankMin: filters.rankMin,
      rankMax: filters.rankMax,
      keyword: filters.keyword || undefined
    })
    schools.value = schoolList
    currentPage.value = 1
    expandedSchoolIds.value = new Set()

    const programs = await listPrograms({
      countryCode: filters.countryCode || undefined,
      subjectCategoryCode: filters.subjectCategoryCode || undefined,
      directionCode: filters.directionCode || undefined,
      keyword: filters.keyword || undefined
    })

    const schoolIdSet = new Set(schoolList.map((item) => item.id))
    regroupPrograms(programs.filter((item) => schoolIdSet.has(item.schoolId)))
  } catch (error) {
    ElMessage.error(readError(error))
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  expandedSchoolIds.value = new Set()
  nextTick(() => {
    const element = pageTopRef.value
    if (!element) return
    const top = element.getBoundingClientRect().top + window.scrollY - 84
    window.scrollTo({ top: Math.max(top, 0), behavior: 'smooth' })
  })
}

function toggleExpand(schoolId: number) {
  const next = new Set(expandedSchoolIds.value)
  if (next.has(schoolId)) next.delete(schoolId)
  else next.add(schoolId)
  expandedSchoolIds.value = next
}

async function addToList(programId: number) {
  if (!authStore.isLoggedIn) {
    await confirmLoginRequired(router, '加入申请清单')
    return
  }
  addingProgramId.value = programId
  try {
    await addApplication(programId)
    ElMessage.success('已加入申请清单')
  } catch (error) {
    ElMessage.error(readError(error))
  } finally {
    addingProgramId.value = null
  }
}

onMounted(async () => {
  try {
    const result = await getUniversityMeta()
    meta.countries = result.countries
    meta.subjectCategories = result.subjectCategories
    meta.directions = result.directions
    meta.applicationStatuses = result.applicationStatuses
    await search()
  } catch (error) {
    ElMessage.error(readError(error))
  }
})
</script>

<style scoped>
.universities-page {
  --accent: #0f4c81;
  --accent-soft: #e9f1f8;
  --line: #dfe6ef;
  --text-main: #1f2f46;
  --text-sub: #5f6f86;
  display: grid;
  gap: 16px;
}

.page-head {
  display: grid;
  gap: 6px;
  padding: 6px 2px 2px;
}

.crumb {
  margin: 0;
  color: #7d8ca3;
  font-size: 12px;
  letter-spacing: 0.03em;
}

.filter-panel {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px 14px 12px;
  background: linear-gradient(180deg, #fbfdff 0%, #f7faff 100%);
  display: grid;
  gap: 12px;
}

.filter-row {
  display: grid;
  grid-template-columns: 90px minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #e9eef5;
}

.filter-row:last-of-type {
  border-bottom: none;
  padding-bottom: 0;
}

.filter-label {
  color: var(--text-main);
  font-weight: 600;
  font-size: 14px;
}

.chip-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  border: none;
  border-bottom: 2px solid transparent;
  background: transparent;
  color: #3c4e68;
  border-radius: 0;
  padding: 4px 2px;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s ease, border-color 0.2s ease;
}

.chip.active {
  border-color: var(--accent);
  color: var(--accent);
  font-weight: 600;
}

.rank-inputs {
  grid-column: 3 / 4;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-sub);
}

.filter-actions {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 90px 100px;
  gap: 8px;
}

.result-list {
  display: grid;
  gap: 14px;
}

.school-list-inner {
  display: grid;
  gap: 14px;
}

.pager-wrap {
  margin-top: 6px;
  display: flex;
  justify-content: flex-end;
}

.school-pagination :deep(.btn-prev),
.school-pagination :deep(.btn-next),
.school-pagination :deep(.el-pager li) {
  border: 1px solid #d8e5f4;
  border-radius: 10px;
  min-width: 34px;
  height: 34px;
  margin: 0 4px;
  color: #355073;
  background: linear-gradient(180deg, #ffffff, #f4f8ff);
  transition: transform 0.22s ease, box-shadow 0.22s ease, color 0.22s ease, border-color 0.22s ease;
}

.school-pagination :deep(.btn-prev:hover),
.school-pagination :deep(.btn-next:hover),
.school-pagination :deep(.el-pager li:hover) {
  transform: translateY(-2px) scale(1.03);
  box-shadow: 0 8px 16px rgba(41, 86, 146, 0.18);
  border-color: #8cb4e4;
  color: #0f4c81;
}

.school-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(120deg, #0f4c81, #2f6aa1);
  color: #fff;
  border-color: #0f4c81;
  box-shadow: 0 10px 18px rgba(15, 76, 129, 0.24);
  transform: translateY(-1px);
}

.school-fade-enter-active,
.school-fade-leave-active {
  transition: all 0.28s ease;
}

.school-fade-enter-from {
  opacity: 0;
  transform: translateY(8px) scale(0.995);
}

.school-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.995);
}

.school-card {
  border: 1px solid var(--line);
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 8px 28px rgba(26, 41, 64, 0.06);
}

.school-main {
  padding: 16px;
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
}

.school-logo {
  width: 92px;
  height: 92px;
  border-radius: 10px;
  border: 1px solid #d9e3ef;
  background: linear-gradient(145deg, #19283f, #0f1c30);
  color: #d9e5f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.school-center {
  display: grid;
  gap: 6px;
}

.school-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.school-title-row h3 {
  margin: 0;
  color: var(--text-main);
  font-size: 30px;
  line-height: 1.2;
}

.school-en {
  margin: 2px 0 0;
  color: #7a899f;
  font-size: 15px;
}

.rank-badge {
  border-radius: 999px;
  background: var(--accent-soft);
  color: var(--accent);
  padding: 5px 12px;
  font-weight: 600;
  font-size: 13px;
}

.school-meta {
  margin: 0;
  color: var(--text-sub);
  font-size: 14px;
}

.tag-list {
  margin-top: 2px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  border: 1px solid #d6e3f2;
  color: #3f5e86;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: #f7fbff;
}

.school-actions {
  align-self: center;
  justify-self: end;
}

.program-list {
  border-top: 1px solid #e8eef6;
  padding: 12px 16px;
  background: #fbfcfe;
}

.program-rows {
  display: grid;
  gap: 10px;
}

.program-row {
  border: 1px solid #e4eaf2;
  border-radius: 10px;
  padding: 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.program-name {
  font-weight: 600;
  color: #2b3d58;
  font-size: 14px;
}

.program-sub {
  margin-top: 4px;
  color: #7688a2;
  font-size: 13px;
}

@media (max-width: 960px) {
  .filter-row {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }

  .rank-inputs {
    grid-column: 1 / 2;
    margin-top: 4px;
  }

  .filter-actions {
    grid-template-columns: 1fr;
  }

  .program-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .school-main {
    grid-template-columns: 1fr;
  }

  .school-actions {
    justify-self: start;
  }
}
</style>
