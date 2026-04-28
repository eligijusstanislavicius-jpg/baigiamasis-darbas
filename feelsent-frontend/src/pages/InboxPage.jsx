import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Inbox, Package, CheckCircle2, XCircle } from 'lucide-react'
import { getInbox, open, guess, react } from '../api/messages'

const TONES = ['SUPPORTIVE', 'FUNNY', 'ROMANTIC', 'BIRTHDAY']
const TONE_LABELS = { SUPPORTIVE: 'Palaikantis', FUNNY: 'Juokingas', ROMANTIC: 'Romantiškas', BIRTHDAY: 'Gimtadieninis' }

const REACTIONS = [
  { value: 'WARMED_UP',  emoji: '🤗', label: 'Sušildė' },
  { value: 'COMFORTED',  emoji: '🫂', label: 'Paguodė' },
  { value: 'INSPIRED',   emoji: '✨', label: 'Įkvėpė' },
  { value: 'CHEERED_UP', emoji: '😊', label: 'Pradžiugino' },
  { value: 'SURPRISED',  emoji: '🎉', label: 'Nustebino' },
  { value: 'CALMED',     emoji: '🕊️', label: 'Nuramino' },
]

const containerVariants = {
  hidden: {},
  show: { transition: { staggerChildren: 0.08 } },
}

