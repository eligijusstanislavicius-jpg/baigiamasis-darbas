import { useState } from 'react'
import api from '../api/axios'

export default function ContactPage() {
  const [text, setText] = useState('')
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  const handleSend = async () => {
    if (!text.trim()) return setError('Įvesk žinutės tekstą')
    setError('')
    setLoading(true)
    try {
      await api.post('/api/contact', { text })
      setSuccess(true)
      setText('')
    } catch (err) {
      setError(err.response?.data?.message || 'Klaida siunčiant žinutę')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-8 max-w-lg">
      <h2 className="text-xl font-bold mb-2">📩 Susisiekti su administratoriumi</h2>
      <p className="text-sm text-slate-400 mb-6">
        Parašyk žinutę — administratorius ją gaus pranešimų lange ir susisieks su tavimi.
      </p>

      {success ? (
        <div className="bg-green-50 border border-green-200 rounded-xl p-6 text-center">
          <p className="text-green-700 font-medium mb-4">Žinutė išsiųsta sėkmingai!</p>
          <button
            onClick={() => setSuccess(false)}
            className="bg-indigo-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-indigo-700"
          >
            Siųsti dar vieną
          </button>
        </div>
      ) : (
        <div className="bg-white border rounded-xl p-6">
          <textarea
            className="w-full border rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-400 resize-none"
            rows={5}
            placeholder="Aprašyk savo klausimą ar pageidavimą..."
            value={text}
            onChange={(e) => setText(e.target.value)}
          />
          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          <button
            onClick={handleSend}
            disabled={loading}
            className="w-full bg-indigo-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-indigo-700 disabled:opacity-50"
          >
            {loading ? 'Siunčiama...' : 'Siųsti žinutę'}
          </button>
        </div>
      )}
    </div>
  )
}
