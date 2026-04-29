<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">OfferBridge 留学申请辅助平台</p>
        <h1>把留学中介、院校与经验，放进同一个清晰路径。</h1>
        <p class="hero-desc">
          从透明筛选到申请清单，从机构沟通到真实经验，OfferBridge 帮你把复杂决策变成可比较、可追踪、可协同的流程。
        </p>
        <div class="hero-actions">
          <el-button class="primary-action" type="primary" size="large" round @click="go('/agencies')">
            开始筛选中介
          </el-button>
          <el-button class="secondary-action" size="large" round @click="go('/universities')">
            浏览院校项目
          </el-button>
        </div>
      </div>

      <div class="hero-product" aria-label="OfferBridge 产品界面预览">
        <img :src="agencyImage" alt="中介筛选产品界面预览" />
      </div>
    </section>

    <section class="carousel-section reveal-item">
      <div class="section-heading">
        <p class="eyebrow">Core Experience</p>
        <h2>一个入口，连接申请前的关键判断。</h2>
      </div>

      <div
        class="showcase-carousel"
        @mouseenter="pauseCarousel"
        @mouseleave="resumeCarousel"
      >
        <div class="carousel-media">
          <transition name="slide-fade" mode="out-in">
            <img
              :key="currentSlide.title"
              :src="currentSlide.image"
              :alt="currentSlide.alt"
            />
          </transition>
        </div>

        <div class="carousel-copy">
          <p>{{ currentSlide.kicker }}</p>
          <h3>{{ currentSlide.title }}</h3>
          <span>{{ currentSlide.desc }}</span>
          <el-button class="text-action" text @click="go(currentSlide.path)">
            <span>{{ currentSlide.action }}</span>
            <span class="action-arrow">→</span>
          </el-button>
        </div>

        <div class="carousel-dots" aria-label="轮播切换">
          <button
            v-for="(slide, index) in carouselSlides"
            :key="slide.title"
            :class="{ active: activeSlideIndex === index }"
            type="button"
            :aria-label="`查看${slide.title}`"
            @click="setSlide(index)"
          ></button>
        </div>
      </div>
    </section>

    <section
      v-for="(section, index) in showcaseSections"
      :key="section.title"
      class="feature-section reveal-item"
      :class="{ 'feature-section--reverse': index % 2 === 1 }"
    >
      <div class="feature-copy">
        <p class="eyebrow">{{ section.kicker }}</p>
        <h2>{{ section.title }}</h2>
        <p>{{ section.desc }}</p>
        <div class="feature-points">
          <span v-for="point in section.points" :key="point">{{ point }}</span>
        </div>
        <el-button class="text-action" text @click="go(section.path)">
          <span>{{ section.action }}</span>
          <span class="action-arrow">→</span>
        </el-button>
      </div>

      <div class="feature-visual" :class="section.visualClass" aria-hidden="true">
        <div class="visual-window">
          <div class="visual-topline"></div>
          <div class="visual-body">
            <span v-for="item in section.visualItems" :key="item"></span>
          </div>
        </div>
      </div>
    </section>

    <section class="final-cta reveal-item">
      <p class="eyebrow">开始规划你的申请路径</p>
      <h2>从今天开始，把每一步申请放进看得见的系统。</h2>
      <div class="hero-actions">
        <el-button class="primary-action" type="primary" size="large" round @click="go('/auth')">
          登录/注册
        </el-button>
        <el-button class="secondary-action" size="large" round @click="go('/forum')">
          查看论坛经验
        </el-button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import agencyImage from '../assets/home/agency-screening.png'
import forumImage from '../assets/home/forum-insights.png'
import universityImage from '../assets/home/university-planning.png'
import { useAuthStore } from '../stores/auth'

interface HeroSlide {
  kicker: string
  title: string
  desc: string
  action: string
  path: string
  image: string
  alt: string
}

interface ShowcaseSection {
  kicker: string
  title: string
  desc: string
  points: string[]
  action: string
  path: string
  visualClass: string
  visualItems: string[]
}

const router = useRouter()
const authStore = useAuthStore()
const activeSlideIndex = ref(0)
let carouselTimer: number | undefined
let revealObserver: IntersectionObserver | undefined

const carouselSlides: HeroSlide[] = [
  {
    kicker: 'Agency Screening',
    title: '用同一把尺，比较不同中介。',
    desc: '把价格区间、擅长方向、信誉评价和服务记录放在同一界面里，减少反复沟通前的不确定。',
    action: '进入中介筛选',
    path: '/agencies',
    image: agencyImage,
    alt: '中介筛选产品界面'
  },
  {
    kicker: 'University Planning',
    title: '院校和项目，不再散落在表格里。',
    desc: '按国家、学科、专业方向和申请组合查看项目，把目标、备选与收藏清单整理成一条路径。',
    action: '浏览院校项目',
    path: '/universities',
    image: universityImage,
    alt: '院校规划产品界面'
  },
  {
    kicker: 'Community Insights',
    title: '把经验帖变成可参考的判断。',
    desc: '从问答、政策讨论到申请复盘，让真实经历成为筛选中介和规划院校时的辅助信息。',
    action: '查看论坛经验',
    path: '/forum',
    image: forumImage,
    alt: '论坛经验产品界面'
  }
]

