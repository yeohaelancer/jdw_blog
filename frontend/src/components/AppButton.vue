<script setup>
defineProps({
  variant: { type: String, default: 'primary' }, // primary | secondary | ghost
  size: { type: String, default: 'md' }, // sm | md | lg
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  type: { type: String, default: 'button' }
})
defineEmits(['click'])
</script>

<template>
  <button
    :type="type"
    class="app-button"
    :class="[`variant-${variant}`, `size-${size}`, { loading }]"
    :disabled="disabled || loading"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="spinner" aria-hidden="true" />
    <slot />
  </button>
</template>

<style scoped>
.app-button {
  font-family: var(--font-heading);
  font-weight: 600;
  border: none;
  border-radius: 9999px;
  background: var(--color-primary);
  color: #fff;
  cursor: pointer;
  transition: transform 0.12s ease, opacity 0.12s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.app-button:hover:not(:disabled) {
  opacity: 0.9;
}
.app-button:active:not(:disabled) {
  transform: scale(0.97);
}
.app-button:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}
.app-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.size-sm { padding: 6px 14px; font-size: 13px; }
.size-md { padding: 10px 20px; font-size: 14px; }
.size-lg { padding: 14px 28px; font-size: 17px; }

.variant-secondary {
  background: var(--color-secondary);
}
.variant-ghost {
  background: transparent;
  color: var(--color-ink);
  border: 1px solid var(--color-border);
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
