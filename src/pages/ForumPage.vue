<template>
  <section class="page-card fade-up forum-shell">
    <div class="forum-head">
      <div>
        <h2 class="section-title">论坛</h2>
        <p class="section-desc">学生可发布经验帖和 offer 墙内容，支持点赞、评论、收藏、转发与通知。</p>
      </div>
      <div class="head-actions">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-button plain @click="openNotificationDrawer">互动通知</el-button>
        </el-badge>
        <el-button type="primary" :disabled="!isStudent" @click="openCreateDialog">发布帖子</el-button>
      </div>
    </div>

    <el-alert
      v-if="!isStudent"
      title="当前账号不是学生角色，仅支持浏览，发帖与互动功能仅学生可用。"
      type="warning"
      :closable="false"
      show-icon
      class="role-alert"
    />

    <div class="forum-toolbar">
      <el-radio-group v-model="activeChannel">
        <el-radio-button v-for="item in channelSegments" :key="item.value" :label="item.value">{{ item.label }}</el-radio-button>
      </el-radio-group>
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索标题或正文关键词" clearable @keyup.enter="reloadPosts(1)">
          <template #append>
            <el-button @click="reloadPosts(1)">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div v-loading="loadingPosts" class="post-list">
      <el-empty v-if="!posts.length" description="暂无帖子，来发布第一条吧" />
      <article v-for="item in posts" :key="item.postId" class="post-card">
        <header class="post-header">
          <h3 class="post-title" @click="openPostDetail(item.postId)">{{ item.title }}</h3>
          <span class="post-time">{{ formatTime(item.createdAt) }}</span>
        </header>
        <div class="tag-row">
          <el-tag type="primary" effect="light" size="small">{{ channelText(item.channel) }}</el-tag>
          <el-tag v-for="tag in item.tags" :key="`${item.postId}-${tag}`" size="small" effect="plain">{{ tag }}</el-tag>
        </div>
        <p class="post-summary">{{ item.summary }}</p>
        <div class="action-row">
          <el-button text @click="toggleLike(item)" :disabled="!isStudent">{{ item.viewerLiked ? '取消点赞' : '点赞' }} {{ item.likeCount }}</el-button>
          <el-button text @click="toggleFavorite(item)" :disabled="!isStudent">{{ item.viewerFavorited ? '取消收藏' : '收藏' }} {{ item.favoriteCount }}</el-button>
          <el-button text @click="openPostDetail(item.postId)">评论 {{ item.commentCount }}</el-button>
          <el-button text @click="sharePost(item)">转发 {{ item.shareCount }}</el-button>
        </div>
      </article>
    </div>

    <div class="pager-wrap" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="page"
        :page-size="pageSize"
        @current-change="reloadPosts"
      />
    </div>

    <el-dialog v-model="createDialogVisible" title="发布帖子" width="760px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="分栏目">
          <el-radio-group v-model="createForm.channel">
            <el-radio-button label="EXPERIENCE">留学经验贴</el-radio-button>
            <el-radio-button label="OFFER_WALL">offer墙</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="createForm.title" maxlength="120" show-word-limit placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="标签（最多 8 个，回车添加）">
          <el-select v-model="createForm.tags" multiple filterable allow-create default-first-option placeholder="如：背景提升、申请季、CS" />
        </el-form-item>
        <el-form-item label="正文">
          <div class="editor-wrap">
            <div class="editor-tools">
              <el-button size="small" @click="execFormat('bold')">B</el-button>
              <el-button size="small" @click="execFormat('italic')">I</el-button>
              <el-button size="small" @click="execFormat('insertUnorderedList')">• 列表</el-button>
              <el-button size="small" @click="execFormat('insertOrderedList')">1. 列表</el-button>
              <el-button size="small" @click="execFormat('formatBlock', 'blockquote')">引用</el-button>
              <el-button size="small" @click="insertImageUrl">图片</el-button>
            </div>
            <div ref="editorRef" class="editor-content" contenteditable="true" spellcheck="false" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" size="55%" title="帖子详情">
      <div v-if="postDetail" class="detail-body">
        <h3 class="detail-title">{{ postDetail.title }}</h3>
        <div class="tag-row mb12">
          <el-tag type="primary" effect="light" size="small">{{ channelText(postDetail.channel) }}</el-tag>
          <el-tag v-for="tag in postDetail.tags" :key="`detail-${tag}`" size="small" effect="plain">{{ tag }}</el-tag>
        </div>
        <div class="detail-content" v-html="postDetail.contentHtml || ''" />

        <div class="action-row detail-actions">
          <el-button text @click="toggleLike(postDetail)" :disabled="!isStudent">{{ postDetail.viewerLiked ? '取消点赞' : '点赞' }} {{ postDetail.likeCount }}</el-button>
          <el-button text @click="toggleFavorite(postDetail)" :disabled="!isStudent">{{ postDetail.viewerFavorited ? '取消收藏' : '收藏' }} {{ postDetail.favoriteCount }}</el-button>
          <el-button text @click="sharePost(postDetail)">转发 {{ postDetail.shareCount }}</el-button>
        </div>

        <div class="comment-section">
          <h4>评论（{{ postDetail.commentCount }}）</h4>
          <el-input
            v-model="commentDraft"
            type="textarea"
            :rows="3"
            maxlength="5000"
            show-word-limit
            placeholder="写下你的看法..."
            :disabled="!isStudent"
          />
          <div class="comment-send">
            <el-button type="primary" :disabled="!isStudent" :loading="sendingComment" @click="submitComment">发送评论</el-button>
          </div>
          <div class="comment-list" v-loading="loadingComments">
            <el-empty v-if="!comments.length" description="暂无评论" />
            <div v-for="item in comments" :key="item.commentId" class="comment-item">
              <p class="comment-meta">用户 {{ item.authorUserId }} · {{ formatTime(item.createdAt) }}</p>
              <p class="comment-text">{{ item.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <el-drawer v-model="notificationVisible" title="论坛互动通知" size="420px">
      <div class="notify-head">
        <span>未读 {{ unreadCount }}</span>
        <el-button link type="primary" @click="markAllNotificationsRead">全部已读</el-button>
      </div>
      <div v-loading="loadingNotifications" class="notify-list">
        <el-empty v-if="!notifications.length" description="暂无通知" />
        <div v-for="item in notifications" :key="item.notificationId" class="notify-item" :class="{ unread: !item.read }" @click="openNotificationPost(item.postId)">
          <p class="notify-text">用户 {{ item.actorUserId }} {{ notificationText(item.type) }}了你的帖子</p>
          <p class="notify-time">{{ formatTime(item.createdAt) }}</p>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { ApiError } from '../services/http'
import {
  addForumComment,
  createForumPost,
  favoriteForumPost,
  getForumPostDetail,
  likeForumPost,
  listForumComments,
  listForumNotifications,
  listForumPosts,
  markForumNotificationsRead,
  shareForumPost,
  unfavoriteForumPost,
  unlikeForumPost
} from '../services/forum'
import { useAuthStore } from '../stores/auth'
import type { ForumChannel, ForumNotificationType, ForumPost, ForumNotification } from '../types/forum'

const authStore = useAuthStore()
const route = useRoute()

const loadingPosts = ref(false)
const activeChannel = ref<ForumChannel>('EXPERIENCE')
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const posts = ref<ForumPost[]>([])

const createDialogVisible = ref(false)
const creating = ref(false)
const editorRef = ref<HTMLDivElement | null>(null)
const createForm = reactive({
  channel: 'EXPERIENCE' as ForumChannel,
  title: '',
  tags: [] as string[]
})

const detailVisible = ref(false)
const postDetail = ref<ForumPost | null>(null)
const comments = ref<Array<{ commentId: string; authorUserId: number; content: string; createdAt: string }>>([])
const loadingComments = ref(false)
const commentDraft = ref('')
const sendingComment = ref(false)

const notificationVisible = ref(false)
const loadingNotifications = ref(false)
const notifications = ref<ForumNotification[]>([])
const unreadCount = ref(0)

const channelSegments: Array<{ label: string; value: ForumChannel }> = [
  { label: '留学经验贴', value: 'EXPERIENCE' },
  { label: 'offer墙', value: 'OFFER_WALL' }
]

const isStudent = computed(() => authStore.authMeta?.role === 'STUDENT')

watch(activeChannel, () => {
  reloadPosts(1)
})

onMounted(async () => {
  await reloadPosts(1)
  await loadNotifications()

  const postId = String(route.query.postId || '')
  if (postId) {
    openPostDetail(postId)
  }
})

function channelText(channel: ForumChannel) {
  return channel === 'EXPERIENCE' ? '留学经验贴' : 'offer墙'
}

function notificationText(type: ForumNotificationType) {
  if (type === 'COMMENT') return '评论'
  if (type === 'FAVORITE') return '收藏'
  return '点赞'
}

function formatTime(value: string) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

async function reloadPosts(nextPage = page.value) {
  loadingPosts.value = true
  try {
    const data = await listForumPosts({
      channel: activeChannel.value,
      keyword: keyword.value.trim() || undefined,
      page: nextPage,
      pageSize: pageSize.value
    })
    posts.value = data.items
    total.value = data.total
    page.value = data.page
    pageSize.value = data.pageSize
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '帖子加载失败')
  } finally {
    loadingPosts.value = false
  }
}

function openCreateDialog() {
  if (!isStudent.value) return
  createDialogVisible.value = true
  nextTick(() => {
    if (editorRef.value) editorRef.value.innerHTML = ''
  })
}

function execFormat(command: string, value?: string) {
  editorRef.value?.focus()
  document.execCommand(command, false, value)
}

function insertImageUrl() {
  const url = window.prompt('请输入图片链接')
  if (!url) return
  execFormat('insertImage', url)
}

async function submitPost() {
  const contentHtml = editorRef.value?.innerHTML?.trim() || ''
  if (!createForm.title.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }
  if (!contentHtml || !contentHtml.replace(/<[^>]+>/g, '').trim()) {
    ElMessage.warning('正文不能为空')
    return
  }
  creating.value = true
  try {
    await createForumPost({
      channel: createForm.channel,
      title: createForm.title,
      contentHtml,
      tags: createForm.tags
    })
    ElMessage.success('发布成功')
    createDialogVisible.value = false
    createForm.title = ''
    createForm.tags = []
    if (editorRef.value) editorRef.value.innerHTML = ''
    await reloadPosts(1)
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '发布失败')
  } finally {
    creating.value = false
  }
}

