<template>
  <section class="page-card fade-up forum-shell">
    <div class="forum-head">
      <div>
        <h2 class="section-title">论坛社区</h2>
        <p class="section-desc">搜索社区帖子，并按栏目筛选。</p>
      </div>
      <div class="head-actions">
        <el-button plain class="jump-btn" @click="goMine">我的</el-button>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-button plain class="jump-btn" @click="openNotificationDrawer">互动通知</el-button>
        </el-badge>
        <el-button type="primary" class="jump-btn" @click="goCreate">发布帖子</el-button>
      </div>
    </div>

    <el-alert
      v-if="isLoggedIn && !isStudent"
      title="当前账号不是学生角色，仅支持浏览；发帖与互动功能仅学生可用。"
      type="warning"
      :closable="false"
      show-icon
      class="role-alert"
    />

    <div class="search-hero">
      <el-input v-model="keywordInput" size="large" placeholder="搜索标题、正文、标签" clearable @keyup.enter="handleSearch">
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="filter-strip">
      <div class="filter-row">
        <span class="label">栏目</span>
        <el-radio-group v-model="channel" class="switch-group">
          <el-radio-button label="EXPERIENCE">留学经验贴</el-radio-button>
          <el-radio-button label="OFFER_WALL">offer墙</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div v-loading="loadingPosts" class="post-list" :key="`${channel}-${page}`">
      <el-empty v-if="!posts.length" description="暂无帖子，试试搜索关键词" />

      <article v-for="item in posts" :key="item.postId" class="post-card">
        <header class="post-header">
          <div class="post-head-left">
            <h3 class="post-title">{{ item.title }}</h3>
            <p class="post-author">{{ item.authorDisplayName || '某同学' }} · {{ formatTime(item.createdAt) }}</p>
          </div>
          <div class="post-right" v-if="canManage(item)">
            <el-button text type="primary" @click="goEdit(item.postId)">编辑</el-button>
            <el-button text type="danger" @click="removePost(item)">删除</el-button>
          </div>
        </header>

        <div class="tag-row">
          <el-tag type="primary" effect="light" size="small">{{ channelText(item.channel) }}</el-tag>
          <el-tag v-for="tag in visibleTags(item.tags, item.channel)" :key="`${item.postId}-${tag}`" size="small" effect="plain">{{ tag }}</el-tag>
        </div>

        <div class="post-body">
          <div v-if="isContentExpanded(item.postId)" class="detail-content" v-html="getPostContentHtml(item)" />
          <p v-else class="post-summary">{{ item.summary }}</p>
          <el-button link type="primary" @click="toggleContent(item)">
            {{ isContentExpanded(item.postId) ? '收起全文' : '展开全文' }}
          </el-button>
        </div>

        <div class="action-row">
          <el-button text class="action-btn like" :class="{ active: item.viewerLiked }" @click="toggleLike(item)">
            <span class="heart-icon">❤</span>
            <span>{{ item.likeCount }}</span>
          </el-button>
          <el-button text class="action-btn favorite" :class="{ active: item.viewerFavorited }" @click="toggleFavorite(item)">
            <el-icon><Star /></el-icon>
            <span>{{ item.favoriteCount }}</span>
          </el-button>
          <el-button text class="action-btn" :class="{ active: isCommentExpanded(item.postId) }" @click="toggleComment(item.postId)">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ item.commentCount }}</span>
          </el-button>
          <el-button text class="action-btn" @click="sharePost(item)">
            <el-icon><Share /></el-icon>
            <span>{{ item.shareCount }}</span>
          </el-button>
        </div>

        <div v-if="isCommentExpanded(item.postId)" class="comment-section" v-loading="isCommentsLoading(item.postId)">
          <h4>评论（{{ item.commentCount }}）</h4>
          <el-input
            :model-value="commentDraftByPost[item.postId] || ''"
            @update:model-value="onCommentInput(item.postId, $event)"
            type="textarea"
            :rows="3"
            maxlength="5000"
            show-word-limit
            placeholder="写下你的看法..."
          />
          <div class="comment-send">
            <el-button type="primary" :loading="sendingCommentPostId === item.postId" @click="submitComment(item)">发送评论</el-button>
          </div>
          <div class="comment-list">
            <el-empty v-if="!(commentsByPost[item.postId] || []).length" description="暂无评论" />
            <div v-for="comment in commentsByPost[item.postId] || []" :key="comment.commentId" class="comment-item">
              <p class="comment-meta">用户 {{ comment.authorUserId }} · {{ formatTime(comment.createdAt) }}</p>
              <p class="comment-text">{{ comment.content }}</p>
            </div>
          </div>
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
        @current-change="(p: number) => reloadPosts(p)"
      />
    </div>

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
import { ChatDotRound, Share, Star } from '@element-plus/icons-vue'
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { ApiError } from '../services/http'
import {
  addForumComment,
  deleteForumPost,
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
import type { ForumChannel, ForumComment, ForumNotification, ForumNotificationType, ForumPost } from '../types/forum'
import { confirmLoginRequired } from '../utils/authPrompt'

const router = useRouter()
const authStore = useAuthStore()

const loadingPosts = ref(false)
const channel = ref<ForumChannel>('EXPERIENCE')
const keywordInput = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const posts = ref<ForumPost[]>([])
let reloadPostsSeq = 0

const expandedContentPostIds = ref<Set<string>>(new Set())
const detailContentByPost = ref<Record<string, string>>({})
const expandedCommentPostIds = ref<Set<string>>(new Set())
const commentsByPost = ref<Record<string, ForumComment[]>>({})
const loadingCommentsByPost = ref<Record<string, boolean>>({})
const commentDraftByPost = ref<Record<string, string>>({})
const sendingCommentPostId = ref<string | null>(null)

const notificationVisible = ref(false)
const loadingNotifications = ref(false)
const notifications = ref<ForumNotification[]>([])
const unreadCount = ref(0)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const isStudent = computed(() => authStore.authMeta?.role === 'STUDENT')
const currentUserId = computed(() => authStore.authMeta?.userId || 0)

watch(channel, () => {
  reloadPosts(1)
})

onMounted(async () => {
  await reloadPosts(1)
  if (isLoggedIn.value) {
    await loadNotifications()
  }
})

function channelText(value: ForumChannel) {
  return value === 'EXPERIENCE' ? '留学经验贴' : 'offer墙'
}

function visibleTags(tags: string[] = [], value: ForumChannel) {
  const channelLabel = channelText(value).toLowerCase()
  const seen = new Set<string>()
  return tags.filter((tag) => {
    const clean = (tag || '').trim()
    const key = clean.toLowerCase()
    if (!clean || key === channelLabel || seen.has(key)) return false
    seen.add(key)
    return true
  })
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

function canManage(post: ForumPost) {
  return isStudent.value && post.authorUserId === currentUserId.value
}

function isContentExpanded(postId: string) {
  return expandedContentPostIds.value.has(postId)
}

function isCommentExpanded(postId: string) {
  return expandedCommentPostIds.value.has(postId)
}

function isCommentsLoading(postId: string) {
  return Boolean(loadingCommentsByPost.value[postId])
}

function getPostContentHtml(post: ForumPost) {
  return detailContentByPost.value[post.postId] || post.contentHtml || `<p>${post.summary}</p>`
}

async function ensureForumAction(actionText: string) {
  if (!isLoggedIn.value) {
    await confirmLoginRequired(router, actionText)
    return false
  }
  if (!isStudent.value) {
    ElMessage.warning('当前账号不是学生角色，仅学生可执行该操作')
    return false
  }
  return true
}

async function goMine() {
  if (!isLoggedIn.value) {
    await confirmLoginRequired(router, '查看我的论坛')
    return
  }
  router.push('/forum/mine')
}

async function goCreate() {
  if (!(await ensureForumAction('发布帖子'))) return
  router.push('/forum/new')
}

function goEdit(postId: string) {
  router.push(`/forum/edit/${postId}`)
}

function handleSearch() {
  reloadPosts(1)
}

async function reloadPosts(nextPage = page.value) {
  const seq = ++reloadPostsSeq
  loadingPosts.value = true
  try {
    const data = await listForumPosts({
      mode: 'COMMUNITY',
      channel: channel.value,
      keyword: keywordInput.value.trim() || undefined,
      page: nextPage,
      pageSize: pageSize.value
    })
    if (seq !== reloadPostsSeq) return
    posts.value = data.items
    total.value = data.total
    page.value = data.page
    pageSize.value = data.pageSize
  } catch (error) {
    if (seq !== reloadPostsSeq) return
    ElMessage.error(error instanceof ApiError ? error.message : '帖子加载失败')
  } finally {
    if (seq !== reloadPostsSeq) return
    loadingPosts.value = false
  }
}

async function ensurePostDetail(postId: string) {
  if (detailContentByPost.value[postId]) return
  const detail = await getForumPostDetail(postId)
  detailContentByPost.value = { ...detailContentByPost.value, [postId]: detail.contentHtml || '' }
}

async function toggleContent(post: ForumPost) {
  const next = new Set(expandedContentPostIds.value)
  if (next.has(post.postId)) {
    next.delete(post.postId)
    expandedContentPostIds.value = next
    return
  }
  try {
    await ensurePostDetail(post.postId)
    next.add(post.postId)
    expandedContentPostIds.value = next
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '帖子详情加载失败')
  }
}

async function toggleComment(postId: string) {
  const next = new Set(expandedCommentPostIds.value)
  if (next.has(postId)) {
    next.delete(postId)
    expandedCommentPostIds.value = next
    return
  }
  next.add(postId)
  expandedCommentPostIds.value = next
  await loadComments(postId)
}

async function loadComments(postId: string) {
  if (loadingCommentsByPost.value[postId]) return
  loadingCommentsByPost.value = { ...loadingCommentsByPost.value, [postId]: true }
  try {
    const data = await listForumComments(postId)
    commentsByPost.value = { ...commentsByPost.value, [postId]: data.items }
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '评论加载失败')
  } finally {
    loadingCommentsByPost.value = { ...loadingCommentsByPost.value, [postId]: false }
  }
}

