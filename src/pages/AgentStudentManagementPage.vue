<template>
  <section class="student-management fade-up">
    <header class="page-head">
      <div>
        <h2 class="section-title">学生服务管理</h2>
        <p class="section-desc">处理待报价订单，跟进已开案学生的阶段成果和待办确认。</p>
      </div>
    </header>

    <div class="management-layout">
      <aside class="case-list" v-loading="loading">
        <el-empty v-if="orders.length === 0 && !loading" description="暂无学生服务订单" />
        <button
          v-for="order in orders"
          :key="order.id"
          class="case-card"
          :class="{ active: selectedId === order.id }"
          @click="selectOrder(order.id)"
        >
          <div class="case-top">
            <strong class="case-student">{{ order.studentName || order.studentPhone || `学生 #${order.studentUserId}` }}</strong>
            <el-tag size="small" :type="statusTagType(order.orderStatus)">{{ statusLabel(order.orderStatus) }}</el-tag>
          </div>
          <p class="case-service">{{ order.serviceTitle || order.teamNameSnapshot }}</p>
          <span class="case-amount">{{ amountText(order) }}</span>
        </button>
      </aside>

      <main class="case-detail" v-loading="detailLoading">
        <el-empty v-if="!detail" description="请选择一位学生" />

        <template v-else>
          <section class="detail-hero">
            <div>
              <span class="eyebrow">{{ detail.order.orderNo }}</span>
              <h3>{{ detail.order.serviceTitle || detail.order.teamNameSnapshot }}</h3>
              <p>{{ detail.order.orgNameSnapshot }} · {{ statusLabel(detail.order.orderStatus) }}</p>
            </div>
            <strong>{{ amountText(detail.order) }}</strong>
          </section>

          <section v-if="detail.order.orderStatus === 'PENDING_QUOTE'" class="quote-editor">
            <h3>提交报价</h3>
            <el-form label-position="top">
              <el-form-item label="服务标题">
                <el-input v-model="quoteForm.serviceTitle" />
              </el-form-item>
              <el-form-item label="最终价格（CNY）">
                <el-input-number v-model="quoteForm.finalAmount" :min="0" :precision="2" :step="1000" :controls="false" class="full" />
              </el-form-item>
              <el-form-item label="服务内容与边界">
                <el-input v-model="quoteForm.quoteDesc" type="textarea" :rows="4" />
              </el-form-item>
              <el-button type="primary" :loading="savingQuote" @click="submitQuote">提交给学生支付</el-button>
            </el-form>
          </section>

          <section v-if="detail.stages.length" class="stage-panel">
            <div class="panel-head">
              <h3>阶段交付</h3>
              <span>提交成果后等待学生确认，退回会回到待中介处理。</span>
            </div>
            <article v-for="stage in detail.stages" :key="stage.id" class="stage-row">
              <div class="stage-main">
                <div class="stage-title">
                  <strong>{{ stage.stageOrder }}. {{ stage.stageName }}</strong>
                  <el-tag size="small" :type="stageTagType(stage.status)">{{ stageLabel(stage.status) }}</el-tag>
                </div>
                <p v-if="stage.deliverableText">{{ stage.deliverableText }}</p>
                <a v-if="stage.deliverableUrl" :href="stage.deliverableUrl" target="_blank" rel="noreferrer" class="legacy-link">
                  查看材料链接
                </a>
                <div v-if="stage.attachments?.length" class="attachment-list compact">
                  <a
                    v-for="attachment in stage.attachments"
                    :key="attachment.id || attachment.fileUrl"
                    :href="attachment.fileUrl"
                    target="_blank"
                    rel="noreferrer"
                    class="attachment-chip"
                  >
                    <el-icon><Picture v-if="attachment.contentType === 'IMAGE'" /><Document v-else /></el-icon>
                    <span>{{ attachment.fileName }}</span>
                  </a>
                </div>
                <p v-if="stage.studentFeedback" class="feedback">学生退回：{{ stage.studentFeedback }}</p>
              </div>
              <el-button v-if="canSubmitStage(stage.status)" size="small" @click="openStageSubmit(stage.id)">提交成果</el-button>
            </article>
          </section>

          <section v-if="detail.todos.length" class="todo-panel">
            <h3>待办确认</h3>
            <article v-for="todo in detail.todos" :key="todo.id" class="todo-row">
              <div>
                <strong>{{ todo.title }}</strong>
                <p>{{ todo.description || '暂无说明' }}</p>
              </div>
              <div class="todo-actions">
                <el-tag size="small">{{ todoLabel(todo.status) }}</el-tag>
                <el-button v-if="todo.status === 'WAITING_CONFIRMATION'" size="small" type="success" @click="confirmTodoItem(todo.id)">
                  确认完成
                </el-button>
              </div>
            </article>
          </section>

          <section v-if="detail.order.caseId" class="todo-create">
            <h3>新增待办</h3>
            <div class="todo-create-row">
              <el-input v-model="todoForm.title" placeholder="待办标题" />
              <el-select v-model="todoForm.ownerRole" style="width: 140px">
                <el-option label="学生处理" value="STUDENT" />
                <el-option label="中介处理" value="AGENT" />
              </el-select>
              <el-button @click="addTodo">添加</el-button>
            </div>
            <el-input v-model="todoForm.description" class="todo-desc" placeholder="待办说明" />
          </section>
        </template>
      </main>
    </div>

  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Picture } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  confirmTodo,
  createAgentTodo,
  getAgentServiceOrder,
  listAgentServiceOrders,
  quoteServiceOrder
} from '../services/order'
import type { AgentOrderSummary, OrderDetail } from '../types/order'

