<script setup>
defineProps({
  comment: { type: Object, required: true }
})
defineEmits(['reply', 'delete'])
</script>

<template>
  <div class="comment" :class="{ reply: comment.parentId }">
    <div class="bubble">
      <div class="head">
        <strong>{{ comment.authorNickname }}</strong>
        <span class="date">{{ new Date(comment.createdAt).toLocaleString() }}</span>
      </div>
      <p v-if="comment.deleted" class="deleted">삭제된 댓글입니다.</p>
      <p v-else-if="comment.secret" class="secret">🔒 비밀 댓글입니다.</p>
      <p v-else class="text">{{ comment.content }}</p>
    </div>
    <div v-if="!comment.deleted && !comment.parentId" class="actions">
      <button type="button" class="link-btn" @click="$emit('reply', comment)">답글</button>
      <button type="button" class="link-btn" @click="$emit('delete', comment)">삭제</button>
    </div>
    <div v-else-if="!comment.deleted" class="actions">
      <button type="button" class="link-btn" @click="$emit('delete', comment)">삭제</button>
    </div>
  </div>
</template>

<style scoped>
.comment {
  margin-bottom: 16px;
}
.comment.reply {
  margin-left: 32px;
}
.bubble {
  position: relative;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  padding: 12px 16px;
}
.comment:not(.reply) .bubble::before {
  content: '';
  position: absolute;
  left: 20px;
  top: -9px;
  width: 14px;
  height: 14px;
  background: var(--color-surface);
  border-left: 2px solid var(--color-border);
  border-top: 2px solid var(--color-border);
  transform: rotate(45deg);
}
.comment.reply .bubble::before {
  content: '';
  position: absolute;
  left: -9px;
  top: 14px;
  width: 14px;
  height: 14px;
  background: var(--color-surface);
  border-left: 2px solid var(--color-border);
  border-bottom: 2px solid var(--color-border);
  transform: rotate(45deg);
}
.head {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 6px;
}
.date {
  color: var(--color-ink-soft);
}
.text {
  margin: 0;
  white-space: pre-wrap;
}
.deleted, .secret {
  margin: 0;
  color: var(--color-ink-soft);
  font-style: italic;
}
.actions {
  margin-top: 6px;
  display: flex;
  gap: 12px;
}
.link-btn {
  background: none;
  border: none;
  color: var(--color-primary-dark);
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}
</style>
