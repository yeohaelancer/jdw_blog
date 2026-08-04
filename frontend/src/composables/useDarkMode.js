import { ref, watchEffect } from 'vue'

const STORAGE_KEY = 'jdw-dark-mode'
const isDark = ref(localStorage.getItem(STORAGE_KEY) === 'true')

watchEffect(() => {
  document.documentElement.dataset.theme = isDark.value ? 'dark' : 'light'
  localStorage.setItem(STORAGE_KEY, String(isDark.value))
})

export function useDarkMode() {
  function toggle() {
    isDark.value = !isDark.value
  }
  return { isDark, toggle }
}
