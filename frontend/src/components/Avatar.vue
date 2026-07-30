<script setup>
defineProps({
  src: { type: String, default: null },
  size: { type: String, default: 'md' } // sm | md | lg
})
</script>

<template>
  <span class="avatar" :class="`size-${size}`">
    <img v-if="src" :src="src" alt="" />
    <span v-else class="fallback">🙂</span>
  </span>
</template>

<style scoped>
.avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid var(--color-accent-yellow);
  padding: 2px;
  flex-shrink: 0;
}
.avatar img,
.avatar .fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid var(--color-border);
  background: var(--color-surface);
  object-fit: cover;
}
.size-sm { width: 28px; height: 28px; }
.size-md { width: 40px; height: 40px; }
.size-lg { width: 72px; height: 72px; }
.size-lg .fallback { font-size: 28px; }
</style>
