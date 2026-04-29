<template>
  <section class="chat-panel">
    <aside class="conversation-pane" v-loading="loadingConversations">
      <div class="conversation-controls">
        <el-input v-model="keyword" size="large" placeholder="搜索30天内的联系人" clearable>
          <template #suffix><Search class="search-icon" /></template>
        </el-input>
        <div class="chat-filters">
          <button
            v-for="item in filterOptions"
            :key="item.value"
            type="button"
            :class="{ active: activeFilter === item.value }"
            @click="activeFilter = item.value"
          >
            {{ item.label }}
          </button>
        </div>
      </div>
      <div class="conversation-list">
        <el-empty v-if="!filteredConversations.length" description="暂无会话" />
        <article
          v-for="item in filteredConversations"
          :key="item.conversationId"
          class="conversation-item"
          :class="{ active: item.conversationId === activeConversationId }"
          role="button"
          tabindex="0"
          @click="selectConversation(item.conversationId)"
          @keydown.enter.prevent="selectConversation(item.conversationId)"
        >
          <div class="avatar">{{ avatarText(item.peerName) }}</div>
          <div class="conversation-meta">
            <div class="conversation-title">
              <strong>{{ item.peerName }}</strong>
              <span class="conversation-date">{{ formatDate(item.updatedAt) }}</span>
            </div>
            <div class="conversation-tags">
              <span class="stage-tag" :class="`stage-${relationshipStage(item)}`">{{ relationshipLabel(item) }}</span>
              <button
                type="button"
                class="star-btn"
                :class="{ active: item.viewerStarred }"
                :title="item.viewerStarred ? '取消收藏' : '收藏会话'"
                @click.stop="toggleStar(item)"
              >
                <component :is="item.viewerStarred ? StarFilled : Star" />
              </button>
            </div>
            <p class="conversation-sub">{{ item.peerSubtitle }}</p>
            <p class="conversation-last">{{ item.lastMessage || '暂无消息' }}</p>
          </div>
          <span v-if="item.unreadCount > 0" class="unread-dot">{{ item.unreadCount }}</span>
        </article>
      </div>
    </aside>

    <main class="chat-window">
      <el-empty v-if="!activeConversation" description="请选择会话" />
      <template v-else>
        <header class="chat-header">
          <div>
            <h3>{{ activeConversation.peerName }}</h3>
            <p>{{ activeHeaderSubtitle }}</p>
          </div>
          <el-tag effect="plain" class="chat-tag">{{ mode === 'agent' ? '学生咨询' : '中介沟通' }}</el-tag>
        </header>

        <div ref="messageListRef" class="message-list" v-loading="loadingMessages">
          <template v-for="(message, index) in messages" :key="message.id">
            <div v-if="shouldShowTimeDivider(message, index)" class="time-divider">{{ formatDividerTime(message.createdAt) }}</div>
            <div class="message-row" :class="{ mine: message.mine }">
              <div class="message-bubble" :class="`type-${message.contentType.toLowerCase()}`">
                <a v-if="message.contentType === 'IMAGE'" :href="message.content" target="_blank" rel="noopener" class="image-message">
                  <img :src="message.content" :alt="fileNameFromUrl(message.content)" />
                </a>
                <a v-else-if="message.contentType === 'FILE'" :href="message.content" target="_blank" rel="noopener" class="file-message">
                  <Document class="file-icon" />
                  <span>{{ fileNameFromUrl(message.content) }}</span>
                </a>
                <div v-else-if="message.contentType === 'ACTION_CARD'" class="action-card">
                  <div class="action-card-head">
                    <Document v-if="parseActionPayload(message)?.actionType === 'RESUME_ACCESS'" />
                    <Phone v-else-if="parseActionPayload(message)?.actionType === 'PHONE_EXCHANGE'" />
                    <ChatRound v-else />
                    <div>
                      <strong>{{ actionTitle(message) }}</strong>
                      <span :class="`action-status status-${actionStatus(message).toLowerCase()}`">{{ actionStatusLabel(message) }}</span>
                    </div>
                  </div>
                  <p>{{ actionDescription(message) }}</p>
                  <div class="action-card-actions">
                    <template v-if="canRespondAction(message)">
                      <el-button size="small" plain @click="respondAction(message, false)">拒绝</el-button>
                      <el-button size="small" type="primary" @click="respondAction(message, true)">同意</el-button>
                    </template>
                    <template v-else-if="actionStatus(message) === 'APPROVED'">
                      <el-button v-if="parseActionPayload(message)?.actionType === 'RESUME_ACCESS'" size="small" type="primary" plain @click="openResume">查看简历</el-button>
                      <el-button v-else size="small" type="primary" plain @click="openContact(parseActionPayload(message)?.actionType)">查看联系方式</el-button>
                    </template>
                  </div>
                </div>
                <p v-else>{{ message.content }}</p>
              </div>
              <span v-if="isLastMineMessage(message, index)" class="read-state">{{ message.status === 'READ' ? '已读' : '送达' }}</span>
            </div>
          </template>
        </div>

        <footer class="message-composer">
          <div class="composer-tools">
            <el-button :icon="ChatRound" circle title="快捷语" />
            <el-button class="action-tool" :icon="Document" title="简历授权" @click="startAction('RESUME_ACCESS')">{{ mode === 'agent' ? '要简历' : '发简历' }}</el-button>
            <el-button class="action-tool" :icon="Phone" title="交换电话" @click="startAction('PHONE_EXCHANGE')">换电话</el-button>
            <el-button class="action-tool" :icon="ChatRound" title="交换微信" @click="startAction('WECHAT_EXCHANGE')">换微信</el-button>
            <el-button v-if="mode === 'student'" :icon="Picture" circle title="图片" :loading="uploading" @click="imageInputRef?.click()" />
            <el-button v-if="mode === 'student'" :icon="Document" circle title="文件" :loading="uploading" @click="fileInputRef?.click()" />
            <input ref="imageInputRef" class="hidden-input" type="file" accept="image/*" @change="onPickImage" />
            <input ref="fileInputRef" class="hidden-input" type="file" @change="onPickFile" />
          </div>
          <el-input
            v-model="draft"
            type="textarea"
            :rows="3"
            resize="none"
            maxlength="1000"
            show-word-limit
            placeholder="按 Enter 键发送，按 Ctrl+Enter 换行"
            @keydown.enter.exact.prevent="submitMessage"
            @keydown.ctrl.enter.exact.prevent="draft += '\n'"
          />
          <div class="composer-actions">
            <span>Enter 发送，Ctrl+Enter 换行</span>
            <el-button type="primary" :disabled="!draft.trim()" :loading="sending" @click="submitMessage">发送</el-button>
          </div>
        </footer>

        <el-dialog v-model="resumeDrawerVisible" title="附件预览" width="min(1080px, 92vw)" class="resume-preview-dialog" top="5vh">
          <div v-loading="loadingResume" class="resume-preview-shell">
            <template v-if="resume">
              <article class="resume-paper">
                <header class="resume-paper-head">
                  <div class="resume-title-block">
                    <h2>{{ resume.displayName }} - {{ resume.targetMajorText || '学术背景' }}</h2>
                    <div class="resume-identity">
                      <span>{{ educationLabel(resume.educationLevel) }}</span>
                      <span>{{ resume.intakeTerm || '入学季待完善' }}</span>
                    </div>
                    <p>{{ resume.schoolName || '学校待完善' }} · {{ resume.major || '专业待完善' }}</p>
                    <p>目标国家：{{ resume.targetCountries?.map((item) => item.countryName).join('、') || '待完善' }}</p>
                  </div>
                  <div class="resume-photo-placeholder">
                    <strong>{{ avatarText(resume.displayName) }}</strong>
                    <span>学术简历</span>
                  </div>
                </header>

                <section class="resume-paper-section">
                  <h3>教育背景</h3>
                  <div class="resume-line-item">
                    <div>
                      <strong>{{ resume.schoolName || '学校待完善' }} - {{ resume.major || '专业待完善' }}</strong>
                      <p>GPA {{ formatResumeGpa(resume) }} · {{ resume.rankValue ? `排名前 ${resume.rankValue}%` : '排名待完善' }}</p>
                      <p>语言成绩：{{ languageSummary(resume) }}</p>
                    </div>
                    <time>{{ resume.intakeTerm || '入学季待完善' }}</time>
                  </div>

                  <div v-if="resume.exchangeExperience" class="resume-line-item">
                    <div>
                      <strong>{{ resume.exchangeExperience.universityName || '交换大学待完善' }} - {{ resume.exchangeExperience.countryName || '国家待完善' }}</strong>
                      <p>海外交换项目，GPA {{ resume.exchangeExperience.gpaValue ?? '待完善' }}</p>
                      <p>{{ resume.exchangeExperience.majorCourses || '主修课程待完善' }}</p>
                    </div>
                    <time>{{ resume.exchangeExperience.startDate || '-' }} - {{ resume.exchangeExperience.endDate || '-' }}</time>
                  </div>
                </section>

                <section class="resume-paper-section">
                  <h3>科研与论文</h3>
                  <p v-if="!resume.researchExperiences?.length" class="resume-empty-line">暂无科研经历</p>
                  <div v-for="item in resume.researchExperiences" :key="item.id || item.projectName" class="resume-line-item">
                    <div>
                      <strong>{{ item.projectName || '科研项目' }}</strong>
                      <p>{{ item.contentSummary || '暂无内容摘要' }}</p>
                      <p v-if="item.publications?.length">论文发表：{{ publicationSummary(item.publications) }}</p>
                    </div>
                    <time>{{ item.startDate || '-' }} - {{ item.endDate || '-' }}</time>
                  </div>
                </section>

                <section class="resume-paper-section">
                  <h3>竞赛经历</h3>
                  <p v-if="!resume.competitionExperiences?.length" class="resume-empty-line">暂无竞赛经历</p>
                  <div v-for="item in resume.competitionExperiences" :key="item.id || item.competitionName" class="resume-line-item">
                    <div>
                      <strong>{{ item.competitionName || '竞赛' }}</strong>
                      <p>{{ item.competitionLevel || '级别待完善' }} · {{ item.award || '奖项待完善' }}</p>
                      <p>{{ item.roleDesc || '暂无职责说明' }}</p>
                    </div>
                    <time>{{ item.eventDate || '时间待完善' }}</time>
                  </div>
                </section>

                <section class="resume-paper-section">
                  <h3>实习</h3>
                  <p v-if="!resume.workExperiences?.length" class="resume-empty-line">暂无实习或工作经历</p>
                  <div v-for="item in resume.workExperiences" :key="item.id || `${item.companyName}-${item.positionName}`" class="resume-line-item resume-work-item">
                    <div>
                      <strong>{{ item.companyName || '单位待完善' }} - {{ item.positionName || '岗位待完善' }}</strong>
                      <div v-if="item.keywords" class="resume-keywords">
                        <span v-for="keyword in splitKeywords(item.keywords)" :key="keyword">{{ keyword }}</span>
                      </div>
                      <p>{{ item.contentSummary || '暂无内容摘要' }}</p>
                    </div>
                    <time>{{ item.startDate || '-' }} - {{ item.endDate || '-' }}</time>
                  </div>
                </section>
              </article>
            </template>
          </div>
        </el-dialog>
      </template>
    </main>
  </section>
