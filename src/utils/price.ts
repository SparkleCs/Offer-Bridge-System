function trimNumber(value: number) {
  if (Number.isInteger(value)) return String(value)
  return value.toFixed(1).replace(/\.0$/, '')
}

function compactSinglePrice(value: number) {
  if (value >= 10000) return { value: trimNumber(value / 10000), unit: 'w' }
  if (value >= 1000) return { value: trimNumber(value / 1000), unit: 'k' }
  return { value: trimNumber(value), unit: '' }
}

export function formatCompactPrice(min?: number | null, max?: number | null) {
  const low = Number(min || 0)
  const high = Number(max || 0)
  if (low <= 0 && high <= 0) return '价格待沟通'

  const start = Math.min(low, high)
  const end = Math.max(low, high)
  const startText = compactSinglePrice(start)
  const endText = compactSinglePrice(end)
  if (start === end) return `${startText.value}${startText.unit}`
  if (startText.unit && startText.unit === endText.unit) {
    return `${startText.value}-${endText.value}${endText.unit}`
  }
  return `${startText.value}${startText.unit}-${endText.value}${endText.unit}`
}
