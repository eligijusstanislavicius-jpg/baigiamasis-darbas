import api from './axios'

export const getAll = () => api.get('/api/friendships')
export const getPending = () => api.get('/api/friendships/pending')
export const sendRequest = (receiverId, relationshipType) =>
  api.post('/api/friendships/request', { receiverId, relationshipType })
export const accept = (id) => api.patch(`/api/friendships/${id}/accept`)
export const decline = (id) => api.patch(`/api/friendships/${id}/decline`)
export const remove = (id) => api.delete(`/api/friendships/${id}`)
