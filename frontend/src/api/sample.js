import http from './http'

export const sampleApi = {
  list: () => http.get('/samples'),
  get: (id) => http.get(`/samples/${id}`),
  create: (payload) => http.post('/samples', payload),
  update: (id, payload) => http.put(`/samples/${id}`, payload),
  remove: (id) => http.delete(`/samples/${id}`)
}
