import api from './axios'

export const getAll = () => api.get('/api/notifications')
export const markRead = (id) => api.patch(`/api/notifications/${id}/read`)
export const markAllRead = () => api.patch('/api/notifications/read-all')
