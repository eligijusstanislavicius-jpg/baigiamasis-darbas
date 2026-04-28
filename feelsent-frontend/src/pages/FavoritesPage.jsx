import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Heart, Trash2 } from 'lucide-react'
import { getAll, remove } from '../api/favorites'
import { getMyUnique, removeMyUnique } from '../api/uniqueWishes'

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.06 } },
}
const item = {
  hidden: { opacity: 0, y: 16 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.35, ease: [0.22, 1, 0.36, 1] } },
  exit:   { opacity: 0, scale: 0.95, transition: { duration: 0.2 } },
}

export default function FavoritesPage() {
  const [data, setData] = useState(null)
  const [uniqueWishes, setUniqueWishes] = useState([])

  const load = async () => {
    const [fRes, uRes] = await Promise.all([getAll(), getMyUnique()])
    setData(fRes.data)
    setUniqueWishes(uRes.data)
  }

  useEffect(() => { load() }, [])

  const expiryLabel = (expiresAt) => {
    if (!expiresAt) return 'Galioja iki ištrynimo'
    const days = Math.floor((new Date(expiresAt) - Date.now()) / 86400000)
    if (days < 0) return 'Pasibaigęs'
    if (days === 0) return 'Baigiasi šiandien'
    return `Galioja dar ${days} d.`
  }

  const handleRemove = async (id) => {
    await remove(id)
    load()
  }

  const handleRemoveUnique = async (id) => {
    await removeMyUnique(id)
    load()
  }

  if (!data) return (
    <div className="p-8 flex items-center gap-3" style={{ color: 'var(--text-muted)' }}>
      <div className="w-5 h-5 rounded-full border-2 animate-spin"
        style={{ borderColor: 'var(--accent-from)', borderTopColor: 'transparent' }} />
      Kraunama...
    </div>
  )

  const total = (data.wishes?.length || 0) + uniqueWishes.length

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
            <Heart size={20} color="white" strokeWidth={2} />
          </div>
          <div>
            <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>
              Mano palinkėjimų sąrašas
            </h1>
            <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
              {total} / {data.max} palinkėjimų
            </p>
          </div>
        </div>
      </motion.div>

      {total === 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass text-center py-16 px-8"
        >
          <div className="text-5xl mb-4">🤍</div>
          <p className="font-semibold" style={{ color: 'var(--text-primary)' }}>Sąrašas tuščias</p>
          <p className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>
            Siunčiant žinutę galite išsaugoti mėgstamus palinkėjimus
          </p>
        </motion.div>
      )}

      {/* Asmeniniai palinkėjimai */}
      <AnimatePresence>
        {uniqueWishes.length > 0 && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="mb-6">
            <p
              className="text-xs font-bold uppercase tracking-wide mb-3"
              style={{ color: 'var(--accent-to)' }}
            >
              Asmeniniai palinkėjimai
            </p>
            <motion.div className="flex flex-col gap-2" variants={stagger} initial="hidden" animate="show">
              {uniqueWishes.map((u) => (
                <motion.div
                  key={u.id}
                  variants={item}
                  exit="exit"
                  layout
                  className="glass py-4 flex items-center justify-between"
                  style={{ borderLeft: '3px solid var(--accent-to)', paddingLeft: '20px', paddingRight: '30px' }}
                >
                  <div className="min-w-0 flex-1" style={{ paddingLeft: '5px' }}>
                    <p className="font-medium text-sm" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{u.text}</p>
                    <p className="text-xs mt-0.5" style={{ color: 'var(--accent-to)' }}>
                      {expiryLabel(u.expiresAt)}
                    </p>
                  </div>
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    onClick={() => handleRemoveUnique(u.id)}
                    className="ml-4 p-1.5 rounded-lg transition-colors shrink-0"
                    style={{ color: 'var(--text-muted)' }}
                    onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                    onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                  >
                    <Trash2 size={15} />
                  </motion.button>
                </motion.div>
              ))}
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Bendri palinkėjimai */}
      <AnimatePresence>
        {data.wishes?.length > 0 && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            {uniqueWishes.length > 0 && (
              <p
                className="text-xs font-bold uppercase tracking-wide mb-3"
                style={{ color: 'var(--text-muted)' }}
              >
                Bendri palinkėjimai
              </p>
            )}
            <motion.div className="flex flex-col gap-2" variants={stagger} initial="hidden" animate="show">
              {data.wishes.map((w) => (
                <motion.div
                  key={w.id}
                  variants={item}
                  exit="exit"
                  layout
                  className="glass py-4 flex items-center justify-between"
                  style={{ paddingLeft: '20px', paddingRight: '30px' }}
                >
                  <div className="min-w-0 flex-1" style={{ paddingLeft: '5px' }}>
                    <p className="font-medium text-sm" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{w.text}</p>
                  </div>
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    onClick={() => handleRemove(w.id)}
                    className="ml-4 p-1.5 rounded-lg transition-colors shrink-0"
                    style={{ color: 'var(--text-muted)' }}
                    onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                    onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                  >
                    <Trash2 size={15} />
                  </motion.button>
                </motion.div>
              ))}
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
