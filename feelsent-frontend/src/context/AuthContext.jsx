import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

const MOOD_PROMPT_THRESHOLD_MS = 24 * 60 * 60 * 1000 // 24 val.

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')) } catch { return null }
  })
  const [showMoodPrompt, setShowMoodPrompt] = useState(false)

  const saveAuth = (token, userData) => {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(userData))
    setToken(token)
    setUser(userData)

    const moodSetAt = localStorage.getItem('moodSetAt')
    const isStale = !moodSetAt || (Date.now() - parseInt(moodSetAt)) > MOOD_PROMPT_THRESHOLD_MS
    if (isStale) setShowMoodPrompt(true)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
    setShowMoodPrompt(false)
  }

  const isAdmin = () => user?.role === 'ADMIN'

  return (
    <AuthContext.Provider value={{ token, user, saveAuth, logout, isAdmin, showMoodPrompt, setShowMoodPrompt }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
