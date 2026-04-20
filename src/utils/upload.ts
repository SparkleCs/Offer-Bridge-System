import { ElMessage } from 'element-plus'
import { ApiError } from '../services/http'

export const MAX_UPLOAD_SIZE_MB = 10
export const MAX_UPLOAD_SIZE_BYTES = MAX_UPLOAD_SIZE_MB * 1024 * 1024
export const UPLOAD_TOO_LARGE_MESSAGE = `文件超过 ${MAX_UPLOAD_SIZE_MB}MB，请压缩后重试`

export function validateUploadFileSize(file: File) {
  if (file.size > MAX_UPLOAD_SIZE_BYTES) {
    ElMessage.warning(UPLOAD_TOO_LARGE_MESSAGE)
    return false
  }
  return true
}

export function getUploadErrorMessage(error: unknown, fallback = '上传失败') {
  if (error instanceof ApiError) {
    if (error.code === 'BIZ_FILE_TOO_LARGE') {
      return UPLOAD_TOO_LARGE_MESSAGE
    }
    return error.message || fallback
  }
  return fallback
}
