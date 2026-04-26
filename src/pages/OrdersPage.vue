<template>
  <section class="orders-page fade-up">
    <header class="orders-head">
      <div>
        <h2 class="section-title">订单与申请进度</h2>
        <p class="section-desc">查看报价、完成支付，并与中介双向确认每个服务阶段。</p>
      </div>
      <el-button :loading="loading" @click="loadOrders">刷新</el-button>
    </header>

    <div class="orders-layout">
      <aside class="order-list" v-loading="loading">
        <el-empty v-if="orders.length === 0 && !loading" description="暂无服务订单，请先从中介套餐页创建" />
        <button
          v-for="order in orders"
          :key="order.id"
          class="order-card"
          :class="{ active: selectedId === order.id }"
          @click="selectOrder(order.id)"
        >
          <div class="card-top">
            <strong>{{ order.serviceTitle || order.teamNameSnapshot }}</strong>
            <el-tag :type="statusTagType(order.orderStatus)" size="small">{{ statusLabel(order.orderStatus) }}</el-tag>
          </div>
          <p>{{ order.orgNameSnapshot }} · {{ order.teamNameSnapshot }}</p>
          <span class="amount">{{ amountText(order) }}</span>
        </button>
      </aside>

      <main class="order-detail" v-loading="detailLoading">
        <el-empty v-if="!detail" description="请选择订单查看详情" />

        <template v-else>
          <section class="summary-panel">
            <div>
              <span class="eyebrow">ORDER {{ detail.order.orderNo }}</span>
              <h3>{{ detail.order.serviceTitle || detail.order.teamNameSnapshot }}</h3>
              <p>{{ detail.order.orgNameSnapshot }}</p>
            </div>
            <div class="summary-actions">
              <strong>{{ amountText(detail.order) }}</strong>
              <el-button v-if="detail.order.orderStatus === 'WAITING_PAYMENT'" type="primary" :loading="paying" @click="payOrder">
                沙箱支付
              </el-button>
              <el-button v-if="canCancel(detail.order.orderStatus)" @click="cancelOrder">取消订单</el-button>
              <el-button v-if="canRefund(detail.order.orderStatus)" type="warning" @click="openRefundDialog">申请退款</el-button>
            </div>
          </section>

          <el-alert
            v-if="detail.order.orderStatus === 'PENDING_QUOTE'"
            title="订单已创建，等待中介补充服务内容与最终报价。"
            type="info"
            show-icon
            :closable="false"
          />
          <el-alert
            v-if="detail.order.orderStatus === 'WAITING_PAYMENT'"
            title="中介已报价，请确认服务内容和金额后支付。"
            type="success"
            show-icon
            :closable="false"
          />

          <section class="quote-panel">
            <h3>服务说明</h3>
            <p>{{ detail.order.quoteDesc || '中介尚未提交报价说明。' }}</p>
          </section>

          <section v-if="detail.stages.length" class="progress-panel">
            <div class="section-head-inline">
              <h3>申请服务进度</h3>
              <span class="muted">中介提交成果后，由你确认通过或退回修改。</span>
            </div>
            <div class="stage-list">
              <article v-for="stage in detail.stages" :key="stage.id" class="stage-item" :class="stage.status.toLowerCase()">
                <div class="stage-index">{{ stage.stageOrder }}</div>
                <div class="stage-body">
                  <div class="stage-title">
                    <strong>{{ stage.stageName }}</strong>
                    <el-tag size="small" :type="stageTagType(stage.status)">{{ stageLabel(stage.status) }}</el-tag>
                  </div>
                  <p v-if="stage.deliverableText" class="deliverable">{{ stage.deliverableText }}</p>
                  <a v-if="stage.deliverableUrl" :href="stage.deliverableUrl" target="_blank" rel="noreferrer">查看成果材料</a>
                  <p v-if="stage.studentFeedback" class="feedback">退回意见：{{ stage.studentFeedback }}</p>
                  <div v-if="stage.status === 'WAITING_CONFIRMATION'" class="stage-actions">
                    <el-button type="success" size="small" @click="confirmStageItem(stage.id)">确认通过</el-button>
                    <el-button size="small" @click="openRejectDialog(stage.id)">退回修改</el-button>
                  </div>
                </div>
              </article>
            </div>
          </section>

          <section v-if="detail.todos.length" class="todo-panel">
            <h3>待办事项</h3>
            <div class="todo-grid">
              <article v-for="todo in detail.todos" :key="todo.id" class="todo-item">
                <div>
                  <strong>{{ todo.title }}</strong>
                  <p>{{ todo.description || '暂无说明' }}</p>
                </div>
                <div class="todo-actions">
                  <el-tag size="small" :type="todo.status === 'COMPLETED' ? 'success' : 'info'">{{ todoLabel(todo.status) }}</el-tag>
                  <el-button
                    v-if="todo.ownerRole === 'STUDENT' && todo.status === 'OPEN'"
                    size="small"
                    @click="completeTodoItem(todo.id)"
                  >
                    标记完成
                  </el-button>
                </div>
              </article>
            </div>
          </section>
        </template>
      </main>
    </div>

    <el-dialog v-model="rejectDialogVisible" title="退回阶段" width="420px">
      <el-input v-model="rejectFeedback" type="textarea" :rows="4" placeholder="请说明需要调整的地方" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="rejectStageItem">提交退回</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundDialogVisible" title="申请退款" width="420px">
      <el-input v-model="refundReason" type="textarea" :rows="4" placeholder="请填写退款原因" />
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="submitRefund">提交申请</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  closeServiceOrder,
  completeTodo,
  confirmStage,
  createPayment,
  getMyOrder,
  listMyOrders,
  mockPaySuccess,
  rejectStage,
  requestRefund
} from '../services/order'
import type { OrderDetail, OrderSummary } from '../types/order'