const showcaseSections: ShowcaseSection[] = [
  {
    kicker: '01 / Transparent Choice',
    title: '透明中介筛选，让第一步不靠猜。',
    desc: '用国家、专业、预算、服务阶段和信誉评价建立筛选入口，先看清差异，再决定是否沟通。',
    points: ['价格区间', '擅长方向', '信誉评价'],
    action: '筛选中介',
    path: '/agencies',
    visualClass: 'visual-agency',
    visualItems: ['92', '4.8', '12', '38']
  },
  {
    kicker: '02 / Program Map',
    title: '院校项目地图，整理你的申请组合。',
    desc: '把学校、项目、方向和申请清单放到同一个视图里，帮助你更稳地比较目标、冲刺与保底。',
    points: ['项目筛选', '申请清单', '方向对比'],
    action: '查看院校',
    path: '/universities',
    visualClass: 'visual-school',
    visualItems: ['UK', 'US', 'AU', 'SG']
  },
  {
    kicker: '03 / Application Flow',
    title: '申请进度协同，关键节点不再断层。',
    desc: '学生、中介与平台围绕订单、阶段成果和消息通知协同推进，让交付状态更清楚。',
    points: ['订单管理', '阶段成果', '消息提醒'],
    action: '进入个人中心',
    path: '/me',
    visualClass: 'visual-flow',
    visualItems: ['01', '02', '03', '04']
  },
  {
    kicker: '04 / Real Stories',
    title: '真实经验社区，补齐系统外的信息。',
    desc: '浏览申请复盘、政策交流和问答讨论，把别人的经验转化为自己的判断边界。',
    points: ['经验帖', '问答讨论', '政策交流'],
    action: '浏览论坛',
    path: '/forum',
    visualClass: 'visual-forum',
    visualItems: ['Q', 'A', 'Hot', 'New']
  },
  {
    kicker: '05 / AI Recommendation',
    title: 'AI 推荐申请路径，把下一步变得更明确。',
    desc: '基于学生背景、预算、国家偏好、院校目标与中介服务记录，生成中介筛选、院校组合和下一步行动建议。',
    points: ['背景画像', '路径建议', '下一步清单'],
    action: '开始智能筛选',
    path: '/agencies',
    visualClass: 'visual-ai',
    visualItems: ['AI', '92', 'Plan', 'Next']
  }
]

const currentSlide = computed(() => carouselSlides[activeSlideIndex.value])

function go(path: string) {
  router.push(path)
}

function setSlide(index: number) {
  activeSlideIndex.value = index
  restartCarousel()
}

function nextSlide() {
  activeSlideIndex.value = (activeSlideIndex.value + 1) % carouselSlides.length
}

function startCarousel() {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return
  carouselTimer = window.setInterval(nextSlide, 5200)
}

function stopCarousel() {
  if (carouselTimer) {
    window.clearInterval(carouselTimer)
    carouselTimer = undefined
  }
}

function restartCarousel() {
  stopCarousel()
  startCarousel()
}

function pauseCarousel() {
  stopCarousel()
}

function resumeCarousel() {
  startCarousel()
}

function setupReveal() {
  const items = document.querySelectorAll<HTMLElement>('.reveal-item')
  if (!('IntersectionObserver' in window)) {
    items.forEach((item) => item.classList.add('is-visible'))
    return
  }

  revealObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible')
          revealObserver?.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.18 }
  )

  items.forEach((item) => revealObserver?.observe(item))
}

onMounted(() => {
  if (authStore.isLoggedIn && !authStore.profileCompleted) {
    ElMessage.warning({
      message: '请尽快完成信息补充与身份认证，以解锁完整功能',
      duration: 3000
    })
  }

  startCarousel()
  setupReveal()
})

onBeforeUnmount(() => {
  stopCarousel()
  revealObserver?.disconnect()
})
</script>

