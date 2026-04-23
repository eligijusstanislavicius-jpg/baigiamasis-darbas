import api from './axios'

export const getAll = () => api.get('/api/favorite-wishes')
export const add = (wishId) => api.post('/api/favorite-wishes', { wishId })
export const remove = (id) => api.delete(`/api/favorite-wishes/${id}`)
