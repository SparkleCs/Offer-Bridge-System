<template>
  <div class="me-layout fade-up">
    <aside class="left-nav page-card">
      <h3>简历目录</h3>
      <button
        v-for="item in navItems"
        :key="item.id"
        class="nav-item"
        :class="{ active: activeSection === item.id }"
        @click="scrollToSection(item.id)"
      >
        {{ item.label }}
      </button>
    </aside>

    <main ref="centerPaneRef" class="center-pane" @scroll="onCenterScroll">
      <section id="basic" class="page-card section-card">
        <h2>基础信息</h2>
        <el-form label-position="top">
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="姓名"><el-input v-model="basicForm.name" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="basicForm.email" /></el-form-item></el-col>
            <el-col :span="12">
              <el-form-item label="学历层次">
                <el-select v-model="basicForm.educationLevel">
                  <el-option v-for="item in educationOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12"><el-form-item label="学校"><el-input v-model="basicForm.schoolName" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="当前专业"><el-input v-model="basicForm.major" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="意向专业"><el-input v-model="basicForm.targetMajorText" placeholder="例如：计算机科学/数据科学" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="入学季"><el-input v-model="basicForm.intakeTerm" placeholder="例如：2027 Fall" /></el-form-item></el-col>
            <el-col :span="12">
              <el-form-item label="目标国家（多选）">
                <el-select v-model="selectedCountryNames" multiple collapse-tags collapse-tags-tooltip placeholder="请选择目标国家">
                  <el-option v-for="item in targetCountryOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="预算">
            <div class="budget-row">
              <el-select v-model="basicForm.budgetCurrency" placeholder="币种">
                <el-option label="CNY" value="CNY" />
                <el-option label="USD" value="USD" />
                <el-option label="GBP" value="GBP" />
                <el-option label="EUR" value="EUR" />
              </el-select>
              <el-input-number v-model="basicForm.budgetMin" :min="0" controls-position="right" placeholder="最低预算" />
              <el-input-number v-model="basicForm.budgetMax" :min="0" controls-position="right" placeholder="最高预算" />
            </div>
          </el-form-item>
          <el-form-item label="预算备注（仅自己可见）"><el-input v-model="basicForm.budgetNote" type="textarea" :rows="2" /></el-form-item>
        </el-form>

        <div class="section-footer">
          <div class="footer-left"></div>
          <el-button type="primary" :loading="savingBasic" @click="saveBasic">保存基础信息</el-button>
        </div>
      </section>

      <section id="applicationList" class="page-card section-card">
        <div class="section-head-inline">
          <h2>申请清单</h2>
          <el-button :loading="loadingApplicationList" @click="loadApplicationList">刷新</el-button>
        </div>

        <el-empty v-if="applicationList.items.length === 0 && !loadingApplicationList" description="暂无申请清单，请先从院校项目页添加" />

        <el-table v-else :data="applicationList.items" v-loading="loadingApplicationList" stripe>
          <el-table-column label="项目" min-width="230">
            <template #default="{ row }">
              <div class="program-cell-title">{{ row.program.programName }}</div>
              <div class="program-cell-sub">{{ row.program.schoolNameCn }} · {{ row.program.directionName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="匹配" width="120">
            <template #default="{ row }">
              <div class="match-mini-score">{{ row.matchResult.matchScore }}</div>
              <div class="match-mini-tier">{{ row.matchResult.matchTier }}</div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="180">
            <template #default="{ row }">
              <el-select
                :model-value="row.statusCode"
                :disabled="updatingApplicationId === row.id"
                @update:model-value="(value: string) => changeApplicationStatus(row.id, String(value))"
              >
                <el-option v-for="option in applicationStatusOptions" :key="option.code" :label="option.name" :value="option.code" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button
                link
                type="danger"
                :loading="removingApplicationId === row.id"
                @click="removeApplicationItem(row.id)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <section id="academic" class="page-card section-card">
        <h2>学术背景</h2>
        <el-form label-position="top">
          <el-row :gutter="12">
            <el-col :span="8">
              <el-form-item label="GPA 制式">
                <el-select v-model="academicForm.gpaScale">
                  <el-option label="4.0 制" value="FOUR_POINT" />
                  <el-option label="百分制" value="PERCENTAGE" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="GPA 分数">
                <el-input-number
                  v-model="academicForm.gpaValue"
                  :min="0"
                  :max="gpaMax"
                  :step="gpaStep"
                  controls-position="right"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="排名百分位">
                <div class="rank-percent-row">
                  <span class="rank-prefix">前</span>
                  <el-select
                    v-model="rankInputText"
                    filterable
                    allow-create
                    default-first-option
                    placeholder="选择或输入"
                    @change="onRankChange"
                  >
                    <el-option v-for="item in rankQuickOptions" :key="item" :label="String(item)" :value="String(item)" />
                  </el-select>
                  <span class="rank-suffix">%</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <div class="section-head-inline">
            <h3>语言成绩</h3>
            <el-button size="small" :disabled="academicForm.languageScores.length >= 5" @click="addLanguageScore">新增</el-button>
          </div>
          <div v-for="(item, index) in academicForm.languageScores" :key="index" class="score-row">
            <el-select v-model="item.languageType" placeholder="语言类型">
              <el-option v-for="type in languageOptions" :key="type" :label="type" :value="type" />
            </el-select>
            <el-input-number v-model="item.score" :min="0" :max="1000" :step="0.5" controls-position="right" />
            <el-button text type="danger" @click="removeLanguageScore(index)">删除</el-button>
          </div>
        </el-form>

        <div class="section-footer">
          <div class="footer-left"></div>
          <el-button type="primary" :loading="savingAcademic" @click="saveAcademic">保存学术背景</el-button>
        </div>
      </section>

      <section id="exchange" class="page-card section-card">
        <h2>交换经历</h2>
        <el-form label-position="top">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="国家">
                <el-select v-model="exchangeForm.countryName" placeholder="请选择国家">
                  <el-option v-for="item in targetCountryOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12"><el-form-item label="大学"><el-input v-model="exchangeForm.universityName" placeholder="请输入交换大学" /></el-form-item></el-col>
            <el-col :span="12">
              <el-form-item label="GPA（4分满分）">
                <el-input-number
                  v-model="exchangeForm.gpaValue"
                  :min="0"
                  :max="4"
                  :step="0.01"
                  controls-position="right"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6"><el-form-item label="开始时间"><el-input v-model="exchangeForm.startDate" placeholder="如：2025-09" /></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="结束时间"><el-input v-model="exchangeForm.endDate" placeholder="如：2026-01" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="主修课程">
            <el-input
              v-model="exchangeForm.majorCourses"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 8 }"
              placeholder="请输入主修课程，可用逗号分隔"
            />
          </el-form-item>
        </el-form>

        <div class="section-footer">
          <div class="footer-left"></div>
          <el-button type="primary" :loading="savingExchange" @click="saveExchange">保存交换经历</el-button>
        </div>
      </section>

      <section id="research" class="page-card section-card">
        <h2>科研经历</h2>
        <div v-for="(item, index) in researchForm.items" :key="index" class="list-card">
          <div class="section-head-inline">
            <strong>科研经历 {{ index + 1 }}</strong>
            <el-button text type="danger" @click="removeResearch(index)">删除</el-button>
          </div>
          <el-row :gutter="12">
            <el-col :span="12"><el-input v-model="item.projectName" placeholder="项目名称" /></el-col>
            <el-col :span="6"><el-input v-model="item.startDate" placeholder="开始时间" /></el-col>
            <el-col :span="6"><el-input v-model="item.endDate" placeholder="结束时间" /></el-col>
          </el-row>
          <el-input v-model="item.contentSummary" type="textarea" :autosize="{ minRows: 2, maxRows: 8 }" placeholder="科研内容" class="mt8" />
          <el-switch v-model="item.hasPublication" active-text="有发表论文" inactive-text="无发表论文" class="mt8" />

          <div v-if="item.hasPublication" class="mt8">
            <div class="section-head-inline">
              <span>论文发表</span>
              <el-button size="small" @click="addPublication(index)">新增论文</el-button>
            </div>
            <div v-for="(pub, pubIndex) in item.publications" :key="pubIndex" class="pub-row">
              <el-input v-model="pub.title" placeholder="论文标题" />
              <el-input v-model="pub.authorRole" placeholder="作者身份" />
              <el-input v-model="pub.journalName" placeholder="期刊/会议" />
              <el-input-number v-model="pub.publishedYear" :min="1900" :max="2100" controls-position="right" />
              <el-button text type="danger" @click="removePublication(index, pubIndex)">删除</el-button>
            </div>
          </div>
        </div>

        <div class="section-footer">
          <el-button @click="addResearch">新增</el-button>
          <el-button type="primary" :loading="savingResearch" @click="saveResearch">保存</el-button>
        </div>
      </section>

      <section id="competition" class="page-card section-card">
        <h2>竞赛经历</h2>
        <div v-for="(item, index) in competitionForm.items" :key="index" class="list-card">
          <div class="section-head-inline">
            <strong>竞赛 {{ index + 1 }}</strong>
            <el-button text type="danger" @click="removeCompetition(index)">删除</el-button>
          </div>
          <el-row :gutter="12">
            <el-col :span="8"><el-input v-model="item.competitionName" placeholder="竞赛名称" /></el-col>
            <el-col :span="6"><el-input v-model="item.competitionLevel" placeholder="级别" /></el-col>
            <el-col :span="6"><el-input v-model="item.award" placeholder="奖项" /></el-col>
            <el-col :span="4"><el-input v-model="item.eventDate" placeholder="时间" /></el-col>
          </el-row>
          <el-input v-model="item.roleDesc" type="textarea" :autosize="{ minRows: 2, maxRows: 8 }" placeholder="职责说明" class="mt8" />
        </div>

        <div class="section-footer">
          <el-button @click="addCompetition">新增</el-button>
          <el-button type="primary" :loading="savingCompetition" @click="saveCompetition">保存</el-button>
        </div>
      </section>

      <section id="work" class="page-card section-card">
        <h2>工作/实习经历</h2>
        <div v-for="(item, index) in workForm.items" :key="index" class="list-card">
          <div class="section-head-inline">
            <strong>工作经历 {{ index + 1 }}</strong>
            <el-button text type="danger" @click="removeWork(index)">删除</el-button>
          </div>
          <el-row :gutter="12">
            <el-col :span="8"><el-input v-model="item.companyName" placeholder="公司名称" /></el-col>
            <el-col :span="6"><el-input v-model="item.positionName" placeholder="岗位" /></el-col>
            <el-col :span="5"><el-input v-model="item.startDate" placeholder="开始时间" /></el-col>
            <el-col :span="5"><el-input v-model="item.endDate" placeholder="结束时间" /></el-col>
          </el-row>
          <el-input v-model="item.keywords" placeholder="关键词（逗号分隔）" class="mt8" />
          <el-input
            v-model="item.contentSummary"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 10 }"
            placeholder="工作内容"
            class="mt8"
          />
        </div>

        <div class="section-footer">
          <el-button @click="addWork">新增</el-button>
          <el-button type="primary" :loading="savingWork" @click="saveWork">保存</el-button>
        </div>
      </section>

      <section id="verification" class="page-card section-card">
        <h2>身份认证</h2>
        <p class="muted">当前状态：实名 {{ verificationStatus.realNameStatus }} / 学籍 {{ verificationStatus.educationStatus }}</p>
        <el-form label-position="top">
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="真实姓名"><el-input v-model="verifyForm.realName" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="身份证号"><el-input v-model="verifyForm.idNo" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="身份证图片">
                <el-upload :show-file-list="false" :http-request="handleIdCardUpload" accept="image/*">
                  <el-button :loading="uploadingIdCard">上传身份证</el-button>
                </el-upload>
                <div v-if="verifyForm.idCardImageUrl" class="upload-result">{{ verifyForm.idCardImageUrl }}</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学生证图片">
                <el-upload :show-file-list="false" :http-request="handleStudentCardUpload" accept="image/*">
                  <el-button :loading="uploadingStudentCard">上传学生证</el-button>
                </el-upload>
                <div v-if="verifyForm.studentCardImageUrl" class="upload-result">{{ verifyForm.studentCardImageUrl }}</div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div class="section-footer">
          <div class="footer-left"></div>
          <el-button type="primary" :loading="savingVerification" @click="submitVerification">保存</el-button>
        </div>
      </section>
    </main>

    <aside class="right-progress page-card">
      <h3>申请进度</h3>
      <div class="percent">{{ progress.overallPercent }}%</div>
      <el-progress :percentage="progress.overallPercent" :stroke-width="10" />
      <p class="current">当前阶段：{{ progress.currentStage }}</p>
      <p class="next">下一步：{{ progress.nextAction }}</p>
      <p class="updated">更新于 {{ progress.updatedAtText }}</p>
      <div class="milestones">
        <div v-for="item in progress.milestones" :key="item.name" class="milestone" :class="item.status">
          <div class="dot"></div>
          <div class="content">
            <div class="name">{{ item.name }}</div>
            <div class="eta">{{ item.etaText }}</div>
          </div>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { ApiError } from '../services/http'