</template>

<script setup lang="ts">
import { Client } from '@stomp/stompjs'
import { ChatRound, Document, Phone, Picture, Search, Star, StarFilled } from '@element-plus/icons-vue'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { readAccessToken } from '../services/http'
import { listChatConversations, listChatMessages, markChatRead, sendChatMessage, starChatConversation, startChatAction, respondChatAction, getStudentResume, getExchangedContact, unstarChatConversation, uploadChatFile } from '../services/message'
import { getStudentProfile, updateStudentWechat } from '../services/student'
import { getMyAgencyProfile, updateMyWechat } from '../services/agency'
import type { ChatActionPayload, ChatActionType, ChatConversationItem, ChatMessageItem, StudentAcademicResume } from '../types/message'
import { getUploadErrorMessage, validateUploadFileSize } from '../utils/upload'

type ConversationFilter = 'all' | 'new' | 'unread' | 'active' | 'servicing' | 'ended' | 'starred'

const props = defineProps<{
  initialConversationId?: string
  mode?: 'student' | 'agent'
}>()

const emit = defineEmits<{
  unreadChange: [count: number]
}>()

const conversations = ref<ChatConversationItem[]>([])
const messages = ref<ChatMessageItem[]>([])
const activeConversationId = ref('')
const keyword = ref('')
const activeFilter = ref<ConversationFilter>('all')
const draft = ref('')
const loadingConversations = ref(false)
const loadingMessages = ref(false)
const sending = ref(false)
const uploading = ref(false)
const loadingResume = ref(false)
const resumeDrawerVisible = ref(false)
const resume = ref<StudentAcademicResume | null>(null)
const messageListRef = ref<HTMLElement | null>(null)
const imageInputRef = ref<HTMLInputElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
let stompClient: Client | null = null

