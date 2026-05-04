import type { PagedResult } from './agency'

export type RecommendationMatchLevel = 'HIGH' | 'MEDIUM' | 'LOW'

export interface StudentAgencyTeamRecommendationItem {
  teamId: number
  teamName: string
  teamIntro: string
  orgName: string
  city: string
  serviceCountryScope: string
  serviceMajorScope: string
  caseCount: number
  successRate: number
  avgRating: number
  responseEfficiencyScore: number
  priceTextPlaceholder: string
  recommendScore: number
  matchLevel: RecommendationMatchLevel
  matchReasons: string[]
  matchedTags: string[]
  favorited?: boolean
}

export interface AgencyTeamStudentRecommendationItem {
  studentUserId: number
  displayName: string
  educationLevel?: string | null
  schoolName?: string | null
  major?: string | null
  gpaValue?: number | null
  gpaScale?: string | null
  languageSummary?: string | null
  targetCountries?: string | null
  targetMajorText?: string | null
  intakeTerm?: string | null
  recommendScore: number
  matchLevel: RecommendationMatchLevel
  matchReasons: string[]
  matchedTags: string[]
}

export type AgencyTeamStudentRecommendationPage = PagedResult<AgencyTeamStudentRecommendationItem>
