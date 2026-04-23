import api from './axios'

export const getMe = () => api.get('/api/users/me')
export const updateMood = (moodStatus) => api.patch('/api/users/me/mood', { moodStatus })
export const updateWant = (moodWant) => api.patch('/api/users/me/want', { moodWant })
export const getPoints = () => api.get('/api/users/me/points')
