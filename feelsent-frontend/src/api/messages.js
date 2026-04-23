import api from './axios'

export const getInbox = () => api.get('/api/messages/inbox')
export const getSent = () => api.get('/api/messages/sent')
export const send = (data) => api.post('/api/messages', data)
export const open = (id) => api.patch(`/api/messages/${id}/open`)
export const guess = (id, tone) => api.patch(`/api/messages/${id}/guess`, { tone })
export const react = (id, reaction) => api.patch(`/api/messages/${id}/react`, { reaction })
export const getReactions = () => api.get('/api/messages/reactions')
