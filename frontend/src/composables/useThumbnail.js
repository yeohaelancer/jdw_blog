import { reactive } from 'vue'

// 모듈 스코프 상태 - 앱 전체에서 한 번 깨진 것으로 확인된 URL은 재검사하지 않고 공유한다.
const brokenUrls = reactive(new Set())
const checkedUrls = new Set()

function preload(url) {
  if (!url || checkedUrls.has(url)) return
  checkedUrls.add(url)
  const img = new Image()
  img.onerror = () => brokenUrls.add(url)
  img.src = url
}

/**
 * 썸네일 URL이 실제로 로드 가능한지 확인한다. 스토리지에서 파일이 삭제되는 등
 * 이유로 깨진 이미지 URL이 DB에 남아있는 경우, 빈 박스 대신 플레이스홀더로 대체하기 위함.
 */
export function useThumbnail() {
  function hasImage(url) {
    if (!url) return false
    preload(url)
    return !brokenUrls.has(url)
  }
  return { hasImage }
}