const route = useRoute()
const router = useRouter()

const orders = ref<AgentOrderSummary[]>([])
const detail = ref<OrderDetail | null>(null)
const selectedId = ref<number | null>(null)
const loading = ref(false)
const detailLoading = ref(false)
const savingQuote = ref(false)

const quoteForm = reactive({
  serviceTitle: '',
  finalAmount: 0,
  quoteDesc: ''
})

const todoForm = reactive({
  title: '',
  description: '',
  ownerRole: 'STUDENT'
})

function statusLabel(status: string) {
  const dict: Record<string, string> = {
    PENDING_QUOTE: '待报价',
    WAITING_PAYMENT: '待学生支付',
    IN_SERVICE: '服务中',
    REFUND_REQUESTED: '退款中',
    CLOSED: '已关闭'
  }
  return dict[status] || status
}

function statusTagType(status: string) {
  if (status === 'PENDING_QUOTE') return 'warning'
  if (status === 'IN_SERVICE') return 'success'
  if (status === 'REFUND_REQUESTED') return 'danger'
  return 'info'
}

function stageLabel(status: string) {
  const dict: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    WAITING_AGENT: '待中介处理',
    WAITING_CONFIRMATION: '待学生确认',
    COMPLETED: '已完成'
  }
  return dict[status] || status
}

function stageTagType(status: string) {
  if (status === 'COMPLETED') return 'success'
  if (status === 'WAITING_CONFIRMATION') return 'warning'
  if (status === 'WAITING_AGENT') return 'danger'
  return 'info'
}

function todoLabel(status: string) {
  const dict: Record<string, string> = {
    OPEN: '待处理',
    WAITING_CONFIRMATION: '待确认',
    COMPLETED: '已完成'
  }
  return dict[status] || status
}

function amountText(order: { finalAmount?: number | null; currency?: string }) {
  if (order.finalAmount == null) return '待报价'
  return `${order.currency || 'CNY'} ${Number(order.finalAmount).toLocaleString()}`
}

function canSubmitStage(status: string) {
  return status === 'IN_PROGRESS' || status === 'WAITING_AGENT'
}

async function loadOrders() {
  loading.value = true
  try {
    orders.value = await listAgentServiceOrders()
    const queryOrderId = Number(route.query.orderId || 0)
    const preferredId = queryOrderId && orders.value.some((item) => item.id === queryOrderId) ? queryOrderId : selectedId.value
    if (!preferredId && orders.value.length) await selectOrder(orders.value[0].id)
    else if (preferredId) await selectOrder(preferredId)
  } catch (error: any) {
    ElMessage.error(error?.message || '订单加载失败')
  } finally {
    loading.value = false
  }
}