import {
  getStudentCompetition,
  getStudentExchangeExperience,
  getStudentResearch,
  getStudentSourceCheck,
  getStudentVerificationStatus,
  getStudentWork,
  saveStudentCompetition,
  saveStudentExchangeExperience,
  saveStudentResearch,
  saveStudentWork,
  submitStudentVerification,
  updateStudentAcademicProfile,
  updateStudentBasicProfile,
  uploadStudentFile
} from '../services/student'
import {
  getApplicationList,
  updateApplicationStatus as updateUniversityApplicationStatus,
  removeApplication as removeUniversityApplication
} from '../services/university'
import type {
  ApplicationProgressView,
  CompetitionItem,
  EducationLevel,
  ExchangeExperience,
  GpaScale,
  LanguageType,
  PublicationItem,
  ResearchItem,
  WorkItem
} from '../types/student'
import type { ApplicationListItem } from '../types/university'

interface LoadErrorItem {
  source: string
  message: string
  traceId?: string | null
}

const authStore = useAuthStore()
const centerPaneRef = ref<HTMLElement | null>(null)
const activeSection = ref('basic')
const rankInputText = ref('')
const loadErrors = ref<LoadErrorItem[]>([])

const navItems = [
  { id: 'basic', label: '基础信息' },
  { id: 'applicationList', label: '申请清单' },
  { id: 'academic', label: '学术背景' },
  { id: 'exchange', label: '交换经历' },
  { id: 'research', label: '科研经历' },
  { id: 'competition', label: '竞赛经历' },
  { id: 'work', label: '工作经历' },
  { id: 'verification', label: '身份认证' }
]

