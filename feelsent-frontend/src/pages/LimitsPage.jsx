import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Settings, Trash2 } from 'lucide-react'
import { getAll, setLimit, removeLimit } from '../api/limits'
import { getAll as getFriends } from '../api/friendships'

const selectStyle = {
  background: 'rgba(255,255,255,0.55)',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255,255,255,0.80)',
  borderRadius: '12px',
  padding: '10px 16px',
  fontSize: '0.9rem',
  color: 'var(--text-primary)',
  outline: 'none',
  width: '100%',
  fontFamily: 'inherit',
}

export default function LimitsPage() {
  const [limits, setLimits] = useState([])
  const [friends, setFriends] = useState([])
  const [senderId, setSenderId] = useState('')
  const [dailyLimit, setDailyLimit] = useState(1)
  const [err, setErr] = useState('')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    const [lRes, fRes] = await Promise.all([getAll(), getFriends()])
    setLimits(lRes.data)
    setFriends(fRes.data)
  }

  useEffect(() => { load() }, [])

  const getFriendId = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    return f.senderId === me?.id ? f.receiverId : f.senderId
  }

  const getFriendName = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    return f.senderId === me?.id ? f.receiverFirstName : f.senderFirstName
  }

  const handleSet = async () => {
    if (!senderId) return setErr('Pasirink draugą')
    setErr('')
    setLoading(true)
    try {
      await setLimit(parseInt(senderId), dailyLimit)
      load()
    } catch (err) {
      setErr(err.response?.data?.message || 'Klaida')
    } finally {
      setLoading(false)
    }
  }

  const handleRemove = async (sid) => {
    await removeLimit(sid)
    load()
  }

  return (
    <div className="p-8 max-w-2xl" style={{ paddingLeft: '2.5rem' }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-3 mb-2"
      >
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
        >
          <Settings size={20} color="white" strokeWidth={2} />
        </div>
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Žinučių limitai</h1>
      </motion.div>
      <p className="text-sm mb-8 ml-[52px]" style={{ color: 'var(--text-muted)' }}>
        Riboji kiek žinučių per dieną gali gauti iš konkretaus draugo.
      </p>

      {/* Nustatymo forma */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="glass p-6 mb-6"
      >
        <h3 className="font-semibold text-sm mb-4" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
          Nustatyti limitą
        </h3>
        <select
          style={selectStyle}
          className="mb-3"
          value={senderId}
          onChange={(e) => setSenderId(e.target.value)}
        >
          <option value="">Pasirink draugą</option>
          {friends.map((f) => (
            <option key={f.id} value={getFriendId(f)}>{getFriendName(f)}</option>
          ))}
        </select>
        <div className="flex items-center gap-3 mb-4" style={{ paddingLeft: '5px' }}>
          <label className="text-sm" style={{ color: 'var(--text-primary)' }}>Žinučių per dieną:</label>
          <input
            type="number"
            min={1}
            max={50}
            value={dailyLimit}
            onChange={(e) => setDailyLimit(parseInt(e.target.value))}
            className="glass-input"
            style={{ width: '5rem' }}
          />
        </div>
        {err && <p className="text-sm mb-3" style={{ color: '#be185d' }}>{err}</p>}
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.97 }}
          onClick={handleSet}
          disabled={loading}
          className="btn-gradient w-full py-2.5"
        >
          {loading ? 'Saugoma...' : 'Išsaugoti'}
        </motion.button>
      </motion.div>

      {/* Esami limitai */}
      <div className="flex items-center justify-between mb-3">
        <p className="font-semibold text-sm" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
          Nustatyti limitai ({limits.length})
        </p>
      </div>
      {limits.length === 0 && (
        <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Nėra nustatytų limitų.</p>
      )}
      <AnimatePresence>
        <div className="flex flex-col gap-2">
          {limits.map((l) => (
            <motion.div
              key={l.id}
              layout
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="glass px-5 py-3 flex items-center justify-between"
            >
              <div className="min-w-0 flex-1" style={{ paddingLeft: '5px' }}>
                <p className="font-semibold text-sm truncate" style={{ color: 'var(--text-primary)' }}>
                  {l.senderFirstName}
                </p>
                <p className="text-xs" style={{ color: 'var(--text-muted)' }}>
                  Maks. {l.dailyLimit} žinutė/ų per dieną
                </p>
              </div>
              <motion.button
                whileHover={{ scale: 1.1 }}
                onClick={() => handleRemove(l.senderId)}
                className="p-1.5 rounded-lg transition-colors"
                style={{ color: 'var(--text-muted)' }}
                onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
              >
                <Trash2 size={15} />
              </motion.button>
            </motion.div>
          ))}
        </div>
      </AnimatePresence>
    </div>
  )
}