async function removePost(post: ForumPost) {
  try {
    await ElMessageBox.confirm('删除后帖子及其评论/点赞/收藏/通知都会清除，确认删除吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteForumPost(post.postId)
    ElMessage.success('删除成功')
    await reloadPosts(page.value)
  } catch (error) {
    if (error instanceof Error && error.message === 'cancel') return
    ElMessage.error(error instanceof ApiError ? error.message : '删除失败')
  }
}

function syncPostInList(postId: string, patch: Partial<ForumPost>) {
  posts.value = posts.value.map((item) => (item.postId === postId ? { ...item, ...patch } : item))
}

async function toggleLike(post: ForumPost) {
  if (!(await ensureForumAction('点赞'))) return
  try {
    const state = post.viewerLiked ? await unlikeForumPost(post.postId) : await likeForumPost(post.postId)
    syncPostInList(post.postId, {
      viewerLiked: state.liked,
      viewerFavorited: state.favorited,
      likeCount: state.likeCount,
      favoriteCount: state.favoriteCount
    })
    await loadNotifications()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function toggleFavorite(post: ForumPost) {
  if (!(await ensureForumAction('收藏帖子'))) return
  try {
    const state = post.viewerFavorited ? await unfavoriteForumPost(post.postId) : await favoriteForumPost(post.postId)
    syncPostInList(post.postId, {
      viewerLiked: state.liked,
      viewerFavorited: state.favorited,
      likeCount: state.likeCount,
      favoriteCount: state.favoriteCount
    })
    await loadNotifications()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '操作失败')
  }
}

async function sharePost(post: ForumPost) {
  if (!(await ensureForumAction('分享帖子'))) return
  try {
    const data = await shareForumPost(post.postId)
    await copyText(data.shareUrl)
    syncPostInList(post.postId, { shareCount: data.shareCount })
    ElMessage.success('分享链接已复制')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '转发失败')
  }
}

function updateCommentDraft(postId: string, value: string) {
  commentDraftByPost.value = { ...commentDraftByPost.value, [postId]: value }
}

function onCommentInput(postId: string, value: string | number | null | undefined) {
  updateCommentDraft(postId, String(value || ''))
}

async function submitComment(post: ForumPost) {
  if (!(await ensureForumAction('发表评论'))) return
  const content = (commentDraftByPost.value[post.postId] || '').trim()
  if (!content) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  sendingCommentPostId.value = post.postId
  try {
    const newComment = await addForumComment(post.postId, { content })
    const current = commentsByPost.value[post.postId] || []
    commentsByPost.value = { ...commentsByPost.value, [post.postId]: [...current, newComment] }
    commentDraftByPost.value = { ...commentDraftByPost.value, [post.postId]: '' }
    syncPostInList(post.postId, { commentCount: post.commentCount + 1 })
    ElMessage.success('评论成功')
    await loadNotifications()
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '评论失败')
  } finally {
    sendingCommentPostId.value = null
  }
}