const targetCountryOptions = ['英国', '美国', '澳洲', '新西兰']
const rankQuickOptions = [50, 40, 30, 20, 10]

const educationOptions: Array<{ label: string; value: EducationLevel }> = [
  { label: '高中', value: 'HIGH_SCHOOL' },
  { label: '大专', value: 'COLLEGE' },
  { label: '本科', value: 'UNDERGRAD' },
  { label: '硕士', value: 'MASTER' },
  { label: '博士', value: 'PHD' },
  { label: '其他', value: 'OTHER' }
]

const languageOptions: LanguageType[] = ['CET4', 'CET6', 'TOEFL', 'IELTS', 'PTE']

const savingBasic = ref(false)
const savingAcademic = ref(false)
const savingExchange = ref(false)
const savingResearch = ref(false)
const savingCompetition = ref(false)
const savingWork = ref(false)
const savingVerification = ref(false)
const loadingApplicationList = ref(false)
const updatingApplicationId = ref<number | null>(null)
const removingApplicationId = ref<number | null>(null)
const uploadingIdCard = ref(false)
const uploadingStudentCard = ref(false)

const basicForm = reactive({
  name: '',
  email: '',
  educationLevel: 'UNDERGRAD' as EducationLevel,
  schoolName: '',
  major: '',
  targetMajorText: '',
  intakeTerm: '',
  budgetCurrency: 'CNY',
  budgetMin: null as number | null,
  budgetMax: null as number | null,
  budgetNote: '',
  targetCountries: [] as Array<{ countryName: string }>
})

