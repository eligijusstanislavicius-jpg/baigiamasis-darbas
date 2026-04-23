import api from './axios'

export const suggest = (friendId) => api.get(`/api/wishes/suggest/${friendId}`)
export const getById = (id) => api.get(`/api/wishes/${id}`)
export const getFiltered = (receiverId, tone) =>
  api.get('/api/wishes', { params: { receiverId, tone } })
