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

  const load = async () => {
    const res = await getAll()
    setNotifications(res.data)
  }

  useEffect(() => { load() }, [])

  const handleMarkRead = async (id) => {
    await markRead(id)
    setNotifications((prev) => prev.map((n) => n.id === id ? { ...n, isRead: true } : n))
  }

  const handleMarkAll = async () => {
    await markAllRead()
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })))
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
            className={`bg-white border rounded-xl px-5 py-4 flex items-start gap-3 ${
              !n.isRead ? 'border-indigo-200 bg-indigo-50' : ''
            }`}
          >
            <span className="text-xl mt-0.5">{TYPE_ICON[n.type] || '📌'}</span>
            <div className="flex-1 min-w-0">
              <p className="text-sm text-slate-800">{n.text}</p>
              <p className="text-xs text-slate-400 mt-1">
                {new Date(n.createdAt).toLocaleString('lt-LT')}
              </p>
            </div>
            {!n.isRead && (
              <button
                onClick={() => handleMarkRead(n.id)}
                className="text-xs text-indigo-600 hover:underline shrink-0"
              >
                Perskaityti
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