const cardVariants = {
  hidden: { opacity: 0, y: 24 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.45, ease: [0.22, 1, 0.36, 1] } },
  exit:   { opacity: 0, scale: 0.95, transition: { duration: 0.25 } },
}

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

  if (loading) return (
    <div className="p-8 flex items-center gap-3" style={{ color: 'var(--text-muted)' }}>
      <div className="w-5 h-5 rounded-full border-2 border-t-transparent animate-spin"
        style={{ borderColor: 'var(--accent-from)', borderTopColor: 'transparent' }} />
      Kraunama...
    </div>
  )

  if (error) return (
    <div className="p-8 text-sm" style={{ color: '#be185d' }}>{error}</div>
  )

  return (
    <div className="p-8 max-w-2xl" style={{ paddingLeft: "2.5rem" }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }}
        className="flex items-center gap-3 mb-8"
      >
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
        >
          <Inbox size={20} color="white" strokeWidth={2} />
        </div>
        <div>
          <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>
            Gautos žinutės
          </h1>
          {messages.length > 0 && (
            <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
              {messages.length} {messages.length === 1 ? 'žinutė' : 'žinutės'} laukia
            </p>
          )}
        </div>
      </motion.div>

      {/* Tuščias inbox */}
      {messages.length === 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="glass text-center py-16 px-8"
        >
          <div className="text-6xl mb-4">📭</div>
          <p className="text-lg font-semibold mb-1" style={{ color: 'var(--text-primary)' }}>
            Nėra naujų žinučių
          </p>
          <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
            Kai draugai atsiųs palinkėjimų, jie atsiras čia
          </p>
        </motion.div>
      )}

      {/* Žinučių sąrašas */}
      <motion.div
        className="flex flex-col gap-4"
        variants={containerVariants}
        initial="hidden"
        animate="show"
      >
        <AnimatePresence>
          {messages.map((m) => (
            <motion.div
              key={m.id}
              variants={cardVariants}
              exit="exit"
              layout
              className="glass p-6"
            >
              {/* Siuntėjas + laikas */}
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                  <div
                    className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white"
                    style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
                  >
                    {m.senderFirstName?.[0]?.toUpperCase()}
                  </div>
                  <div>
                    <p className="text-sm font-semibold" style={{ color: 'var(--text-primary)' }}>
                      {m.senderFirstName}
                    </p>
                  </div>
                </div>
                <p className="text-xs" style={{ color: 'var(--text-muted)' }}>
                  {new Date(m.sentAt).toLocaleString('lt-LT')}
                </p>
              </div>

              {/* SENT — neatidarytas */}
              {m.status === 'SENT' && (
                <div className="text-center py-4">
                  <div className="text-5xl mb-4">📩</div>
                  <p className="text-sm mb-4" style={{ color: 'var(--text-muted)' }}>
                    Gavai naują palinkėjimą!
                  </p>
                  <motion.button
                    whileHover={{ y: -1 }}
                    whileTap={{ scale: 0.97 }}
                    onClick={() => handleOpen(m.id)}
                    className="btn-gradient px-8 py-2.5"
                  >
                    Atidaryti
                  </motion.button>
                </div>
              )}

              {/* OPENED — GUESS režimas */}
              {m.status === 'OPENED' && m.sendMode === 'GUESS' && (
                <div>
                  {m.imageUrl && (
                    <div className="flex justify-center mb-4">
                      <img
                        src={m.imageUrl}
                        alt="palinkėjimas"
                        className="w-48 h-48 object-cover rounded-xl"
                        style={{ border: '1px solid var(--glass-border)' }}
                        onError={(e) => { e.target.style.display = 'none' }}
                      />
                    </div>
                  )}
                  <p className="text-center text-sm font-semibold mb-3" style={{ color: 'var(--text-primary)' }}>
                    Atspėk žinutės toną:
                  </p>
                  <div className="grid grid-cols-2 gap-2">
                    {TONES.map((t) => (
                      <motion.button
                        key={t}
                        whileHover={{ y: -1 }}
                        whileTap={{ scale: 0.97 }}
                        onClick={() => handleGuess(m.id, t)}
                        className="glass-sm py-2.5 text-sm font-medium transition-all"
                        style={{ color: 'var(--text-primary)' }}
                        onMouseEnter={e => {
                          e.currentTarget.style.background = 'linear-gradient(135deg, rgba(190,24,93,0.12), rgba(124,58,237,0.12))'
                          e.currentTarget.style.color = 'var(--accent-from)'
                        }}
                        onMouseLeave={e => {
                          e.currentTarget.style.background = ''
                          e.currentTarget.style.color = 'var(--text-primary)'
                        }}
                      >
                        {TONE_LABELS[t]}
                      </motion.button>
                    ))}
                  </div>
                </div>
              )}

              {/* OPENED — SIMPLE arba PASSIVE */}
              {m.status === 'OPENED' && m.sendMode !== 'GUESS' && (
                <WishContent m={m} onReact={handleReact} />
              )}

              {/* GUESSED */}
              {m.status === 'GUESSED' && (
                <div>
                  <div className="flex items-center gap-2 mb-3">
                    {m.guessResult ? (
                      <>
                        <CheckCircle2 size={16} style={{ color: '#16a34a' }} />
                        <span className="text-sm font-semibold" style={{ color: '#16a34a' }}>
                          Teisingai atspėjai! +5 taškai
                        </span>
                      </>
                    ) : (
                      <>
                        <XCircle size={16} style={{ color: '#be185d' }} />
                        <span className="text-sm font-semibold" style={{ color: '#be185d' }}>
                          Neatspėjai
                        </span>
                      </>
                    )}
                  </div>
                  <WishContent m={m} onReact={handleReact} />
                </div>
              )}
            </motion.div>
          ))}
        </AnimatePresence>
      </motion.div>
    </div>
  )
}

function WishContent({ m, onReact }) {
  return (
    <div className="w-full">
      <div
        className="w-full rounded-2xl px-5 py-4 mb-4 text-center"
        style={{ background: 'rgba(190,24,93,0.07)', border: '1px solid rgba(190,24,93,0.15)' }}
      >
        <p className="text-base font-medium leading-relaxed" style={{ color: 'var(--text-primary)' }}>
          {m.wishText}
        </p>
        <p className="text-xs mt-2" style={{ color: 'var(--text-muted)' }}>
          Tonas: {m.wishToneLabel}
        </p>
      </div>
      <p className="text-sm font-semibold mb-2 text-center" style={{ color: 'var(--text-primary)' }}>
        Kaip jauteis?
      </p>
      <div className="flex flex-wrap justify-center gap-2">
        {REACTIONS.map((r) => (
          <motion.button
            key={r.value}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => onReact(m.id, r.value)}
            className="glass-sm flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium"
            style={{ color: 'var(--text-primary)' }}
          >
            <span>{r.emoji}</span> {r.label}
          </motion.button>
        ))}
      </div>
    </div>
  )
}
