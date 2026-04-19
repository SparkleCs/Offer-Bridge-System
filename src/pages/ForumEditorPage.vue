<template>
  <section class="page-card fade-up editor-page">
    <div class="editor-head">
      <div>
        <p class="crumb">论坛 / {{ isEditMode ? '编辑帖子' : '发布帖子' }}</p>
        <h2 class="section-title">{{ isEditMode ? '编辑帖子' : '发布帖子' }}</h2>
        <p class="section-desc">支持标题、标签和富文本正文编辑，内容可随时保存。</p>
      </div>
      <div class="head-actions">
        <el-button @click="goBack">返回论坛</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">{{ isEditMode ? '保存修改' : '发布' }}</el-button>
      </div>
    </div>

    <el-form label-position="top" class="editor-form" v-loading="loading">
      <el-form-item label="分栏目">
        <el-radio-group v-model="form.channel">
          <el-radio-button label="EXPERIENCE">留学经验贴</el-radio-button>
          <el-radio-button label="OFFER_WALL">offer墙</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="标题">
        <el-input v-model="form.title" maxlength="120" show-word-limit placeholder="请输入标题" />
      </el-form-item>

      <el-form-item label="标签（最多 8 个，回车添加）">
        <el-select v-model="form.tags" multiple filterable allow-create default-first-option placeholder="如：背景提升、申请季、CS" />
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
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { ApiError } from '../services/http'
import { createForumPost, getForumPostDetail, updateForumPost } from '../services/forum'
import { useAuthStore } from '../stores/auth'
import type { ForumChannel } from '../types/forum'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const editorRef = ref<HTMLDivElement | null>(null)

const form = reactive({
  channel: 'EXPERIENCE' as ForumChannel,
  title: '',
  tags: [] as string[]
})

const postId = computed(() => String(route.params.postId || ''))
const isEditMode = computed(() => Boolean(postId.value))

onMounted(async () => {
  await nextTick()
  if (editorRef.value) editorRef.value.innerHTML = ''

  if (!isEditMode.value) return

  loading.value = true
  try {
    const detail = await getForumPostDetail(postId.value)
    if (detail.authorUserId !== authStore.authMeta?.userId) {
      ElMessage.error('仅帖子作者可编辑')
      router.replace('/forum')
      return
    }
    form.channel = detail.channel
    form.title = detail.title
    form.tags = [...detail.tags]
    if (editorRef.value) editorRef.value.innerHTML = detail.contentHtml || ''
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '帖子加载失败')
    router.replace('/forum')
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.push('/forum')
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

async function submit() {
  const contentHtml = editorRef.value?.innerHTML?.trim() || ''
  if (!form.title.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }
  if (!contentHtml || !contentHtml.replace(/<[^>]+>/g, '').trim()) {
    ElMessage.warning('正文不能为空')
    return
  }

  submitting.value = true
  try {
    if (isEditMode.value) {
      await updateForumPost(postId.value, {
        channel: form.channel,
        title: form.title,
        contentHtml,
        tags: form.tags
      })
      ElMessage.success('帖子已更新')
    } else {
      await createForumPost({
        channel: form.channel,
        title: form.title,
        contentHtml,
        tags: form.tags
      })
      ElMessage.success('发布成功')
    }
    router.push('/forum')
  } catch (error) {
    ElMessage.error(error instanceof ApiError ? error.message : '保存失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.editor-page {
  max-width: 980px;
  margin: 0 auto;
}

.editor-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.crumb {
  margin: 0 0 4px;
  color: #7489a5;
  font-size: 12px;
}

.head-actions {
  display: flex;
  gap: 10px;
}

.editor-form {
  margin-top: 16px;
}

.editor-wrap {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #dbe6f1;
  border-radius: 12px;
  overflow: hidden;
}

.editor-tools {
  padding: 12px;
  border-bottom: 1px solid #e5edf6;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  background: #f8fbff;
}

.editor-content {
  min-height: 420px;
  max-height: calc(100vh - 360px);
  overflow: auto;
  padding: 14px;
  outline: none;
  line-height: 1.75;
}

@media (max-width: 768px) {
  .editor-head {
    flex-direction: column;
    align-items: stretch;
  }

  .head-actions {
    justify-content: space-between;
  }

  .editor-content {
    min-height: 300px;
    max-height: none;
  }
}
</style>
