export type EducationLevel = 'HIGH_SCHOOL' | 'COLLEGE' | 'UNDERGRAD' | 'MASTER' | 'PHD' | 'OTHER'
export type GpaScale = 'FOUR_POINT' | 'PERCENTAGE'
export type LanguageType = 'CET4' | 'CET6' | 'TOEFL' | 'IELTS' | 'PTE'

export interface LanguageScore {
  languageType: LanguageType
  score: number
}

export interface TargetCountryItem {
  countryName: string
}

export interface PublicationItem {
  title: string
  authorRole: string
  authorOrder?: string | null
  journalName: string
  publicationLevel?: string | null
  journalPartition?: string | null
  indexedInfo?: string | null
  publishedYear: number | null
}

export interface ResearchItem {
  id?: number
  projectName: string
  roleName?: string | null
  roleLevel?: string | null
  relevanceLevel?: string | null
  startDate: string
  endDate: string
  durationMonths?: number | null
  contentSummary: string
  hasPublication: boolean
  publications: PublicationItem[]
}

export interface CompetitionItem {
  id?: number
  competitionName: string
  competitionLevel: string
  award: string
  awardLevel?: string | null
  relevanceLevel?: string | null
  roleDesc: string
  eventDate: string
}

export interface WorkItem {
  id?: number
  companyName: string
  companyTier?: string | null
  positionName: string
  relevanceLevel?: string | null
  titleLevel?: string | null
  startDate: string
  endDate: string
  durationMonths?: number | null
  keywords: string
  contentSummary: string
}

export interface StudentProfile {
  userId: number
  phone: string
  name: string | null
  email: string | null
  wechatId: string | null
  educationLevel: EducationLevel | null
  schoolName: string | null
  undergraduateSchoolTier: string | null
  major: string | null
  gpaValue: number | null
  gpaScale: GpaScale | null
  languageScores: LanguageScore[]
  rankValue: number | null
  targetMajorText: string | null
  intakeTerm: string | null
  budgetCurrency: string | null
  budgetMin: number | null
  budgetMax: number | null
  budgetNote: string | null
  targetCountries: TargetCountryItem[]
  profileCompleted: boolean
}

export interface UpdateStudentBasicPayload {
  name: string
  email: string
  wechatId?: string
  educationLevel: EducationLevel
  schoolName: string
  undergraduateSchoolTier?: string | null
  major: string
  targetMajorText: string
  intakeTerm: string
  budgetCurrency: string
  budgetMin: number | null
  budgetMax: number | null
  budgetNote: string
  targetCountries: TargetCountryItem[]
}

export interface UpdateStudentAcademicPayload {
  gpaValue: number
  gpaScale: GpaScale
  languageScores: LanguageScore[]
  rankValue: number | null
}

export interface ResearchView {
  items: ResearchItem[]
}

export interface CompetitionView {
  items: CompetitionItem[]
}

export interface WorkView {
  items: WorkItem[]
}

export interface ExchangeExperience {
  countryName: string
  universityName: string
  schoolTier?: string | null
  gpaValue: number | null
  majorCourses: string
  relevanceLevel?: string | null
  startDate: string
  endDate: string
  durationMonths?: number | null
}

export interface BackgroundScoreView {
  gpaScore: number
  languageScore: number
  publicationScore: number
  researchScore: number
  internshipScore: number
  exchangeScore: number
  competitionScore: number
  undergraduateSchoolScore: number
  overallAcademicScore: number
  scoreVersion: string
  scoreDetailJson: string | null
}

export interface VerificationSubmitPayload {
  realName: string
  idNo: string
  idCardImageUrl: string
  studentCardImageUrl: string
}

export interface VerificationStatus {
  realNameStatus: string
  educationStatus: string
  verificationCompleted: boolean
}

export interface ApplicationProgressMilestone {
  name: string
  status: 'done' | 'doing' | 'todo'
  etaText: string
}

export interface ApplicationProgressView {
  overallPercent: number
  currentStage: string
  nextAction: string
  milestones: ApplicationProgressMilestone[]
  updatedAtText: string
}

export interface StudentSourceCheckView {
  enabled: boolean
  databaseName: string | null
  researchTable: 'OK' | 'MISSING' | 'ERROR'
  competitionTable: 'OK' | 'MISSING' | 'ERROR'
  workTable: 'OK' | 'MISSING' | 'ERROR'
  exchangeTable: 'OK' | 'MISSING' | 'ERROR'
}