<style scoped>
.home-page {
  overflow: hidden;
  color: #111827;
  background:
    linear-gradient(180deg, #fbfcff 0%, #f4f7fb 48%, #ffffff 100%);
}

.hero-section,
.carousel-section,
.feature-section,
.final-cta {
  width: min(100%, 1440px);
  margin: 0 auto;
  padding: 76px clamp(20px, 5vw, 84px);
}

.hero-section {
  min-height: calc(100vh - 72px);
  display: grid;
  grid-template-columns: minmax(0, 0.88fr) minmax(420px, 1.12fr);
  align-items: center;
  gap: clamp(32px, 5vw, 76px);
  padding-top: 64px;
  padding-bottom: 42px;
}

.hero-copy {
  animation: heroIn 0.8s ease both;
}

.eyebrow {
  margin: 0 0 16px;
  color: #2e5fb9;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.hero-copy h1,
.section-heading h2,
.feature-copy h2,
.final-cta h2 {
  margin: 0;
  color: #0b1220;
  font-weight: 800;
  line-height: 1.08;
}

.hero-copy h1 {
  max-width: 760px;
  font-size: clamp(44px, 6vw, 86px);
}

.hero-desc {
  max-width: 680px;
  margin: 24px 0 0;
  color: #536176;
  font-size: clamp(17px, 1.7vw, 22px);
  line-height: 1.75;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 34px;
}

.hero-actions :deep(.el-button) {
  min-width: 148px;
  height: 46px;
  font-weight: 700;
}

.primary-action {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-text-color: #ffffff;
  --el-button-hover-bg-color: #3b82ff;
  --el-button-hover-border-color: #3b82ff;
  --el-button-hover-text-color: #ffffff;
  --el-button-active-bg-color: #155de8;
  --el-button-active-border-color: #155de8;
  border-color: var(--primary);
  background: var(--primary);
  color: #ffffff;
  box-shadow: 0 12px 26px rgba(31, 107, 255, 0.18);
}

.primary-action:hover,
.primary-action:focus {
  border-color: #3b82ff;
  background: #3b82ff;
  color: #ffffff;
  transform: translateY(-1px);
}

.secondary-action {
  --el-button-text-color: var(--primary);
  --el-button-hover-text-color: #155de8;
  --el-button-hover-bg-color: rgba(255, 255, 255, 0.94);
  --el-button-hover-border-color: rgba(31, 107, 255, 0.32);
  border-color: rgba(31, 107, 255, 0.2);
  background: rgba(255, 255, 255, 0.74);
  color: var(--primary);
}

.secondary-action:hover,
.secondary-action:focus {
  border-color: rgba(31, 107, 255, 0.32);
  background: rgba(255, 255, 255, 0.94);
  color: #155de8;
  transform: translateY(-1px);
}

.hero-product {
  position: relative;
  padding: 14px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 30px 80px rgba(15, 23, 42, 0.13);
  animation: productFloat 5.8s ease-in-out infinite;
}

.hero-product::before {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(31, 107, 255, 0.18), rgba(24, 179, 168, 0.1), transparent 54%);
  z-index: -1;
}

.hero-product img,
.carousel-media img {
  display: block;
  width: 100%;
  border-radius: 8px;
}

.section-heading {
  max-width: 760px;
  margin-bottom: 36px;
}

.section-heading h2,
.feature-copy h2,
.final-cta h2 {
  font-size: clamp(34px, 4.2vw, 62px);
}

.showcase-carousel {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
  align-items: center;
  gap: 40px;
  min-height: 520px;
  padding: clamp(18px, 3vw, 36px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 22px 70px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(18px);
}

.carousel-media {
  overflow: hidden;
  border-radius: 8px;
  background: #eef4fb;
}

.carousel-copy p,
.feature-copy p,
.final-cta p {
  margin: 0;
}

.feature-copy .eyebrow,
.final-cta .eyebrow {
  margin: 0 0 16px;
}

.carousel-copy p {
  color: #2e5fb9;
  font-size: 13px;
  font-weight: 800;
  text-transform: uppercase;
}

.carousel-copy h3 {
  margin: 14px 0 16px;
  color: #0b1220;
  font-size: clamp(30px, 3vw, 48px);
  line-height: 1.12;
}

.carousel-copy span,
.feature-copy > p {
  display: block;
  color: #5c6a7f;
  font-size: 18px;
  line-height: 1.75;
}

.text-action {
  --el-button-text-color: var(--primary);
  --el-button-hover-text-color: #155de8;
  --el-button-active-text-color: #155de8;
  --el-button-hover-bg-color: transparent;
  position: relative;
  margin-top: 22px;
  padding: 0;
  color: var(--primary);
  font-weight: 800;
  transition: color 0.22s ease, transform 0.22s ease;
}

.text-action.el-button.is-text:not(.is-disabled):hover,
.text-action.el-button.is-text:not(.is-disabled):focus {
  background: transparent;
  color: #155de8;
}

.text-action :deep(span) {
  display: inline-flex;
  align-items: center;
}

.text-action::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -3px;
  height: 1px;
  background: rgba(31, 107, 255, 0.32);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.22s ease;
}

.text-action:hover,
.text-action:focus {
  color: #155de8;
  transform: translateY(-1px);
}

.text-action:hover::after,
.text-action:focus::after {
  transform: scaleX(1);
}

.action-arrow {
  margin-left: 7px;
  color: rgba(31, 107, 255, 0.72);
  font-weight: 700;
  transition: transform 0.22s ease, color 0.22s ease;
}

.text-action:hover .action-arrow,
.text-action:focus .action-arrow {
  color: #155de8;
  transform: translateX(3px);
}

.carousel-dots {
  position: absolute;
  right: 34px;
  bottom: 28px;
  display: flex;
  gap: 9px;
}

.carousel-dots button {
  width: 34px;
  height: 4px;
  border: 0;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.16);
  cursor: pointer;
  transition: width 0.25s ease, background 0.25s ease;
}

