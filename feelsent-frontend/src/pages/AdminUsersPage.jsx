import { useEffect, useState } from 'react'
import { getUsers, deleteUser, notifyUser } from '../api/admin'

function lastSeenLabel(lastLoginAt) {
  if (!lastLoginAt) return 'Niekada neprisijungęs'
  const days = Math.floor((Date.now() - new Date(lastLoginAt)) / 86400000)
  if (days === 0) return 'Prisijungė šiandien'
  return `${days} d. neprisijungęs`
}

export default function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [openMsgId, setOpenMsgId] = useState(null)
  const [msgText, setMsgText] = useState('')

  const load = async () => {
    const res = await getUsers()
    setUsers(res.data)
  }

  useEffect(() => { load() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Tikrai ištrinti vartotoją? Visi jo duomenys bus prarasti.')) return
    try {
      await deleteUser(id)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleSendMsg = async (id) => {
    if (!msgText.trim()) return
    try {
      await notifyUser(id, msgText)
      setMsgText('')
      setOpenMsgId(null)
      alert('Pranešimas išsiųstas')
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  return (
    <div className="p-8 max-w-3xl">
      <h2 className="text-xl font-bold mb-6">👥 Visi vartotojai</h2>

      <div className="flex flex-col gap-3">
        {users.map((u) => (
          <div key={u.id} className="bg-white border rounded-xl p-5">
            <div className="flex items-start justify-between gap-4">
              <div>
                <div className="flex items-center gap-2">
                  <p className="font-semibold">{u.firstName} {u.lastName}</p>
                  {u.role === 'ADMIN' && (
                    <span className="px-1.5 py-0.5 bg-violet-100 text-violet-700 text-xs font-semibold rounded-full">Admin</span>
                  )}
                </div>
                <p className="text-xs text-slate-400 mt-0.5">
                  {lastSeenLabel(u.lastLoginAt)} · {u.points} taškų
                </p>
              </div>
              <div className="flex gap-2 shrink-0">
                <button
                  onClick={() => { setOpenMsgId(openMsgId === u.id ? null : u.id); setMsgText('') }}
                  className="px-3 py-1.5 text-xs border rounded-lg text-slate-600 hover:bg-slate-50"
                >
                  Pranešimas
                </button>
                {u.role !== 'ADMIN' && (
                  <button
                    onClick={() => handleDelete(u.id)}
                    className="px-3 py-1.5 text-xs border border-red-200 rounded-lg text-red-600 hover:bg-red-50"
                  >
                    Ištrinti
                  </button>
                )}
              </div>
            </div>

            {openMsgId === u.id && (
              <div className="mt-3 pt-3 border-t">
                <textarea
                  className="w-full border rounded-lg px-3 py-2 text-sm mb-2 focus:outline-none focus:ring-2 focus:ring-red-400 resize-none"
                  rows={2}
                  placeholder="Pranešimo tekstas"
                  value={msgText}
                  onChange={(e) => setMsgText(e.target.value)}
                />
                <button
                  onClick={() => handleSendMsg(u.id)}
                  className="bg-red-600 text-white px-4 py-1.5 rounded-lg text-sm hover:bg-red-700"
                >
                  Siųsti
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
