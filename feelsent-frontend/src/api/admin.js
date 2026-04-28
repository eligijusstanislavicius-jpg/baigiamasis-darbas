import api from './axios'

export const getUsers = () => api.get('/api/admin/users')
export const deleteUser = (id) => api.delete(`/api/admin/users/${id}`)
export const getWishes = (page = 0, size = 8) => api.get(`/api/admin/wishes?page=${page}&size=${size}`)
export const addWish = (data) => api.post('/api/admin/wishes', data)
export const deactivateWish = (id) => api.patch(`/api/admin/wishes/${id}/deactivate`)
export const notifyAll = (text) => api.post('/api/admin/notify/all', { text })
export const notifyUser = (userId, text) => api.post(`/api/admin/notify/${userId}`, { text })
