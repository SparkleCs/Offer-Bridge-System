import { ElMessageBox } from 'element-plus'

export async function confirmLogout() {
  try {
    await ElMessageBox.confirm(
      '<div class="logout-confirm-content"><span class="logout-confirm-icon">!</span><div><strong>确认退出登录</strong></div></div>',
      '',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确认退出',
        cancelButtonText: '取消',
        customClass: 'logout-confirm-box',
        confirmButtonClass: 'logout-confirm-primary',
        cancelButtonClass: 'logout-confirm-secondary'
      }
    )
    return true
  } catch {
    return false
  }
}