const mode = computed(() => props.mode || 'student')
const filterOptions = computed<Array<{ label: string; value: ConversationFilter }>>(() => {
  if (mode.value === 'agent') {
    return [
      { label: '全部', value: 'all' },
      { label: '新咨询', value: 'new' },
      { label: '未读', value: 'unread' },
      { label: '沟通中', value: 'active' },
      { label: '服务中', value: 'servicing' },
      { label: '已结束', value: 'ended' },
      { label: '收藏', value: 'starred' }
    ]
  }
  return [
    { label: '全部', value: 'all' },
    { label: '未读', value: 'unread' },
    { label: '咨询中', value: 'active' },
    { label: '合作中', value: 'servicing' },
    { label: '已结束', value: 'ended' },
    { label: '收藏', value: 'starred' }
  ]
})
const activeConversation = computed(() => conversations.value.find((item) => item.conversationId === activeConversationId.value) || null)
const activeHeaderSubtitle = computed(() => {
  if (!activeConversation.value) return ''
  if (mode.value === 'agent') {
    return activeConversation.value.peerSubtitle || '学校待完善 · 专业待完善'
  }
  return `${activeConversation.value.orgName || '机构待完善'} · ${activeConversation.value.teamName || '套餐待完善'}`
})
const filteredConversations = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return conversations.value.filter((item) => {
    if (!matchesFilter(item, activeFilter.value)) return false
    if (!key) return true
    return [
      item.peerName,
      item.peerSubtitle,
      item.teamName,
      item.orgName,
      item.studentSchoolName,
      item.studentMajor,
      item.studentTargetMajorText,
      item.lastMessage
    ].some((value) => String(value || '').toLowerCase().includes(key))
  })
})

onMounted(async () => {
  await loadConversations()
  connectSocket()
})

onBeforeUnmount(() => {
  stompClient?.deactivate()
})

watch(() => props.initialConversationId, async (next) => {
  if (next && next !== activeConversationId.value) {
    await selectConversation(next)
  }
})

watch(activeFilter, async () => {
  await loadConversations(false)
})

async function loadConversations(autoSelect = true) {
  loadingConversations.value = true
  try {
    const data = await listChatConversations({ page: 1, pageSize: 100, filter: activeFilter.value })
    conversations.value = data.records
    emit('unreadChange', data.unreadCount)
    if (!autoSelect) {
      if (!conversations.value.some((item) => item.conversationId === activeConversationId.value)) {
        activeConversationId.value = ''
        messages.value = []
      }
      return
    }
    const preferredId = props.initialConversationId || activeConversationId.value
    const nextId = conversations.value.some((item) => item.conversationId === preferredId) ? preferredId : conversations.value[0]?.conversationId || ''
    if (nextId) await selectConversation(nextId)
    else {
      activeConversationId.value = ''
      messages.value = []
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '会话加载失败')
  } finally {
    loadingConversations.value = false
  }
}

async function selectConversation(conversationId: string) {
  activeConversationId.value = conversationId
  const selectedUnread = conversations.value.find((item) => item.conversationId === conversationId)?.unreadCount || 0
  if (selectedUnread > 0) {
    conversations.value = conversations.value.map((item) => item.conversationId === conversationId ? { ...item, unreadCount: 0 } : item)
    emit('unreadChange', conversations.value.reduce((sum, item) => sum + item.unreadCount, 0))
  }
  loadingMessages.value = true
  try {
    const data = await listChatMessages(conversationId, { page: 1, pageSize: 200 })
    messages.value = data.records
    await markChatRead(conversationId).catch(() => null)
    conversations.value = conversations.value.map((item) => item.conversationId === conversationId ? { ...item, unreadCount: 0 } : item)
    emit('unreadChange', conversations.value.reduce((sum, item) => sum + item.unreadCount, 0))
    await scrollToBottom()
  } catch (error: any) {
    ElMessage.error(error?.message || '消息加载失败')
  } finally {
    loadingMessages.value = false
  }
}

