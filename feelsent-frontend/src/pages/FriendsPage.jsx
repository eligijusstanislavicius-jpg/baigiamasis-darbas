import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Users, Search, UserPlus, Check, X, Trash2 } from 'lucide-react'
import { getAll, getPending, sendRequest, accept, decline, remove } from '../api/friendships'
import api from '../api/axios'

const REL_TYPES = [
  { value: 'FRIEND',      label: 'Draugas' },
  { value: 'PARTNER',     label: 'Partneris' },
  { value: 'HUSBAND',     label: 'Vyras' },
  { value: 'WIFE',        label: 'Žmona' },
  { value: 'MOTHER',      label: 'Mama' },
  { value: 'FATHER',      label: 'Tėtis' },
  { value: 'SON',         label: 'Sūnus' },
  { value: 'DAUGHTER',    label: 'Duktė' },
  { value: 'BROTHER',     label: 'Brolis' },
  { value: 'SISTER',      label: 'Sesuo' },
  { value: 'GRANDFATHER', label: 'Senelis' },
  { value: 'GRANDMOTHER', label: 'Močiutė' },
]

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.06 } },
}
const item = {
  hidden: { opacity: 0, y: 16 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.35, ease: [0.22, 1, 0.36, 1] } },
}

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

export default function FriendsPage() {
  const [friends, setFriends] = useState([])
  const [pending, setPending] = useState([])
  const [email, setEmail] = useState('')
  const [relType, setRelType] = useState('FRIEND')
  const [searchResult, setSearchResult] = useState(null)
  const [searchErr, setSearchErr] = useState('')
  const [sendErr, setSendErr] = useState('')
  const [loading, setLoading] = useState(false)
  const [acceptTypes, setAcceptTypes] = useState({})

  const load = async () => {
    const [fRes, pRes] = await Promise.all([getAll(), getPending()])
    setFriends(fRes.data)
    setPending(pRes.data)
    const defaults = {}
    pRes.data.forEach((f) => { defaults[f.id] = 'FRIEND' })
    setAcceptTypes((prev) => ({ ...defaults, ...prev }))
  }

  useEffect(() => { load() }, [])

  const handleSearch = async () => {
    setSearchErr('')
    setSearchResult(null)
    try {
      const res = await api.get(`/api/users/search?email=${encodeURIComponent(email)}`)
      setSearchResult(res.data)
    } catch {
      setSearchErr('Vartotojas nerastas')
    }
  }

  const handleSend = async () => {
    if (!searchResult) return
    setSendErr('')
    setLoading(true)
    try {
      await sendRequest(searchResult.id, relType)
      setEmail('')
      setSearchResult(null)
      alert('Draugystės užklausa išsiųsta!')
    } catch (err) {
      setSendErr(err.response?.data?.message || 'Klaida')
    } finally {
      setLoading(false)
    }
  }

  const handleAccept = async (id) => {
    const rel = acceptTypes[id] || 'FRIEND'
    try {
      await accept(id, rel)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Nepavyko priimti')
    }
  }

  const handleDecline = async (id) => {
    await decline(id)
    load()
  }

  const handleRemove = async (id) => {
    if (!confirm('Tikrai pašalinti draugą?')) return
    try {
      await remove(id)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Nepavyko pašalinti draugo')
    }
  }

  const getMe = () => JSON.parse(localStorage.getItem('user'))

  const getFriendName = (f) => {
    const me = getMe()
    return f.senderId === me?.id ? f.receiverFirstName : f.senderFirstName
  }

  const getMyRelLabel = (f) => {
    const me = getMe()
    return f.senderId === me?.id ? f.senderRelationshipTypeLabel : f.receiverRelationshipTypeLabel
  }

  const getFriendMood = (f) => {
    const me = getMe()
    return f.senderId === me?.id
      ? (f.receiverMoodStatusLabel || '😶 Nenustatyta')
      : (f.senderMoodStatusLabel || '😶 Nenustatyta')
  }

  return (
    <div className="p-8 max-w-2xl" style={{ paddingLeft: "2.5rem" }}>
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
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Draugai</h1>
      </motion.div>

      {/* Pakvietimo forma */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="glass p-6 mb-6"
      >
        <div className="flex items-center gap-2 mb-4" style={{ paddingLeft: '5px' }}>
          <UserPlus size={16} style={{ color: 'var(--accent-from)' }} />
          <h3 className="font-semibold text-sm" style={{ color: 'var(--text-primary)' }}>Pakviesti draugą</h3>
        </div>
        <div className="flex gap-2 mb-3">
          <input
            className="glass-input flex-1"
            placeholder="El. paštas"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
          <motion.button
            whileHover={{ y: -1 }}
            whileTap={{ scale: 0.97 }}
            onClick={handleSearch}
            className="flex items-center gap-1.5 px-4 py-2 rounded-xl text-sm font-medium"
            style={{
              background: 'rgba(255,255,255,0.6)',
              border: '1px solid rgba(255,255,255,0.8)',
              color: 'var(--text-primary)',
            }}
          >
            <Search size={15} /> Ieškoti
          </motion.button>
        </div>
        {searchErr && <p className="text-sm mb-2" style={{ color: '#be185d' }}>{searchErr}</p>}
        {searchResult && (
          <motion.div
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            className="glass-sm px-4 py-3 mb-3"
            style={{ background: 'rgba(190,24,93,0.06)' }}
          >
            <p className="font-medium text-sm" style={{ color: 'var(--text-primary)' }}>
              {searchResult.firstName} {searchResult.lastName}
            </p>
          </motion.div>
        )}
        <label className="block text-xs mb-1" style={{ color: 'var(--text-muted)' }}>
          Kas šis žmogus man yra?
        </label>
        <select style={selectStyle} className="mb-3" value={relType} onChange={(e) => setRelType(e.target.value)}>
          {REL_TYPES.map((r) => (
            <option key={r.value} value={r.value}>{r.label}</option>
          ))}
        </select>
        {sendErr && <p className="text-sm mb-2" style={{ color: '#be185d' }}>{sendErr}</p>}
        <motion.button
          whileHover={{ y: -1 }}
          whileTap={{ scale: 0.97 }}
          onClick={handleSend}
          disabled={!searchResult || loading}
          className="btn-gradient w-full py-2.5"
        >
          Siųsti užklausą
        </motion.button>
      </motion.div>

      {/* Laukiančios užklausos */}
      <AnimatePresence>
        {pending.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            className="mb-6"
            style={{ marginTop: '10px' }}
          >
            <p
              className="text-xs font-bold uppercase tracking-wide mb-3"
              style={{ color: 'var(--accent-from)' }}
            >
              ⏳ Laukia patvirtinimo ({pending.length})
            </p>
            <div className="flex flex-col gap-2">
              {pending.map((f) => (
                <motion.div
                  key={f.id}
                  layout
                  className="glass p-4"
                  style={{ borderLeft: '3px solid var(--accent-from)', paddingLeft: '5px' }}
                >
                  <div className="flex items-center justify-between mb-2">
                    <div style={{ paddingLeft: '10px' }}>
                      <p className="font-semibold text-sm" style={{ color: 'var(--text-primary)' }}>
                        {f.senderFirstName} {f.senderLastName}
                      </p>
                      <p className="text-xs" style={{ color: 'var(--text-muted)' }}>
                        nori prisijungti kaip: {f.senderRelationshipTypeLabel}
                      </p>
                    </div>
                    <motion.button
                      whileHover={{ scale: 1.1 }}
                      onClick={() => handleDecline(f.id)}
                      className="p-1.5 rounded-lg"
                      style={{ color: '#be185d' }}
                    >
                      <X size={15} />
                    </motion.button>
                  </div>
                  <label className="block text-xs mb-1" style={{ color: 'var(--text-muted)' }}>
                    Kas man yra šis žmogus?
                  </label>
                  <div className="flex gap-2">
                    <select
                      style={{ ...selectStyle, flex: 1 }}
                      value={acceptTypes[f.id] || 'FRIEND'}
                      onChange={(e) => setAcceptTypes((prev) => ({ ...prev, [f.id]: e.target.value }))}
                    >
                      {REL_TYPES.map((r) => (
                        <option key={r.value} value={r.value}>{r.label}</option>
                      ))}
                    </select>
                    <motion.button
                      whileHover={{ scale: 1.03 }}
                      whileTap={{ scale: 0.97 }}
                      onClick={() => handleAccept(f.id)}
                      className="btn-gradient flex items-center gap-1.5"
                      style={{ padding: '5px' }}
                    >
                      <Check size={14} /> Priimti
                    </motion.button>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Draugų sąrašas */}
      <div className="flex items-center justify-between mb-3" style={{ marginTop: '10px' }}>
        <p className="font-semibold text-sm" style={{ color: 'var(--text-primary)' }}>
          Draugai ({friends.length})
        </p>
      </div>
      {friends.length === 0 && (
        <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Kol kas draugų nėra.</p>
      )}
      <motion.div className="flex flex-col gap-2" variants={stagger} initial="hidden" animate="show">
        {friends.map((f) => (
          <motion.div
            key={f.id}
            variants={item}
            layout
            className="glass py-3 flex items-center justify-between"
            style={{ paddingLeft: '1px', paddingRight: '30px' }}
          >
            <div className="flex items-center gap-3 min-w-0 flex-1">
              <div
                className="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold text-white shrink-0"
                style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
              >
                {getFriendName(f)?.[0]?.toUpperCase()}
              </div>
              <div className="min-w-0">
                <p className="font-semibold text-sm truncate" style={{ color: 'var(--text-primary)' }}>
                  {getFriendName(f)}
                </p>
                <p className="text-xs truncate" style={{ color: 'var(--text-muted)' }}>
                  {getMyRelLabel(f)} · {getFriendMood(f)}
                </p>
              </div>
            </div>
            <motion.button
              whileHover={{ scale: 1.1 }}
              onClick={() => handleRemove(f.id)}
              className="p-1.5 rounded-lg transition-colors"
              style={{ color: 'var(--text-muted)' }}
              onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
              onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
            >
              <Trash2 size={15} />
            </motion.button>
          </motion.div>
        ))}
      </motion.div>
    </div>
  )
}
