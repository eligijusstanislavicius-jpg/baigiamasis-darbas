import { useEffect, useState } from 'react'
import { getAll, add, remove } from '../api/favorites'
import api from '../api/axios'

export default function FavoritesPage() {
  const [data, setData] = useState(null)
  const [wishId, setWishId] = useState('')
  const [addErr, setAddErr] = useState('')
  const [loading, setLoading] = useState(false)

  const load = async () => {
    const res = await getAll()
    setData(res.data)
  }

  useEffect(() => { load() }, [])

  const handleAdd = async () => {
    const id = parseInt(wishId)
    if (!id) return setAddErr('Įvesk palinkėjimo ID')
    setAddErr('')
    setLoading(true)
    try {
      await add(id)
      setWishId('')
      load()
    } catch (err) {
      setAddErr(err.response?.data?.message || 'Klaida')
    } finally {
      setLoading(false)
    }
  }

  const handleRemove = async (id) => {
    await remove(id)
    load()
  }

  if (!data) return <div className="p-8 text-slate-400">Kraunama...</div>

  return (
    <div className="p-8 max-w-2xl">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold">❤️ Mano sąrašas</h2>
        <span className="text-sm text-slate-400">{data.count}/{data.max}</span>
      </div>

      <div className="flex flex-col gap-3 mb-6">
        {data.wishes.length === 0 && <p className="text-slate-400 text-sm">Sąrašas tuščias.</p>}
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

      {data.remaining > 0 && (
        <div className="bg-white border rounded-xl p-5">
          <h3 className="font-semibold text-sm mb-3">Pridėti pagal ID</h3>
          <p className="text-xs text-slate-400 mb-3">
            Palinkėjimo ID matosi kai naudoji „Siųsti" → „Pasiūlymai" (šiuo metu techninis laukas).
          </p>
          <div className="flex gap-2">
            <input
              className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="Palinkėjimo ID (pvz. 5)"
              value={wishId}
              onChange={(e) => setWishId(e.target.value)}
              type="number"
            />
            <button
              onClick={handleAdd}
              disabled={loading}
              className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-indigo-700 disabled:opacity-50"
            >
              Pridėti
            </button>
          </div>
          {addErr && <p className="text-red-500 text-xs mt-2">{addErr}</p>}
        </div>
      )}
    </div>
  )
}