const route = useRoute()
const orders = ref<OrderSummary[]>([])
const detail = ref<OrderDetail | null>(null)
const selectedId = ref<number | null>(null)
const loading = ref(false)
const detailLoading = ref(false)
const paying = ref(false)
const rejectDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const pendingRejectStageId = ref<number | null>(null)
const rejectFeedback = ref('')
const refundReason = ref('')

function statusLabel(status: string) {
  const dict: Record<string, string> = {
    PENDING_QUOTE: '待报价',
    WAITING_PAYMENT: '待支付',
    PAID: '已支付',
    IN_SERVICE: '服务中',
    COMPLETED: '已完成',
    REFUND_REQUESTED: '退款中',
    CLOSED: '已关闭'
  }
  return dict[status] || status
}

function statusTagType(status: string) {
  if (status === 'IN_SERVICE' || status === 'COMPLETED') return 'success'
  if (status === 'WAITING_PAYMENT') return 'warning'
  if (status === 'REFUND_REQUESTED') return 'danger'
  return 'info'
}

function stageLabel(status: string) {
  const dict: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    WAITING_STUDENT: '等待学生',
    WAITING_AGENT: '等待中介',
    WAITING_CONFIRMATION: '待确认',
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
    WAITING_CONFIRMATION: '待中介确认',
    COMPLETED: '已完成'
  }
  return dict[status] || status
}

function amountText(order: OrderSummary) {
  if (order.finalAmount == null) return '待中介报价'
  return `${order.currency || 'CNY'} ${Number(order.finalAmount).toLocaleString()}`
}

function canCancel(status: string) {
  return status === 'PENDING_QUOTE' || status === 'WAITING_PAYMENT'
}

function canRefund(status: string) {
  return status === 'IN_SERVICE' || status === 'COMPLETED'
}

