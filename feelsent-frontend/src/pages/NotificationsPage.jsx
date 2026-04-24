import { useEffect, useState } from 'react'
import { getAll, markRead, markAllRead } from '../api/notifications'

const TYPE_ICON = {
  FRIEND_REQUEST: '👥',
  FRIEND_REQUEST_ACCEPTED: '✅',
  NEW_MESSAGE: '📬',
  MESSAGE_REACTED: '❤️',
  GUESS_CORRECT: '🎯',
  MESSAGE_EXPIRED: '⌛',
}

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([])
  const [expanded, setExpanded] = useState(null)

  const load = async () => {
    try {
      const res = await getAll()
      setNotifications(res.data)
    } catch {}
  }

  useEffect(() => { load() }, [])

  const handleMarkRead = async (id) => {
    try {
      await markRead(id)
      setNotifications((prev) => prev.map((n) => n.id === id ? { ...n, isRead: true } : n))
    } catch (err) {
      alert(err.response?.data?.message || 'Nepavyko pažymėti kaip perskaitytą')
    }
  }

  const handleMarkAll = async () => {
    try {
      await markAllRead()
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })))
    } catch {}
  }

  const unread = notifications.filter((n) => !n.isRead).length

  return (
    <div className="p-8 max-w-2xl">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold">🔔 Pranešimai</h2>
        {unread > 0 && (
          <button
            onClick={handleMarkAll}
            className="text-sm text-indigo-600 hover:underline"
          >
            Pažymėti visus kaip perskaitytus
          </button>
        )}
      </div>

      {notifications.length === 0 && <p className="text-slate-400">Nėra pranešimų.</p>}
      <div className="flex flex-col gap-2">
        {notifications.map((n) => (
          <div
            key={n.id}
            className={`bg-white border rounded-xl px-5 py-4 ${
              !n.isRead ? 'border-indigo-200 bg-indigo-50' : ''
            }`}
          >
            <div className="flex items-start gap-3">
              <span className="text-xl mt-0.5">{TYPE_ICON[n.type] || '📌'}</span>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-slate-800">{n.text}</p>
                <p className="text-xs text-slate-400 mt-1">
                  {new Date(n.createdAt).toLocaleString('lt-LT')}
                </p>
                {expanded === n.id && n.type === 'MESSAGE_REACTED' && (
                  <div className="mt-2 text-sm text-slate-600 bg-white rounded-lg px-3 py-2 border">
                    <p>{n.text}</p>
                  </div>
                )}
              </div>
              <div className="flex items-center gap-2 shrink-0">
                {n.type === 'MESSAGE_REACTED' && (
                  <button
                    onClick={() => setExpanded(expanded === n.id ? null : n.id)}
                    className="text-xs text-slate-400 hover:text-slate-600"
                  >
                    {expanded === n.id ? '▲' : '▼'}
                  </button>
                )}
                {!n.isRead && (
                  <button
                    onClick={() => handleMarkRead(n.id)}
                    className="text-xs text-indigo-600 hover:underline"
                  >
                    Perskaityti
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
