import api from './axios'

// Admino API
export const getAllUnique = () => api.get('/api/admin/unique-wishes')
export const createUnique = (data) => api.post('/api/admin/unique-wishes', data)
export const updateUnique = (id, text) => api.put(`/api/admin/unique-wishes/${id}`, { text })
export const assignUnique = (id, data) => api.post(`/api/admin/unique-wishes/${id}/assign`, data)

// Vartotojo API
export const getMyUnique = () => api.get('/api/unique-wishes/mine')
export const removeMyUnique = (id) => api.delete(`/api/unique-wishes/mine/${id}`)
