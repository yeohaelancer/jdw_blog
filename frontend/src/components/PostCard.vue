<script setup>
import TagChip from './TagChip.vue'

defineProps({
  post: { type: Object, required: true },
  showBlogLink: { type: Boolean, default: false }
})
</script>

<template>
  <div class="post-card">
    <RouterLink v-if="post.thumbnailUrl" :to="`/posts/${post.id}`" class="thumb-link">
      <div class="thumb" :style="{ backgroundImage: `url(${post.thumbnailUrl})` }" />
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
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  color: var(--color-ink);
  transition: transform 0.15s ease;
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
  transform: translateY(-3px);
}
.thumb {
  aspect-ratio: 16 / 9;
  background-size: cover;
  background-position: center;
  border-bottom: 1px solid var(--color-border);
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
