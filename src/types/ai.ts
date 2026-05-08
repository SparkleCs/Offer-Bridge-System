export interface AiGapAnalysisItem {
  dimension: string
  current: string
  target: string
  priority: string
}

export interface AiImprovementSuggestionItem {
  priority: string
  action: string
  reason: string
}

export interface AiProgramRecommendationItem {
  programId: number
  schoolId?: number | null
  schoolName: string
  programName: string
  countryName?: string | null
  directionName?: string | null
  qsRank?: number | null
  usnewsRank?: number | null
  rankingSource?: 'QS' | 'USNEWS' | null
  primaryRank?: number | null
  ruleMatchScore: number
  mlScore: number
  admissionProbabilityEstimate: number
  matchTier: string
  confidenceLevel: string
  reasonTags: string[]
  aiSummary: string
}

export interface AiSchoolRecommendationItem {
  schoolId: number
  schoolName: string
  countryName?: string | null
  qsRank?: number | null
  usnewsRank?: number | null
  rankingSource?: 'QS' | 'USNEWS' | null
  primaryRank?: number | null
  mlScore: number
  admissionProbabilityEstimate: number
  matchTier: string
  confidenceLevel?: string | null
  gpaScore?: number | null
  languageScore?: number | null
  softBackgroundScore?: number | null
  schoolSelectivityScore?: number | null
  admissionBarScore?: number | null
  schoolHeatScore?: number | null
  reasonTags: string[]
  aiSummary: string
}

export interface AiReportView {
  reportId: number
  status: string
  overallSummary: string
  generatedAt: string
  modelProvider?: string | null
  modelVersion?: string | null
  schoolRecommendations: AiSchoolRecommendationItem[]
  recommendations: AiProgramRecommendationItem[]
  gapAnalysis: AiGapAnalysisItem[]
  improvementSuggestions: AiImprovementSuggestionItem[]
  riskWarnings: string[]
}
