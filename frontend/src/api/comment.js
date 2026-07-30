import http from './http'

export const fetchComments = (postId) => http.get(`/posts/${postId}/comments`)

export const createComment = (postId, payload) => http.post(`/posts/${postId}/comments`, payload)

export const deleteComment = (postId, commentId) => http.delete(`/posts/${postId}/comments/${commentId}`)
