import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Bell, CheckCheck, ArrowRight } from 'lucide-react'
import { getAll, markRead, markAllRead } from '../api/notifications'

const TYPE_ICON = {
  FRIEND_REQUEST:          '👥',
  FRIEND_REQUEST_ACCEPTED: '✅',
  NEW_MESSAGE:             '📬',
  MESSAGE_REACTED:         '❤️',
  GUESS_CORRECT:           '🎯',
  MESSAGE_EXPIRED:         '⌛',
  FRIEND_REMOVED:          '👋',
  MESSAGE_LIMIT_SET:       '🚫',
}

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.05 } },
}
const item = {
  hidden: { opacity: 0, x: -16 },
  show:   { opacity: 1, x: 0, transition: { duration: 0.35, ease: [0.22, 1, 0.36, 1] } },
  exit:   { opacity: 0, x: 16, transition: { duration: 0.2 } },
}

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([])
  const navigate = useNavigate()

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
    } catch {}
  }

  const handleMarkAll = async () => {
    try {
      await markAllRead()
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })))
    } catch {}
  }

  const handleReadMessage = async (n) => {
    await handleMarkRead(n.id)
    navigate('/inbox')
  }

  const unread = notifications.filter((n) => !n.isRead).length

  return (
    <div className="p-8 max-w-2xl" style={{ paddingLeft: "2.5rem" }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between mb-8"
      >
        <div className="flex items-center gap-3">
          <div
            className="w-10 h-10 rounded-xl flex items-center justify-center"
            style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
          >
            <Bell size={20} color="white" strokeWidth={2} />
          </div>
          <div>
            <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Pranešimai</h1>
            {unread > 0 && (
              <p className="text-sm" style={{ color: 'var(--text-muted)' }}>{unread} neperskaityti</p>
            )}
          </div>
        </div>
        {unread > 0 && (
          <motion.button
            whileHover={{ scale: 1.03 }}
            whileTap={{ scale: 0.97 }}
            onClick={handleMarkAll}
            className="flex items-center gap-1.5 text-sm font-medium px-4 py-2 rounded-xl"
            style={{
              background: 'rgba(255,255,255,0.6)',
              border: '1px solid rgba(255,255,255,0.8)',
              color: 'var(--text-primary)',
            }}
          >
            <CheckCheck size={15} /> Pažymėti visus
          </motion.button>
        )}
      </motion.div>

      {notifications.length === 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass text-center py-16 px-8"
        >
          <div className="text-5xl mb-4">🔔</div>
          <p className="font-semibold" style={{ color: 'var(--text-primary)' }}>Nėra pranešimų</p>
          <p className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>
            Pranešimai atsiras čia kai bus aktyvumo
          </p>
        </motion.div>
      )}

      <motion.div
        className="flex flex-col gap-2"
        variants={stagger}
        initial="hidden"
        animate="show"
      >
        <AnimatePresence>
          {notifications.map((n) => (
            <motion.div
              key={n.id}
              variants={item}
              layout
              onClick={() => !n.isRead && handleMarkRead(n.id)}
              className="glass px-5 py-4 cursor-pointer transition-all"
              style={!n.isRead ? {
                background: 'rgba(190,24,93,0.07)',
                borderLeft: '3px solid var(--accent-from)',
              } : {}}
              whileHover={{ y: -1 }}
            >
              <div className="flex items-start gap-3">
                <span className="text-xl mt-0.5 shrink-0">{TYPE_ICON[n.type] || '📌'}</span>
                <div className="flex-1 min-w-0">
                  <p className="text-sm" style={{ color: 'var(--text-primary)' }}>{n.text}</p>
                  <p className="text-xs mt-1" style={{ color: 'var(--text-muted)' }}>
                    {new Date(n.createdAt).toLocaleString('lt-LT')}
                  </p>
                </div>
                {n.type === 'NEW_MESSAGE' && (
                  <div className="shrink-0">
                    {n.messageStatus === 'SENT' ? (
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        onClick={(e) => { e.stopPropagation(); handleReadMessage(n) }}
                        className="flex items-center gap-1 text-xs font-semibold"
                        style={{ color: 'var(--accent-from)' }}
                      >
                        Perskaityti <ArrowRight size={12} />
                      </motion.button>
                    ) : (
                      <span className="text-xs" style={{ color: 'var(--text-muted)' }}>Perskaityta</span>
                    )}
                  </div>
                )}
                {!n.isRead && n.type !== 'NEW_MESSAGE' && (
                  <div className="w-2 h-2 rounded-full shrink-0 mt-1.5" style={{ background: 'var(--accent-from)' }} />
                )}
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </motion.div>
    </div>
  )
}