const academicForm = reactive({
  gpaValue: 3,
  gpaScale: 'FOUR_POINT' as GpaScale,
  rankValue: null as number | null,
  languageScores: [{ languageType: 'IELTS' as LanguageType, score: 6.5 }]
})

const researchForm = reactive({ items: [] as ResearchItem[] })
const competitionForm = reactive({ items: [] as CompetitionItem[] })
const workForm = reactive({ items: [] as WorkItem[] })
const exchangeForm = reactive<ExchangeExperience>({
  countryName: '',
  universityName: '',
  gpaValue: null,
  majorCourses: '',
  startDate: '',
  endDate: ''
})

const applicationList = reactive({
  items: [] as ApplicationListItem[]
})

const applicationStatusOptions = [
  { code: 'COLLECTED', name: '已收藏' },
  { code: 'TO_EVALUATE', name: '待评估' },
  { code: 'PREPARING', name: '准备申请' },
  { code: 'APPLYING', name: '申请中' },
  { code: 'SUBMITTED', name: '已提交' },
  { code: 'ADMITTED', name: '已录取' },
  { code: 'REJECTED', name: '已拒绝' }
]

const verifyForm = reactive({
  realName: '',
  idNo: '',
  idCardImageUrl: '',
  studentCardImageUrl: ''
})

const verificationStatus = reactive({
  realNameStatus: 'UNVERIFIED',
  educationStatus: 'UNVERIFIED'
})

const selectedCountryNames = computed({
  get: () => basicForm.targetCountries.map((it) => it.countryName),
  set: (values: string[]) => {
    basicForm.targetCountries = values.map((countryName) => ({ countryName }))
  }
})

const gpaStep = computed(() => (academicForm.gpaScale === 'PERCENTAGE' ? 1 : 0.01))
const gpaMax = computed(() => (academicForm.gpaScale === 'PERCENTAGE' ? 100 : 4))

watch(
  () => academicForm.gpaScale,
  (nextScale) => {
    if (nextScale === 'FOUR_POINT' && academicForm.gpaValue > 4) {
      academicForm.gpaValue = 4
    }
    if (nextScale === 'PERCENTAGE' && academicForm.gpaValue > 100) {
      academicForm.gpaValue = 100
    }
  }
)

watch(
  () => academicForm.rankValue,
  (value) => {
    rankInputText.value = value === null ? '' : String(value)
  }
)

function onRankChange(value: string) {
  if (!value) {
    academicForm.rankValue = null
    return
  }
  const num = Number(value)
  academicForm.rankValue = Number.isFinite(num) ? num : null
}

function scrollToSection(id: string) {
  const container = centerPaneRef.value
  if (!container) return
  const node = container.querySelector<HTMLElement>(`#${id}`)
  if (!node) return
  container.scrollTo({ top: node.offsetTop - 10, behavior: 'smooth' })
  activeSection.value = id
}

