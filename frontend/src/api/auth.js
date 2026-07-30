import http from './http'

export const checkEmail = (email) => http.get('/auth/check-email', { params: { email } })

export const signup = (payload) => http.post('/auth/signup', payload)

export const login = (payload) => http.post('/auth/login', payload)

export const fetchMe = () => http.get('/auth/me')
