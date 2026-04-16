export interface FilterOption {
  code: string
  name: string
  parentCode?: string
}

export interface UniversityMeta {
  countries: FilterOption[]
  subjectCategories: FilterOption[]
  directions: FilterOption[]
  applicationStatuses: FilterOption[]
}

export interface SchoolListItem {
  id: number
  schoolNameCn: string
  schoolNameEn: string
  countryCode: string
  countryName: string
  cityName: string
  qsRank: number
  advantageSubjects: string
  tuitionMin: number | null
  tuitionMax: number | null
  tuitionCurrency: string | null
}

export interface ProgramListItem {
  id: number
  schoolId: number
  schoolNameCn: string
  schoolNameEn: string
  qsRank: number
  countryCode: string
  countryName: string
  programName: string
  collegeName: string
  subjectCategoryCode: string
  subjectCategoryName: string
  directionCode: string
  directionName: string
  studyMode: string
  durationMonths: number | null
  tuitionAmount: number | null
  tuitionCurrency: string | null
  languageType: string | null
  languageMinScore: number | null
  gpaMinRecommend: number | null
}

export interface ProgramMatchResult {
  matchScore: number
  matchTier: string
  reasonTags: string[]
}

export interface ProgramDetail {
  basic: ProgramListItem
  degreeType: string
  requiresGre: boolean
  requiresGmat: boolean
  backgroundPreference: string
  applicationRoundsOverview: string
  suitableTags: string
  intakeTerm: string
  programSummary: string
  matchResult: ProgramMatchResult
}

export interface SchoolDetail {
  id: number
  schoolNameCn: string
  schoolNameEn: string
  countryCode: string
  countryName: string
  cityName: string
  qsRank: number
  rankingYear: number
  schoolSummary: string
  tuitionMin: number | null
  tuitionMax: number | null
  tuitionCurrency: string | null
  durationMinMonths: number | null
  durationMaxMonths: number | null
  languageRequirementRange: string
  advantageSubjects: string
  representativePrograms: ProgramListItem[]
}

export interface ApplicationListItem {
  id: number
  programId: number
  statusCode: string
  noteText: string | null
  program: ProgramListItem
  matchResult: ProgramMatchResult
}

export interface ApplicationListView {
  items: ApplicationListItem[]
}
