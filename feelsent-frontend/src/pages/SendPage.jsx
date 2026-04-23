import { useEffect, useState } from 'react'
import { getAll } from '../api/friendships'
import { suggest } from '../api/wishes'
import { getAll as getFavorites } from '../api/favorites'
import { send } from '../api/messages'

const SEND_MODES = [
  { value: 'SIMPLE', label: '📨 Paprastas', desc: 'Gavėjas iš karto mato tekstą' },
  { value: 'GUESS', label: '🎭 Atspėk', desc: 'Gavėjas mato tik paveikslėlį ir turi atspėti toną' },
]

export default function SendPage() {
  const [step, setStep] = useState(1)
  const [friends, setFriends] = useState([])
  const [selectedFriend, setSelectedFriend] = useState(null)
  const [wishes, setWishes] = useState([])
  const [favorites, setFavorites] = useState([])
  const [showFavorites, setShowFavorites] = useState(false)
  const [selectedWish, setSelectedWish] = useState(null)
  const [sendMode, setSendMode] = useState('SIMPLE')
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    getAll().then((res) => setFriends(res.data)).catch(() => {})
  }, [])

  const handleSelectFriend = async (friend) => {
    setSelectedFriend(friend)
    setError('')
    try {
      const [wRes, fRes] = await Promise.all([
        suggest(friend.receiverId === undefined ? friend.senderId : friend.receiverId),
        getFavorites(),
      ])
      setWishes(wRes.data)
      setFavorites(fRes.data.wishes)
    } catch {
      setError('Nepavyko įkelti palinkėjimų')
    }
    setStep(2)
  }

  const handleSelectWish = (wish) => {
    setSelectedWish(wish)
    setStep(3)
  }

  const handleSend = async () => {
    setLoading(true)
    setError('')
    try {
      const friendId = getFriendId(selectedFriend)
      await send({ receiverId: friendId, wishId: selectedWish.wishId || selectedWish.id, sendMode })
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

  const getFriendId = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    if (f.senderUsername === me?.username) return f.receiverId
    return f.senderId
  }

  const getFriendName = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    if (f.senderUsername === me?.username) return `${f.receiverFirstName} (${f.receiverUsername})`
    return `${f.senderFirstName} (${f.senderUsername})`
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
                <p className="text-sm text-slate-400">{f.relationshipTypeLabel}</p>
              </button>
            ))}
          </div>
        </div>
      )}

      {step === 2 && (
        <div className="max-w-lg">
          <div className="flex items-center gap-3 mb-4">
            <button onClick={() => setStep(1)} className="text-slate-400 hover:text-slate-600">← Atgal</button>
            <h2 className="text-xl font-bold">Pasirink palinkėjimą</h2>
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
              Mano sąrašas ({favorites.length})
            </button>
          </div>
          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          <div className="flex flex-col gap-3">
            {(showFavorites ? favorites : wishes).map((w) => (
              <button
                key={w.wishId || w.id}
                onClick={() => handleSelectWish(w)}
                className="bg-white border rounded-xl px-5 py-4 text-left hover:border-indigo-400 hover:bg-indigo-50 transition-colors"
              >
                <p className="font-medium">{w.text}</p>
                <p className="text-sm text-slate-400 mt-1">{w.toneLabel}</p>
              </button>
            ))}
            {(showFavorites ? favorites : wishes).length === 0 && (
              <p className="text-slate-400 text-sm">Nieko nėra.</p>
            )}
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="max-w-lg">
          <div className="flex items-center gap-3 mb-4">
            <button onClick={() => setStep(2)} className="text-slate-400 hover:text-slate-600">← Atgal</button>
            <h2 className="text-xl font-bold">Siuntimo režimas</h2>
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
