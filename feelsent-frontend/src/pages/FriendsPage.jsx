import { useEffect, useState } from 'react'
import { getAll, getPending, sendRequest, accept, decline, remove } from '../api/friendships'
import api from '../api/axios'

const REL_TYPES = [
  { value: 'FRIEND', label: 'Draugas' },
  { value: 'PARTNER', label: 'Partneris' },
  { value: 'MOTHER', label: 'Mama' },
  { value: 'FATHER', label: 'Tėtis' },
  { value: 'CHILD', label: 'Vaikas' },
  { value: 'BROTHER', label: 'Brolis' },
  { value: 'SISTER', label: 'Sesuo' },
  { value: 'GRANDFATHER', label: 'Senelis' },
  { value: 'GRANDMOTHER', label: 'Močiutė' },
]

export default function FriendsPage() {
  const [friends, setFriends] = useState([])
  const [pending, setPending] = useState([])
  const [email, setEmail] = useState('')
  const [relType, setRelType] = useState('FRIEND')
  const [searchResult, setSearchResult] = useState(null)
  const [searchErr, setSearchErr] = useState('')
  const [sendErr, setSendErr] = useState('')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    const [fRes, pRes] = await Promise.all([getAll(), getPending()])
    setFriends(fRes.data)
    setPending(pRes.data)
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
    await accept(id)
    load()
  }

  const handleDecline = async (id) => {
    await decline(id)
    load()
  }

  const handleRemove = async (id) => {
    if (!confirm('Tikrai pašalinti draugą?')) return
    await remove(id)
    load()
  }

  const getFriendName = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    if (f.senderUsername === me?.username)
      return `${f.receiverFirstName} (${f.receiverUsername})`
    return `${f.senderFirstName} (${f.senderUsername})`
  }

  return (
    <div className="p-8 max-w-2xl">
      <h2 className="text-xl font-bold mb-6">👥 Draugai</h2>

      {/* Pakvietimo forma */}
      <div className="bg-white border rounded-xl p-6 mb-6">
        <h3 className="font-semibold mb-3">Pakviesti draugą</h3>
        <div className="flex gap-2 mb-3">
          <input
            className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
            placeholder="El. paštas"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <button
            onClick={handleSearch}
            className="bg-slate-100 px-4 py-2 rounded-lg text-sm hover:bg-slate-200"
          >
            Ieškoti
          </button>
        </div>
        {searchErr && <p className="text-red-500 text-sm mb-2">{searchErr}</p>}
        {searchResult && (
          <div className="bg-indigo-50 border border-indigo-200 rounded-lg p-3 mb-3">
            <p className="font-medium text-sm">{searchResult.firstName} {searchResult.lastName} (@{searchResult.username})</p>
          </div>
        )}
        <select
          className="w-full border rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          value={relType}
          onChange={(e) => setRelType(e.target.value)}
        >
          {REL_TYPES.map((r) => (
            <option key={r.value} value={r.value}>{r.label}</option>
          ))}
        </select>
        {sendErr && <p className="text-red-500 text-sm mb-2">{sendErr}</p>}
        <button
          onClick={handleSend}
          disabled={!searchResult || loading}
          className="w-full bg-indigo-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-indigo-700 disabled:opacity-40"
        >
          Siųsti užklausą
        </button>
      </div>

      {/* Laukiančios užklausos */}
      {pending.length > 0 && (
        <div className="mb-6">
          <h3 className="font-semibold mb-3 text-orange-600">⏳ Laukia patvirtinimo ({pending.length})</h3>
          <div className="flex flex-col gap-2">
            {pending.map((f) => (
              <div key={f.id} className="bg-white border border-orange-200 rounded-xl px-5 py-3 flex items-center justify-between">
                <div>
                  <p className="font-medium text-sm">{f.senderFirstName} ({f.senderUsername})</p>
                  <p className="text-xs text-slate-400">{f.relationshipTypeLabel}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleAccept(f.id)} className="text-green-600 hover:text-green-700 text-sm font-medium">✅ Priimti</button>
                  <button onClick={() => handleDecline(f.id)} className="text-red-500 hover:text-red-600 text-sm">❌</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Draugų sąrašas */}
      <h3 className="font-semibold mb-3">Draugai ({friends.length})</h3>
      {friends.length === 0 && <p className="text-slate-400 text-sm">Kol kas draugų nėra.</p>}
      <div className="flex flex-col gap-2">
        {friends.map((f) => (
          <div key={f.id} className="bg-white border rounded-xl px-5 py-3 flex items-center justify-between">
            <div>
              <p className="font-medium text-sm">{getFriendName(f)}</p>
              <p className="text-xs text-slate-400">
                {f.relationshipTypeLabel} • Nuotaika: {f.senderUsername === JSON.parse(localStorage.getItem('user'))?.username ? (f.receiverMoodStatusLabel || '😶 Nenustatyta') : (f.senderMoodStatusLabel || '😶 Nenustatyta')}
              </p>
            </div>
            <button
              onClick={() => handleRemove(f.id)}
              className="text-slate-300 hover:text-red-500 text-sm"
            >
              Šalinti
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
