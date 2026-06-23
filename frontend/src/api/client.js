import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
})

// Attach JWT to every request automatically
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/**
 * Extracts a human-readable error message from an Axios error.
 * The backend may return { "error": "..." } or { "message": "..." } — we check both.
 * Falls back to the provided default string if neither field is present.
 */
export function getErrorMessage(err, fallback = 'Something went wrong. Please try again.') {
  return err.response?.data?.message ?? err.response?.data?.error ?? fallback
}

export default client