function onCenterScroll() {
  const container = centerPaneRef.value
  if (!container) return
  const scrollTop = container.scrollTop
  let nearestId = navItems[0].id
  let nearestDiff = Number.MAX_SAFE_INTEGER

  for (const item of navItems) {
    const node = container.querySelector<HTMLElement>(`#${item.id}`)
    if (!node) continue
    const diff = Math.abs(node.offsetTop - scrollTop - 12)
    if (diff < nearestDiff) {
      nearestDiff = diff
      nearestId = item.id
    }
  }
  activeSection.value = nearestId
}

function addLanguageScore() {
  academicForm.languageScores.push({ languageType: 'TOEFL', score: 80 })
}

function removeLanguageScore(index: number) {
  academicForm.languageScores.splice(index, 1)
}

function createResearchItem(): ResearchItem {
  return {
    projectName: '',
    startDate: '',
    endDate: '',
    contentSummary: '',
    hasPublication: false,
    publications: []
  }
}

function createPublication(): PublicationItem {
  return {
    title: '',
    authorRole: '',
    journalName: '',
    publishedYear: null
  }
}

function addResearch() {
  researchForm.items.push(createResearchItem())
}

function removeResearch(index: number) {
  researchForm.items.splice(index, 1)
}

function addPublication(researchIndex: number) {
  researchForm.items[researchIndex].publications.push(createPublication())
}

function removePublication(researchIndex: number, pubIndex: number) {
  researchForm.items[researchIndex].publications.splice(pubIndex, 1)
}

function addCompetition() {
  competitionForm.items.push({
    competitionName: '',
    competitionLevel: '',
    award: '',
    roleDesc: '',
    eventDate: ''
  })
}

function removeCompetition(index: number) {
  competitionForm.items.splice(index, 1)
}

function addWork() {
  workForm.items.push({
    companyName: '',
    positionName: '',
    startDate: '',
    endDate: '',
    keywords: '',
    contentSummary: ''
  })
}

function removeWork(index: number) {
  workForm.items.splice(index, 1)
}

async function loadApplicationList() {
  loadingApplicationList.value = true
  try {
    const result = await getApplicationList()
    applicationList.items = result.items || []
  } finally {
    loadingApplicationList.value = false
  }
}

async function changeApplicationStatus(applicationId: number, statusCode: string) {
  updatingApplicationId.value = applicationId
  try {
    const result = await updateUniversityApplicationStatus(applicationId, statusCode)
    applicationList.items = result.items || []
    ElMessage.success('申请状态已更新')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '状态更新失败')
  } finally {
    updatingApplicationId.value = null
  }
}

async function removeApplicationItem(applicationId: number) {
  removingApplicationId.value = applicationId
  try {
    const result = await removeUniversityApplication(applicationId)
    applicationList.items = result.items || []
    ElMessage.success('已从申请清单移除')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '移除失败')
  } finally {
    removingApplicationId.value = null
  }
}

function validateBasicForm() {
  if (!basicForm.name || !basicForm.educationLevel || !basicForm.schoolName || !basicForm.major) {
    ElMessage.warning('请完整填写基础信息')
    return false
  }
  return true
}

function validateAcademicForm() {
  if (academicForm.gpaScale === 'FOUR_POINT' && (academicForm.gpaValue < 0 || academicForm.gpaValue > 4)) {
    ElMessage.warning('4.0 制 GPA 应在 0-4.0')
    return false
  }
  if (academicForm.gpaScale === 'PERCENTAGE' && (academicForm.gpaValue < 0 || academicForm.gpaValue > 100)) {
    ElMessage.warning('百分制 GPA 应在 0-100')
    return false
  }
  if (academicForm.rankValue !== null && (academicForm.rankValue < 1 || academicForm.rankValue > 100)) {
    ElMessage.warning('排名百分位应在 1-100 之间')
    return false
  }
  const types = new Set(academicForm.languageScores.map((it) => it.languageType))
  if (types.size !== academicForm.languageScores.length) {
    ElMessage.warning('语言类型不能重复')
    return false
  }
  return true
}

function readErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    if (error.detail) {
      return `${error.message} (${error.detail})`
    }
    return error.message
  }
  return '未知错误'
}

