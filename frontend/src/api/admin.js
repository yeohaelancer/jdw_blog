import http from './http'

export const fetchAllUsers = () => http.get('/admin/users')

export const updateUserStatus = (userId, status) => http.patch(`/admin/users/${userId}/status`, { status })

export const updateUserRole = (userId, role) => http.patch(`/admin/users/${userId}/role`, { role })

export const fetchAllPosts = () => http.get('/admin/posts')

export const deletePostAsAdmin = (postId) => http.delete(`/admin/posts/${postId}`)