async function selectOrder(orderId: number) {
  selectedId.value = orderId
  detailLoading.value = true
  try {
    detail.value = await getAgentServiceOrder(orderId)
    quoteForm.serviceTitle = detail.value.order.serviceTitle || detail.value.order.teamNameSnapshot
    quoteForm.finalAmount = Number(detail.value.order.finalAmount || 0)
    quoteForm.quoteDesc = detail.value.order.quoteDesc || ''
  } catch (error: any) {
    ElMessage.error(error?.message || '订单详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

async function refresh(next?: OrderDetail) {
  if (next) detail.value = next
  orders.value = await listAgentServiceOrders()
}

async function submitQuote() {
  if (!detail.value || !quoteForm.serviceTitle.trim() || !quoteForm.quoteDesc.trim() || quoteForm.finalAmount <= 0) {
    ElMessage.warning('请完善报价信息')
    return
  }
  savingQuote.value = true
  try {
    const next = await quoteServiceOrder(detail.value.order.id, quoteForm)
    await refresh(next)
    ElMessage.success('报价已提交')
  } catch (error: any) {
    ElMessage.error(error?.message || '提交报价失败')
  } finally {
    savingQuote.value = false
  }
}

function openStageSubmit(stageId: number) {
  if (!detail.value) return
  router.push(`/agent-workbench/students/${detail.value.order.id}/stages/${stageId}/submit`)
}

async function confirmTodoItem(todoId: number) {
  if (!detail.value) return
  const next = await confirmTodo(detail.value.order.id, todoId)
  await refresh(next)
  ElMessage.success('待办已确认')
}

async function addTodo() {
  if (!detail.value || !todoForm.title.trim()) {
    ElMessage.warning('请填写待办标题')
    return
  }
  const next = await createAgentTodo(detail.value.order.id, todoForm)
  todoForm.title = ''
  todoForm.description = ''
  await refresh(next)
  ElMessage.success('待办已添加')
}

loadOrders()
</script>

<style scoped>
.student-management {
  max-width: 1240px;
  margin: 0 auto;
}

.page-head,
.case-top,
.stage-title,
.todo-row,
.detail-hero,
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.management-layout {
  display: grid;
  grid-template-columns: 330px 1fr;
  gap: 16px;
  margin-top: 16px;
}

.case-list,
.case-detail,
.detail-hero,
.quote-editor,
.stage-panel,
.todo-panel,
.todo-create {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
}

.case-list,
.case-detail {
  min-height: 620px;
  padding: 12px;
}

.case-card {
  width: 100%;
  text-align: left;
  border: 1px solid #e2ebf2;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  background: #f9fcfd;
  cursor: pointer;
}

.case-card.active {
  border-color: #409eff;
  background: #eef7ff;
}

.case-card p,
.detail-hero p,
.stage-main p,
.todo-row p,
.panel-head span {
  color: #687b88;
  margin: 6px 0 0;
}

.case-student {
  color: #182635;
  font-size: 16px;
  line-height: 1.35;
  font-weight: 700;
}

.case-service {
  color: #5f7483;
  font-size: 15px;
  line-height: 1.4;
  font-weight: 600;
}

.case-amount {
  display: inline-block;
  margin-top: 2px;
  color: #d93026;
  font-size: 15px;
  line-height: 1.35;
  font-weight: 700;
}

.detail-hero,
.quote-editor,
.stage-panel,
.todo-panel,
.todo-create {
  padding: 16px;
  margin-bottom: 12px;
}

.detail-hero h3 {
  margin: 4px 0 0;
  font-size: 22px;
}

.detail-hero strong {
  font-size: 20px;
  color: #0f4c81;
}

.eyebrow {
  font-size: 12px;
  color: #7a8994;
}

.full {
  width: 100%;
}

.stage-row {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px solid #edf2f6;
}

.stage-row:last-child {
  border-bottom: 0;
}

.stage-main {
  flex: 1;
}

.feedback {
  color: #b14b38 !important;
}

.legacy-link {
  display: inline-block;
  margin-top: 8px;
  color: #0f70c8;
}

.attachment-list {
  display: grid;
  gap: 8px;
  margin-top: 10px;
}

.attachment-list.compact {
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.attachment-chip {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #285c7a;
  text-decoration: none;
}

.attachment-chip {
  border: 1px solid #dbe7ef;
  border-radius: 8px;
  background: #f7fbfd;
  padding: 8px 10px;
}

.attachment-chip span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-row {
  padding: 12px 0;
  border-bottom: 1px solid #edf2f6;
}

.todo-row:last-child {
  border-bottom: 0;
}

.todo-actions,
.todo-create-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.todo-desc,
.mt {
  margin-top: 10px;
}

@media (max-width: 980px) {
  .management-layout {
    grid-template-columns: 1fr;
  }

  .detail-hero,
  .todo-row,
  .stage-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