async function submitMessage() {
  if (!activeConversationId.value || !draft.value.trim()) return
  const content = draft.value.trim()
  draft.value = ''
  sending.value = true
  try {
    if (stompClient?.connected) {
      stompClient.publish({
        destination: `/app/chats/${activeConversationId.value}/send`,
        body: JSON.stringify({ content })
      })
    } else {
      const saved = await sendChatMessage(activeConversationId.value, { content })
      upsertMessage(saved)
    }
  } catch (error: any) {
    draft.value = content
    ElMessage.error(error?.message || '发送失败')
  } finally {
    sending.value = false
  }
}

async function onPickImage(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  await uploadAndSend(file, 'IMAGE')
}

async function onPickFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  await uploadAndSend(file, 'FILE')
}

async function uploadAndSend(file: File, contentType: 'IMAGE' | 'FILE') {
  if (!activeConversationId.value || !validateUploadFileSize(file)) return
  uploading.value = true
  try {
    const data = await uploadChatFile(file)
    const saved = await sendChatMessage(activeConversationId.value, {
      content: data.url,
      contentType
    })
    upsertMessage(saved)
  } catch (error) {
    ElMessage.error(getUploadErrorMessage(error, '附件发送失败'))
  } finally {
    uploading.value = false
  }
}

async function startAction(actionType: ChatActionType) {
  if (!activeConversationId.value) return
  if (mode.value === 'student' && !(await confirmStudentAction(actionType))) return
  if (actionType === 'WECHAT_EXCHANGE' && !(await ensureWechatReady())) return
  try {
    const saved = await startChatAction(activeConversationId.value, { actionType })
    upsertMessage(saved)
    if (actionType === 'RESUME_ACCESS' && mode.value === 'student') {
      patchConversationGrant(actionType)
      ElMessage.success('已向中介开放学术简历')
    } else {
      ElMessage.success(actionType === 'RESUME_ACCESS' ? '已发送简历查看请求' : '已发送互换请求')
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '请求发送失败')
  }
}

async function confirmStudentAction(actionType: ChatActionType) {
  const titleMap: Record<ChatActionType, string> = {
    RESUME_ACCESS: '确认发送简历？',
    PHONE_EXCHANGE: '确认发起交换电话？',
    WECHAT_EXCHANGE: '确认发起交换微信？'
  }
  const messageMap: Record<ChatActionType, string> = {
    RESUME_ACCESS: '确认后，中介将获得本会话内查看你学术简历的权限。简历不包含电话、邮箱、微信、预算和证件信息。',
    PHONE_EXCHANGE: '确认后，将向中介发送互换电话请求；对方同意后，双方都可以查看电话。',
    WECHAT_EXCHANGE: '确认后，将向中介发送互换微信请求；对方同意后，双方都可以查看微信。'
  }
  try {
    await ElMessageBox.confirm(messageMap[actionType], titleMap[actionType], {
      confirmButtonText: '确认发送',
      cancelButtonText: '取消',
      type: 'warning'
    })
    return true
  } catch {
    return false
  }
}

async function respondAction(message: ChatMessageItem, approved: boolean) {
  const payload = parseActionPayload(message)
  if (!payload || !activeConversationId.value) return
  if (approved && payload.actionType === 'WECHAT_EXCHANGE' && !(await ensureWechatReady())) return
  try {
    const saved = await respondChatAction(activeConversationId.value, message.id, { approved })
    upsertMessage(saved)
    if (approved) patchConversationGrant(payload.actionType)
    ElMessage.success(approved ? '已同意' : '已拒绝')
  } catch (error: any) {
    ElMessage.error(error?.message || '操作失败')
  }
}

async function openResume() {
  if (!activeConversationId.value) return
  resumeDrawerVisible.value = true
  loadingResume.value = true
  try {
    resume.value = await getStudentResume(activeConversationId.value)
  } catch (error: any) {
    ElMessage.error(error?.message || '简历加载失败')
  } finally {
    loadingResume.value = false
  }
}

async function openContact(actionType?: ChatActionType) {
  if (!activeConversationId.value || (actionType !== 'PHONE_EXCHANGE' && actionType !== 'WECHAT_EXCHANGE')) return
  try {
    const data = await getExchangedContact(activeConversationId.value, actionType)
    const label = actionType === 'PHONE_EXCHANGE' ? '电话' : '微信'
    await ElMessageBox.alert(`我的${label}：${data.ownContact}\n对方${label}：${data.peerContact}`, `${label}互换结果`, {
      confirmButtonText: '知道了'
    })
  } catch (error: any) {
    ElMessage.error(error?.message || '联系方式加载失败')
  }
}

async function ensureWechatReady() {
  try {
    if (mode.value === 'student') {
      const profile = await getStudentProfile()
      if (profile.wechatId?.trim()) return true
      const value = await promptWechat()
      await updateStudentWechat(value)
      return true
    }
    const profile = await getMyAgencyProfile()
    if (profile.wechatId?.trim()) return true
    const value = await promptWechat()
    await updateMyWechat(value)
    return true
  } catch (error: any) {
    if (error === 'cancel' || error === 'close') return false
    ElMessage.error(error?.message || '微信号保存失败')
    return false
  }
}

