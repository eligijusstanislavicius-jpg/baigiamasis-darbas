import api from './axios'

export const suggest = (friendId, count = 3) => api.get(`/api/wishes/suggest/${friendId}?count=${count}`)
export const getById = (id) => api.get(`/api/wishes/${id}`)
