import http from './http'

export const fetchMyNeighbors = () => http.get('/neighbors/mine')

export const fetchNeighborStatus = (blogId) => http.get(`/blogs/${blogId}/neighbor-status`)

export const addNeighbor = (blogId) => http.post(`/blogs/${blogId}/neighbors`)

export const removeNeighbor = (blogId) => http.delete(`/blogs/${blogId}/neighbors`)
