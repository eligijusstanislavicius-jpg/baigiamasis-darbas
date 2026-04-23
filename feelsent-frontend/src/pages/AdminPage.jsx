import { useEffect, useState } from 'react'
import { getUsers, deleteUser, getWishes, addWish, deactivateWish } from '../api/admin'

export default function AdminPage() {
  const [users, setUsers] = useState([])
  const [wishes, setWishes] = useState([])
  const [tab, setTab] = useState('users')
  const [newWish, setNewWish] = useState({ text: '', tone: 'SUPPORTIVE', relationshipType: 'FRIEND' })
  const [err, setErr] = useState('')

  const load = async () => {
    const [uRes, wRes] = await Promise.all([getUsers(), getWishes()])
    setUsers(uRes.data)
    setWishes(wRes.data)
  }

  useEffect(() => { load() }, [])

  const handleDeleteUser = async (id) => {
    if (!confirm('Tikrai ištrinti vartotoją?')) return
    try {
      await deleteUser(id)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleAddWish = async () => {
    setErr('')
    try {
      await addWish(newWish)
      setNewWish({ text: '', tone: 'SUPPORTIVE', relationshipType: 'FRIEND' })
      load()
    } catch (err) {
      setErr(err.response?.data?.message || 'Klaida')
    }
  }

  const handleDeactivate = async (id) => {
    await deactivateWish(id)
    load()
  }

  return (
    <div className="p-8 max-w-4xl">
      <h2 className="text-xl font-bold mb-6">🛡️ Administravimas</h2>

      <div className="flex gap-3 mb-6">
        <button
          onClick={() => setTab('users')}
          className={`px-5 py-2 rounded-full text-sm font-medium ${tab === 'users' ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
        >
          Vartotojai ({users.length})
        </button>
        <button
          onClick={() => setTab('wishes')}
          className={`px-5 py-2 rounded-full text-sm font-medium ${tab === 'wishes' ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
        >
          Palinkėjimai ({wishes.length})
        </button>
      </div>

      {tab === 'users' && (
        <div className="flex flex-col gap-2">
          {users.map((u) => (
            <div key={u.id} className="bg-white border rounded-xl px-5 py-3 flex items-center justify-between">
              <div>
                <p className="font-medium text-sm">{u.firstName} {u.lastName} (@{u.username})</p>
                <p className="text-xs text-slate-400">{u.points} taškų</p>
              </div>
              <button
                onClick={() => handleDeleteUser(u.id)}
                className="text-xs text-red-500 hover:text-red-700"
              >
                Ištrinti
              </button>
            </div>
          ))}
        </div>
      )}

      {tab === 'wishes' && (
        <div>
          <div className="bg-white border rounded-xl p-5 mb-5">
            <h3 className="font-semibold mb-3">Pridėti palinkėjimą</h3>
            <textarea
              className="w-full border rounded-lg px-3 py-2 text-sm mb-2 focus:outline-none focus:ring-2 focus:ring-red-400 resize-none"
              rows={2}
              placeholder="Palinkėjimo tekstas"
              value={newWish.text}
              onChange={(e) => setNewWish({ ...newWish, text: e.target.value })}
            />
            <div className="flex gap-2 mb-3">
              <select
                className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                value={newWish.tone}
                onChange={(e) => setNewWish({ ...newWish, tone: e.target.value })}
              >
                {['SUPPORTIVE','FUNNY','ROMANTIC','BIRTHDAY'].map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
              <select
                className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                value={newWish.relationshipType}
                onChange={(e) => setNewWish({ ...newWish, relationshipType: e.target.value })}
              >
                {['FRIEND','PARTNER','MOTHER','FATHER','CHILD','BROTHER','SISTER','GRANDFATHER','GRANDMOTHER','ALL'].map((r) => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
            </div>
            {err && <p className="text-red-500 text-xs mb-2">{err}</p>}
            <button
              onClick={handleAddWish}
              className="bg-red-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-red-700"
            >
              Pridėti
            </button>
          </div>

          <div className="flex flex-col gap-2">
            {wishes.map((w) => (
              <div key={w.id} className="bg-white border rounded-xl px-5 py-3 flex items-start justify-between gap-3">
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium">{w.text}</p>
                  <p className="text-xs text-slate-400 mt-0.5">{w.toneLabel} • {w.relationshipType}</p>
                </div>
                <button
                  onClick={() => handleDeactivate(w.id)}
                  className="text-xs text-slate-400 hover:text-red-500 shrink-0"
                >
                  Deaktyvuoti
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
