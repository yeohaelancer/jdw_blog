<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import * as postApi from '@/api/post'
import * as commentApi from '@/api/comment'
import { renderMarkdown } from '@/utils/markdown'
import WatercolorCard from '@/components/WatercolorCard.vue'
import TagChip from '@/components/TagChip.vue'
import LikeHeart from '@/components/LikeHeart.vue'
import SpeechBubbleComment from '@/components/SpeechBubbleComment.vue'
import AppButton from '@/components/AppButton.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const post = ref(null)
const related = ref([])
const comments = ref([])
const errorMessage = ref('')
const commentContent = ref('')
const commentSecret = ref(false)
const replyTarget = ref(null)
const posting = ref(false)

const postId = () => Number(route.params.id)
const isOwner = () => authStore.isLoggedIn && post.value?.authorUserId === authStore.userId

async function onDeletePost() {
  if (!confirm('이 게시글을 삭제할까요? 삭제 후에는 되돌릴 수 없습니다.')) return
  await postApi.deletePost(postId())
  router.push(`/blogs/${authStore.blogId}`)
}

function onBack() {
  if (post.value) {
    router.push(`/blogs/${post.value.blogId}`)
  } else {
    router.push('/explore')
  }
}

async function loadRelated() {
  const res = await postApi.fetchPosts({ blogId: post.value.blogId, size: 12 })
  related.value = res.data.items
    .filter((p) => p.id !== post.value.id)
    .sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
    .slice(0, 3)
}

async function load() {
  errorMessage.value = ''
  try {
    const res = await postApi.fetchPost(postId())
    post.value = res.data
    const commentsRes = await commentApi.fetchComments(postId())
    comments.value = commentsRes.data
    await loadRelated()
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || '게시글을 불러올 수 없습니다.'
  }
}

async function onToggleLike() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  const prevLiked = post.value.likedByMe
  post.value.likedByMe = !prevLiked
  post.value.likeCount += prevLiked ? -1 : 1
  try {
    const res = await postApi.toggleLike(postId())
    post.value.likedByMe = res.data.liked
  } catch {
    post.value.likedByMe = prevLiked
    post.value.likeCount += prevLiked ? 1 : -1
  }
}

function onReply(comment) {
  replyTarget.value = comment
}

async function submitComment() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (!commentContent.value.trim()) return
  posting.value = true
  try {
    await commentApi.createComment(postId(), {
      content: commentContent.value,
      parentId: replyTarget.value?.id || null,
      secret: commentSecret.value
    })
    commentContent.value = ''
    commentSecret.value = false
    replyTarget.value = null
    const commentsRes = await commentApi.fetchComments(postId())
    comments.value = commentsRes.data
    post.value.commentCount += 1
  } finally {
    posting.value = false
  }
}

async function onDeleteComment(comment) {
  await commentApi.deleteComment(postId(), comment.id)
  const commentsRes = await commentApi.fetchComments(postId())
  comments.value = commentsRes.data
  post.value.commentCount -= 1
}

const renderedContent = computed(() => renderMarkdown(post.value?.content))

onMounted(load)
</script>