async function openPostDetail(postId: string) {
  detailVisible.value = true
  loadingComments.value = true
  try {
    const [detail, commentData] = await Promise.all([getForumPostDetail(postId), listForumComments(postId)])
    postDetail.value = detail
    comments.value = commentData.items
    commentDraft.value = ''
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '帖子详情加载失败')
  } finally {
    loadingComments.value = false
  }
}

function syncPostInList(postId: string, patch: Partial<ForumPost>) {
  posts.value = posts.value.map((item) => (item.postId === postId ? { ...item, ...patch } : item))
  if (postDetail.value?.postId === postId) {
    postDetail.value = { ...postDetail.value, ...patch }
  }
}

async function toggleLike(post: ForumPost) {
  if (!isStudent.value) return
  try {
    const state = post.viewerLiked ? await unlikeForumPost(post.postId) : await likeForumPost(post.postId)
    syncPostInList(post.postId, {
      viewerLiked: state.liked,
      viewerFavorited: state.favorited,
      likeCount: state.likeCount,
      favoriteCount: state.favoriteCount
    })
    if (postDetail.value?.postId === post.postId && !post.viewerLiked) {
      await loadNotifications()
    }
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function toggleFavorite(post: ForumPost) {
  if (!isStudent.value) return
  try {
    const state = post.viewerFavorited ? await unfavoriteForumPost(post.postId) : await favoriteForumPost(post.postId)
    syncPostInList(post.postId, {
      viewerLiked: state.liked,
      viewerFavorited: state.favorited,
      likeCount: state.likeCount,
      favoriteCount: state.favoriteCount
    })
    if (postDetail.value?.postId === post.postId && !post.viewerFavorited) {
      await loadNotifications()
    }
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function sharePost(post: ForumPost) {
  try {
    const data = await shareForumPost(post.postId)
    await copyText(data.shareUrl)
    syncPostInList(post.postId, { shareCount: data.shareCount })
    ElMessage.success('分享链接已复制')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '转发失败')
  }
}

async function submitComment() {
  if (!isStudent.value || !postDetail.value) return
  const content = commentDraft.value.trim()
  if (!content) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  sendingComment.value = true
  try {
    const newComment = await addForumComment(postDetail.value.postId, { content })
    comments.value = [...comments.value, newComment]
    syncPostInList(postDetail.value.postId, { commentCount: (postDetail.value.commentCount || 0) + 1 })
    commentDraft.value = ''
    ElMessage.success('评论成功')
    await loadNotifications()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '评论失败')
  } finally {
    sendingComment.value = false
  }
}

function openNotificationDrawer() {
  notificationVisible.value = true
  loadNotifications()
}

async function loadNotifications() {
  loadingNotifications.value = true
  try {
    const data = await listForumNotifications({ page: 1, pageSize: 20 })
    notifications.value = data.items
    unreadCount.value = data.unreadCount
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '通知加载失败')
  } finally {
    loadingNotifications.value = false
  }
}

async function markAllNotificationsRead() {
  try {
    await markForumNotificationsRead({ markAll: true })
    notifications.value = notifications.value.map((item) => ({ ...item, read: true }))
    unreadCount.value = 0
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function openNotificationPost(postId: string) {
  const unreadIds = notifications.value.filter((item) => !item.read).map((item) => item.notificationId)
  if (unreadIds.length > 0) {
    await markForumNotificationsRead({ notificationIds: unreadIds })
    notifications.value = notifications.value.map((item) => ({ ...item, read: true }))
    unreadCount.value = 0
  }
  notificationVisible.value = false
  openPostDetail(postId)
}

async function copyText(text: string) {
  if (navigator.clipboard && window.isSecureContext) {
    await navigator.clipboard.writeText(text)
    return
  }

  const input = document.createElement('textarea')
  input.value = text
  input.style.position = 'fixed'
  input.style.opacity = '0'
  document.body.appendChild(input)
  input.select()
  document.execCommand('copy')
  document.body.removeChild(input)
}
</script>

<style scoped>
.forum-shell {
  max-width: 980px;
  margin: 0 auto;
}

.forum-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-alert {
  margin-top: 14px;
}

.forum-toolbar {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-box {
  flex: 1;
}

.post-list {
  margin-top: 16px;
  display: grid;
  gap: 14px;
}

.post-card {
  border: 1px solid #dbe6f1;
  border-radius: 14px;
  padding: 14px;
  background: linear-gradient(165deg, #ffffff, #f8fbff);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  cursor: pointer;
}

.post-time {
  color: #7b8ca2;
  font-size: 13px;
}

.tag-row {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.post-summary {
  margin: 10px 0;
  color: #4f617b;
  line-height: 1.65;
}

.action-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pager-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.editor-wrap {
  border: 1px solid #dbe6f1;
  border-radius: 10px;
  overflow: hidden;
}

.editor-tools {
  padding: 10px;
  border-bottom: 1px solid #e5edf6;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.editor-content {
  min-height: 240px;
  padding: 12px;
  outline: none;
  line-height: 1.7;
}

.detail-body {
  padding-right: 4px;
}

.detail-title {
  margin: 0;
  font-size: 24px;
}

.mb12 {
  margin-bottom: 12px;
}

.detail-content {
  line-height: 1.8;
  color: #263f57;
}

.detail-content :deep(img) {
  max-width: 100%;
}

.detail-actions {
  margin-top: 12px;
}

.comment-section {
  margin-top: 16px;
  border-top: 1px solid #e6edf5;
  padding-top: 14px;
}

.comment-section h4 {
  margin: 0 0 10px;
}

.comment-send {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.comment-item {
  border: 1px solid #e6edf5;
  border-radius: 10px;
  padding: 10px;
  background: #fff;
}

.comment-meta {
  margin: 0;
  color: #7b8ca2;
  font-size: 12px;
}

.comment-text {
  margin: 6px 0 0;
  line-height: 1.6;
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
  border: 1px solid #e6edf5;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}

.notify-item.unread {
  border-color: #8bb7ff;
  background: #f3f8ff;
}

.notify-text,
.notify-time {
  margin: 0;
}

.notify-time {
  margin-top: 6px;
  color: #7b8ca2;
  font-size: 12px;
}

@media (max-width: 768px) {
  .forum-head,
  .forum-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .head-actions {
    justify-content: space-between;
  }
}
</style>
