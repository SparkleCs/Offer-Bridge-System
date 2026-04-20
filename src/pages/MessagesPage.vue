<template>
  <section class="page-card fade-up message-shell">
    <div class="message-head">
      <h2 class="section-title">消息中心</h2>
      <p class="section-desc">私聊、论坛互动、系统通知分域管理。</p>
    </div>

    <el-tabs v-model="activeTab" class="mt16">
      <el-tab-pane label="学生-中介私聊" name="chat">
        <div class="message-card">
          <h3>当前上下文</h3>
          <p>团队ID：{{ teamId || '未指定' }}</p>
          <p>团队名称：{{ teamName || '未指定' }}</p>
          <el-alert title="私聊模块开发中，后续将在此接入会话列表与聊天窗口。" type="info" :closable="false" show-icon />
        </div>
      </el-tab-pane>

      <el-tab-pane :label="`系统通知${systemUnreadCount > 0 ? ` (${systemUnreadCount})` : ''}`" name="system-notification">
        <div class="notify-head">
          <span>未读：{{ systemUnreadCount }}</span>
          <el-button link type="primary" @click="markAllSystemRead">全部已读</el-button>
        </div>
        <div v-loading="systemLoading" class="notify-list">
          <el-empty v-if="!systemNotifications.length" description="暂无系统通知" />
          <div v-for="item in systemNotifications" :key="item.id" class="notify-item" :class="{ unread: item.status === 'UNREAD' }" @click="markSystemOneRead(item.id)">
            <p class="notify-title">{{ item.title }}</p>
            <p class="notify-content">{{ item.content }}</p>
            <p class="notify-time">{{ formatTime(item.createdAt) }}</p>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane :label="`论坛通知${unreadCount > 0 ? ` (${unreadCount})` : ''}`" name="forum-notification">
        <div class="notify-head">
          <span>未读：{{ unreadCount }}</span>
          <el-button link type="primary" @click="markAllRead">全部已读</el-button>
        </div>
        <div v-loading="loading" class="notify-list">
          <el-empty v-if="!notifications.length" description="暂无通知" />
          <div v-for="item in notifications" :key="item.notificationId" class="notify-item" :class="{ unread: !item.read }" @click="goPost(item.postId)">
            <p class="notify-title">用户 {{ item.actorUserId }} {{ notificationText(item.type) }}了你的帖子</p>
            <p class="notify-time">{{ formatTime(item.createdAt) }}</p>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { ApiError } from '../services/http'
import { listForumNotifications, markForumNotificationsRead } from '../services/forum'
import { listSystemNotifications, markSystemNotificationsRead } from '../services/message'
import type { ForumNotificationType, ForumNotification } from '../types/forum'
import type { SystemNotificationItem } from '../types/message'

const route = useRoute()
const router = useRouter()
const teamId = computed(() => String(route.query.teamId || ''))
const teamName = computed(() => String(route.query.teamName || ''))

const activeTab = ref('chat')
const loading = ref(false)
const notifications = ref<ForumNotification[]>([])
const unreadCount = ref(0)

const systemLoading = ref(false)
const systemNotifications = ref<SystemNotificationItem[]>([])
const systemUnreadCount = ref(0)

onMounted(() => {
  loadNotifications()
  loadSystemNotifications()
})

function formatTime(value: string) {
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

function notificationText(type: ForumNotificationType) {
  if (type === 'COMMENT') return '评论'
  if (type === 'FAVORITE') return '收藏'
  return '点赞'
}

async function loadNotifications() {
  loading.value = true
  try {
    const data = await listForumNotifications({ page: 1, pageSize: 30 })
    notifications.value = data.items
    unreadCount.value = data.unreadCount
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '通知加载失败')
  } finally {
    loading.value = false
  }
}

async function loadSystemNotifications() {
  systemLoading.value = true
  try {
    const data = await listSystemNotifications({ page: 1, pageSize: 50 })
    systemNotifications.value = data.records
    systemUnreadCount.value = data.unreadCount
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '系统通知加载失败')
  } finally {
    systemLoading.value = false
  }
}

async function markAllSystemRead() {
  try {
    await markSystemNotificationsRead({ markAll: true })
    systemNotifications.value = systemNotifications.value.map((item) => ({ ...item, status: 'READ' }))
    systemUnreadCount.value = 0
    ElMessage.success('系统通知已全部标记已读')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function markSystemOneRead(id: number) {
  const target = systemNotifications.value.find((item) => item.id === id)
  if (!target || target.status === 'READ') return
  try {
    await markSystemNotificationsRead({ ids: [id] })
    target.status = 'READ'
    systemUnreadCount.value = Math.max(0, systemUnreadCount.value - 1)
  } catch {
    // ignore
  }
}

async function markAllRead() {
  try {
    await markForumNotificationsRead({ markAll: true })
    notifications.value = notifications.value.map((item) => ({ ...item, read: true }))
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function goPost(postId: string) {
  const unreadIds = notifications.value.filter((item) => !item.read).map((item) => item.notificationId)
  if (unreadIds.length > 0) {
    await markForumNotificationsRead({ notificationIds: unreadIds })
  }
  await router.push({ path: '/forum', query: { postId } })
}
</script>

<style scoped>
.message-shell {
  max-width: 920px;
  margin: 0 auto;
}

.mt16 {
  margin-top: 16px;
}

.message-card {
  border: 1px solid #dbe6f1;
  border-radius: 14px;
  padding: 16px;
  background: linear-gradient(160deg, #ffffff, #f8fbff);
}

.message-card h3 {
  margin: 0 0 8px;
}

.message-card p {
  margin: 4px 0;
  color: #5b738b;
}

.notify-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notify-list {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.notify-item {
  border: 1px solid #dbe6f1;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}

.notify-item.unread {
  border-color: #9ec1ff;
  background: #f5f9ff;
}

.notify-title,
.notify-time,
.notify-content {
  margin: 0;
}

.notify-content {
  margin-top: 6px;
  color: #4f6581;
}

.notify-time {
  margin-top: 6px;
  color: #6e829c;
  font-size: 12px;
}
</style>
