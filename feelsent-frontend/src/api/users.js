import api from './axios'

export const getMe = () => api.get('/api/users/me')
export const updateMood = (moodStatus) => api.patch('/api/users/me/mood', { moodStatus: moodStatus || null })
export const updateWant = (moodWant) => api.patch('/api/users/me/want', { moodWant: moodWant || null })
export const clearMood = () => api.patch('/api/users/me/mood', {})
export const clearWant = () => api.patch('/api/users/me/want', {})
export const getPoints = () => api.get('/api/users/me/points')
export const deleteAccount = () => api.delete('/api/users/me')
