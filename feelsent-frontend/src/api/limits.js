import api from './axios'

export const getAll = () => api.get('/api/message-limits')
export const setLimit = (senderId, dailyLimit) =>
  api.post('/api/message-limits', { senderId, dailyLimit })
export const removeLimit = (senderId) => api.delete(`/api/message-limits/${senderId}`)
export const getLimitInfo = (receiverId) => api.get(`/api/message-limits/info/${receiverId}`)
