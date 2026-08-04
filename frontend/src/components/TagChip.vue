<script setup>
defineProps({
  label: { type: String, required: true },
  clickable: { type: Boolean, default: false }
})
defineEmits(['click'])
</script>

<template>
  <span
    class="tag-chip"
    :class="{ clickable }"
    :role="clickable ? 'button' : undefined"
    :tabindex="clickable ? 0 : undefined"
    @click="clickable && $emit('click', label)"
    @keyup.enter="clickable && $emit('click', label)"
  >
    #{{ label }}
  </span>
</template>

<style scoped>
.tag-chip {
  display: inline-block;
  background: var(--color-accent-soft);
  color: var(--color-accent-soft-text);
  font-family: var(--font-heading);
  font-weight: 600;
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 9999px;
}
.clickable {
  cursor: pointer;
}
.clickable:focus-visible {
  outline: 2px solid var(--color-primary-dark);
  outline-offset: 2px;
}
</style>
