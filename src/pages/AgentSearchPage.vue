<template>
  <section class="agent-search-page fade-up">
    <div class="search-panel">
      <div class="search-bar">
        <el-input
          v-model="filters.keyword"
          size="large"
          placeholder="搜索学生姓名、学校、专业、目标国家"
          clearable
          @keyup.enter="loadStudents(1)"
        >
          <template #suffix><Search class="search-icon" /></template>
        </el-input>
        <el-button type="primary" size="large" :loading="loading" @click="loadStudents(1)">搜索</el-button>
      </div>

      <div class="filter-row">
        <span class="filter-label">目标国家</span>
        <div class="chip-group">
          <button v-for="item in countryOptions" :key="item.value" class="chip" :class="{ active: filters.country === item.value }" @click="setFilter('country', item.value)">
            {{ item.label }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <span class="filter-label">学历层次</span>
        <div class="chip-group">
          <button v-for="item in educationOptions" :key="item.value" class="chip" :class="{ active: filters.educationLevel === item.value }" @click="setFilter('educationLevel', item.value)">
            {{ item.label }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <span class="filter-label">成绩语言</span>
        <div class="chip-group">
          <button v-for="item in scoreOptions" :key="item.value" class="chip" :class="{ active: filters.scoreBucket === item.value }" @click="setFilter('scoreBucket', item.value)">
            {{ item.label }}
          </button>
        </div>
      </div>

      <div class="filter-row">
        <span class="filter-label">学科领域</span>
        <div class="chip-group">
          <button class="chip" :class="{ active: !filters.subjectCategoryCode }" @click="setFilter('subjectCategoryCode', '')">不限</button>
          <button
            v-for="item in subjectOptions"
            :key="item.code"
            class="chip"
            :class="{ active: filters.subjectCategoryCode === item.code }"
            @click="setFilter('subjectCategoryCode', item.code)"
          >
            {{ item.name }}
          </button>
        </div>
      </div>
    </div>

    <div class="result-head">
      <strong>学生库</strong>
      <span>共 {{ total }} 位匹配学生</span>
    </div>

    <div class="student-list" v-loading="loading">
      <el-empty v-if="students.length === 0 && !loading" description="暂无匹配学生" />
      <article v-for="student in students" :key="student.studentUserId" class="student-card">
        <el-button class="chat-btn" type="primary" plain :loading="startingStudentId === student.studentUserId" @click="openChat(student)">沟通</el-button>
        <div class="avatar">{{ student.displayName.slice(0, 1) || '学' }}</div>
        <div class="student-main">
          <div class="student-title">
            <h3>{{ student.displayName }}</h3>
            <span>{{ educationLabel(student.educationLevel) }}</span>
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
      <el-pagination background layout="prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="loadStudents" />
    </div>

    <el-dialog v-model="teamDialogVisible" title="选择沟通团队产品" width="420px">
      <el-select v-model="selectedTeamId" class="full" placeholder="请选择团队产品">
        <el-option v-for="team in publishedTeams" :key="team.teamId" :label="team.teamName" :value="team.teamId" />
      </el-select>
      <template #footer>
        <el-button @click="teamDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="startingStudentId !== null" @click="confirmStartChat">开始沟通</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyWorkbenchAccess, listTeamProducts, searchAgentStudents } from '../services/agency'
import { agentStartChat } from '../services/message'
import { getUniversityMeta } from '../services/university'
import type { AgentStudentSearchItem, TeamProductSummaryItem } from '../types/agency'
import type { FilterOption } from '../types/university'

const router = useRouter()
const pageSize = 12

const filters = reactive({
  keyword: '',
  country: '',
  educationLevel: '',
  scoreBucket: '',
  subjectCategoryCode: ''
})

const countryOptions = [
  { label: '不限', value: '' },
  { label: '美国', value: '美国' },
  { label: '英国', value: '英国' },
  { label: '加拿大', value: '加拿大' },
  { label: '澳洲', value: '澳洲' },
  { label: '新加坡', value: '新加坡' },
  { label: '中国香港', value: '中国香港' },
  { label: '欧洲', value: 'EUROPE' },
  { label: '其他', value: 'OTHER' }
]

const educationOptions = [
  { label: '不限', value: '' },
  { label: '高中', value: 'HIGH_SCHOOL' },
  { label: '专科', value: 'COLLEGE' },
  { label: '本科', value: 'UNDERGRAD' },
  { label: '硕士', value: 'MASTER' },
  { label: '博士', value: 'PHD' },
  { label: '其他', value: 'OTHER' }
]

const scoreOptions = [
  { label: '不限', value: '' },
  { label: 'GPA 3.0+', value: 'GPA_3_0' },
  { label: 'GPA 3.3+', value: 'GPA_3_3' },
  { label: 'GPA 3.5+', value: 'GPA_3_5' },
  { label: '雅思 6.5+', value: 'IELTS_6_5' },
  { label: '雅思 7+', value: 'IELTS_7' },
  { label: '托福 90+', value: 'TOEFL_90' },
  { label: '托福 100+', value: 'TOEFL_100' }
]

const educationLabels: Record<string, string> = {
  HIGH_SCHOOL: '高中',
  COLLEGE: '专科',
  UNDERGRAD: '本科',
  MASTER: '硕士',
  PHD: '博士',
  OTHER: '其他'
}

const students = ref<AgentStudentSearchItem[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const subjectOptions = ref<FilterOption[]>([])
const teams = ref<TeamProductSummaryItem[]>([])
const canChatStudent = ref(false)
const startingStudentId = ref<number | null>(null)
const pendingStudent = ref<AgentStudentSearchItem | null>(null)
const teamDialogVisible = ref(false)
const selectedTeamId = ref<number | null>(null)

const publishedTeams = computed(() => teams.value.filter((team) => team.publishStatus === 'PUBLISHED'))

onMounted(async () => {
  await Promise.all([loadMeta(), loadAccessAndTeams(), loadStudents(1)])
})

function setFilter(field: keyof typeof filters, value: string) {
  filters[field] = filters[field] === value ? '' : value
  loadStudents(1)
}

async function loadMeta() {
  try {
    const meta = await getUniversityMeta()
    subjectOptions.value = meta.subjectCategories.filter((item) => ['ENG', 'BUS', 'SCI', 'HUM'].includes(item.code))
  } catch {
    subjectOptions.value = [
      { code: 'ENG', name: '工科' },
      { code: 'BUS', name: '商科' },
      { code: 'SCI', name: '理科' },
      { code: 'HUM', name: '文社科' }
    ]
  }
}

async function loadAccessAndTeams() {
  try {
    const [access, productList] = await Promise.all([
      getMyWorkbenchAccess(),
      listTeamProducts()
    ])
    canChatStudent.value = access.canChatStudent
    teams.value = productList
  } catch {
    canChatStudent.value = false
    teams.value = []
  }
}

async function loadStudents(nextPage = page.value) {
  page.value = nextPage
  loading.value = true
  try {
    const data = await searchAgentStudents({
      page: page.value,
      pageSize,
      keyword: filters.keyword.trim(),
      country: filters.country,
      educationLevel: filters.educationLevel,
      scoreBucket: filters.scoreBucket,
      subjectCategoryCode: filters.subjectCategoryCode
    })
    students.value = data.records
    total.value = data.total
  } catch (error: any) {
    ElMessage.error(error?.message || '学生搜索失败')
  } finally {
    loading.value = false
  }
}

async function openChat(student: AgentStudentSearchItem) {
  if (!canChatStudent.value) {
    ElMessage.warning('你当前没有沟通学生权限，请联系机构管理员开通。')
    return
  }
  if (publishedTeams.value.length === 0) {
    ElMessage.warning('请先发布至少一个团队产品，再发起沟通。')
    return
  }
  pendingStudent.value = student
  if (publishedTeams.value.length === 1) {
    selectedTeamId.value = publishedTeams.value[0].teamId
    await confirmStartChat()
    return
  }
  selectedTeamId.value = publishedTeams.value[0]?.teamId || null
  teamDialogVisible.value = true
}

async function confirmStartChat() {
  if (!pendingStudent.value || !selectedTeamId.value) {
    ElMessage.warning('请选择团队产品')
    return
  }
  startingStudentId.value = pendingStudent.value.studentUserId
  try {
    const data = await agentStartChat({
      studentUserId: pendingStudent.value.studentUserId,
      teamId: selectedTeamId.value
    })
    teamDialogVisible.value = false
    await router.push({ path: '/agent-workbench/communication', query: { conversationId: data.conversation.conversationId } })
  } catch (error: any) {
    ElMessage.error(error?.message || '发起沟通失败')
  } finally {
    startingStudentId.value = null
  }
}

function educationLabel(value?: string | null) {
  return value ? educationLabels[value] || value : '学历待完善'
}

function formatGpa(student: AgentStudentSearchItem) {
  if (student.gpaValue == null) return '待完善'
  const suffix = student.gpaScale === 'PERCENTAGE' ? '/100' : '/4.0'
  return `${student.gpaValue}${suffix}`
}
</script>

<style scoped>
.agent-search-page {
  min-height: calc(100vh - 40px);
}

.search-panel,
.student-card {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 12px 28px rgba(16, 34, 63, 0.06);
}

.search-panel {
  padding: 18px;
}

.search-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  margin-bottom: 18px;
}

.search-bar :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 8px;
  font-size: 15px;
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #7f8fa0;
}

.filter-row {
  display: grid;
  grid-template-columns: 78px minmax(0, 1fr);
  align-items: start;
  gap: 12px;
  padding: 10px 0;
  border-top: 1px solid #edf2f6;
}

.filter-label {
  color: #657789;
  font-size: 14px;
  line-height: 32px;
  font-weight: 650;
}

.chip-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  height: 32px;
  padding: 0 13px;
  border: 1px solid #d6dde6;
  border-radius: 999px;
  background: #fff;
  color: #26323c;
  font-size: 13px;
  font-weight: 650;
  cursor: pointer;
  transition: background .18s ease, color .18s ease, border-color .18s ease, box-shadow .18s ease;
}

