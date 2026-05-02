import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Send, ChevronLeft, Heart, Sparkles, RefreshCw } from 'lucide-react'
import { getAll } from '../api/friendships'
import { suggest } from '../api/wishes'
import { getAll as getFavorites, add as addFavorite } from '../api/favorites'
import { send } from '../api/messages'
import { getLimitInfo } from '../api/limits'
import { getMyUnique } from '../api/uniqueWishes'

const SEND_MODES = [
  { value: 'SIMPLE', label: 'Paprastas',  desc: 'Gavėjas iš karto mato tekstą', emoji: '📨' },
  { value: 'GUESS',  label: 'Gavėjas turi atspėti', desc: 'Gavėjas nemato palinkėjimo, turi atspėti palinkėjimo stilių', emoji: '🎭' },
]

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.4, ease: [0.22, 1, 0.36, 1] } },
}

export default function SendPage() {
  const [step, setStep] = useState(1)
  const [friends, setFriends] = useState([])
  const [selectedFriend, setSelectedFriend] = useState(null)
  const [wishCache, setWishCache] = useState([])
  const [wishPage, setWishPage] = useState(0)
  const [favorites, setFavorites] = useState([])
  const [uniqueFavorites, setUniqueFavorites] = useState([])
  const [showFavorites, setShowFavorites] = useState(false)
  const [selectedWish, setSelectedWish] = useState(null)
  const [sendMode, setSendMode] = useState(null)
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')
  const [savedWishes, setSavedWishes] = useState(new Set())
  const [limitInfo, setLimitInfo] = useState(null)

  useEffect(() => {
    getAll().then((res) => setFriends(res.data)).catch(() => {})
  }, [])

  const getLimitLabel = () => {
    if (!limitInfo) return null
    if (!limitInfo.limited) return 'Neapribotas skaičius žinučių'
    return `${limitInfo.remaining} iš ${limitInfo.dailyLimit} žinučių liko šiandien`
  }

  const getLimitColor = () => {
    if (!limitInfo?.limited) return 'var(--text-primary)'
    if (limitInfo.remaining === 0) return '#be185d'
    return 'var(--text-primary)'
  }

  const getMe = () => JSON.parse(localStorage.getItem('user'))

  const getFriendId = (f) => {
    const me = getMe()
    return f.senderId === me?.id ? f.receiverId : f.senderId
  }

  const getFriendName = (f) => {
    const me = getMe()
    return f.senderId === me?.id ? f.receiverFirstName : f.senderFirstName
  }

  const getFriendMood = (f) => {
    const me = getMe()
    return f.senderId === me?.id
      ? (f.receiverMoodStatusLabel || '😶 Nenustatyta')
      : (f.senderMoodStatusLabel || '😶 Nenustatyta')
  }

  const handleSelectFriend = async (friend) => {
    setSelectedFriend(friend)
    setLimitInfo(null)
    setError('')
    setWishPage(0)
    try {
      const friendId = getFriendId(friend)
      const [wRes, fRes, lRes, uRes] = await Promise.all([
        suggest(friendId, 9),
        getFavorites(),
        getLimitInfo(friendId),
        getMyUnique(),
      ])
      setWishCache(wRes.data)
      setFavorites(fRes.data.wishes)
      setSavedWishes(new Set(fRes.data.wishes.map((w) => w.wishId)))
      setLimitInfo(lRes.data)
      setUniqueFavorites(uRes.data.map((u) => ({ ...u, isUnique: true })))
    } catch (err) {
      setError(err.response?.data?.message || 'Nepavyko įkelti palinkėjimų')
    }
    setStep(2)
  }

  const wishes = wishCache.slice(wishPage * 3, wishPage * 3 + 3)
  const maxPage = Math.floor((wishCache.length - 1) / 3)

  const handleSelectWish = (wish) => {
    setSelectedWish(wish)
    setStep(3)
  }

  const handleSave = async (e, wish) => {
    e.stopPropagation()
    const id = wish.id || wish.wishId
    try {
      await addFavorite(id)
      setSavedWishes((prev) => new Set([...prev, id]))
    } catch (err) {
      const msg = err.response?.data?.message || ''
      if (msg.includes('jau išsaugotas')) {
        setSavedWishes((prev) => new Set([...prev, id]))
        return
      }
      alert(msg || `Klaida: ${err.response?.status}`)
    }
  }

  const handleSend = async () => {
    setLoading(true)
    setError('')
    try {
      const friendId = getFriendId(selectedFriend)
      const payload = { receiverId: friendId, sendMode: sendMode || 'SIMPLE' }
      if (selectedWish.isUnique) {
        payload.uniqueWishId = selectedWish.id
      } else {
        payload.wishId = selectedWish.wishId || selectedWish.id
      }
      await send(payload)
      setSuccess(true)
      setStep(1)
      setSelectedFriend(null)
      setSelectedWish(null)
      setSendMode(null)
    } catch (err) {
      setError(err.response?.data?.message || 'Nepavyko išsiųsti')
    } finally {
      setLoading(false)
    }
  }

  if (success) return (
    <div className="p-8 flex items-center justify-center min-h-[60vh]">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="glass text-center py-16 px-12 max-w-sm w-full"
      >
        <div className="text-6xl mb-4">✅</div>
        <h2 className="text-xl font-bold mb-2" style={{ color: 'var(--text-primary)' }}>Išsiųsta!</h2>
        <p className="text-sm mb-6" style={{ color: 'var(--text-muted)' }}>Palinkėjimas išsiųstas sėkmingai.</p>
        <motion.button
          whileHover={{ scale: 1.03 }}
          whileTap={{ scale: 0.97 }}
          onClick={() => setSuccess(false)}
          className="btn-gradient py-2.5"
          style={{ paddingLeft: '5px', paddingRight: '5px' }}
        >
          Siųsti dar
        </motion.button>
      </motion.div>
    </div>
  )

  return (
    <div className="p-8" style={{ paddingLeft: "2.5rem" }}>
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
          <Send size={20} color="white" strokeWidth={2} />
        </div>
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Siųsti palinkėjimą</h1>
      </motion.div>

      {/* Žingsniai */}
      <div className="flex items-center gap-2 mb-8 max-w-xs">
        {[1, 2, 3].map((s) => (
          <div key={s} className={`flex items-center gap-2 ${s < 3 ? 'flex-1' : ''}`}>
            <div
              className="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all"
              style={{
                background: step >= s
                  ? 'linear-gradient(135deg, var(--accent-from), var(--accent-to))'
                  : 'rgba(255,255,255,0.5)',
                color: step >= s ? 'white' : 'var(--text-muted)',
                border: step >= s ? 'none' : '1px solid rgba(255,255,255,0.7)',
              }}
            >
              {s}
            </div>
            {s < 3 && (
              <div
                className="flex-1 h-0.5 rounded-full transition-all"
                style={{ background: step > s ? 'var(--accent-from)' : 'rgba(255,255,255,0.5)' }}
              />
            )}
          </div>
        ))}
      </div>

      <AnimatePresence mode="wait">
        {/* 1 žingsnis — draugo pasirinkimas */}
        {step === 1 && (
          <motion.div key="step1" variants={fadeUp} initial="hidden" animate="show" className="max-w-lg">
            <h2 className="text-lg font-semibold mb-4" style={{ color: 'var(--text-primary)' }}>
              Pasirink draugą
            </h2>
            {friends.length === 0 && (
              <div className="glass text-center py-10 px-6">
                <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
                  Neturi draugų. Eik į Draugai ir pakvieski.
                </p>
              </div>
            )}
            <div className="flex flex-col gap-2">
              {friends.map((f) => (
                <motion.button
                  key={f.id}
                  whileHover={{ y: -2 }}
                  whileTap={{ scale: 0.99 }}
                  onClick={() => handleSelectFriend(f)}
                  className="glass text-left px-5 py-3 flex items-center gap-3"
                >
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
                      {f.relationshipTypeLabel} · {getFriendMood(f)}
                    </p>
                  </div>
                </motion.button>
              ))}
            </div>
          </motion.div>
        )}

        {/* 2 žingsnis — palinkėjimo pasirinkimas */}
        {step === 2 && (
          <motion.div key="step2" variants={fadeUp} initial="hidden" animate="show" className="max-w-lg">
            <div className="flex items-center gap-3 mb-4">
              <button
                onClick={() => setStep(1)}
                className="flex items-center gap-1 text-sm transition-colors"
                style={{ color: 'var(--text-primary)' }}
                onMouseEnter={e => e.currentTarget.style.color = 'var(--accent-from)'}
                onMouseLeave={e => e.currentTarget.style.color = 'var(--text-primary)'}
              >
                <ChevronLeft size={16} /> Atgal
              </button>
              <div>
                <h2 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                  Pasirink palinkėjimą
                </h2>
                {getLimitLabel() && (
                  <p className="text-xs mt-0.5" style={{ color: getLimitColor() }}>
                    {getLimitLabel()}
                  </p>
                )}
              </div>
            </div>

            {/* Tab perjungiklis */}
            <div className="flex gap-2 mb-4">
              {[
                { key: false, label: 'Pasiūlymai', icon: <Sparkles size={14} /> },
                { key: true,  label: `Mano sąrašas (${favorites.length + uniqueFavorites.length})`, icon: <Heart size={14} /> },
              ].map(({ key, label, icon }) => (
                <motion.button
                  key={String(key)}
                  whileTap={{ scale: 0.97 }}
                  onClick={() => setShowFavorites(key)}
                  className="flex items-center gap-1.5 py-1.5 rounded-full text-sm font-medium transition-all"
                  style={showFavorites === key ? {
                    background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
                    color: 'white',
                    paddingLeft: '5px',
                    paddingRight: '5px',
                  } : {
                    background: 'rgba(255,255,255,0.5)',
                    color: 'var(--text-muted)',
                    border: '1px solid rgba(255,255,255,0.7)',
                    paddingLeft: '5px',
                    paddingRight: '5px',
                  }}
                >
                  {icon} {label}
                </motion.button>
              ))}
            </div>

            {error && <p className="text-sm mb-3" style={{ color: '#be185d' }}>{error}</p>}

            <div className="flex flex-col gap-3">
              {showFavorites ? (
                <>
                  {uniqueFavorites.map((w) => (
                    <motion.div
                      key={'u-' + w.id}
                      whileHover={{ y: -2 }}
                      whileTap={{ scale: 0.99 }}
                      className="glass cursor-pointer py-4"
                      style={{ borderLeft: '3px solid var(--accent-to)', paddingLeft: '25px', paddingRight: '20px' }}
                      onClick={() => handleSelectWish(w)}
                    >
                      <p className="font-medium text-sm break-words" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{w.text}</p>
                      <p className="text-xs mt-1" style={{ color: 'var(--accent-to)' }}>Asmeninis palinkėjimas</p>
                    </motion.div>
                  ))}
                  {favorites.map((w) => {
                    const id = w.id || w.wishId
                    return (
                      <motion.div
                        key={id}
                        whileHover={{ y: -2 }}
                        whileTap={{ scale: 0.99 }}
                        className="glass cursor-pointer py-4"
                        style={{ paddingLeft: '25px', paddingRight: '20px' }}
                        onClick={() => handleSelectWish(w)}
                      >
                        <p className="font-medium text-sm break-words" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{w.text}</p>
                      </motion.div>
                    )
                  })}
                  {favorites.length === 0 && uniqueFavorites.length === 0 && (
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Sąrašas tuščias.</p>
                  )}
                </>
              ) : (
                <>
                  {wishes.map((w) => {
                    const id = w.id || w.wishId
                    const saved = savedWishes.has(id)
                    return (
                      <motion.div
                        key={id}
                        whileHover={{ y: -2 }}
                        className="glass cursor-pointer py-4 flex items-center gap-3"
                        style={{ paddingLeft: '25px', paddingRight: '20px' }}
                        onClick={() => handleSelectWish(w)}
                      >
                        <div className="flex-1 min-w-0">
                          <p className="font-medium text-sm break-words" style={{ color: 'var(--text-primary)', wordBreak: 'break-word' }}>{w.text}</p>
                        </div>
                        <motion.button
                          whileHover={{ scale: 1.2 }}
                          whileTap={{ scale: 0.9 }}
                          onClick={(e) => handleSave(e, w)}
                          disabled={saved}
                          className="shrink-0 text-lg"
                          style={{ opacity: saved ? 0.4 : 1 }}
                        >
                          {saved ? '❤️' : '🤍'}
                        </motion.button>
                      </motion.div>
                    )
                  })}
                  {wishes.length === 0 && (
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Nieko nėra.</p>
                  )}
                  {wishPage < maxPage && (
                    <motion.button
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.97 }}
                      onClick={() => setWishPage((p) => p + 1)}
                      className="flex items-center gap-2 self-center text-sm font-medium mt-1"
                      style={{ color: 'var(--accent-from)' }}
                    >
                      <RefreshCw size={14} />
                      Rodyti kitas 3 ({maxPage - wishPage} kart. liko)
                    </motion.button>
                  )}
                </>
              )}
            </div>
          </motion.div>
        )}

        {/* 3 žingsnis — siuntimo režimas */}
        {step === 3 && (
          <motion.div key="step3" variants={fadeUp} initial="hidden" animate="show" className="max-w-lg">
            <div className="flex items-center gap-3 mb-4">
              <button
                onClick={() => setStep(2)}
                className="flex items-center gap-1 text-sm transition-colors"
                style={{ color: 'var(--text-primary)' }}
                onMouseEnter={e => e.currentTarget.style.color = 'var(--accent-from)'}
                onMouseLeave={e => e.currentTarget.style.color = 'var(--text-primary)'}
              >
                <ChevronLeft size={16} /> Atgal
              </button>
              <div>
                <h2 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                  Siuntimo režimas
                </h2>
                {getLimitLabel() && (
                  <p className="text-xs mt-0.5" style={{ color: getLimitColor() }}>
                    {getLimitLabel()}
                  </p>
                )}
              </div>
            </div>

            {/* Pasirinktas palinkėjimas */}
            <div
              className="glass-sm py-4"
              style={{ background: 'rgba(190,24,93,0.06)', paddingLeft: '25px', paddingRight: '20px', marginBottom: '10px' }}
            >
              <p className="font-medium text-sm" style={{ color: 'var(--text-primary)' }}>{selectedWish.text}</p>
            </div>

            <p className="text-sm font-medium mb-3" style={{ color: 'var(--text-primary)' }}>
              Žinutę gali išsiųsti šiais būdais:
            </p>

            <div className="flex flex-col gap-3" style={{ marginBottom: '10px' }}>
              {SEND_MODES.map((m) => (
                <motion.button
                  key={m.value}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => setSendMode(m.value)}
                  className="glass text-left py-4 transition-all"
                  style={sendMode === m.value ? {
                    borderColor: 'var(--accent-from)',
                    background: 'rgba(190,24,93,0.08)',
                    paddingLeft: '25px',
                    paddingRight: '20px',
                  } : {
                    paddingLeft: '25px',
                    paddingRight: '20px',
                  }}
                >
                  <p className="font-semibold text-sm" style={{ color: 'var(--text-primary)' }}>
                    {m.emoji} {m.label}
                  </p>
                  <p className="text-xs mt-1" style={{ color: 'var(--text-primary)' }}>{m.desc}</p>
                </motion.button>
              ))}
            </div>

            {error && <p className="text-sm mb-3" style={{ color: '#be185d' }}>{error}</p>}

            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.97 }}
              onClick={handleSend}
              disabled={loading}
              className="btn-gradient w-full py-3 flex items-center justify-center gap-2"
            >
              <Send size={16} />
              {loading ? 'Siunčiama...' : 'Siųsti'}
            </motion.button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
