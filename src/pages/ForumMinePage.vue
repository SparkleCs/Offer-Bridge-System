<template>
  <section class="fade-up forum-shell forum-page">
    <div class="forum-head">
      <div>
        <h2 class="section-title">我的论坛</h2>
        <p class="section-desc">管理我发布、赞过、收藏的帖子。</p>
      </div>
      <div class="head-actions">
        <el-button plain class="jump-btn" @click="goCommunity">返回社区</el-button>
        <el-button type="primary" class="jump-btn" :disabled="!isStudent" @click="goCreate">发布帖子</el-button>
      </div>
    </div>

    <el-alert
      v-if="!isStudent"
      title="当前账号不是学生角色，仅支持浏览；发帖与互动功能仅学生可用。"
      type="warning"
      :closable="false"
      show-icon
      class="role-alert"
    />

    <div class="search-hero">
      <el-input v-model="keywordInput" size="large" placeholder="搜索我的帖子、赞过与收藏" clearable @keyup.enter="handleSearch">
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="filter-strip">
      <div class="filter-row">
        <span class="label">我的</span>
        <el-radio-group v-model="mineTab" class="switch-group">
          <el-radio-button label="POSTED">帖子</el-radio-button>
          <el-radio-button label="LIKED">赞过</el-radio-button>
          <el-radio-button label="FAVORITED">收藏</el-radio-button>
        </el-radio-group>
      </div>

      <div class="filter-row">
        <span class="label">栏目</span>
        <el-radio-group v-model="channel" class="switch-group">
          <el-radio-button label="EXPERIENCE">留学经验贴</el-radio-button>
          <el-radio-button label="OFFER_WALL">offer墙</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div v-loading="loadingPosts" class="post-list" :key="`${mineTab}-${channel}-${page}`">
      <el-empty v-if="!posts.length" :description="emptyText" />

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
          <el-tag v-for="tag in item.tags" :key="`${item.postId}-${tag}`" size="small" effect="plain">{{ tag }}</el-tag>
        </div>

        <div class="post-body">
          <div v-if="isContentExpanded(item.postId)" class="detail-content" v-html="getPostContentHtml(item)" />
          <p v-else class="post-summary">{{ item.summary }}</p>
          <el-button link type="primary" @click="toggleContent(item)">
            {{ isContentExpanded(item.postId) ? '收起全文' : '展开全文' }}
          </el-button>
        </div>

        <div class="action-row">
          <el-button text class="action-btn like" :class="{ active: item.viewerLiked }" :disabled="!isStudent" @click="toggleLike(item)">
            <span class="heart-icon">❤</span>
            <span>{{ item.likeCount }}</span>
          </el-button>
          <el-button text class="action-btn favorite" :class="{ active: item.viewerFavorited }" :disabled="!isStudent" @click="toggleFavorite(item)">
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
            :disabled="!isStudent"
          />
          <div class="comment-send">
            <el-button type="primary" :disabled="!isStudent" :loading="sendingCommentPostId === item.postId" @click="submitComment(item)">发送评论</el-button>
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
  listForumPosts,
  shareForumPost,
  unfavoriteForumPost,
  unlikeForumPost
} from '../services/forum'
import { useAuthStore } from '../stores/auth'
import type { ForumChannel, ForumComment, ForumPost } from '../types/forum'

const router = useRouter()
const authStore = useAuthStore()

const loadingPosts = ref(false)
const mineTab = ref<'POSTED' | 'LIKED' | 'FAVORITED'>('POSTED')
const channel = ref<ForumChannel>('EXPERIENCE')
const keywordInput = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const posts = ref<ForumPost[]>([])

const expandedContentPostIds = ref<Set<string>>(new Set())
const detailContentByPost = ref<Record<string, string>>({})
const expandedCommentPostIds = ref<Set<string>>(new Set())
const commentsByPost = ref<Record<string, ForumComment[]>>({})
const loadingCommentsByPost = ref<Record<string, boolean>>({})
const commentDraftByPost = ref<Record<string, string>>({})
const sendingCommentPostId = ref<string | null>(null)

const isStudent = computed(() => authStore.authMeta?.role === 'STUDENT')
const currentUserId = computed(() => authStore.authMeta?.userId || 0)
let reloadPostsSeq = 0
const emptyText = computed(() => {
  if (mineTab.value === 'POSTED') return '你还没有发布帖子'
  if (mineTab.value === 'LIKED') return '你还没有赞过帖子'
  return '你还没有收藏帖子'
})

watch([mineTab, channel], () => {
  reloadPosts(1)
})

onMounted(async () => {
  await reloadPosts(1)
})

