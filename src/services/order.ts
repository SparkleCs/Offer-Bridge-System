import { apiRequest } from './http'
import type { AgentOrderSummary, OrderDetail, OrderSummary, PayResult, SubmitStagePayload } from '../types/order'

export function createServiceOrder(payload: { teamId: number; remark?: string }) {
  return apiRequest<OrderSummary>('/api/v1/orders', {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function listMyOrders() {
  return apiRequest<OrderSummary[]>('/api/v1/orders', { method: 'GET' }, true)
}

export function getMyOrder(orderId: number) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}`, { method: 'GET' }, true)
}

export function createPayment(orderId: number) {
  return apiRequest<PayResult>(`/api/v1/orders/${orderId}/pay`, { method: 'POST' }, true)
}

export function mockPaySuccess(orderId: number) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}/pay/mock-success`, { method: 'POST' }, true)
}

export function closeServiceOrder(orderId: number) {
  return apiRequest<void>(`/api/v1/orders/${orderId}/close`, { method: 'PUT' }, true)
}

export function requestRefund(orderId: number, reason: string) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}/refund`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  }, true)
}

export function confirmStage(orderId: number, stageId: number) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}/stages/${stageId}/confirm`, { method: 'POST' }, true)
}

export function rejectStage(orderId: number, stageId: number, feedback: string) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}/stages/${stageId}/reject`, {
    method: 'POST',
    body: JSON.stringify({ feedback })
  }, true)
}

export function completeTodo(orderId: number, todoId: number) {
  return apiRequest<OrderDetail>(`/api/v1/orders/${orderId}/todos/${todoId}/complete`, { method: 'POST' }, true)
}

export function listAgentServiceOrders() {
  return apiRequest<AgentOrderSummary[]>('/api/v1/agency/service-orders', { method: 'GET' }, true)
}

export function getAgentServiceOrder(orderId: number) {
  return apiRequest<OrderDetail>(`/api/v1/agency/service-orders/${orderId}`, { method: 'GET' }, true)
}

export function quoteServiceOrder(orderId: number, payload: {
  finalAmount: number
  serviceTitle: string
  quoteDesc: string
  assignedMemberId?: number
}) {
  return apiRequest<OrderDetail>(`/api/v1/agency/service-orders/${orderId}/quote`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function submitStage(orderId: number, stageId: number, payload: SubmitStagePayload) {
  return apiRequest<OrderDetail>(`/api/v1/agency/service-orders/${orderId}/stages/${stageId}/submit`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function createAgentTodo(orderId: number, payload: {
  title: string
  description?: string
  stageId?: number
  ownerRole: string
}) {
  return apiRequest<OrderDetail>(`/api/v1/agency/service-orders/${orderId}/todos`, {
    method: 'POST',
    body: JSON.stringify(payload)
  }, true)
}

export function confirmTodo(orderId: number, todoId: number) {
  return apiRequest<OrderDetail>(`/api/v1/agency/service-orders/${orderId}/todos/${todoId}/confirm`, { method: 'POST' }, true)
}