async function loadData() {
  loadErrors.value = []

  try {
    const profile = await authStore.loadProfile()
    if (profile) {
      basicForm.name = profile.name || ''
      basicForm.email = profile.email || ''
      basicForm.educationLevel = (profile.educationLevel || 'UNDERGRAD') as EducationLevel
      basicForm.schoolName = profile.schoolName || ''
      basicForm.major = profile.major || ''
      basicForm.targetMajorText = profile.targetMajorText || ''
      basicForm.intakeTerm = profile.intakeTerm || ''
      basicForm.budgetCurrency = profile.budgetCurrency || 'CNY'
      basicForm.budgetMin = profile.budgetMin ?? null
      basicForm.budgetMax = profile.budgetMax ?? null
      basicForm.budgetNote = profile.budgetNote || ''
      basicForm.targetCountries = profile.targetCountries?.length ? profile.targetCountries.map((it) => ({ ...it })) : []

      academicForm.gpaValue = profile.gpaValue ?? 3
      academicForm.gpaScale = (profile.gpaScale || 'FOUR_POINT') as GpaScale
      academicForm.rankValue = profile.rankValue ?? null
      academicForm.languageScores = profile.languageScores?.length
        ? profile.languageScores.map((it) => ({ ...it }))
        : [{ languageType: 'IELTS', score: 6.5 }]
    }
  } catch (error) {
    loadErrors.value.push({
      source: 'profile',
      message: readErrorMessage(error),
      traceId: error instanceof ApiError ? error.traceId : null
    })
    console.error('[MePage] profile load failed', error)
  }

  const tasks: Array<{
    source: string
    request: () => Promise<unknown>
    onSuccess: (value: unknown) => void
  }> = [
    {
      source: 'exchange',
      request: getStudentExchangeExperience,
      onSuccess: (value) => {
        const data = value as ExchangeExperience
        exchangeForm.countryName = data.countryName || ''
        exchangeForm.universityName = data.universityName || ''
        exchangeForm.gpaValue = data.gpaValue ?? null
        exchangeForm.majorCourses = data.majorCourses || ''
        exchangeForm.startDate = data.startDate || ''
        exchangeForm.endDate = data.endDate || ''
      }
    },
    {
      source: 'research',
      request: getStudentResearch,
      onSuccess: (value) => {
        const data = value as { items: ResearchItem[] }
        researchForm.items = data.items?.length ? data.items.map((it) => ({ ...it, publications: it.publications || [] })) : []
      }
    },
    {
      source: 'competition',
      request: getStudentCompetition,
      onSuccess: (value) => {
        const data = value as { items: CompetitionItem[] }
        competitionForm.items = data.items?.length ? data.items.map((it) => ({ ...it })) : []
      }
    },
    {
      source: 'work',
      request: getStudentWork,
      onSuccess: (value) => {
        const data = value as { items: WorkItem[] }
        workForm.items = data.items?.length ? data.items.map((it) => ({ ...it, contentSummary: it.contentSummary || '' })) : []
      }
    },
    {
      source: 'verification',
      request: getStudentVerificationStatus,
      onSuccess: (value) => {
        const data = value as { realNameStatus: string; educationStatus: string }
        verificationStatus.realNameStatus = data.realNameStatus
        verificationStatus.educationStatus = data.educationStatus
      }
    },
    {
      source: 'application-list',
      request: getApplicationList,
      onSuccess: (value) => {
        const data = value as { items: ApplicationListItem[] }
        applicationList.items = data.items || []
      }
    }
  ]

  await Promise.all(
    tasks.map(async (task) => {
      try {
        const value = await task.request()
        task.onSuccess(value)
      } catch (error) {
        loadErrors.value.push({
          source: task.source,
          message: readErrorMessage(error),
          traceId: error instanceof ApiError ? error.traceId : null
        })
        console.error(`[MePage] ${task.source} load failed`, error)
      }
    })
  )

  if (loadErrors.value.length > 0) {
    try {
      const check = await getStudentSourceCheck()
      console.warn('[MePage] source check', check)
    } catch {
      // ignore
    }
    console.warn('[MePage] partial load errors', loadErrors.value)
  }
}

