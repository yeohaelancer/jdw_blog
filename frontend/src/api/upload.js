import http from './http'

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post('/uploads', formData, { headers: { 'Content-Type': undefined } })
}
