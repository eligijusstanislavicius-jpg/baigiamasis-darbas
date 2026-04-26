import { useEffect, useState } from 'react'
import { getAll, remove } from '../api/favorites'
import { getMyUnique, removeMyUnique } from '../api/uniqueWishes'

export default function FavoritesPage() {
  const [data, setData] = useState(null)
  const [uniqueWishes, setUniqueWishes] = useState([])

  const load = async () => {
    const [fRes, uRes] = await Promise.all([getAll(), getMyUnique()])
    setData(fRes.data)
    setUniqueWishes(uRes.data)
  }

  useEffect(() => { load() }, [])

  const expiryLabel = (expiresAt) => {
    if (!expiresAt) return 'Galioja iki ištrynimo'
    const days = Math.floor((new Date(expiresAt) - Date.now()) / 86400000)
    if (days < 0) return 'Pasibaigęs'
    if (days === 0) return 'Baigiasi šiandien'
    return `Galioja dar ${days} d.`
  }

  const handleRemove = async (id) => {
    await remove(id)
    load()
  }

  const handleRemoveUnique = async (id) => {
    await removeMyUnique(id)
    load()
  }

  if (!data) return <div className="p-8 text-slate-400">Kraunama...</div>

  return (
    <div className="p-8 max-w-2xl">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold">❤️ Mano sąrašas</h2>
        <span className="text-sm text-slate-400">{data.count}/{data.max}</span>
      </div>

      {uniqueWishes.length > 0 && (
        <div className="mb-4">
          <p className="text-xs font-semibold text-violet-600 uppercase tracking-wide mb-2">Asmeniniai palinkėjimai</p>
          <div className="flex flex-col gap-2">
            {uniqueWishes.map((u) => (
              <div key={u.id} className="bg-violet-50 border border-violet-200 rounded-xl px-5 py-4 flex items-center justify-between">
                <div>
                  <p className="font-medium text-sm">{u.text}</p>
                  <p className="text-xs text-violet-400 mt-0.5">{expiryLabel(u.expiresAt)}</p>
                </div>
                <button
                  onClick={() => handleRemoveUnique(u.id)}
                  className="text-slate-300 hover:text-red-500 text-sm ml-4"
                >
                  Šalinti
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="flex flex-col gap-3 mb-6">
        {data.wishes.length === 0 && uniqueWishes.length === 0 && <p className="text-slate-400 text-sm">Sąrašas tuščias.</p>}
        {data.wishes.length > 0 && <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-1">Bendri palinkėjimai</p>}
        {data.wishes.map((w) => (
          <div key={w.id} className="bg-white border rounded-xl px-5 py-4 flex items-center justify-between">
            <div>
              <p className="font-medium text-sm">{w.text}</p>
              <p className="text-xs text-slate-400 mt-0.5">{w.toneLabel}</p>
            </div>
            <button
              onClick={() => handleRemove(w.id)}
              className="text-slate-300 hover:text-red-500 text-sm ml-4"
            >
              Šalinti
            </button>
          </div>
        ))}
      </div>

    </div>
  )
}
