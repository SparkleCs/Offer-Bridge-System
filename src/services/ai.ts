import { apiRequest } from './http'
import type { AiReportView } from '../types/ai'

export function generateAiRecommendations() {
  return apiRequest<AiReportView>('/api/v1/ai/recommendations', { method: 'POST' }, true)
}

export function getLatestAiReport() {
  return apiRequest<AiReportView | null>('/api/v1/ai/reports/latest', { method: 'GET' }, true)
}

export function analyzeAiProgram(programId: number) {
  return apiRequest<AiReportView>(`/api/v1/ai/programs/${programId}/analysis`, { method: 'POST' }, true)
}
