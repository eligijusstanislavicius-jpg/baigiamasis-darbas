import { useEffect, useState } from 'react'
import { getUsers, deleteUser, getWishes, addWish, deactivateWish, notifyAll } from '../api/admin'
import { getAllUnique, createUnique, updateUnique, assignUnique } from '../api/uniqueWishes'

const TONE_OPTIONS = [
  { value: 'SUPPORTIVE', label: 'Palaikantis' },
  { value: 'FUNNY',      label: 'Juokingas' },
  { value: 'ROMANTIC',   label: 'Romantiškas' },
  { value: 'BIRTHDAY',   label: 'Gimtadieninis' },
]

const RELATIONSHIP_OPTIONS = [
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

export default function AdminPage({ defaultTab = 'users' }) {
  const [users, setUsers] = useState([])
  const [wishes, setWishes] = useState([])
  const [uniqueWishes, setUniqueWishes] = useState([])
  const [tab, setTab] = useState(defaultTab)
  const [newWish, setNewWish] = useState({ text: '', tone: 'SUPPORTIVE', relationshipType: 'FRIEND' })
  const [wishErr, setWishErr] = useState('')
  const [notifyAllText, setNotifyAllText] = useState('')

  // Unikalių palinkėjimų state
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

  const load = async () => {
    const [uRes, wRes, uwRes] = await Promise.all([getUsers(), getWishes(), getAllUnique()])
    setUsers(uRes.data)
    setWishes(wRes.data)
    setUniqueWishes(uwRes.data)
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
      load()
    } catch (err) {
      setWishErr(err.response?.data?.message || 'Klaida')
    }
  }

  const handleDeactivate = async (id) => {
    await deactivateWish(id)
    load()
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
      load()
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
      load()
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
      load()
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

  const regularUsers = users.filter(u => u.role === 'USER')

  return (
    <div className="p-8 max-w-4xl">
      <h2 className="text-xl font-bold mb-6">🛡️ Administravimas</h2>

      <div className="flex gap-3 mb-6">
        <button
          onClick={() => setTab('users')}
          className={`px-5 py-2 rounded-full text-sm font-medium ${tab === 'users' ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
        >
          Vartotojai ({regularUsers.length})
        </button>
        <button
          onClick={() => setTab('wishes')}
          className={`px-5 py-2 rounded-full text-sm font-medium ${tab === 'wishes' ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
        >
          Bendri palinkėjimai ({wishes.length})
        </button>
        <button
          onClick={() => setTab('unique')}
          className={`px-5 py-2 rounded-full text-sm font-medium ${tab === 'unique' ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
        >
          Unikalūs ({uniqueWishes.length})
        </button>
      </div>

      {tab === 'users' && (
        <div>
          <div className="flex flex-col gap-2 mb-8">
            {regularUsers.map((u) => (
              <div key={u.id} className="bg-white border rounded-xl px-5 py-3 flex items-center justify-between">
                <div>
                  <p className="font-medium text-sm">{u.firstName} {u.lastName}</p>
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

          <div className="bg-white border rounded-xl p-5 mb-4">
            <h3 className="font-semibold mb-3">Pranešimas visiems vartotojams</h3>
            <textarea
              className="w-full border rounded-lg px-3 py-2 text-sm mb-2 focus:outline-none focus:ring-2 focus:ring-red-400 resize-none"
              rows={2}
              placeholder="Pranešimo tekstas"
              value={notifyAllText}
              onChange={(e) => setNotifyAllText(e.target.value)}
            />
            <button
              onClick={handleNotifyAll}
              className="bg-red-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-red-700"
            >
              Siųsti visiems
            </button>
          </div>

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
              <div className="flex-1">
                <label className="text-xs text-slate-500 mb-1 block">Tonas</label>
                <select
                  className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                  value={newWish.tone}
                  onChange={(e) => setNewWish({ ...newWish, tone: e.target.value })}
                >
                  {TONE_OPTIONS.map((t) => (
                    <option key={t.value} value={t.value}>{t.label}</option>
                  ))}
                </select>
              </div>
              <div className="flex-1">
                <label className="text-xs text-slate-500 mb-1 block">Ryšio tipas</label>
                <select
                  className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                  value={newWish.relationshipType}
                  onChange={(e) => setNewWish({ ...newWish, relationshipType: e.target.value })}
                >
                  {RELATIONSHIP_OPTIONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>
            </div>
            {wishErr && <p className="text-red-500 text-xs mb-2">{wishErr}</p>}
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
                  <p className="text-xs text-slate-400 mt-0.5">{w.toneLabel} • {REL_LABEL[w.relationshipType] ?? w.relationshipType}</p>
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

      {tab === 'unique' && (
        <div>
          <div className="bg-white border rounded-xl p-5 mb-5">
            <h3 className="font-semibold mb-3">Sukurti unikalų palinkėjimą</h3>
            <textarea
              className="w-full border rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-red-400 resize-none"
              rows={2}
              placeholder="Palinkėjimo tekstas"
              value={newUniqueText}
              onChange={(e) => setNewUniqueText(e.target.value)}
            />
            <div className="flex gap-2 mb-3">
              <div className="flex-1">
                <label className="text-xs text-slate-500 mb-1 block">Priskirti vartotojui (nebūtina)</label>
                <select
                  className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
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
                  <label className="text-xs text-slate-500 mb-1 block">Galiojimas</label>
                  <div className="flex flex-col gap-1">
                    <label className="flex items-center gap-2 text-sm text-slate-600">
                      <input type="checkbox" checked={newUniquePermanent} onChange={(e) => setNewUniquePermanent(e.target.checked)} />
                      Iki ištrynimo
                    </label>
                    {!newUniquePermanent && (
                      <input
                        type="datetime-local"
                        className="border rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                        value={newUniqueExpiry}
                        onChange={(e) => setNewUniqueExpiry(e.target.value)}
                      />
                    )}
                  </div>
                </div>
              )}
            </div>
            {uniqueErr && <p className="text-red-500 text-xs mb-2">{uniqueErr}</p>}
            <button
              onClick={handleCreateUnique}
              className="bg-red-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-red-700"
            >
              Sukurti
            </button>
          </div>

          <div className="flex flex-col gap-3">
            {uniqueWishes.length === 0 && <p className="text-slate-400 text-sm">Nėra unikalių palinkėjimų.</p>}
            {uniqueWishes.map((w) => (
              <div key={w.id} className="bg-white border rounded-xl p-4">
                {editingUniqueId === w.id ? (
                  <div className="flex gap-2 mb-3">
                    <input
                      className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                      value={editingUniqueText}
                      onChange={(e) => setEditingUniqueText(e.target.value)}
                    />
                    <button onClick={() => handleUpdateUnique(w.id)} className="bg-red-600 text-white px-3 py-1.5 rounded-lg text-sm hover:bg-red-700">Išsaugoti</button>
                    <button onClick={() => setEditingUniqueId(null)} className="text-slate-400 text-sm px-2">Atšaukti</button>
                  </div>
                ) : (
                  <div className="flex items-start justify-between gap-3 mb-2">
                    <p className="text-sm font-medium flex-1">{w.text}</p>
                    <button
                      onClick={() => { setEditingUniqueId(w.id); setEditingUniqueText(w.text) }}
                      className="text-xs text-slate-400 hover:text-red-500 shrink-0"
                    >
                      Redaguoti
                    </button>
                  </div>
                )}

                {w.assignments.length > 0 && (
                  <div className="flex flex-wrap gap-2 mb-2">
                    {w.assignments.map((a) => (
                      <span key={a.userUniqueWishId} className="text-xs bg-slate-100 px-2 py-0.5 rounded-full text-slate-600">
                        {a.firstName} {a.lastName} · {expiryLabel(a.expiresAt)}
                      </span>
                    ))}
                  </div>
                )}

                {assigningId === w.id ? (
                  <div className="border-t pt-3 mt-2 flex flex-wrap gap-2 items-end">
                    <div>
                      <label className="text-xs text-slate-500 mb-1 block">Vartotojas</label>
                      <select
                        className="border rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
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
                      <label className="text-xs text-slate-500 mb-1 block">Galiojimas</label>
                      <label className="flex items-center gap-1 text-xs text-slate-600 mb-1">
                        <input type="checkbox" checked={assignPermanent} onChange={(e) => setAssignPermanent(e.target.checked)} />
                        Iki ištrynimo
                      </label>
                      {!assignPermanent && (
                        <input
                          type="datetime-local"
                          className="border rounded-lg px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                          value={assignExpiry}
                          onChange={(e) => setAssignExpiry(e.target.value)}
                        />
                      )}
                    </div>
                    <button onClick={() => handleAssignUnique(w.id)} className="bg-red-600 text-white px-3 py-1.5 rounded-lg text-sm hover:bg-red-700">Priskirti</button>
                    <button onClick={() => setAssigningId(null)} className="text-slate-400 text-sm px-2">Atšaukti</button>
                  </div>
                ) : (
                  <button
                    onClick={() => { setAssigningId(w.id); setAssignUserId(''); setAssignExpiry(''); setAssignPermanent(false) }}
                    className="text-xs text-red-600 hover:text-red-700 border border-red-200 rounded-lg px-3 py-1 mt-1"
                  >
                    + Priskirti vartotojui
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