async function openNotificationDrawer() {
  if (!isLoggedIn.value) {
    await confirmLoginRequired(router, '查看互动通知')
    return
  }
  notificationVisible.value = true
  loadNotifications()
}

async function loadNotifications() {
  if (!isLoggedIn.value) {
    notifications.value = []
    unreadCount.value = 0
    return
  }
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

  try {
    const detail = await getForumPostDetail(postId)
    channel.value = detail.channel
    await reloadPosts(1)
    notificationVisible.value = false
    await toggleComment(postId)
  } catch {
    await reloadPosts(1)
    notificationVisible.value = false
  }
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
  max-width: 1140px;
  margin: 0 auto;
}

.forum-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.jump-btn {
  min-height: 42px;
  padding: 0 18px;
  transition: transform 0.2s cubic-bezier(0.2, 0.9, 0.25, 1), box-shadow 0.2s ease;
}

.jump-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(27, 71, 121, 0.15);
}

.role-alert {
  margin-top: 14px;
}

.search-hero {
  margin-top: 16px;
  border: 1px solid #dbe7f4;
  border-radius: 18px;
  padding: 16px;
  background: linear-gradient(135deg, #ffffff, #f6f9ff);
}

.filter-strip {
  margin-top: 14px;
  border: 1px solid #e0e9f5;
  border-radius: 16px;
  padding: 14px;
  background: #fff;
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.label {
  width: 44px;
  color: #667d9f;
  font-size: 14px;
}

.switch-group :deep(.el-radio-button__inner) {
  min-height: 42px;
  height: 42px;
  line-height: 42px;
  padding: 0 18px;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s cubic-bezier(0.2, 0.9, 0.25, 1);
}

.switch-group :deep(.is-active .el-radio-button__inner) {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(37, 99, 235, 0.22);
}

.post-list {
  margin-top: 18px;
  display: grid;
  gap: 16px;
}

.post-card {
  border: 1px solid #dbe6f1;
  border-radius: 16px;
  padding: 18px;
  background: linear-gradient(165deg, #ffffff, #f8fbff);
  animation: riseIn 0.22s ease-out;
}

@keyframes riseIn {
  from {
    transform: translateY(4px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
}

.post-head-left {
  min-width: 0;
}

.post-title {
  margin: 0;
  font-size: 22px;
}

.post-author {
  margin: 6px 0 0;
  color: #6f85a5;
  font-size: 14px;
}

.post-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-row {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.post-body {
  margin-top: 10px;
}

.post-summary {
  margin: 0;
  color: #4f617b;
  line-height: 1.7;
}

.detail-content {
  color: #34455e;
  line-height: 1.75;
  word-break: break-word;
}

.action-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #5a6d88;
  padding: 8px 10px;
  border-radius: 10px;
  transition: all 0.18s ease;
}

.action-btn:hover {
  background: #eef4ff;
  transform: translateY(-1px);
}

.action-btn.active.like,
.action-btn.like.active {
  color: #e53935;
  background: #fff2f3;
}

.action-btn.active.favorite,
.action-btn.favorite.active {
  color: #d48d00;
  background: #fff9e7;
}

.heart-icon {
  font-size: 15px;
  line-height: 1;
}

.comment-section {
  margin-top: 12px;
  border-top: 1px dashed #d7e2ef;
  padding-top: 12px;
}

.comment-section h4 {
  margin: 0 0 8px;
  color: #365173;
}

.comment-send {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}

.comment-item {
  background: #f7faff;
  border: 1px solid #e3edf8;
  border-radius: 10px;
  padding: 10px;
}

.comment-meta {
  margin: 0;
  color: #6d86a7;
  font-size: 12px;
}

.comment-text {
  margin: 6px 0 0;
  color: #2e4360;
  white-space: pre-wrap;
}

.pager-wrap {
  margin-top: 18px;
  display: flex;
  justify-content: center;
}

.notify-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  color: #597292;
}

.notify-list {
  display: grid;
  gap: 8px;
}

.notify-item {
  border: 1px solid #e2ebf7;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.notify-item:hover {
  border-color: #a9c5ef;
  transform: translateY(-1px);
}

.notify-item.unread {
  background: #f4f8ff;
  border-color: #bfd7ff;
}

.notify-text {
  margin: 0;
  color: #2f4564;
}

.notify-time {
  margin: 6px 0 0;
  color: #7d91ad;
  font-size: 12px;
}

@media (max-width: 768px) {
  .forum-head {
    flex-direction: column;
  }

  .head-actions {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .post-card {
    padding: 14px;
  }

  .post-title {
    font-size: 19px;
  }
}
</style>