function channelText(value: ForumChannel) {
  return value === 'EXPERIENCE' ? '留学经验贴' : 'offer墙'
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

function goCommunity() {
  router.push('/forum')
}

function goCreate() {
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
    const reaction = mineTab.value === 'POSTED' ? undefined : mineTab.value
    const data = await listForumPosts({
      mode: 'MINE',
      channel: channel.value,
      reaction,
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
  if (!isStudent.value) return
  try {
    const state = post.viewerLiked ? await unlikeForumPost(post.postId) : await likeForumPost(post.postId)
    syncPostInList(post.postId, {
      viewerLiked: state.liked,
      viewerFavorited: state.favorited,
      likeCount: state.likeCount,
      favoriteCount: state.favoriteCount
    })
    if (mineTab.value === 'LIKED' && !state.liked) {
      await reloadPosts(1)
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
    if (mineTab.value === 'FAVORITED' && !state.favorited) {
      await reloadPosts(1)
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

function updateCommentDraft(postId: string, value: string) {
  commentDraftByPost.value = { ...commentDraftByPost.value, [postId]: value }
}

function onCommentInput(postId: string, value: string | number | null | undefined) {
  updateCommentDraft(postId, String(value || ''))
}

async function submitComment(post: ForumPost) {
  if (!isStudent.value) return
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
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '评论失败')
  } finally {
    sendingCommentPostId.value = null
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
  display: grid;
  gap: 12px;
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

.forum-page {
  --forum-blue: var(--primary);
  --forum-teal: var(--primary-2);
  --forum-ink: #10223f;
  --forum-muted: #667891;
  --forum-line: rgba(164, 181, 204, 0.32);
  max-width: 1180px;
  padding: 20px clamp(16px, 3vw, 28px) 42px;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.forum-page .forum-head {
  position: relative;
  align-items: center;
  padding: 22px 0 20px;
  border-bottom: 1px solid rgba(164, 181, 204, 0.22);
}

.forum-page .forum-head::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: -1px;
  width: 108px;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(90deg, var(--forum-blue), rgba(24, 179, 168, 0.62));
}

.forum-page .section-title {
  font-size: clamp(32px, 4vw, 50px);
  line-height: 1.08;
  letter-spacing: 0;
}

.forum-page .section-desc {
  margin-top: 12px;
  color: var(--forum-muted);
  font-size: 17px;
}

.forum-page .head-actions {
  gap: 10px;
}

.forum-page .jump-btn {
  min-height: 42px;
  padding: 0 18px;
  border-radius: 999px;
  font-weight: 700;
  box-shadow: none;
}

.forum-page .jump-btn.el-button--primary {
  box-shadow: 0 12px 24px rgba(31, 107, 255, 0.16);
}

.forum-page .jump-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(31, 107, 255, 0.12);
}

.forum-page .role-alert {
  margin-top: 18px;
  border-radius: 14px;
}

.forum-page .search-hero {
  margin-top: 22px;
  padding: 10px;
  border: 1px solid var(--forum-line);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 18px 50px rgba(16, 34, 63, 0.06);
  backdrop-filter: blur(16px);
}

.forum-page .search-hero :deep(.el-input__wrapper) {
  min-height: 54px;
  padding: 0 20px;
  border-radius: 18px 0 0 18px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 0 0 1px rgba(219, 231, 244, 0.72);
}

.forum-page .search-hero :deep(.el-input-group__append) {
  overflow: hidden;
  border-radius: 0 18px 18px 0;
  background: #f7fbff;
  box-shadow: inset 0 0 0 1px rgba(219, 231, 244, 0.72);
}

.forum-page .search-hero :deep(.el-input-group__append .el-button) {
  min-width: 84px;
  color: var(--forum-blue);
  font-weight: 800;
}

.forum-page .filter-strip {
  margin-top: 16px;
  padding: 12px;
  border: 1px solid var(--forum-line);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 14px 38px rgba(16, 34, 63, 0.04);
}

.forum-page .filter-row {
  gap: 14px;
}

.forum-page .label {
  width: auto;
  min-width: 44px;
  color: #7386a3;
  font-size: 13px;
  font-weight: 800;
}

.forum-page .switch-group {
  display: inline-flex;
  gap: 8px;
  padding: 5px;
  border: 1px solid rgba(164, 181, 204, 0.28);
  border-radius: 999px;
  background: rgba(246, 250, 255, 0.92);
}

.forum-page .switch-group :deep(.el-radio-button__inner) {
  min-height: 34px;
  height: 34px;
  padding: 0 16px;
  border: 0;
  border-radius: 999px !important;
  color: #5f718a;
  background: transparent;
  font-weight: 800;
  box-shadow: none;
}

.forum-page .switch-group :deep(.el-radio-button:first-child .el-radio-button__inner),
.forum-page .switch-group :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 999px !important;
}

.forum-page .switch-group :deep(.is-active .el-radio-button__inner) {
  color: #ffffff;
  background: linear-gradient(135deg, var(--forum-blue), #5d96ff);
  box-shadow: 0 10px 20px rgba(31, 107, 255, 0.16);
  transform: translateY(-1px);
}

.forum-page .post-list {
  margin-top: 22px;
  gap: 18px;
}

.forum-page .post-card {
  position: relative;
  overflow: hidden;
  padding: 24px 26px 22px;
  border: 1px solid rgba(164, 181, 204, 0.3);
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(248, 251, 255, 0.9)),
    radial-gradient(circle at 100% 0%, rgba(31, 107, 255, 0.08), transparent 34%);
  box-shadow: 0 18px 52px rgba(16, 34, 63, 0.06);
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.forum-page .post-card:hover {
  transform: translateY(-3px);
  border-color: rgba(31, 107, 255, 0.24);
  box-shadow: 0 24px 64px rgba(16, 34, 63, 0.09);
}

.forum-page .post-title {
  color: var(--forum-ink);
  font-size: clamp(24px, 2.6vw, 32px);
  line-height: 1.2;
  letter-spacing: 0;
}

.forum-page .post-author {
  margin-top: 8px;
  color: #7b8ca5;
  font-size: 13px;
}

.forum-page .post-right :deep(.el-button) {
  padding: 4px 6px;
  font-weight: 700;
}

.forum-page .tag-row {
  margin-top: 14px;
  gap: 7px;
}

.forum-page .tag-row :deep(.el-tag) {
  height: 26px;
  border-radius: 999px;
  font-weight: 700;
}

.forum-page .tag-row :deep(.el-tag--primary) {
  background: rgba(31, 107, 255, 0.09);
  border-color: rgba(31, 107, 255, 0.18);
}

.forum-page .post-body {
  margin-top: 16px;
}

.forum-page .post-summary,
.forum-page .detail-content {
  color: #42536b;
  font-size: 16px;
  line-height: 1.85;
}

.forum-page .post-body :deep(.el-button.is-link) {
  margin-top: 6px;
  padding: 0;
  font-weight: 800;
}

.forum-page .action-row {
  margin-top: 18px;
  gap: 8px;
}

.forum-page .action-btn {
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  color: #63758e;
  background: rgba(246, 250, 255, 0.72);
  box-shadow: inset 0 0 0 1px rgba(164, 181, 204, 0.18);
}

.forum-page .action-btn:hover {
  color: var(--forum-blue);
  background: rgba(238, 244, 255, 0.96);
  box-shadow: inset 0 0 0 1px rgba(31, 107, 255, 0.16);
}

.forum-page .action-btn.active:not(.like):not(.favorite) {
  color: var(--forum-blue);
  background: rgba(31, 107, 255, 0.08);
}

.forum-page .comment-section {
  margin-top: 18px;
  padding: 18px;
  border: 1px solid rgba(164, 181, 204, 0.24);
  border-radius: 18px;
  background: rgba(247, 250, 255, 0.76);
}

.forum-page .comment-section h4 {
  margin-bottom: 12px;
  color: var(--forum-ink);
}

.forum-page .comment-section :deep(.el-textarea__inner) {
  border-radius: 14px;
  box-shadow: 0 0 0 1px rgba(219, 231, 244, 0.88) inset;
}

.forum-page .comment-item {
  border-color: rgba(164, 181, 204, 0.24);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.84);
}

@media (max-width: 768px) {
  .forum-page {
    padding: 12px 4px 32px;
  }

  .forum-page .forum-head {
    align-items: stretch;
    padding-top: 12px;
  }

  .forum-page .head-actions {
    justify-content: flex-start;
  }

  .forum-page .jump-btn {
    flex: 1 1 auto;
  }

  .forum-page .search-hero {
    border-radius: 18px;
  }

  .forum-page .search-hero :deep(.el-input__wrapper) {
    min-height: 48px;
    border-radius: 14px 0 0 14px;
  }

  .forum-page .filter-strip {
    border-radius: 18px;
  }

  .forum-page .switch-group {
    width: 100%;
    overflow-x: auto;
  }

  .forum-page .post-card {
    padding: 18px;
    border-radius: 20px;
  }

  .forum-page .post-title {
    font-size: 22px;
  }
}
</style>
