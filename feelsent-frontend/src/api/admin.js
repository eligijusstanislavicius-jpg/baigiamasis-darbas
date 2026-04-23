import api from './axios'

export const getUsers = () => api.get('/api/admin/users')
export const deleteUser = (id) => api.delete(`/api/admin/users/${id}`)
export const getWishes = () => api.get('/api/admin/wishes')
export const addWish = (data) => api.post('/api/admin/wishes', data)
export const deactivateWish = (id) => api.patch(`/api/admin/wishes/${id}/deactivate`)
