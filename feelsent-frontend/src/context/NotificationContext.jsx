import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { getAll } from '../api/notifications'
import { useAuth } from './AuthContext'

const NotificationCtx = createContext({ unreadNotifs: 0, setUnreadNotifs: () => {}, refreshUnread: () => {} })

export function NotificationProvider({ children }) {
  const [unreadNotifs, setUnreadNotifs] = useState(0)
  const { token } = useAuth()

  const refreshUnread = useCallback(async () => {
    try {
      const res = await getAll()
      setUnreadNotifs(res.data.filter(n => !n.isRead).length)
    } catch {}
  }, [])

  useEffect(() => {
    if (!token) return
    refreshUnread()
    const interval = setInterval(refreshUnread, 30000)
    return () => clearInterval(interval)
  }, [token, refreshUnread])

  return (
    <NotificationCtx.Provider value={{ unreadNotifs, setUnreadNotifs, refreshUnread }}>
      {children}
    </NotificationCtx.Provider>
  )
}

export const useNotifications = () => useContext(NotificationCtx)
