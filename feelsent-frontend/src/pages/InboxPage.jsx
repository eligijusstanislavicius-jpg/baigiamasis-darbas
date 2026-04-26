import { useEffect, useState } from 'react'
import { getInbox, open, guess, react } from '../api/messages'

const TONES = ['SUPPORTIVE', 'FUNNY', 'ROMANTIC', 'BIRTHDAY']
const TONE_LABELS = { SUPPORTIVE: 'Palaikantis', FUNNY: 'Juokingas', ROMANTIC: 'Romantiškas', BIRTHDAY: 'Gimtadieninis' }

const REACTIONS = [
  { value: 'WARMED_UP', emoji: '🤗', label: 'Sušildė' },
  { value: 'COMFORTED', emoji: '🫂', label: 'Paguodė' },
  { value: 'INSPIRED', emoji: '✨', label: 'Įkvėpė' },
  { value: 'CHEERED_UP', emoji: '😊', label: 'Pradžiugino' },
  { value: 'SURPRISED', emoji: '🎉', label: 'Nustebino' },
  { value: 'CALMED', emoji: '🕊️', label: 'Nuramino' },
]

export default function InboxPage() {
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async () => {
    try {
      const res = await getInbox()
      setMessages(res.data)
    } catch {
      setError('Nepavyko įkelti žinučių')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleOpen = async (id) => {
    try {
      const res = await open(id)
      setMessages((prev) => prev.map((m) => (m.id === id ? res.data : m)))
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleGuess = async (id, tone) => {
    try {
      const res = await guess(id, tone)
      setMessages((prev) => prev.map((m) => (m.id === id ? res.data : m)))
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  const handleReact = async (id, reaction) => {
    try {
      await react(id, reaction)
      setMessages((prev) => prev.filter((m) => m.id !== id))
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
    }
  }

  if (loading) return <div className="p-8 text-slate-400">Kraunama...</div>
  if (error) return <div className="p-8 text-red-500">{error}</div>

  return (
    <div className="p-8">
      <h2 className="text-xl font-bold mb-6">📬 Gautos žinutės</h2>
      {messages.length === 0 && (
        <p className="text-slate-400">Nėra naujų žinučių.</p>
      )}
      <div className="flex flex-col gap-4 max-w-2xl">
        {messages.map((m) => (
          <div key={m.id} className="bg-white rounded-xl shadow-sm border p-6">
            <p className="text-sm text-slate-400 mb-1">
              Nuo: <span className="font-medium text-slate-700">{m.senderFirstName}</span>
            </p>
            <p className="text-xs text-slate-300 mb-4">
              {new Date(m.sentAt).toLocaleString('lt-LT')}
            </p>

            {/* SENT — neatidarytas */}
            {m.status === 'SENT' && (
              <div className="text-center">
                <div className="text-5xl mb-4">📩</div>
                <p className="text-slate-500 mb-4">Gavai palinkėjimą!</p>
                <button
                  onClick={() => handleOpen(m.id)}
                  className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700"
                >
                  Atidaryti
                </button>
              </div>
            )}

            {/* OPENED — GUESS режimas */}
            {m.status === 'OPENED' && m.sendMode === 'GUESS' && (
              <div>
                <div className="flex justify-center mb-4">
                  <img
                    src={m.imageUrl}
                    alt="palinkėjimas"
                    className="w-48 h-48 object-cover rounded-lg border"
                    onError={(e) => { e.target.style.display = 'none' }}
                  />
                </div>
                <p className="text-center text-slate-600 mb-4 font-medium">Atspėk toną:</p>
                <div className="grid grid-cols-2 gap-2">
                  {TONES.map((t) => (
                    <button
                      key={t}
                      onClick={() => handleGuess(m.id, t)}
                      className="border-2 border-indigo-200 text-indigo-700 rounded-lg py-2 hover:bg-indigo-50 font-medium"
                    >
                      {TONE_LABELS[t]}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* OPENED — SIMPLE arba PASSIVE */}
            {m.status === 'OPENED' && m.sendMode !== 'GUESS' && (
              <div>
                <p className="text-lg font-medium text-slate-800 mb-2">{m.wishText}</p>
                <p className="text-sm text-slate-400 mb-4">Tonas: {m.wishToneLabel}</p>
                <p className="text-slate-600 mb-3 font-medium">Reaguok:</p>
                <div className="flex flex-wrap gap-2">
                  {REACTIONS.map((r) => (
                    <button
                      key={r.value}
                      onClick={() => handleReact(m.id, r.value)}
                      className="flex items-center gap-1 border rounded-lg px-3 py-1.5 hover:bg-slate-50 text-sm"
                    >
                      <span>{r.emoji}</span> {r.label}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* GUESSED */}
            {m.status === 'GUESSED' && (
              <div>
                <p className="text-sm mb-1">
                  {m.guessResult
                    ? <span className="text-green-600 font-medium">✅ Teisingai atspėjai! +5 taškai</span>
                    : <span className="text-red-500">❌ Neatspėjai</span>
                  }
                </p>
                <p className="text-lg font-medium text-slate-800 mb-2">{m.wishText}</p>
                <p className="text-slate-600 mb-3 font-medium">Reaguok:</p>
                <div className="flex flex-wrap gap-2">
                  {REACTIONS.map((r) => (
                    <button
                      key={r.value}
                      onClick={() => handleReact(m.id, r.value)}
                      className="flex items-center gap-1 border rounded-lg px-3 py-1.5 hover:bg-slate-50 text-sm"
                    >
                      <span>{r.emoji}</span> {r.label}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