async function promptWechat() {
  const result: any = await ElMessageBox.prompt('同意或发起交换微信前，请先填写你的微信号。', '完善微信号', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputPattern: /\S{2,100}/,
    inputErrorMessage: '请输入有效微信号'
  })
  return result.value.trim()
}

function connectSocket() {
  const token = readAccessToken()
  if (!token) return
  const apiBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const wsBase = apiBase.replace(/^http/, 'ws')
  stompClient = new Client({
    brokerURL: `${wsBase}/ws?token=${encodeURIComponent(token)}`,
    reconnectDelay: 4000,
    onConnect: () => {
      stompClient?.subscribe('/user/queue/chats', (frame) => {
        const payload = JSON.parse(frame.body) as ChatMessageItem
        handleSocketMessage(payload)
      })
    }
  })
  stompClient.activate()
}

function handleSocketMessage(message: ChatMessageItem) {
  if (message.conversationId === activeConversationId.value) {
    upsertMessage(message)
    conversations.value = conversations.value.map((item) => item.conversationId === message.conversationId ? { ...item, unreadCount: 0 } : item)
    emit('unreadChange', conversations.value.reduce((sum, item) => sum + item.unreadCount, 0))
    markChatRead(message.conversationId).catch(() => null)
  } else {
    conversations.value = conversations.value.map((item) => item.conversationId === message.conversationId
      ? applyMessageToConversation(item, message, item.unreadCount + (message.mine ? 0 : 1))
      : item)
    emit('unreadChange', conversations.value.reduce((sum, item) => sum + item.unreadCount, 0))
  }
}

function upsertMessage(message: ChatMessageItem) {
  const index = messages.value.findIndex((item) => item.id === message.id)
  const existed = index >= 0
  if (index >= 0) {
    messages.value[index] = message
  } else {
    messages.value.push(message)
  }
  conversations.value = conversations.value.map((item) => item.conversationId === message.conversationId
    ? applyMessageToConversation(item, message, item.unreadCount, !existed)
    : item)
  scrollToBottom()
}

async function toggleStar(item: ChatConversationItem) {
  try {
    const updated = item.viewerStarred
      ? await unstarChatConversation(item.conversationId)
      : await starChatConversation(item.conversationId)
    conversations.value = conversations.value.map((conversation) => conversation.conversationId === item.conversationId ? updated : conversation)
  } catch (error: any) {
    ElMessage.error(error?.message || '收藏操作失败')
  }
}

function applyMessageToConversation(item: ChatConversationItem, message: ChatMessageItem, unreadCount: number, incrementCounts = true): ChatConversationItem {
  const studentMessageCount = item.studentMessageCount + (message.senderRole === 'STUDENT' ? 1 : 0)
  const agentMessageCount = item.agentMessageCount + (message.senderRole === 'AGENT_MEMBER' ? 1 : 0)
  const action = parseActionPayload(message)
  const granted = action?.status === 'APPROVED' ? action.actionType : ''
  return {
    ...item,
    lastMessage: displayMessageSummary(message),
    lastSenderRole: message.senderRole,
    studentMessageCount: incrementCounts ? studentMessageCount : item.studentMessageCount,
    agentMessageCount: incrementCounts ? agentMessageCount : item.agentMessageCount,
    resumeAccessGranted: granted === 'RESUME_ACCESS' ? true : item.resumeAccessGranted,
    phoneExchangeGranted: granted === 'PHONE_EXCHANGE' ? true : item.phoneExchangeGranted,
    wechatExchangeGranted: granted === 'WECHAT_EXCHANGE' ? true : item.wechatExchangeGranted,
    unreadCount,
    updatedAt: message.createdAt
  }
}

function patchConversationGrant(actionType: ChatActionType) {
  conversations.value = conversations.value.map((item) => item.conversationId === activeConversationId.value
    ? {
        ...item,
        resumeAccessGranted: actionType === 'RESUME_ACCESS' ? true : item.resumeAccessGranted,
        phoneExchangeGranted: actionType === 'PHONE_EXCHANGE' ? true : item.phoneExchangeGranted,
        wechatExchangeGranted: actionType === 'WECHAT_EXCHANGE' ? true : item.wechatExchangeGranted
      }
    : item)
}

function matchesFilter(item: ChatConversationItem, filter: ConversationFilter) {
  if (filter === 'all') return true
  if (filter === 'unread') return item.unreadCount > 0
  if (filter === 'starred') return item.viewerStarred
  if (filter === 'new') return mode.value === 'agent' && item.studentMessageCount > 0 && item.agentMessageCount === 0
  return relationshipStage(item) === filter
}

function relationshipStage(item: ChatConversationItem): 'active' | 'servicing' | 'ended' {
  if (item.relatedOrderStatus === 'PAID' || item.relatedOrderStatus === 'IN_SERVICE') return 'servicing'
  if (['COMPLETED', 'CLOSED', 'REFUND_REQUESTED'].includes(item.relatedOrderStatus || '')) return 'ended'
  return 'active'
}

function relationshipLabel(item: ChatConversationItem) {
  if (mode.value === 'agent' && item.studentMessageCount > 0 && item.agentMessageCount === 0) return '新咨询'
  const stage = relationshipStage(item)
  if (stage === 'servicing') return mode.value === 'agent' ? '服务中' : '合作中'
  if (stage === 'ended') return '已结束'
  return mode.value === 'agent' ? '沟通中' : '咨询中'
}

