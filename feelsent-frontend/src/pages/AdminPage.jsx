import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Shield, BookOpen, Sparkles, Send, Users, Trash2 } from 'lucide-react'
import { getUsers, deleteUser, getWishes, addWish, deactivateWish, notifyAll } from '../api/admin'
import { getAllUnique, createUnique, updateUnique, assignUnique } from '../api/uniqueWishes'

const TONE_OPTIONS = [
  { value: 'SUPPORTIVE', label: 'Palaikantis' },
  { value: 'FUNNY',      label: 'Juokingas' },
  { value: 'ROMANTIC',   label: 'Romantiškas' },
  { value: 'BIRTHDAY',   label: 'Gimtadieninis' },
]

const RELATIONSHIP_OPTIONS = [
  { value: 'ALL',         label: 'Visi' },
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

const REL_LABEL = Object.fromEntries(RELATIONSHIP_OPTIONS.map(r => [r.value, r.label]))

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

const textareaStyle = {
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
  resize: 'none',
}

const TABS = [
  { key: 'notify',  label: 'Pranešimas visiems', Icon: Send },
  { key: 'wishes',  label: 'Palinkėjimai',        Icon: BookOpen },
  { key: 'unique',  label: 'Unikalūs',            Icon: Sparkles },
  { key: 'users',   label: 'Vartotojai',          Icon: Users },
]

function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null

  const pages = []
  for (let i = 0; i < totalPages; i++) {
    if (totalPages <= 7 || i === 0 || i === totalPages - 1 || Math.abs(i - page) <= 1) {
      pages.push(i)
    } else if (pages[pages.length - 1] !== '...') {
      pages.push('...')
    }
  }

  return (
    <div className="flex items-center justify-center gap-1 mt-4 flex-wrap">
      <button
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
        className="px-2 py-1 rounded-lg text-sm transition-colors"
        style={{ color: page === 0 ? 'var(--text-muted)' : 'var(--accent-from)', opacity: page === 0 ? 0.4 : 1 }}
      >
        ←
      </button>
      {pages.map((p, i) =>
        p === '...' ? (
          <span key={`dots-${i}`} className="px-1 text-sm" style={{ color: 'var(--text-muted)' }}>...</span>
        ) : (
          <button
            key={p}
            onClick={() => onChange(p)}
            className="w-8 h-8 rounded-lg text-sm font-medium transition-all"
            style={p === page ? {
              background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
              color: 'white',
            } : {
              background: 'rgba(255,255,255,0.5)',
              color: 'var(--text-primary)',
            }}
          >
            {p + 1}
          </button>
        )
      )}
      <button
        disabled={page === totalPages - 1}
        onClick={() => onChange(page + 1)}
        className="px-2 py-1 rounded-lg text-sm transition-colors"
        style={{ color: page === totalPages - 1 ? 'var(--text-muted)' : 'var(--accent-from)', opacity: page === totalPages - 1 ? 0.4 : 1 }}
      >
        →
      </button>
    </div>
  )
}

