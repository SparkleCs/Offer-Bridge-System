import { ElMessageBox } from 'element-plus'
import type { Router } from 'vue-router'

export async function confirmLoginRequired(router: Router, actionText: string) {
  try {
    await ElMessageBox.confirm(`未登录无法${actionText}，是否前往登录？`, '需要登录', {
      confirmButtonText: '去登录',
      cancelButtonText: '留在当前页',
      type: 'warning'
    })
    await router.push('/auth')
    return true
  } catch {
    return false
  }
}