async function scrollToBottom() {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

function avatarText(name: string) {
  return (name || '聊').slice(0, 1)
}

function formatDate(value: string) {
  if (!value) return ''
  return new Date(value).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

function formatTime(value: string) {
  if (!value) return ''
  return new Date(value).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', hour12: false })
}

function formatDividerTime(value: string) {
  if (!value) return ''
  const date = new Date(value)
  const now = new Date()
  const sameDay = date.toDateString() === now.toDateString()
  if (sameDay) return formatTime(value)
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false })
}

function shouldShowTimeDivider(message: ChatMessageItem, index: number) {
  if (index === 0) return true
  const prev = messages.value[index - 1]
  if (!prev?.createdAt || !message.createdAt) return false
  return new Date(message.createdAt).getTime() - new Date(prev.createdAt).getTime() > 10 * 60 * 1000
}

function isLastMineMessage(message: ChatMessageItem, index: number) {
  return message.mine && index === messages.value.length - 1
}

function fileNameFromUrl(value: string) {
  try {
    const url = new URL(value, window.location.origin)
    const name = decodeURIComponent(url.pathname.split('/').pop() || '附件')
    return name || '附件'
  } catch {
    return value.split('/').pop() || '附件'
  }
}

function parseActionPayload(message: ChatMessageItem): ChatActionPayload | null {
  if (message.contentType !== 'ACTION_CARD') return null
  try {
    return JSON.parse(message.content) as ChatActionPayload
  } catch {
    return null
  }
}

function actionTitle(message: ChatMessageItem) {
  const payload = parseActionPayload(message)
  if (payload?.actionType === 'RESUME_ACCESS') return payload.requesterRole === 'STUDENT' ? '学生已开放学术简历' : '请求查看学生简历'
  if (payload?.actionType === 'PHONE_EXCHANGE') return '交换电话'
  if (payload?.actionType === 'WECHAT_EXCHANGE') return '交换微信'
  return '互动请求'
}

function actionDescription(message: ChatMessageItem) {
  const payload = parseActionPayload(message)
  const requester = message.mine ? '你' : '对方'
  if (payload?.actionType === 'RESUME_ACCESS') {
    return payload.requesterRole === 'STUDENT'
      ? '学生主动开放一页式学术简历，不包含联系方式、预算和认证材料。'
      : `${requester}希望查看学生学术简历，内容不包含联系方式、预算和认证材料。`
  }
  if (payload?.actionType === 'PHONE_EXCHANGE') return `${requester}希望互换电话，同意后双方都可以查看。`
  if (payload?.actionType === 'WECHAT_EXCHANGE') return `${requester}希望互换微信，同意后双方都可以查看。`
  return '请处理该互动请求。'
}

function actionStatus(message: ChatMessageItem) {
  return parseActionPayload(message)?.status || 'PENDING'
}

function actionStatusLabel(message: ChatMessageItem) {
  const status = actionStatus(message)
  if (status === 'APPROVED') return '已同意'
  if (status === 'REJECTED') return '已拒绝'
  return '待处理'
}

function canRespondAction(message: ChatMessageItem) {
  const payload = parseActionPayload(message)
  return Boolean(payload && payload.status === 'PENDING' && !message.mine)
}

function displayMessageSummary(message: ChatMessageItem) {
  if (message.contentType === 'IMAGE') return '[图片]'
  if (message.contentType === 'FILE') return `[文件] ${fileNameFromUrl(message.content)}`
  const payload = parseActionPayload(message)
  if (payload?.actionType === 'RESUME_ACCESS') return '[简历请求]'
  if (payload?.actionType === 'PHONE_EXCHANGE') return '[电话互换]'
  if (payload?.actionType === 'WECHAT_EXCHANGE') return '[微信互换]'
  return message.content
}

function educationLabel(value?: string | null) {
  const labels: Record<string, string> = {
    HIGH_SCHOOL: '高中',
    COLLEGE: '专科',
    UNDERGRAD: '本科',
    MASTER: '硕士',
    PHD: '博士',
    OTHER: '其他'
  }
  return value ? labels[value] || value : '学历待完善'
}

function formatResumeGpa(value: StudentAcademicResume) {
  if (value.gpaValue == null) return '待完善'
  return `${value.gpaValue}${value.gpaScale === 'PERCENTAGE' ? '/100' : '/4.0'}`
}

function languageSummary(value: StudentAcademicResume) {
  if (!value.languageScores?.length) return '待完善'
  return value.languageScores.map((item) => `${item.languageType} ${item.score}`).join(' / ')
}

function publicationSummary(items: Array<{ title: string; authorRole?: string | null; journalName?: string | null; publishedYear?: number | null }>) {
  return items
    .map((item) => [item.title, item.authorRole, item.journalName, item.publishedYear].filter(Boolean).join(' · '))
    .filter(Boolean)
    .join('；')
}

function splitKeywords(value: string) {
  return value.split(/[,，、/|\s]+/g).map((item) => item.trim()).filter(Boolean).slice(0, 8)
}
</script>

<style scoped>
.chat-panel {
  height: 100%;
  min-height: 0;
  display: grid;
  grid-template-columns: 340px minmax(0, 1fr);
  gap: 12px;
  border-radius: 8px;
  background: #e9f4f7;
  overflow: hidden;
}

.conversation-pane,
.chat-window {
  background: #fff;
  min-height: 0;
}

.conversation-pane {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  overflow: hidden;
}

.conversation-controls {
  padding: 18px 20px 12px;
  border-bottom: 1px solid #e4ebf2;
}

.conversation-controls :deep(.el-input__wrapper) {
  min-height: 42px;
  border-radius: 8px;
  font-size: 14px;
  box-shadow: 0 0 0 1px #d4dbe5 inset;
}