export default function AdminPage({ defaultTab = 'notify' }) {
  const [users, setUsers] = useState([])
  const [wishes, setWishes] = useState([])
  const [wishPage, setWishPage] = useState(0)
  const [wishTotalPages, setWishTotalPages] = useState(1)
  const [uniqueWishes, setUniqueWishes] = useState([])
  const [uniquePage, setUniquePage] = useState(0)
  const [uniqueTotalPages, setUniqueTotalPages] = useState(1)
  const [tab, setTab] = useState(defaultTab)
  const [newWish, setNewWish] = useState({ text: '', tone: 'SUPPORTIVE', relationshipType: 'FRIEND' })
  const [wishErr, setWishErr] = useState('')
  const [notifyAllText, setNotifyAllText] = useState('')

  const [newUniqueText, setNewUniqueText] = useState('')
  const [newUniqueUserId, setNewUniqueUserId] = useState('')
  const [newUniqueExpiry, setNewUniqueExpiry] = useState('')
  const [newUniquePermanent, setNewUniquePermanent] = useState(false)
  const [uniqueErr, setUniqueErr] = useState('')
  const [editingUniqueId, setEditingUniqueId] = useState(null)
  const [editingUniqueText, setEditingUniqueText] = useState('')
  const [assigningId, setAssigningId] = useState(null)
  const [assignUserId, setAssignUserId] = useState('')
  const [assignExpiry, setAssignExpiry] = useState('')
  const [assignPermanent, setAssignPermanent] = useState(false)

  const loadWishes = async (p = wishPage) => {
    const res = await getWishes(p)
    setWishes(res.data.content)
    setWishTotalPages(res.data.totalPages)
  }

  const loadUniqueWishes = async (p = uniquePage) => {
    const res = await getAllUnique(p)
    setUniqueWishes(res.data.content)
    setUniqueTotalPages(res.data.totalPages)
  }

  const load = async () => {
    const [uRes] = await Promise.all([getUsers()])
    setUsers(uRes.data)
    await Promise.all([loadWishes(wishPage), loadUniqueWishes(uniquePage)])
  }

  useEffect(() => { load() }, [])

  const regularUsers = users.filter(u => u.role === 'USER')

  const handleDeleteUser = async (id) => {
    if (!confirm('Tikrai ištrinti vartotoją?')) return
    try {
      await deleteUser(id)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleNotifyAll = async () => {
    if (!notifyAllText.trim()) return
    try {
      await notifyAll(notifyAllText)
      setNotifyAllText('')
      alert('Pranešimas išsiųstas visiems vartotojams')
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleAddWish = async () => {
    setWishErr('')
    try {
      await addWish(newWish)
      setNewWish({ text: '', tone: 'SUPPORTIVE', relationshipType: 'FRIEND' })
      loadWishes(wishPage)
    } catch (err) {
      setWishErr(err.response?.data?.message || 'Klaida')
    }
  }

  const handleDeactivate = async (id) => {
    await deactivateWish(id)
    loadWishes(wishPage)
  }

  const handleCreateUnique = async () => {
    if (!newUniqueText.trim()) return setUniqueErr('Tekstas negali būti tuščias')
    setUniqueErr('')
    try {
      const data = { text: newUniqueText }
      if (newUniqueUserId) data.userId = parseInt(newUniqueUserId)
      if (newUniqueUserId && !newUniquePermanent && newUniqueExpiry) data.expiresAt = newUniqueExpiry + ':00'
      await createUnique(data)
      setNewUniqueText('')
      setNewUniqueUserId('')
      setNewUniqueExpiry('')
      setNewUniquePermanent(false)
      loadUniqueWishes(uniquePage)
    } catch (err) {
      setUniqueErr(err.response?.data?.message || 'Klaida')
    }
  }

  const handleUpdateUnique = async (id) => {
    if (!editingUniqueText.trim()) return
    try {
      await updateUnique(id, editingUniqueText)
      setEditingUniqueId(null)
      setEditingUniqueText('')
      loadUniqueWishes(uniquePage)
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleAssignUnique = async (id) => {
    if (!assignUserId) return alert('Pasirink vartotoją')
    try {
      const data = { userId: parseInt(assignUserId) }
      if (!assignPermanent && assignExpiry) data.expiresAt = assignExpiry + ':00'
      await assignUnique(id, data)
      setAssigningId(null)
      setAssignUserId('')
      setAssignExpiry('')
      setAssignPermanent(false)
      loadUniqueWishes(uniquePage)
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const expiryLabel = (expiresAt) => {
    if (!expiresAt) return 'Iki ištrynimo'
    const days = Math.floor((new Date(expiresAt) - Date.now()) / 86400000)
    if (days < 0) return 'Pasibaigęs'
    if (days === 0) return 'Baigiasi šiandien'
    return `Dar ${days} d.`
  }

  return (
    <div className="p-8 max-w-4xl" style={{ paddingLeft: '2.5rem' }}>
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
          <Shield size={20} color="white" strokeWidth={2} />
        </div>
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Administravimas</h1>
      </motion.div>

      {/* Tab mygtukai */}
      <div className="flex gap-2 flex-wrap" style={{ marginBottom: '10px', marginTop: '12px' }}>
        {TABS.map(({ key, label, Icon }) => (
          <motion.button
            key={key}
            whileTap={{ scale: 0.97 }}
            onClick={() => setTab(key)}
            className="flex items-center justify-center gap-1.5 py-3 rounded-xl text-sm font-medium transition-all"
            style={tab === key ? {
              background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
              color: 'white',
              paddingLeft: '3px',
              paddingRight: '3px',
            } : {
              background: 'rgba(255,255,255,0.5)',
              border: '1px solid rgba(255,255,255,0.7)',
              color: 'var(--text-muted)',
              paddingLeft: '3px',
              paddingRight: '3px',
            }}
          >
            <Icon size={14} />
            {label}
          </motion.button>
        ))}
      </div>

      <AnimatePresence mode="wait">
        {/* Pranešimas visiems */}
        {tab === 'notify' && (
          <motion.div
            key="notify"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
          >
            <div className="glass p-5">
              <h3 className="font-semibold text-sm mb-3" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
                Pranešimas visiems vartotojams
              </h3>
              <p className="text-xs mb-3" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
                Pranešimas bus išsiųstas visiems registruotiems vartotojams.
              </p>
              <textarea
                style={{ ...textareaStyle, marginLeft: '5px', width: 'calc(100% - 5px)' }}
                className="mb-3"
                rows={3}
                placeholder="Pranešimo tekstas..."
                value={notifyAllText}
                onChange={(e) => setNotifyAllText(e.target.value)}
              />
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.97 }}
                onClick={handleNotifyAll}
                className="btn-gradient flex items-center gap-2 py-2"
                style={{ marginLeft: '5px', paddingLeft: '8px', paddingRight: '8px' }}
              >
                <Send size={14} /> Siųsti visiems
              </motion.button>
            </div>
          </motion.div>
        )}

        {/* Palinkėjimai */}
        {tab === 'wishes' && (
          <motion.div
            key="wishes"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
          >
            <div className="glass p-5" style={{ paddingLeft: '25px', marginBottom: '10px' }}>
              <h3 className="font-semibold text-sm mb-3" style={{ color: 'var(--text-primary)' }}>
                Pridėti palinkėjimą
              </h3>
              <textarea
                style={textareaStyle}
                className="mb-3"
                rows={2}
                placeholder="Palinkėjimo tekstas..."
                value={newWish.text}
                onChange={(e) => setNewWish({ ...newWish, text: e.target.value })}
              />
              <div className="flex gap-2 mb-3">
                <div className="flex-1">
                  <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>Stilius</label>
                  <select style={selectStyle} value={newWish.tone} onChange={(e) => setNewWish({ ...newWish, tone: e.target.value })}>
                    {TONE_OPTIONS.map((t) => (
                      <option key={t.value} value={t.value}>{t.label}</option>
                    ))}
                  </select>
                </div>
                <div className="flex-1">
                  <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>Ryšio tipas</label>
                  <select style={selectStyle} value={newWish.relationshipType} onChange={(e) => setNewWish({ ...newWish, relationshipType: e.target.value })}>
                    {RELATIONSHIP_OPTIONS.map((r) => (
                      <option key={r.value} value={r.value}>{r.label}</option>
                    ))}
                  </select>
                </div>
              </div>
              {wishErr && <p className="text-xs mb-2" style={{ color: '#be185d' }}>{wishErr}</p>}
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.97 }}
                onClick={handleAddWish}
                className="btn-gradient py-2"
                style={{ paddingLeft: '5px', paddingRight: '5px' }}
              >
                Pridėti
              </motion.button>
            </div>

            <div className="flex flex-col gap-2">
              {wishes.map((w) => (
                <div key={w.id} className="glass py-3 flex items-start justify-between gap-3" style={{ paddingLeft: '25px', paddingRight: '25px' }}>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium break-words" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{w.text}</p>
                    <p className="text-xs mt-0.5 truncate" style={{ color: 'var(--text-muted)' }}>
                      {w.toneLabel} · {REL_LABEL[w.relationshipType] ?? w.relationshipType}
                    </p>
                  </div>
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    onClick={() => handleDeactivate(w.id)}
                    className="text-xs shrink-0"
                    style={{ color: 'var(--text-muted)' }}
                    onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                    onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                  >
                    Deaktyvuoti
                  </motion.button>
                </div>
              ))}
            </div>
            <Pagination page={wishPage} totalPages={wishTotalPages} onChange={(p) => { setWishPage(p); loadWishes(p) }} />
          </motion.div>
        )}

        {/* Unikalūs palinkėjimai */}
        {tab === 'unique' && (
          <motion.div
            key="unique"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
          >
            <div className="glass p-5" style={{ paddingLeft: '25px', marginBottom: '12px' }}>
              <h3 className="font-semibold text-sm mb-3" style={{ color: 'var(--text-primary)' }}>
                Sukurti unikalų palinkėjimą
              </h3>
              <textarea
                style={textareaStyle}
                className="mb-3"
                rows={2}
                placeholder="Palinkėjimo tekstas..."
                value={newUniqueText}
                onChange={(e) => setNewUniqueText(e.target.value)}
              />
              <div className="flex gap-2 mb-3">
                <div className="flex-1">
                  <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>
                    Priskirti vartotojui (nebūtina)
                  </label>
                  <select
                    style={selectStyle}
                    value={newUniqueUserId}
                    onChange={(e) => setNewUniqueUserId(e.target.value)}
                  >
                    <option value="">Nepriskirti</option>
                    {regularUsers.map((u) => (
                      <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>
                    ))}
                  </select>
                </div>
                {newUniqueUserId && (
                  <div className="flex-1">
                    <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>Galiojimas</label>
                    <label className="flex items-center gap-2 text-sm mb-1" style={{ color: 'var(--text-primary)' }}>
                      <input type="checkbox" checked={newUniquePermanent} onChange={(e) => setNewUniquePermanent(e.target.checked)} />
                      Iki ištrynimo
                    </label>
                    {!newUniquePermanent && (
                      <div className="flex gap-1 items-center flex-wrap">
                        <input
                          type="date"
                          className="glass-input"
                          style={{ minWidth: 0, flex: 1 }}
                          value={newUniqueExpiry.split('T')[0] || ''}
                          onChange={(e) => setNewUniqueExpiry(e.target.value + 'T' + (newUniqueExpiry.split('T')[1] || '00:00'))}
                        />
                        <select
                          className="glass-input"
                          style={{ width: '60px', paddingLeft: '6px', paddingRight: '4px' }}
                          value={newUniqueExpiry.split('T')[1]?.split(':')[0] || '00'}
                          onChange={(e) => setNewUniqueExpiry((newUniqueExpiry.split('T')[0] || '') + 'T' + e.target.value + ':' + (newUniqueExpiry.split('T')[1]?.split(':')[1] || '00'))}
                        >
                          {Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0')).map(h => <option key={h} value={h}>{h}</option>)}
                        </select>
                        <span style={{ color: 'var(--text-muted)', fontWeight: 'bold' }}>:</span>
                        <select
                          className="glass-input"
                          style={{ width: '60px', paddingLeft: '6px', paddingRight: '4px' }}
                          value={newUniqueExpiry.split('T')[1]?.split(':')[1] || '00'}
                          onChange={(e) => setNewUniqueExpiry((newUniqueExpiry.split('T')[0] || '') + 'T' + (newUniqueExpiry.split('T')[1]?.split(':')[0] || '00') + ':' + e.target.value)}
                        >
                          {['00','05','10','15','20','25','30','35','40','45','50','55'].map(m => <option key={m} value={m}>{m}</option>)}
                        </select>
                      </div>
                    )}
                  </div>
                )}
              </div>
              {uniqueErr && <p className="text-xs mb-2" style={{ color: '#be185d' }}>{uniqueErr}</p>}
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.97 }}
                onClick={handleCreateUnique}
                className="btn-gradient py-2"
                style={{ paddingLeft: '5px', paddingRight: '5px' }}
              >
                Sukurti
              </motion.button>
            </div>

            <div className="flex flex-col gap-3">
              {uniqueWishes.length === 0 && (
                <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Nėra unikalių palinkėjimų.</p>
              )}
              {uniqueWishes.map((w) => (
                <div key={w.id} className="glass p-4" style={{ paddingLeft: '25px' }}>
                  {editingUniqueId === w.id ? (
                    <div className="flex gap-2 mb-3">
                      <input
                        className="glass-input flex-1"
                        value={editingUniqueText}
                        onChange={(e) => setEditingUniqueText(e.target.value)}
                      />
                      <motion.button
                        whileTap={{ scale: 0.97 }}
                        onClick={() => handleUpdateUnique(w.id)}
                        className="btn-gradient px-3 py-1.5 text-sm"
                      >
                        Išsaugoti
                      </motion.button>
                      <button
                        onClick={() => setEditingUniqueId(null)}
                        className="text-sm px-2"
                        style={{ color: 'var(--text-muted)' }}
                      >
                        Atšaukti
                      </button>
                    </div>
                  ) : (
                    <div className="flex items-center justify-between gap-3 mb-2">
                      <p className="text-sm font-medium flex-1" style={{ color: 'var(--text-primary)' }}>{w.text}</p>
                      <button
                        onClick={() => { setEditingUniqueId(w.id); setEditingUniqueText(w.text) }}
                        className="text-xs shrink-0"
                        style={{ color: 'var(--text-muted)', paddingRight: '10px' }}
                        onMouseEnter={e => e.currentTarget.style.color = 'var(--accent-from)'}
                        onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                      >
                        Redaguoti
                      </button>
                    </div>
                  )}

                  {w.assignments?.length > 0 && (
                    <div className="flex flex-wrap gap-2 mb-2">
                      {w.assignments.map((a) => (
                        <span
                          key={a.userUniqueWishId}
                          className="text-xs px-2 py-0.5 rounded-full"
                          style={{ background: 'rgba(255,255,255,0.5)', color: 'var(--text-muted)' }}
                        >
                          {a.firstName} {a.lastName} · {expiryLabel(a.expiresAt)}
                        </span>
                      ))}
                    </div>
                  )}

                  {assigningId === w.id ? (
                    <div
                      className="pt-3 mt-2 flex flex-wrap gap-2 items-end"
                      style={{ borderTop: '1px solid rgba(255,255,255,0.5)' }}
                    >
                      <div>
                        <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>Vartotojas</label>
                        <select
                          style={{ ...selectStyle, width: 'auto' }}
                          value={assignUserId}
                          onChange={(e) => setAssignUserId(e.target.value)}
                        >
                          <option value="">Pasirink</option>
                          {regularUsers.map((u) => (
                            <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label className="text-xs mb-1 block" style={{ color: 'var(--text-primary)' }}>Galiojimas</label>
                        <label className="flex items-center gap-1 text-xs mb-1" style={{ color: 'var(--text-primary)' }}>
                          <input type="checkbox" checked={assignPermanent} onChange={(e) => setAssignPermanent(e.target.checked)} />
                          Iki ištrynimo
                        </label>
                        {!assignPermanent && (
                          <div className="flex gap-1 items-center flex-wrap">
                            <input
                              type="date"
                              className="glass-input"
                              style={{ padding: '6px 8px', fontSize: '0.8rem', minWidth: 0, flex: 1 }}
                              value={assignExpiry.split('T')[0] || ''}
                              onChange={(e) => setAssignExpiry(e.target.value + 'T' + (assignExpiry.split('T')[1] || '00:00'))}
                            />
                            <select
                              className="glass-input"
                              style={{ padding: '6px 4px', fontSize: '0.8rem', width: '55px' }}
                              value={assignExpiry.split('T')[1]?.split(':')[0] || '00'}
                              onChange={(e) => setAssignExpiry((assignExpiry.split('T')[0] || '') + 'T' + e.target.value + ':' + (assignExpiry.split('T')[1]?.split(':')[1] || '00'))}
                            >
                              {Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0')).map(h => <option key={h} value={h}>{h}</option>)}
                            </select>
                            <span style={{ color: 'var(--text-muted)', fontWeight: 'bold' }}>:</span>
                            <select
                              className="glass-input"
                              style={{ padding: '6px 4px', fontSize: '0.8rem', width: '55px' }}
                              value={assignExpiry.split('T')[1]?.split(':')[1] || '00'}
                              onChange={(e) => setAssignExpiry((assignExpiry.split('T')[0] || '') + 'T' + (assignExpiry.split('T')[1]?.split(':')[0] || '00') + ':' + e.target.value)}
                            >
                              {['00','05','10','15','20','25','30','35','40','45','50','55'].map(m => <option key={m} value={m}>{m}</option>)}
                            </select>
                          </div>
                        )}
                      </div>
                      <motion.button
                        whileTap={{ scale: 0.97 }}
                        onClick={() => handleAssignUnique(w.id)}
                        className="btn-gradient px-3 py-1.5 text-sm"
                      >
                        Priskirti
                      </motion.button>
                      <button
                        onClick={() => setAssigningId(null)}
                        className="text-sm px-2"
                        style={{ color: 'var(--text-muted)' }}
                      >
                        Atšaukti
                      </button>
                    </div>
                  ) : (
                    <motion.button
                      whileHover={{ scale: 1.02 }}
                      onClick={() => { setAssigningId(w.id); setAssignUserId(''); setAssignExpiry(''); setAssignPermanent(false) }}
                      className="text-xs font-medium px-3 py-1.5 rounded-xl mt-1"
                      style={{
                        color: 'var(--accent-from)',
                        border: '1px solid rgba(190,24,93,0.3)',
                        background: 'rgba(190,24,93,0.06)',
                      }}
                    >
                      + Priskirti vartotojui
                    </motion.button>
                  )}
                </div>
              ))}
            </div>
            <Pagination page={uniquePage} totalPages={uniqueTotalPages} onChange={(p) => { setUniquePage(p); loadUniqueWishes(p) }} />
          </motion.div>
        )}
        {/* Vartotojai */}
        {tab === 'users' && (
          <motion.div
            key="users"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
          >
            <p className="text-xs mb-3" style={{ color: 'var(--text-muted)' }}>
              Viso vartotojų: {users.filter(u => u.role === 'USER').length}
            </p>
            <div className="flex flex-col gap-2">
              {users.filter(u => u.role === 'USER').map((u) => (
                <div
                  key={u.id}
                  className="glass py-3 flex items-center justify-between"
                  style={{ paddingLeft: '20px', paddingRight: '20px' }}
                >
                  <div className="flex items-center gap-3 min-w-0 flex-1">
                    <div
                      className="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold text-white shrink-0"
                      style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
                    >
                      {u.firstName?.[0]?.toUpperCase()}
                    </div>
                    <div className="min-w-0">
                      <p className="font-semibold text-sm truncate" style={{ color: 'var(--text-primary)' }}>
                        {u.firstName} {u.lastName}
                      </p>
                      <p className="text-xs truncate" style={{ color: 'var(--text-muted)' }}>{u.email}</p>
                    </div>
                  </div>
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    onClick={() => handleDeleteUser(u.id)}
                    className="p-1.5 rounded-lg transition-colors shrink-0"
                    style={{ color: 'var(--text-muted)' }}
                    onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
                    onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
                  >
                    <Trash2 size={15} />
                  </motion.button>
                </div>
              ))}
              {users.filter(u => u.role === 'USER').length === 0 && (
                <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Nėra vartotojų.</p>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
