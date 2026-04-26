import { useEffect, useState } from 'react'
import { getAll } from '../api/friendships'
import { suggest } from '../api/wishes'
import { getAll as getFavorites, add as addFavorite } from '../api/favorites'
import { send } from '../api/messages'
import { getLimitInfo } from '../api/limits'
import { getMyUnique } from '../api/uniqueWishes'

const SEND_MODES = [
  { value: 'SIMPLE', label: '📨 Paprastas', desc: 'Gavėjas iš karto mato tekstą' },
  { value: 'GUESS', label: '🎭 Atspėk', desc: 'Gavėjas mato tik paveikslėlį ir turi atspėti toną' },
]

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
  const [sendMode, setSendMode] = useState('SIMPLE')
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
    if (!limitInfo.limited) return 'Žinučių: Neapribota'
    return `Žinučių: ${limitInfo.remaining} iš ${limitInfo.dailyLimit} liko (per 24 val.)`
  }

  const getFriendId = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    if (f.senderId === me?.id) return f.receiverId
    return f.senderId
  }

  const getFriendName = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    if (f.senderId === me?.id) return f.receiverFirstName
    return f.senderFirstName
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
      const payload = { receiverId: friendId, sendMode }
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
      setSendMode('SIMPLE')
    } catch (err) {
      setError(err.response?.data?.message || 'Nepavyko išsiųsti')
    } finally {
      setLoading(false)
    }
  }

  if (success) return (
    <div className="p-8">
      <div className="max-w-md mx-auto bg-white rounded-xl shadow p-8 text-center">
        <div className="text-5xl mb-4">✅</div>
        <h2 className="text-xl font-bold mb-2">Išsiųsta!</h2>
        <p className="text-slate-500 mb-6">Palinkėjimas išsiųstas sėkmingai.</p>
        <button onClick={() => setSuccess(false)} className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700">
          Siųsti dar
        </button>
      </div>
    </div>
  )

  return (
    <div className="p-8">
      <div className="flex items-center gap-2 mb-6">
        {[1, 2, 3].map((s) => (
          <div key={s} className={`flex items-center gap-2 ${s < 3 ? 'flex-1' : ''}`}>
            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step >= s ? 'bg-indigo-600 text-white' : 'bg-slate-200 text-slate-400'}`}>
              {s}
            </div>
            {s < 3 && <div className={`flex-1 h-0.5 ${step > s ? 'bg-indigo-600' : 'bg-slate-200'}`} />}
          </div>
        ))}
      </div>

      {step === 1 && (
        <div className="max-w-lg">
          <h2 className="text-xl font-bold mb-4">Pasirink draugą</h2>
          {friends.length === 0 && <p className="text-slate-400">Neturi draugų. Eik į Draugai ir pakvieski.</p>}
          <div className="flex flex-col gap-2">
            {friends.map((f) => (
              <button
                key={f.id}
                onClick={() => handleSelectFriend(f)}
                className="bg-white border rounded-xl px-5 py-3 text-left hover:border-indigo-400 hover:bg-indigo-50 transition-colors"
              >
                <p className="font-medium">{getFriendName(f)}</p>
                <p className="text-sm text-slate-400">
                  {f.relationshipTypeLabel} • Nuotaika: {f.senderId === JSON.parse(localStorage.getItem('user'))?.id ? (f.receiverMoodStatusLabel || '😶 Nenustatyta') : (f.senderMoodStatusLabel || '😶 Nenustatyta')}
                </p>
              </button>
            ))}
          </div>
        </div>
      )}

      {step === 2 && (
        <div className="max-w-lg">
          <div className="flex items-center gap-3 mb-4">
            <button onClick={() => setStep(1)} className="text-slate-400 hover:text-slate-600">← Atgal</button>
            <div>
              <h2 className="text-xl font-bold">Pasirink palinkėjimą</h2>
              {getLimitLabel() && (
                <p className={`text-xs mt-0.5 ${limitInfo?.limited && limitInfo?.remaining === 0 ? 'text-red-500' : 'text-slate-400'}`}>
                  {getLimitLabel()}
                </p>
              )}
            </div>
          </div>
          <div className="flex gap-3 mb-4">
            <button
              onClick={() => setShowFavorites(false)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium ${!showFavorites ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-600'}`}
            >
              Pasiūlymai
            </button>
            <button
              onClick={() => setShowFavorites(true)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium ${showFavorites ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-600'}`}
            >
              Mano sąrašas ({favorites.length + uniqueFavorites.length})
            </button>
          </div>
          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          <div className="flex flex-col gap-3">
            {showFavorites ? (
              <>
                {uniqueFavorites.map((w) => (
                  <div
                    key={'u-' + w.id}
                    className="bg-violet-50 border border-violet-200 rounded-xl px-5 py-4 flex items-center gap-3 hover:border-violet-400 hover:bg-violet-100 transition-colors cursor-pointer"
                    onClick={() => handleSelectWish(w)}
                  >
                    <div className="flex-1 min-w-0">
                      <p className="font-medium">{w.text}</p>
                      <p className="text-sm text-violet-400 mt-1">Asmeninis</p>
                    </div>
                  </div>
                ))}
                {favorites.map((w) => {
                  const id = w.id || w.wishId
                  return (
                    <div
                      key={id}
                      className="bg-white border rounded-xl px-5 py-4 flex items-center gap-3 hover:border-indigo-400 hover:bg-indigo-50 transition-colors cursor-pointer"
                      onClick={() => handleSelectWish(w)}
                    >
                      <div className="flex-1 min-w-0">
                        <p className="font-medium">{w.text}</p>
                        <p className="text-sm text-slate-400 mt-1">{w.toneLabel}</p>
                      </div>
                    </div>
                  )
                })}
                {favorites.length === 0 && uniqueFavorites.length === 0 && (
                  <p className="text-slate-400 text-sm">Nieko nėra.</p>
                )}
              </>
            ) : (
              <>
                {wishes.map((w) => {
                  const id = w.id || w.wishId
                  const saved = savedWishes.has(id)
                  return (
                    <div
                      key={id}
                      className="bg-white border rounded-xl px-5 py-4 flex items-center gap-3 hover:border-indigo-400 hover:bg-indigo-50 transition-colors cursor-pointer"
                      onClick={() => handleSelectWish(w)}
                    >
                      <div className="flex-1 min-w-0">
                        <p className="font-medium">{w.text}</p>
                        <p className="text-sm text-slate-400 mt-1">{w.toneLabel}</p>
                      </div>
                      <button
                        onClick={(e) => handleSave(e, w)}
                        title={saved ? 'Jau išsaugota' : 'Išsaugoti į mano sąrašą'}
                        className={`shrink-0 text-lg transition-transform hover:scale-125 ${saved ? 'opacity-40 cursor-default' : ''}`}
                        disabled={saved}
                      >
                        {saved ? '❤️' : '🤍'}
                      </button>
                    </div>
                  )
                })}
                {wishes.length === 0 && (
                  <p className="text-slate-400 text-sm">Nieko nėra.</p>
                )}
                {wishPage < maxPage && (
                  <button
                    onClick={() => setWishPage((p) => p + 1)}
                    className="mt-1 text-sm text-indigo-500 hover:text-indigo-700 self-center"
                  >
                    Rodyti kitas 3 ({maxPage - wishPage} kart. liko)
                  </button>
                )}
              </>
            )}
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="max-w-lg">
          <div className="flex items-center gap-3 mb-4">
            <button onClick={() => setStep(2)} className="text-slate-400 hover:text-slate-600">← Atgal</button>
            <div>
              <h2 className="text-xl font-bold">Siuntimo režimas</h2>
              {getLimitLabel() && (
                <p className={`text-xs mt-0.5 ${limitInfo?.limited && limitInfo?.remaining === 0 ? 'text-red-500' : 'text-slate-400'}`}>
                  {getLimitLabel()}
                </p>
              )}
            </div>
          </div>
          <div className="bg-white border rounded-xl p-4 mb-6">
            <p className="font-medium">{selectedWish.text}</p>
            <p className="text-sm text-slate-400 mt-1">{selectedWish.toneLabel}</p>
          </div>
          <div className="flex flex-col gap-3 mb-6">
            {SEND_MODES.map((m) => (
              <button
                key={m.value}
                onClick={() => setSendMode(m.value)}
                className={`border-2 rounded-xl px-5 py-4 text-left transition-colors ${sendMode === m.value ? 'border-indigo-600 bg-indigo-50' : 'border-slate-200 hover:border-indigo-300'}`}
              >
                <p className="font-medium">{m.label}</p>
                <p className="text-sm text-slate-500 mt-1">{m.desc}</p>
              </button>
            ))}
          </div>
          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          <button
            onClick={handleSend}
            disabled={loading}
            className="w-full bg-indigo-600 text-white rounded-xl py-3 font-medium hover:bg-indigo-700 disabled:opacity-50"
          >
            {loading ? 'Siunčiama...' : '📨 Siųsti'}
          </button>
        </div>
      )}
    </div>
  )
}