<template>
  <div class="post-detail" v-if="errorMessage">
    <WatercolorCard class="error-card">{{ errorMessage }}</WatercolorCard>
  </div>
  <div class="post-detail" v-else-if="post">
    <button type="button" class="back-link" @click="onBack">← 목록으로</button>

    <div class="title-row">
      <p class="category-tag-label" v-if="post.categoryName">{{ post.categoryName }} · #{{ post.tags?.[0] || post.categoryName }}</p>
      <div v-if="isOwner()" class="owner-actions">
        <RouterLink :to="`/posts/${post.id}/edit`" class="link-btn">수정</RouterLink>
        <button type="button" class="link-btn danger" @click="onDeletePost">삭제</button>
      </div>
    </div>
    <h1 class="title">{{ post.title }}</h1>
    <div class="author">
      <span>{{ post.authorNickname }}</span>
      <span class="dot">·</span>
      <span>{{ new Date(post.publishedAt || post.createdAt).toLocaleDateString() }}</span>
      <span class="dot">·</span>
      <span>조회 {{ post.viewCount }}</span>
      <span class="dot">·</span>
      <span>댓글 {{ post.commentCount }}</span>
      <span class="dot">·</span>
      <span>좋아요 {{ post.likeCount }}</span>
    </div>

    <div v-if="post.thumbnailUrl" class="thumbnail" :style="{ backgroundImage: `url(${post.thumbnailUrl})` }" />

    <WatercolorCard class="content-card">
      <!-- eslint-disable-next-line vue/no-v-html -->
      <div class="content" v-html="renderedContent" />
    </WatercolorCard>

    <div class="tags" v-if="post.tags?.length">
      <TagChip v-for="t in post.tags" :key="t" :label="t" />
    </div>

    <div class="like-row">
      <LikeHeart :liked="post.likedByMe" :count="post.likeCount" @toggle="onToggleLike" />
    </div>

    <section class="related" v-if="related.length">
      <h3>다른 인기글</h3>
      <div class="related-grid">
        <RouterLink v-for="rl in related" :key="rl.id" :to="`/posts/${rl.id}`" class="related-card">
          <div class="related-thumb" :style="rl.thumbnailUrl ? { backgroundImage: `url(${rl.thumbnailUrl})` } : null">
            <div v-if="!rl.thumbnailUrl" class="related-placeholder" />
          </div>
          <p class="related-title">{{ rl.title }}</p>
        </RouterLink>
      </div>
    </section>

    <section class="comments">
      <h2>댓글 {{ post.commentCount }}</h2>
      <p v-if="comments.length === 0" class="empty">첫 댓글을 남겨보세요 ✍️</p>
      <SpeechBubbleComment
        v-for="c in comments"
        :key="c.id"
        :comment="c"
        @reply="onReply"
        @delete="onDeleteComment"
      />

      <form class="comment-form" @submit.prevent="submitComment">
        <p v-if="replyTarget" class="reply-hint">
          @{{ replyTarget.authorNickname }} 님에게 답글 작성 중
          <button type="button" @click="replyTarget = null">취소</button>
        </p>
        <textarea v-model="commentContent" rows="3" maxlength="1000" placeholder="댓글을 남겨보세요" />
        <div class="comment-form-actions">
          <label class="secret-toggle">
            <input type="checkbox" v-model="commentSecret" /> 비밀 댓글
          </label>
          <AppButton type="submit" size="sm" :loading="posting">등록</AppButton>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.back-link {
  background: none;
  border: none;
  color: var(--color-ink-soft);
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 24px;
  padding: 0;
}
.thumbnail {
  height: 360px;
  border-radius: 20px;
  background-size: cover;
  background-position: center;
  margin-bottom: 32px;
}
.post-detail {
  max-width: 760px;
  margin: 0 auto;
  padding: 48px 40px 100px;
}
.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.category-tag-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0;
}
.owner-actions {
  display: flex;
  gap: 12px;
}
.owner-actions .link-btn {
  background: none;
  border: none;
  color: var(--color-primary-dark);
  cursor: pointer;
  font-size: 13px;
  text-decoration: none;
  padding: 0;
}
.owner-actions .link-btn.danger { color: var(--color-error); }
.title {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.35;
  letter-spacing: -0.01em;
  margin: 16px 0;
}
.author {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
  color: var(--color-ink-soft);
  font-size: 13px;
  padding-bottom: 28px;
  margin-bottom: 28px;
  border-bottom: 1px solid var(--color-border);
}
.dot { margin: 0 6px; }
.content-card { margin-bottom: 16px; }
.content { line-height: 1.75; word-break: break-word; }
.content :deep(img) { max-width: 100%; border: 2px solid var(--color-border); border-radius: 12px; }
.content :deep(h1),
.content :deep(h2),
.content :deep(h3) {
  font-family: var(--font-heading);
  margin: 28px 0 12px;
  line-height: 1.4;
}
.content :deep(h1) { font-size: 26px; }
.content :deep(h2) { font-size: 22px; }
.content :deep(h3) { font-size: 19px; }
.content :deep(p) { margin: 0 0 16px; }
.content :deep(ul),
.content :deep(ol) { margin: 0 0 16px; padding-left: 24px; }
.content :deep(li) { margin: 4px 0; }
.content :deep(li input[type='checkbox']) { margin-right: 6px; }
.content :deep(strong) { color: var(--color-ink); }
.content :deep(a) { color: var(--color-primary-dark); text-decoration: underline; }
.content :deep(blockquote) {
  margin: 0 0 16px;
  padding: 8px 16px;
  border-left: 4px solid var(--color-accent-yellow);
  color: var(--color-ink-soft);
  background: rgba(247, 215, 116, 0.15);
  border-radius: 0 12px 12px 0;
}
.content :deep(code) {
  background: rgba(43, 43, 43, 0.08);
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 0.9em;
}
.content :deep(pre) {
  background: rgba(43, 43, 43, 0.08);
  padding: 16px;
  border-radius: 12px;
  overflow-x: auto;
  margin: 0 0 16px;
}
.content :deep(pre code) { background: none; padding: 0; }
.content :deep(hr) {
  border: none;
  border-top: 2px dashed var(--color-border);
  margin: 24px 0;
}
.tags { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 24px; }
.like-row { display: flex; justify-content: center; margin: 32px 0; }
.related {
  border-top: 1px solid var(--color-border);
  padding-top: 32px;
  margin-bottom: 32px;
}
.related h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 16px;
}
.related-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.related-card {
  display: block;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 14px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
}
.related-thumb {
  height: 100px;
  background-size: cover;
  background-position: center;
}
.related-placeholder {
  width: 100%;
  height: 100%;
  background: var(--color-placeholder-bg);
}
.related-title {
  padding: 12px;
  margin: 0;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.4;
}
.comments h2 { font-size: 20px; }
.empty { color: var(--color-ink-soft); }
.comment-form { margin-top: 24px; }
.comment-form textarea {
  width: 100%;
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 12px;
  font-family: var(--font-body);
  resize: vertical;
}
.comment-form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.secret-toggle { font-size: 13px; color: var(--color-ink-soft); }
.reply-hint {
  font-size: 13px;
  color: var(--color-primary-dark);
  display: flex;
  gap: 8px;
  align-items: center;
}
.reply-hint button {
  background: none;
  border: none;
  color: var(--color-error);
  cursor: pointer;
}
.error-card { text-align: center; margin: 64px auto; max-width: 420px; }
</style>