.conversation-controls :deep(.el-input__inner) {
  font-size: 14px;
  font-weight: 500;
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #97a6b4;
}

.chat-filters {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chat-filters button {
  flex: 0 0 auto;
  min-width: 58px;
  height: 32px;
  padding: 0 12px;
  border: 1px solid #d6dde6;
  border-radius: 999px;
  background: #fff;
  color: #2b3540;
  font-size: 13px;
  font-weight: 650;
  cursor: pointer;
  transition: transform .18s ease, box-shadow .18s ease, background .18s ease, color .18s ease;
}

.chat-filters button.active {
  border-color: #409eff;
  background: #409eff;
  color: #fff;
  box-shadow: 0 6px 12px rgba(64, 158, 255, 0.18);
}

.chat-filters button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 12px rgba(43, 53, 64, 0.1);
}

.conversation-list {
  min-height: 0;
  overflow-y: auto;
  padding: 12px 10px 18px;
}

.conversation-item {
  width: 100%;
  min-height: 78px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  padding: 12px;
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr) auto;
  gap: 10px;
  text-align: left;
  cursor: pointer;
  transition: transform .18s ease, box-shadow .18s ease, background .18s ease;
}

.conversation-item.active,
.conversation-item:hover {
  background: #eef2f6;
  box-shadow: 0 10px 18px rgba(18, 39, 62, 0.08);
}

.conversation-item:hover {
  transform: translateY(-2px);
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 700;
  font-size: 17px;
  background: #4db7c0;
  align-self: center;
}

.conversation-meta {
  min-width: 0;
}