async function loadOrders() {
  loading.value = true
  try {
    orders.value = await listMyOrders()
    const queryOrderId = Number(route.query.orderId || 0)
    const preferredId = queryOrderId && orders.value.some((item) => item.id === queryOrderId) ? queryOrderId : selectedId.value
    if (!preferredId && orders.value.length) {
      await selectOrder(orders.value[0].id)
    } else if (preferredId) {
      await selectOrder(preferredId)
    }
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
    detail.value = await getMyOrder(orderId)
  } catch (error: any) {
    ElMessage.error(error?.message || '订单详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

async function refreshCurrent(next?: OrderDetail) {
  if (next) detail.value = next
  orders.value = await listMyOrders()
}

async function payOrder() {
  if (!detail.value) return
  paying.value = true
  try {
    await createPayment(detail.value.order.id)
    const next = await mockPaySuccess(detail.value.order.id)
    await refreshCurrent(next)
    ElMessage.success('沙箱支付成功，服务流程已开启')
  } catch (error: any) {
    ElMessage.error(error?.message || '支付失败')
  } finally {
    paying.value = false
  }
}

async function cancelOrder() {
  if (!detail.value) return
  await ElMessageBox.confirm('确认取消该订单？', '取消订单')
  await closeServiceOrder(detail.value.order.id)
  detail.value = null
  selectedId.value = null
  await loadOrders()
  ElMessage.success('订单已取消')
}

async function confirmStageItem(stageId: number) {
  if (!detail.value) return
  const next = await confirmStage(detail.value.order.id, stageId)
  await refreshCurrent(next)
  ElMessage.success('阶段已确认')
}

function openRejectDialog(stageId: number) {
  pendingRejectStageId.value = stageId
  rejectFeedback.value = ''
  rejectDialogVisible.value = true
}

async function rejectStageItem() {
  if (!detail.value || !pendingRejectStageId.value || !rejectFeedback.value.trim()) {
    ElMessage.warning('请填写退回意见')
    return
  }
  const next = await rejectStage(detail.value.order.id, pendingRejectStageId.value, rejectFeedback.value)
  rejectDialogVisible.value = false
  await refreshCurrent(next)
  ElMessage.success('已退回中介修改')
}

async function completeTodoItem(todoId: number) {
  if (!detail.value) return
  const next = await completeTodo(detail.value.order.id, todoId)
  await refreshCurrent(next)
  ElMessage.success('待办已提交，等待中介确认')
}

function openRefundDialog() {
  refundReason.value = ''
  refundDialogVisible.value = true
}

async function submitRefund() {
  if (!detail.value || !refundReason.value.trim()) {
    ElMessage.warning('请填写退款原因')
    return
  }
  const next = await requestRefund(detail.value.order.id, refundReason.value)
  refundDialogVisible.value = false
  await refreshCurrent(next)
  ElMessage.success('退款申请已提交')
}

loadOrders()
</script>

<style scoped>
.orders-page {
  max-width: 1240px;
  margin: 0 auto;
}

.orders-head,
.section-head-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.orders-layout {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 16px;
  margin-top: 16px;
}

.order-list,
.order-detail,
.summary-panel,
.quote-panel,
.progress-panel,
.todo-panel {
  border: 1px solid #dce8ef;
  border-radius: 8px;
  background: #fff;
}

.order-list {
  min-height: 620px;
  padding: 10px;
}

.order-card {
  width: 100%;
  text-align: left;
  border: 1px solid #e1ebf2;
  border-radius: 8px;
  background: #f9fcfd;
  padding: 12px;
  margin-bottom: 10px;
  cursor: pointer;
}

.order-card.active {
  border-color: #409eff;
  background: #eef7ff;
}

.card-top,
.stage-title,
.todo-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.order-card p,
.quote-panel p,
.todo-item p,
.summary-panel p {
  color: #667784;
  margin: 6px 0 0;
}

.order-card .card-top strong {
  font-size: 18px;
  line-height: 1.35;
}

.order-card .card-top :deep(.el-tag) {
  font-size: 15px;
  line-height: 24px;
  height: 26px;
  padding: 0 10px;
}

.order-card p {
  font-size: 16px;
  line-height: 1.55;
}

.amount {
  display: block;
  margin-top: 10px;
  font-weight: 700;
  font-size: 17px;
  line-height: 1.4;
  color: #0f4c81;
}

.order-detail {
  min-height: 620px;
  padding: 14px;
}

.summary-panel {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  margin-bottom: 12px;
}

.summary-panel h3 {
  margin: 4px 0 0;
  font-size: 22px;
}

.summary-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.summary-actions strong {
  font-size: 20px;
  color: #0f4c81;
}

.eyebrow,
.muted {
  font-size: 12px;
  color: #7b8b96;
}

.quote-panel,
.progress-panel,
.todo-panel {
  padding: 16px;
  margin-top: 12px;
}

.stage-list {
  margin-top: 14px;
}

.stage-item {
  display: grid;
  grid-template-columns: 34px 1fr;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #edf2f6;
}

.stage-item:last-child {
  border-bottom: 0;
}

.stage-index {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #edf4f8;
  color: #486272;
  font-weight: 700;
}

.stage-item.completed .stage-index {
  background: #e5f6ec;
  color: #2c8d55;
}

.stage-body a {
  color: #0f70c8;
}

.deliverable,
.feedback {
  margin: 8px 0;
  color: #536575;
}

.feedback {
  color: #b14b38;
}

.stage-actions {
  margin-top: 10px;
}

.todo-grid {
  display: grid;
  gap: 10px;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border: 1px solid #e4edf3;
  border-radius: 8px;
  background: #fbfdff;
}

@media (max-width: 980px) {
  .orders-layout {
    grid-template-columns: 1fr;
  }

  .summary-panel,
  .todo-item {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
