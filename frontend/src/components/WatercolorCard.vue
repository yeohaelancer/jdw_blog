<script setup>
defineProps({
  tint: { type: String, default: 'none' } // blue | coral | yellow | none
})
</script>

<template>
  <div class="watercolor-card" :class="`tint-${tint}`">
    <header v-if="$slots.header" class="card-header">
      <slot name="header" />
    </header>
    <div class="card-body">
      <slot />
    </div>
    <footer v-if="$slots.footer" class="card-footer">
      <slot name="footer" />
    </footer>
  </div>
</template>

<style scoped>
.watercolor-card {
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-card);
  padding: 24px;
  transition: box-shadow 0.15s ease;
}
.watercolor-card:hover {
  box-shadow: 5px 5px 0 rgba(43, 43, 43, 0.18);
}
.tint-blue { background: linear-gradient(180deg, #EAF4FC 0%, var(--color-surface) 40%); }
.tint-coral { background: linear-gradient(180deg, #FDEDEB 0%, var(--color-surface) 40%); }
.tint-yellow { background: linear-gradient(180deg, #FCF4DD 0%, var(--color-surface) 40%); }

.card-header { margin-bottom: 16px; }
.card-footer { margin-top: 16px; }
</style>
