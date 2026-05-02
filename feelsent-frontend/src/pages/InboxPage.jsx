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
  const [envelopeState, setEnvelopeState] = useState({})
  // { [messageId]: 'closed' | 'picking' | 'opening' }

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
    setEnvelopeState(s => ({ ...s, [id]: 'opening' }))
    let responseData = null
    try {
      const res = await guess(id, tone)
      responseData = res.data
    } catch (err) {
      alert(err.response?.data?.message || 'Klaida')
      setEnvelopeState(s => ({ ...s, [id]: 'picking' }))
      return
    }
    setTimeout(() => {
      if (responseData) {
        setMessages(prev => prev.map(m => m.id === id ? responseData : m))
      }
    }, 950)
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
                    className="btn-gradient py-2.5"
                    style={{ paddingLeft: '5px', paddingRight: '5px' }}
                  >
                    Atidaryti
                  </motion.button>
                </div>
              )}

              {/* OPENED — GUESS režimas */}
              {m.status === 'OPENED' && m.sendMode === 'GUESS' && (
                <EnvelopeGuess
                  m={m}
                  state={envelopeState[m.id] || 'closed'}
                  onClickEnvelope={() => setEnvelopeState(s => ({ ...s, [m.id]: 'picking' }))}
                  onGuess={(tone) => handleGuess(m.id, tone)}
                />
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

function EnvelopeGuess({ m, state, onClickEnvelope, onGuess }) {
  const isClosed   = state === 'closed'
  const isPicking  = state === 'picking'
  const isOpening  = state === 'opening'

  return (
    <div className="flex flex-col items-center gap-4 py-2">

      {/* Vokas */}
      <motion.div
        className="relative w-full max-w-xs select-none"
        style={{ perspective: '1000px' }}
        onClick={isClosed ? onClickEnvelope : undefined}
        whileHover={isClosed ? { scale: 1.02 } : {}}
        whileTap={isClosed ? { scale: 0.98 } : {}}
      >
        <div
          className="rounded-2xl h-44 relative"
          style={{
            background: 'rgba(255,255,255,0.72)',
            border: '1px solid rgba(255,255,255,0.90)',
            boxShadow: '0 8px 32px rgba(190,24,93,0.10)',
            cursor: isClosed ? 'pointer' : 'default',
            overflow: 'visible',
          }}
        >
          {/* Dekoratyvinė apatinė dalis */}
          <div style={{
            position: 'absolute', bottom: 0, left: 0, right: 0, height: '55%',
            borderRadius: '0 0 20px 20px',
            background: 'rgba(190,24,93,0.04)',
          }} />

          {/* Flapas — trikampis viršuje */}
          <motion.div
            style={{
              position: 'absolute', top: 0, left: 0, right: 0, height: '55%',
              background: 'linear-gradient(160deg, rgba(190,24,93,0.20), rgba(124,58,237,0.20))',
              clipPath: 'polygon(0 0, 100% 0, 50% 100%)',
              transformOrigin: 'top center',
              zIndex: 3,
              borderRadius: '20px 20px 0 0',
            }}
            animate={isOpening ? { rotateX: -180, opacity: 0 } : { rotateX: 0, opacity: 1 }}
            transition={{ duration: 0.55, ease: [0.22, 1, 0.36, 1] }}
          />

          {/* Voko vidurys — emoji + tekstas */}
          <AnimatePresence>
            {!isOpening && (
              <motion.div
                key="inner"
                initial={{ opacity: 1 }}
                exit={{ opacity: 0, transition: { duration: 0.2 } }}
                className="absolute inset-0 flex flex-col items-center justify-center gap-1.5"
                style={{ zIndex: 2, paddingTop: '12px' }}
              >
                <motion.span
                  className="text-4xl"
                  animate={isClosed ? { rotate: [0, -6, 6, 0] } : {}}
                  transition={{ repeat: Infinity, repeatDelay: 2.5, duration: 0.5 }}
                >
                  ✉️
                </motion.span>
                <p className="text-sm font-semibold" style={{ color: 'var(--text-primary)' }}>
                  Turi žinutę!
                </p>
                {isClosed && (
                  <p className="text-xs" style={{ color: 'var(--text-muted)' }}>
                    Paspausk kad atspėtum
                  </p>
                )}
                {isPicking && (
                  <motion.p
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="text-xs font-medium"
                    style={{ color: 'var(--accent-from)' }}
                  >
                    Pasirink stilių žemiau ↓
                  </motion.p>
                )}
              </motion.div>
            )}
          </AnimatePresence>

          {/* Atvirute — islenda kai vokas atsidaro */}
          <AnimatePresence>
            {isOpening && (
              <motion.div
                key="card"
                style={{
                  position: 'absolute',
                  bottom: 8, left: 12, right: 12,
                  zIndex: 4,
                  background: 'white',
                  borderRadius: '14px',
                  padding: '16px',
                  boxShadow: '0 4px 20px rgba(190,24,93,0.18)',
                  border: '1px solid rgba(190,24,93,0.15)',
                }}
                initial={{ y: 50, opacity: 0 }}
                animate={{ y: -36, opacity: 1 }}
                transition={{ delay: 0.3, duration: 0.55, ease: [0.22, 1, 0.36, 1] }}
              >
                <p className="text-sm font-medium text-center leading-relaxed" style={{ color: '#1a1a1a' }}>
                  {m.wishText}
                </p>
                <p className="text-xs mt-1.5 text-center" style={{ color: 'rgba(0,0,0,0.4)' }}>
                  {m.wishToneLabel}
                </p>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </motion.div>

      {/* Stiliaus spėjimo mygtukai */}
      {isPicking && (
        <motion.div
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
          className="w-full"
        >
          <p className="text-xs mb-2.5 text-center font-medium" style={{ color: 'var(--text-primary)' }}>
            Atspėk palinkėjimo stilių:
          </p>
          <div className="grid grid-cols-2 gap-2">
            {TONES.map((t) => (
              <motion.button
                key={t}
                whileHover={{ y: -1 }}
                whileTap={{ scale: 0.97 }}
                onClick={() => onGuess(t)}
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
        </motion.div>
      )}
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
          Stilius: {m.wishToneLabel}
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
