import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Users, Trash2, MessageSquare, ChevronDown } from 'lucide-react'
import { getUsers, deleteUser, notifyUser } from '../api/admin'

function lastSeenLabel(lastLoginAt) {
  if (!lastLoginAt) return 'Niekada neprisijungęs'
  const days = Math.floor((Date.now() - new Date(lastLoginAt)) / 86400000)
  if (days === 0) return 'Prisijungė šiandien'
  return `${days} d. neprisijungęs`
}

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.06 } },
}
const item = {
  hidden: { opacity: 0, y: 16 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.35, ease: [0.22, 1, 0.36, 1] } },
}

export default function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [openMsgId, setOpenMsgId] = useState(null)
  const [msgText, setMsgText] = useState('')

  const load = async () => {
    const res = await getUsers()
    setUsers(res.data)
  }

  useEffect(() => { load() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Tikrai ištrinti vartotoją? Visi jo duomenys bus prarasti.')) return
    try {
      await deleteUser(id)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleSendMsg = async (id) => {
    if (!msgText.trim()) return
    try {
      await notifyUser(id, msgText)
      setMsgText('')
      setOpenMsgId(null)
      alert('Pranešimas išsiųstas')
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  return (
    <div className="p-8 max-w-3xl" style={{ paddingLeft: "2.5rem" }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-3 mb-8"
      >
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
        >
          <Users size={20} color="white" strokeWidth={2} />
        </div>
        <div>
          <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>
            Visi vartotojai
          </h1>
          <p className="text-sm" style={{ color: 'var(--text-muted)' }}>{users.length} vartotojų</p>
        </div>
      </motion.div>

      <motion.div className="flex flex-col gap-3" variants={stagger} initial="hidden" animate="show">
        {users.map((u) => (
          <motion.div key={u.id} variants={item} layout className="glass p-5" style={{ paddingRight: '30px' }}>
            <div className="flex items-start justify-between gap-4">
              <div className="flex items-center gap-3 min-w-0 flex-1">
                <div
                  className="w-10 h-10 rounded-xl flex items-center justify-center text-sm font-bold text-white shrink-0"
                  style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
                >
                  {u.firstName?.[0]?.toUpperCase()}
                </div>
                <div className="min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="font-semibold text-sm truncate" style={{ color: 'var(--text-primary)' }}>
                      {u.firstName} {u.lastName}
                    </p>
                    {u.role === 'ADMIN' && (
                      <span
                        className="px-2 py-0.5 text-xs font-bold rounded-full"
                        style={{
                          background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
                          color: 'white',
                        }}
                      >
                        Admin
                      </span>
                    )}
                  </div>
                  <p className="text-xs mt-0.5 truncate" style={{ color: 'var(--text-muted)' }}>
                    {lastSeenLabel(u.lastLoginAt)} · {u.points} taškų
                  </p>
                </div>
              </div>

              <div className="flex gap-2 shrink-0">
                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={() => { setOpenMsgId(openMsgId === u.id ? null : u.id); setMsgText('') }}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-medium"
                  style={{
                    background: 'rgba(255,255,255,0.5)',
                    border: '1px solid rgba(255,255,255,0.7)',
                    color: 'var(--text-primary)',
                  }}
                >
                  <MessageSquare size={13} />
                  Pranešimas
                  <ChevronDown
                    size={13}
                    style={{
                      transform: openMsgId === u.id ? 'rotate(180deg)' : 'rotate(0deg)',
                      transition: 'transform 0.2s',
                    }}
                  />
                </motion.button>
                {u.role !== 'ADMIN' && (
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => handleDelete(u.id)}
                    className="p-1.5 rounded-xl transition-colors"
                    style={{ color: 'var(--text-muted)' }}
                    onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                    onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                  >
                    <Trash2 size={15} />
                  </motion.button>
                )}
              </div>
            </div>

            <AnimatePresence>
              {openMsgId === u.id && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="overflow-hidden"
                >
                  <div className="mt-4 pt-4" style={{ borderTop: '1px solid rgba(255,255,255,0.5)' }}>
                    <textarea
                      className="glass-input mb-2 resize-none"
                      rows={2}
                      placeholder="Pranešimo tekstas..."
                      value={msgText}
                      onChange={(e) => setMsgText(e.target.value)}
                      style={{ paddingTop: '10px' }}
                    />
                    <motion.button
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.97 }}
                      onClick={() => handleSendMsg(u.id)}
                      className="btn-gradient px-5 py-2 text-sm"
                    >
                      Siųsti
                    </motion.button>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </motion.div>
        ))}
      </motion.div>
    </div>
  )
}