.conversation-title {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.conversation-title strong {
  color: #1f2933;
  font-size: 16px;
  line-height: 1.25;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.conversation-title span {
  color: #8b98a6;
  font-size: 14px;
  font-weight: 500;
  flex: 0 0 auto;
}

.conversation-tags {
  margin-top: 7px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.stage-tag {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  color: #316078;
  background: #edf7fb;
  border: 1px solid #cbe7f2;
}

.stage-servicing {
  color: #0d6b52;
  background: #e8f8f1;
  border-color: #b9e7d3;
}

.stage-ended {
  color: #68717c;
  background: #f2f4f7;
  border-color: #dde3ea;
}

.star-btn {
  width: 24px;
  height: 24px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: #a3afba;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: color .18s ease, background .18s ease, transform .18s ease;
}

.star-btn svg {
  width: 16px;
  height: 16px;
}

.star-btn:hover,
.star-btn.active {
  color: #f0a51a;
  background: #fff6df;
  transform: translateY(-1px);
}

.conversation-sub,
.conversation-last {
  color: #8b98a6;
  margin: 0;
}

.conversation-sub,
.conversation-last {
  margin-top: 4px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.conversation-sub {
  font-size: 14px;
  font-weight: 550;
}

.conversation-last {
  font-size: 13px;
  line-height: 1.35;
}

.unread-dot {
  min-width: 24px;
  height: 24px;
  padding: 0 6px;
  border-radius: 999px;
  color: #fff;
  background: #f56c6c;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 700;
}

.chat-window {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr) auto;
  overflow: hidden;
}

.chat-header {
  min-height: 74px;
  padding: 14px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, #f4fbff, #eef7fb);
  border-bottom: 1px solid #edf1f5;
}

.chat-header h3,
.chat-header p {
  margin: 0;
}

.chat-header h3 {
  font-size: 20px;
  color: #10213a;
}

.chat-header p {
  margin-top: 5px;
  color: #738190;
  font-size: 14px;
  font-weight: 500;
}

.chat-tag {
  font-size: 13px;
  height: 28px;
  padding: 0 10px;
}

.message-list {
  min-height: 0;
  padding: 18px 28px;
  overflow-y: auto;
  background: #fff;
  scroll-behavior: smooth;
}

.message-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 12px;
}

.message-row.mine {
  justify-content: flex-end;
}

.message-bubble {
  max-width: min(560px, 72%);
  padding: 10px 14px;
  border-radius: 8px;
  background: #f4f5f7;
  color: #26323c;
  box-shadow: 0 6px 14px rgba(25, 43, 59, 0.05);
}

.message-row.mine .message-bubble {
  background: #d9f3f6;
}

.message-bubble p {
  margin: 0;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.message-bubble.type-image,
.message-bubble.type-file {
  padding: 8px;
}

.image-message {
  display: block;
}

.image-message img {
  display: block;
  max-width: 240px;
  max-height: 180px;
  border-radius: 6px;
  object-fit: cover;
}

.file-message {
  min-width: 190px;
  max-width: 320px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #26323c;
  text-decoration: none;
  font-size: 14px;
}

.file-message span {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.file-icon {
  width: 22px;
  height: 22px;
  color: #409eff;
  flex: 0 0 auto;
}

.action-card {
  width: min(360px, 100%);
}

.action-card-head {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.action-card-head svg {
  width: 28px;
  height: 28px;
  color: #30aeb8;
  flex: 0 0 auto;
}

.action-card-head strong {
  display: block;
  color: #172638;
  font-size: 15px;
  line-height: 1.35;
}

.action-status {
  display: inline-flex;
  margin-top: 4px;
  height: 22px;
  align-items: center;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.status-pending {
  color: #9a6400;
  background: #fff4dc;
}

.status-approved {
  color: #167047;
  background: #e6f7ef;
}

.status-rejected {
  color: #a43b3b;
  background: #fdecec;
}

.action-card p {
  margin: 10px 0 0;
  color: #526577;
  font-size: 14px;
  line-height: 1.55;
}

.action-card-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.hidden-input {
  display: none;
}

.message-bubble p {
  font-size: 15px;
  line-height: 1.55;
  font-weight: 500;
}

.time-divider {
  width: fit-content;
  margin: 8px auto 14px;
  color: #a5acb5;
  font-size: 13px;
  line-height: 1;
}

.read-state {
  color: #0aa7a7;
  font-size: 12px;
  line-height: 1;
  margin-bottom: 4px;
}

.message-composer {
  border-top: 1px solid #e5ebf0;
  padding: 12px 24px 14px;
  background: #fff;
}

.composer-tools,
.composer-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.composer-tools {
  margin-bottom: 10px;
}

.composer-tools :deep(.el-button) {
  width: 36px;
  height: 36px;
  border-color: #d7dde6;
  color: #6f7e8d;
  transition: transform .18s ease, box-shadow .18s ease, color .18s ease;
}

.composer-tools :deep(.action-tool.el-button) {
  width: auto;
  min-width: 86px;
  padding: 0 12px;
  border-radius: 8px;
}

.composer-tools :deep(.el-button:hover) {
  color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(31, 84, 132, 0.12);
}

.message-composer :deep(.el-textarea__inner) {
  min-height: 82px !important;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 0 0 1px #d9dfe8 inset;
}

.message-composer :deep(.el-textarea__inner::placeholder) {
  color: #aeb5bf;
}

.composer-actions {
  margin-top: 10px;
  justify-content: flex-end;
}

.composer-actions span {
  color: #a8b0b8;
  font-size: 14px;
  font-weight: 500;
}

.composer-actions :deep(.el-button) {
  min-width: 72px;
  height: 38px;
  border-radius: 7px;
  font-size: 14px;
  font-weight: 650;
  transition: transform .18s ease, box-shadow .18s ease, filter .18s ease;
}

.composer-actions :deep(.el-button:not(.is-disabled):hover) {
  transform: translateY(-1px);
  filter: brightness(1.04);
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.25);
}

.resume-preview-dialog :deep(.el-dialog) {
  border-radius: 14px;
}

.resume-preview-dialog :deep(.el-dialog__header) {
  padding: 24px 34px 10px;
  margin-right: 0;
}

.resume-preview-dialog :deep(.el-dialog__title) {
  color: #3d3d3d;
  font-size: 24px;
  font-weight: 800;
}

.resume-preview-dialog :deep(.el-dialog__body) {
  padding: 20px 34px 34px;
}

.resume-preview-shell {
  min-height: 520px;
  max-height: min(78vh, 860px);
  overflow-y: auto;
  padding: 4px 12px 12px;
  background: #fff;
}

.resume-paper {
  width: min(880px, 100%);
  margin: 0 auto;
  color: #111;
  font-family: Arial, "PingFang SC", "Microsoft YaHei", sans-serif;
  font-size: 16px;
  line-height: 1.45;
}

.resume-paper-head {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 170px;
  gap: 42px;
  align-items: start;
  margin-bottom: 18px;
}

.resume-title-block h2,
.resume-title-block p,
.resume-paper-section h3,
.resume-line-item p,
.resume-empty-line {
  margin: 0;
}

.resume-title-block h2 {
  margin-top: 42px;
  color: #000;
  font-size: 34px;
  line-height: 1.25;
  font-weight: 900;
}

.resume-identity {
  margin-top: 28px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  color: #000;
  font-size: 18px;
  font-weight: 650;
}

.resume-title-block p {
  margin-top: 4px;
  color: #111;
  font-size: 17px;
}

.resume-photo-placeholder {
  width: 170px;
  aspect-ratio: 3 / 4;
  margin-top: 8px;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 8px;
  background: linear-gradient(180deg, #e7f4fb, #c9e4f1);
  border: 1px solid #d6e3eb;
  color: #1d586c;
}

.resume-photo-placeholder strong {
  width: 66px;
  height: 66px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #fff;
  font-size: 28px;
  font-weight: 900;
}

.resume-photo-placeholder span {
  font-size: 14px;
  font-weight: 800;
}

.resume-paper-section {
  margin-top: 20px;
}

.resume-paper-section h3 {
  padding-bottom: 8px;
  border-bottom: 2px solid #222;
  color: #000;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 900;
}

.resume-line-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 22px;
  padding: 12px 0 0;
}

.resume-line-item strong {
  color: #000;
  font-size: 17px;
  font-weight: 900;
}

.resume-line-item p {
  margin-top: 4px;
  color: #111;
  font-size: 16px;
  overflow-wrap: anywhere;
}

.resume-line-item time {
  color: #000;
  font-size: 17px;
  font-weight: 900;
  white-space: nowrap;
}

.resume-empty-line {
  padding-top: 12px;
  color: #777;
}

.resume-keywords {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.resume-keywords span {
  padding: 3px 8px;
  border-radius: 4px;
  background: #f0f0f0;
  color: #333;
  font-size: 14px;
}

@media (max-width: 900px) {
  .chat-panel {
    grid-template-columns: 1fr;
    height: 100%;
  }

  .conversation-pane,
  .chat-window {
    min-height: auto;
  }

  .conversation-pane {
    max-height: 320px;
  }

  .resume-paper-head,
  .resume-line-item {
    grid-template-columns: 1fr;
  }

  .resume-title-block h2 {
    margin-top: 8px;
    font-size: 28px;
  }

  .resume-photo-placeholder {
    width: 128px;
    grid-row: 1;
  }

  .resume-line-item time {
    white-space: normal;
  }
}
</style>