.chip.active,
.chip:hover {
  border-color: #409eff;
  background: #409eff;
  color: #fff;
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.18);
}

.result-head {
  margin: 16px 2px 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #6a7a88;
}

.result-head strong {
  color: #172638;
  font-size: 18px;
}

.student-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  min-height: 260px;
}

.student-card {
  position: relative;
  display: grid;
  grid-template-columns: 54px minmax(0, 1fr);
  gap: 14px;
  padding: 18px;
  transition: transform .18s ease, border-color .18s ease, box-shadow .18s ease;
}

.student-card:hover {
  border-color: #95c8f5;
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(42, 96, 145, 0.12);
}

.chat-btn {
  position: absolute;
  top: 14px;
  right: 14px;
  opacity: 0;
  transform: translateY(-3px);
  transition: opacity .18s ease, transform .18s ease;
}

.student-card:hover .chat-btn {
  opacity: 1;
  transform: translateY(0);
}

.avatar {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #fff;
  background: #4db7c0;
  font-size: 20px;
  font-weight: 800;
}

.student-main {
  min-width: 0;
  padding-right: 84px;
}

.student-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.student-title h3,
.student-sub {
  margin: 0;
}

.student-title h3 {
  color: #172638;
  font-size: 18px;
  line-height: 1.3;
}

.student-title span {
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  color: #2e6790;
  background: #eef8ff;
  border: 1px solid #cce6f7;
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
}

.student-sub {
  margin-top: 7px;
  color: #687b88;
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.info-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 12px;
  color: #526577;
  font-size: 13px;
  line-height: 1.35;
}

.info-grid span {
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.pager-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.full {
  width: 100%;
}

@media (max-width: 980px) {
  .search-bar,
  .student-list {
    grid-template-columns: 1fr;
  }

  .filter-row {
    grid-template-columns: 1fr;
    gap: 6px;
  }

  .filter-label {
    line-height: 1.4;
  }
}
</style>
