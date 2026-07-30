import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

http.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

let refreshingPromise = null

http.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const { config, response } = error
    const isAuthEndpoint = config?.url?.startsWith('/auth/')

    if (response?.status === 401 && !isAuthEndpoint && !config._retried) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          config._retried = true
          refreshingPromise = refreshingPromise || http.post('/auth/refresh', { refreshToken })
          const result = await refreshingPromise
          refreshingPromise = null
          localStorage.setItem('accessToken', result.data.accessToken)
          localStorage.setItem('refreshToken', result.data.refreshToken)
          config.headers.Authorization = `Bearer ${result.data.accessToken}`
          return http(config)
        } catch (refreshError) {
          refreshingPromise = null
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
        }
      }
    }

    console.error('[API Error]', response?.data || error.message)
    return Promise.reject(error)
  }
)

export default http
