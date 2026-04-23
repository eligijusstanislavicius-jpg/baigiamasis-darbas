import { useEffect, useState } from 'react'
import { getAll, setLimit, removeLimit } from '../api/limits'
import { getAll as getFriends } from '../api/friendships'

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
    return f.senderUsername === me?.username ? f.receiverId : f.senderId
  }

  const getFriendName = (f) => {
    const me = JSON.parse(localStorage.getItem('user'))
    return f.senderUsername === me?.username
      ? `${f.receiverFirstName} (${f.receiverUsername})`
      : `${f.senderFirstName} (${f.senderUsername})`
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
    <div className="p-8 max-w-2xl">
      <h2 className="text-xl font-bold mb-2">⚙️ Limitai</h2>
      <p className="text-sm text-slate-400 mb-6">Riboji kiek žinučių per dieną gali gauti iš konkretaus draugo.</p>

      <div className="bg-white border rounded-xl p-6 mb-6">
        <h3 className="font-semibold mb-3">Nustatyti limitą</h3>
        <select
          className="w-full border rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          value={senderId}
          onChange={(e) => setSenderId(e.target.value)}
        >
          <option value="">Pasirink draugą</option>
          {friends.map((f) => (
            <option key={f.id} value={getFriendId(f)}>{getFriendName(f)}</option>
          ))}
        </select>
        <div className="flex items-center gap-3 mb-3">
          <label className="text-sm text-slate-600">Žinučių per dieną:</label>
          <input
            type="number"
            min={1}
            max={50}
            value={dailyLimit}
            onChange={(e) => setDailyLimit(parseInt(e.target.value))}
            className="w-20 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
          />
        </div>
        {err && <p className="text-red-500 text-sm mb-2">{err}</p>}
        <button
          onClick={handleSet}
          disabled={loading}
          className="w-full bg-indigo-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-indigo-700 disabled:opacity-50"
        >
          Išsaugoti
        </button>
      </div>

      <h3 className="font-semibold mb-3">Nustatyti limitai ({limits.length})</h3>
      {limits.length === 0 && <p className="text-slate-400 text-sm">Nėra nustatytų limitų.</p>}
      <div className="flex flex-col gap-2">
        {limits.map((l) => (
          <div key={l.id} className="bg-white border rounded-xl px-5 py-3 flex items-center justify-between">
            <div>
              <p className="font-medium text-sm">{l.senderUsername}</p>
              <p className="text-xs text-slate-400">Maks. {l.dailyLimit} žinutė/ų per dieną</p>
            </div>
            <button
              onClick={() => handleRemove(l.senderId)}
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
