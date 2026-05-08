import api from './axios'

export const getUsers = () => api.get('/api/admin/users')
export const deleteUser = (id) => api.delete(`/api/admin/users/${id}`)
export const getWishes = (page = 0, size = 8, active = null, tone = null, relType = null) => {
  const params = new URLSearchParams({ page, size })
  if (active !== null) params.append('active', active)
  if (tone) params.append('tone', tone)
  if (relType) params.append('relType', relType)
  return api.get(`/api/admin/wishes?${params}`)
}
export const addWish = (data) => api.post('/api/admin/wishes', data)
export const deactivateWish = (id) => api.patch(`/api/admin/wishes/${id}/deactivate`)
export const activateWish = (id) => api.patch(`/api/admin/wishes/${id}/activate`)
export const notifyAll = (text) => api.post('/api/admin/notify/all', { text })
export const notifyUser = (userId, text) => api.post(`/api/admin/notify/${userId}`, { text })
