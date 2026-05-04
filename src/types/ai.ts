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
  ruleMatchScore: number
  mlScore: number
  admissionProbabilityEstimate: number
  matchTier: string
  confidenceLevel: string
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
  recommendations: AiProgramRecommendationItem[]
  gapAnalysis: AiGapAnalysisItem[]
  improvementSuggestions: AiImprovementSuggestionItem[]
  riskWarnings: string[]
}
