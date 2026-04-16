<template>
  <div class="home fade-up">
    <section class="hero page-card">
      <div class="hero-content">
        <p class="tag">AI 驱动的留学申请辅助平台</p>
        <h1>透明筛选中介，智能规划申请路径</h1>
        <p>
          覆盖中介筛选、院校筛选与论坛交流，帮助学生用更低成本做出更稳妥选择。
        </p>
        <el-button class="cta" type="primary" size="large" round @click="go('/agencies')">
          开始筛选中介
        </el-button>
      </div>
      <div class="hero-visual" aria-hidden="true">
        <div class="orb orb-a"></div>
        <div class="orb orb-b"></div>
        <div class="orb orb-c"></div>
      </div>
    </section>

    <section class="features">
      <article
        v-for="item in featureItems"
        :key="item.path"
        class="feature-card"
        @click="go(item.path)"
      >
        <h3>{{ item.title }}</h3>
        <p>{{ item.desc }}</p>
        <span class="link">进入模块</span>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

interface FeatureItem {
  title: string
  desc: string
  path: string
}

const router = useRouter()
const authStore = useAuthStore()

const featureItems: FeatureItem[] = [
  { title: '首页', desc: '查看平台核心能力入口与使用引导', path: '/' },
  { title: '中介筛选', desc: '按国家、专业、价格与信誉分多维筛选优质中介', path: '/agencies' },
  { title: '院校', desc: '按国家、学科与专业方向筛选院校与项目', path: '/universities' },
  { title: '论坛', desc: '浏览经验帖、问答讨论与政策交流话题', path: '/forum' },
  { title: '个人中心', desc: '管理申请清单、进度与个人资料', path: '/me' },
  { title: '账号入口', desc: '统一登录/注册入口，支持身份与方式切换', path: '/auth' }
]

function go(path: string) {
  router.push(path)
}

onMounted(() => {
  if (authStore.isLoggedIn && !authStore.profileCompleted) {
    ElMessage.warning({
      message: '请尽快完成信息补充与身份认证，以解锁完整功能',
      duration: 3000
    })
  }
})
</script>

<style scoped>
.home {
  display: grid;
  gap: 20px;
}

.hero {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  min-height: 340px;
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.tag {
  margin: 0;
  color: #2e5fb9;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-size: 12px;
}

h1 {
  margin: 0;
  font-size: 40px;
  line-height: 1.2;
}

p {
  margin: 0;
  color: #56637d;
  line-height: 1.7;
}

.cta {
  width: fit-content;
  margin-top: 10px;
}

.hero-visual {
  position: relative;
  min-height: 220px;
}

.orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(2px);
  animation: float 5.2s ease-in-out infinite;
}

.orb-a {
  width: 240px;
  height: 240px;
  right: 50px;
  top: 28px;
  background: radial-gradient(circle at 30% 30%, rgba(31, 107, 255, 0.6), rgba(31, 107, 255, 0.15));
}

.orb-b {
  width: 170px;
  height: 170px;
  right: -8px;
  top: 110px;
  background: radial-gradient(circle at 30% 30%, rgba(24, 179, 168, 0.55), rgba(24, 179, 168, 0.1));
  animation-delay: 0.5s;
}

.orb-c {
  width: 90px;
  height: 90px;
  right: 210px;
  top: 210px;
  background: radial-gradient(circle at 30% 30%, rgba(38, 58, 97, 0.35), rgba(38, 58, 97, 0.08));
  animation-delay: 0.9s;
}

.features {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-card {
  border-radius: 18px;
  border: 1px solid #dbe4f2;
  background: rgba(255, 255, 255, 0.86);
  min-height: 170px;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  border-color: rgba(31, 107, 255, 0.32);
  box-shadow: 0 14px 28px rgba(16, 34, 63, 0.12);
}

h3 {
  margin: 0 0 8px;
  font-size: 20px;
}

.link {
  display: inline-block;
  margin-top: 14px;
  color: #1f6bff;
  font-weight: 600;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@media (max-width: 980px) {
  .hero {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  h1 {
    font-size: 32px;
  }

  .hero-visual {
    min-height: 180px;
  }

  .features {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .features {
    grid-template-columns: 1fr;
  }
}
</style>
