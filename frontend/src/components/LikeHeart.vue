<script setup>
import { ref } from 'vue'

const props = defineProps({
  liked: { type: Boolean, default: false },
  count: { type: Number, default: 0 }
})
const emit = defineEmits(['toggle'])

const bursting = ref(false)

function onClick() {
  if (!props.liked) {
    bursting.value = true
    setTimeout(() => (bursting.value = false), 400)
  }
  emit('toggle')
}
</script>

<template>
  <button
    type="button"
    class="like-heart"
    :class="{ liked, bursting }"
    :aria-pressed="liked"
    @click="onClick"
  >
    <span class="heart-icon" aria-hidden="true">{{ liked ? '💗' : '🤍' }}</span>
    <span class="count">{{ count }}</span>
  </button>
</template>

<style scoped>
.like-heart {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: 999px;
  padding: 8px 18px;
  cursor: pointer;
  font-family: var(--font-heading);
  font-size: 16px;
  color: var(--color-ink);
}
.like-heart.liked {
  background: #FDEDEB;
  border-color: var(--color-secondary);
}
.heart-icon {
  font-size: 20px;
  transition: transform 0.2s ease;
}
.bursting .heart-icon {
  animation: burst 0.4s ease;
}
@keyframes burst {
  0% { transform: scale(1); }
  40% { transform: scale(1.5); }
  100% { transform: scale(1); }
}
.like-heart:focus-visible {
  outline: 3px solid var(--color-accent-yellow);
  outline-offset: 2px;
}
</style>
