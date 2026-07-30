<script setup>
import TagChip from './TagChip.vue'

defineProps({
  post: { type: Object, required: true },
  showBlogLink: { type: Boolean, default: false }
})
</script>

<template>
  <div class="post-card">
    <RouterLink :to="`/posts/${post.id}`" class="thumb-link">
      <div class="thumb" :style="post.thumbnailUrl ? { backgroundImage: `url(${post.thumbnailUrl})` } : null">
        <span v-if="!post.thumbnailUrl" class="thumb-placeholder">🖌️</span>
      </div>
    </RouterLink>
    <div class="content">
      <RouterLink v-if="showBlogLink && post.blogName" :to="`/blogs/${post.blogId}`" class="blog-link">
        {{ post.blogName }}
      </RouterLink>
      <p class="category" v-if="post.categoryName">{{ post.categoryName }}</p>
      <RouterLink :to="`/posts/${post.id}`" class="title-link">
        <h3 class="title">{{ post.title }}</h3>
      </RouterLink>
      <p class="summary">{{ post.summary }}</p>
      <div class="tags" v-if="post.tags?.length">
        <TagChip v-for="t in post.tags" :key="t" :label="t" />
      </div>
      <div class="meta">
        <span>💗 {{ post.likeCount }}</span>
        <span>💬 {{ post.commentCount }}</span>
        <span>👁️ {{ post.viewCount }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-card {
  display: block;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  color: var(--color-ink);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.thumb-link, .title-link {
  display: block;
  text-decoration: none;
  color: inherit;
}
.blog-link {
  display: inline-block;
  font-size: 12px;
  color: var(--color-ink-soft);
  text-decoration: none;
  margin-bottom: 4px;
}
.blog-link:hover { text-decoration: underline; }
.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 6px 6px 0 rgba(43, 43, 43, 0.18);
}
.thumb {
  aspect-ratio: 16 / 9;
  background: linear-gradient(135deg, #E3F0FF, #FFE9E3);
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 2px solid var(--color-border);
}
.thumb-placeholder {
  font-size: 32px;
  opacity: 0.6;
}
.content {
  padding: 16px;
}
.category {
  font-size: 12px;
  color: var(--color-primary-dark);
  margin: 0 0 4px;
}
.title {
  font-family: var(--font-heading);
  margin: 0 0 8px;
  font-size: 20px;
}
.summary {
  font-size: 14px;
  color: var(--color-ink-soft);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0 0 12px;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}
.meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--color-ink-soft);
}
</style>
