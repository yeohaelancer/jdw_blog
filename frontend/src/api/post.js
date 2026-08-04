import http from './http'

export const fetchPosts = (params) => http.get('/posts', { params })

export const fetchPost = (id) => http.get(`/posts/${id}`)

export const createPost = (payload) => http.post('/posts', payload)

export const updatePost = (id, payload) => http.put(`/posts/${id}`, payload)

export const deletePost = (id) => http.delete(`/posts/${id}`)

export const updateThumbnail = (id, thumbnailUrl) => http.patch(`/posts/${id}/thumbnail`, { thumbnailUrl })

export const toggleLike = (postId) => http.post(`/posts/${postId}/like`)

export const fetchCategories = (blogId) => http.get(`/blogs/${blogId}/categories`)

export const createCategory = (blogId, payload) => http.post(`/blogs/${blogId}/categories`, payload)

export const updateCategory = (blogId, categoryId, payload) => http.put(`/blogs/${blogId}/categories/${categoryId}`, payload)

export const deleteCategory = (blogId, categoryId) => http.delete(`/blogs/${blogId}/categories/${categoryId}`)

export const fetchTagCloud = (blogId) => http.get('/tags/cloud', { params: { blogId } })

export const fetchBlog = (blogId) => http.get(`/blogs/${blogId}`)