async function saveBasic() {
  if (!validateBasicForm()) return
  savingBasic.value = true
  try {
    const profile = await updateStudentBasicProfile({
      name: basicForm.name,
      email: basicForm.email,
      educationLevel: basicForm.educationLevel,
      schoolName: basicForm.schoolName,
      major: basicForm.major,
      targetMajorText: basicForm.targetMajorText,
      intakeTerm: basicForm.intakeTerm,
      budgetCurrency: basicForm.budgetCurrency,
      budgetMin: basicForm.budgetMin,
      budgetMax: basicForm.budgetMax,
      budgetNote: basicForm.budgetNote,
      targetCountries: basicForm.targetCountries
    })
    authStore.profile = profile
    ElMessage.success('基础信息已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingBasic.value = false
  }
}

async function saveAcademic() {
  if (!validateAcademicForm()) return
  savingAcademic.value = true
  try {
    const profile = await updateStudentAcademicProfile({
      gpaValue: academicForm.gpaValue,
      gpaScale: academicForm.gpaScale,
      rankValue: academicForm.rankValue,
      languageScores: academicForm.languageScores.map((it) => ({ languageType: it.languageType, score: Number(it.score) }))
    })
    authStore.profile = profile
    ElMessage.success('学术背景已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingAcademic.value = false
  }
}

function validateExchangeForm() {
  if (!exchangeForm.countryName || !exchangeForm.universityName || !exchangeForm.majorCourses || !exchangeForm.startDate || !exchangeForm.endDate) {
    ElMessage.warning('请完整填写交换经历信息')
    return false
  }
  if (exchangeForm.gpaValue === null || exchangeForm.gpaValue < 0 || exchangeForm.gpaValue > 4) {
    ElMessage.warning('交换经历 GPA 应在 0-4.00 之间')
    return false
  }
  return true
}

async function saveExchange() {
  if (!validateExchangeForm()) return
  savingExchange.value = true
  try {
    const result = await saveStudentExchangeExperience({
      countryName: exchangeForm.countryName,
      universityName: exchangeForm.universityName,
      gpaValue: exchangeForm.gpaValue,
      majorCourses: exchangeForm.majorCourses,
      startDate: exchangeForm.startDate,
      endDate: exchangeForm.endDate
    })
    exchangeForm.countryName = result.countryName || ''
    exchangeForm.universityName = result.universityName || ''
    exchangeForm.gpaValue = result.gpaValue ?? null
    exchangeForm.majorCourses = result.majorCourses || ''
    exchangeForm.startDate = result.startDate || ''
    exchangeForm.endDate = result.endDate || ''
    ElMessage.success('交换经历已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingExchange.value = false
  }
}

async function saveResearch() {
  savingResearch.value = true
  try {
    const result = await saveStudentResearch({ items: researchForm.items })
    researchForm.items = result.items || []
    ElMessage.success('科研经历已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingResearch.value = false
  }
}

async function saveCompetition() {
  savingCompetition.value = true
  try {
    const result = await saveStudentCompetition({ items: competitionForm.items })
    competitionForm.items = result.items || []
    ElMessage.success('竞赛经历已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingCompetition.value = false
  }
}

async function saveWork() {
  savingWork.value = true
  try {
    const result = await saveStudentWork({ items: workForm.items })
    workForm.items = result.items || []
    ElMessage.success('工作经历已保存')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    savingWork.value = false
  }
}

async function handleIdCardUpload(options: UploadRequestOptions) {
  uploadingIdCard.value = true
  try {
    const result = await uploadStudentFile(options.file)
    verifyForm.idCardImageUrl = result.url
    ElMessage.success('身份证图片上传成功')
    options.onSuccess?.(result as never)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '上传失败')
    options.onError?.(error as never)
  } finally {
    uploadingIdCard.value = false
  }
}

async function handleStudentCardUpload(options: UploadRequestOptions) {
  uploadingStudentCard.value = true
  try {
    const result = await uploadStudentFile(options.file)
    verifyForm.studentCardImageUrl = result.url
    ElMessage.success('学生证图片上传成功')
    options.onSuccess?.(result as never)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '上传失败')
    options.onError?.(error as never)
  } finally {
    uploadingStudentCard.value = false
  }
}

async function submitVerification() {
  if (!verifyForm.realName || !verifyForm.idNo || !verifyForm.idCardImageUrl || !verifyForm.studentCardImageUrl) {
    ElMessage.warning('请完整填写认证信息并上传两张证件图片')
    return
  }

  savingVerification.value = true
  try {
    await submitStudentVerification({ ...verifyForm })
    const verify = await getStudentVerificationStatus()
    verificationStatus.realNameStatus = verify.realNameStatus
    verificationStatus.educationStatus = verify.educationStatus
    ElMessage.success('认证已提交，等待审核')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '提交失败')
  } finally {
    savingVerification.value = false
  }
}

const progress = computed<ApplicationProgressView>(() => {
  const sectionStates = [
    Boolean(basicForm.name && basicForm.schoolName && basicForm.major),
    applicationList.items.length > 0,
    Boolean(academicForm.gpaScale && academicForm.gpaValue !== null && academicForm.languageScores.length > 0),
    Boolean(exchangeForm.countryName && exchangeForm.universityName && exchangeForm.gpaValue !== null && exchangeForm.majorCourses),
    researchForm.items.length > 0,
    competitionForm.items.length > 0,
    workForm.items.length > 0,
    verificationStatus.realNameStatus === 'APPROVED' && verificationStatus.educationStatus === 'APPROVED'
  ]

  const doneCount = sectionStates.filter(Boolean).length
  const percent = Math.round((doneCount / sectionStates.length) * 100)
  const stageNames = ['选校定位', '材料准备', '文书完善', '网申投递', '面试/笔试', 'Offer结果', '签证办理', '行前准备']
  const stageIndex = Math.min(Math.floor((percent / 100) * stageNames.length), stageNames.length - 1)
  const currentStage = stageNames[stageIndex]

  const nextAction = !sectionStates[0]
    ? '补全基础信息'
    : !sectionStates[1]
      ? '从院校项目页加入申请清单'
      : !sectionStates[2]
      ? '补全学术背景与语言成绩'
      : !sectionStates[3]
        ? '补全交换经历'
        : !sectionStates[4]
          ? '完善科研经历'
          : !sectionStates[5]
            ? '完善竞赛经历'
            : !sectionStates[6]
              ? '完善工作经历'
              : !sectionStates[7]
                ? '提交并通过身份认证'
                : '继续推进机构联动申请流程'

  const milestones = stageNames.map((name, index) => ({
    name,
    status: (index < stageIndex ? 'done' : index === stageIndex ? 'doing' : 'todo') as 'done' | 'doing' | 'todo',
    etaText: index < stageIndex ? '已完成' : index === stageIndex ? '进行中' : '待开始'
  }))

  return {
    overallPercent: percent,
    currentStage,
    nextAction,
    milestones,
    updatedAtText: new Date().toLocaleString()
  }
})

onMounted(() => {
  loadData().catch((error) => {
    console.error('[MePage] load failed', error)
    ElMessage.error('加载个人中心数据失败')
  })
})
</script>

<style scoped>
.me-layout {
  --pane-height: calc(100vh - 176px);
  width: 100%;
  margin: 0;
  padding: 0;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 240px;
  gap: 14px;
  align-items: stretch;
}

.left-nav,
.center-pane,
.right-progress {
  height: var(--pane-height);
  max-height: var(--pane-height);
}

.left-nav,
.right-progress {
  padding: 16px;
  overflow: auto;
}

.left-nav h3,
.right-progress h3 {
  margin: 0 0 10px;
  font-size: 20pt;
  line-height: 1.2;
  font-weight: 700;
}

.center-pane {
  overflow: auto;
  display: grid;
  gap: 14px;
  padding-right: 6px;
}

.nav-item {
  width: 100%;
  text-align: left;
  border: 0;
  background: #f6f8fb;
  color: #4a5a73;
  padding: 11px 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  font-size: 14pt;
  line-height: 1.25;
}

.nav-item.active {
  background: #e8f3ff;
  color: #1778f2;
  font-weight: 600;
}

.section-card {
  padding: 18px;
}

.section-card h2 {
  margin: 0 0 12px;
  font-size: 20pt;
  line-height: 1.2;
  font-weight: 700;
}

.section-head-inline {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.section-footer {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-left {
  min-width: 120px;
}

.inline-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}

.rank-percent-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 8px;
}

.rank-prefix,
.rank-suffix {
  color: #5f6f88;
  font-weight: 600;
}

.budget-row {
  display: grid;
  grid-template-columns: 140px 1fr 1fr;
  gap: 8px;
}

.score-row {
  margin-top: 8px;
  display: grid;
  grid-template-columns: 180px 180px 80px;
  gap: 8px;
}

.list-card {
  border: 1px solid #e7edf6;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
  background: #fafcff;
}

.pub-row {
  margin-top: 8px;
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr 150px 70px;
  gap: 8px;
}

.program-cell-title {
  font-weight: 600;
  color: #30405b;
}

.program-cell-sub {
  color: #7a8aa3;
  font-size: 12px;
}

.match-mini-score {
  font-weight: 700;
  color: #1f6bff;
  font-size: 18px;
}

.match-mini-tier {
  color: #5f6f88;
  font-size: 12px;
}

.muted {
  color: #71829a;
}

.mt8 {
  margin-top: 8px;
}

.upload-result {
  margin-top: 8px;
  font-size: 12px;
  color: #586c8a;
  word-break: break-all;
}

.percent {
  font-size: 30px;
  font-weight: 700;
  color: #1f6bff;
  margin-bottom: 6px;
}

.current,
.next,
.updated {
  margin: 8px 0;
  color: #586c8a;
  font-size: 13px;
}

.milestones {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}

.milestone {
  display: grid;
  grid-template-columns: 10px 1fr;
  gap: 10px;
}

.milestone .dot {
  width: 10px;
  height: 10px;
  border-radius: 99px;
  margin-top: 5px;
  background: #ccd7e9;
}

.milestone.done .dot {
  background: #21c07a;
}

.milestone.doing .dot {
  background: #1f6bff;
}

.milestone .name {
  font-weight: 600;
  color: #3f5068;
}

.milestone .eta {
  font-size: 12px;
  color: #8191a7;
}

@media (max-width: 1400px) {
  .me-layout {
    grid-template-columns: 200px minmax(0, 1fr) 220px;
  }
}

@media (max-width: 1200px) {
  .me-layout {
    width: 100%;
    margin-left: 0;
    padding: 0;
    grid-template-columns: 1fr;
  }

  .left-nav,
  .center-pane,
  .right-progress {
    height: auto;
    max-height: none;
  }
}
</style>