.carousel-dots button.active {
  width: 58px;
  background: var(--primary);
}

.feature-section {
  display: grid;
  grid-template-columns: minmax(320px, 0.9fr) minmax(0, 1.1fr);
  align-items: center;
  gap: clamp(36px, 6vw, 92px);
  min-height: 680px;
}

.feature-section--reverse {
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
}

.feature-section--reverse .feature-copy {
  order: 2;
}

.feature-copy {
  max-width: 620px;
}

.feature-copy > p {
  margin-top: 22px;
}

.feature-points {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 26px;
}

.feature-points span {
  display: inline-flex;
  align-items: center;
  height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 999px;
  color: #334155;
  background: rgba(255, 255, 255, 0.72);
  font-weight: 700;
}

.feature-visual {
  min-height: 420px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(242, 246, 251, 0.7)),
    linear-gradient(135deg, rgba(31, 107, 255, 0.12), rgba(24, 179, 168, 0.09));
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18), 0 24px 70px rgba(15, 23, 42, 0.08);
}

.visual-window {
  width: min(78%, 520px);
  min-height: 288px;
  padding: 18px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.1);
}

.visual-topline {
  width: 42%;
  height: 10px;
  border-radius: 999px;
  background: #dbeafe;
}

.visual-body {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 28px;
}

.visual-body span {
  min-height: 92px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  color: #0f172a;
  background: #f8fafc;
  font-size: 28px;
  font-weight: 800;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
}

.visual-agency .visual-body span:nth-child(1),
.visual-school .visual-body span:nth-child(2),
.visual-flow .visual-body span:nth-child(3),
.visual-forum .visual-body span:nth-child(1),
.visual-ai .visual-body span:nth-child(1) {
  color: #ffffff;
  background: linear-gradient(135deg, var(--primary), var(--primary-2));
}

.visual-ai .visual-body span:nth-child(2) {
  color: #155de8;
  background: linear-gradient(135deg, #f8fbff, #dbeafe);
}

.visual-ai .visual-body span:nth-child(3),
.visual-ai .visual-body span:nth-child(4) {
  color: #2e5fb9;
  background: #f6f7f9;
}

.final-cta {
  min-height: 520px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.final-cta h2 {
  max-width: 900px;
}

.reveal-item {
  transform: translateY(34px) scale(0.985);
  opacity: 0;
  transition: transform 0.75s ease, opacity 0.75s ease;
}

.reveal-item.is-visible {
  transform: translateY(0) scale(1);
  opacity: 1;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: opacity 0.38s ease, transform 0.38s ease;
}

.slide-fade-enter-from {
  transform: translateX(18px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-18px);
  opacity: 0;
}

@keyframes heroIn {
  from {
    transform: translateY(18px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes productFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12px);
  }
}

@media (max-width: 1060px) {
  .hero-section,
  .showcase-carousel,
  .feature-section,
  .feature-section--reverse {
    grid-template-columns: 1fr;
  }

  .hero-section {
    min-height: auto;
    padding-top: 56px;
  }

  .feature-section,
  .feature-section--reverse {
    min-height: auto;
  }

  .feature-section--reverse .feature-copy {
    order: 0;
  }

  .showcase-carousel {
    min-height: auto;
  }

  .carousel-dots {
    position: static;
    margin-top: 10px;
  }
}

@media (max-width: 720px) {
  .hero-section,
  .carousel-section,
  .feature-section,
  .final-cta {
    padding: 54px 18px;
  }

  .hero-copy h1 {
    font-size: 38px;
  }

  .hero-desc,
  .carousel-copy span,
  .feature-copy > p {
    font-size: 16px;
  }

  .hero-product {
    padding: 8px;
  }

  .showcase-carousel {
    padding: 14px;
  }

  .feature-visual {
    min-height: 330px;
  }

  .visual-window {
    width: 88%;
    min-height: 240px;
  }

  .visual-body span {
    min-height: 76px;
    font-size: 22px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-copy,
  .hero-product,
  .reveal-item,
  .slide-fade-enter-active,
  .slide-fade-leave-active {
    animation: none;
    transition: none;
  }

  .reveal-item {
    transform: none;
    opacity: 1;
  }
}
</style>
