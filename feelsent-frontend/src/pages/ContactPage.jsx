import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Mail, CheckCircle2 } from 'lucide-react'
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
    <div className="p-8 max-w-lg" style={{ paddingLeft: "2.5rem" }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-3 mb-2"
      >
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
        >
          <Mail size={20} color="white" strokeWidth={2} />
        </div>
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>
          Susisiekti su Admin
        </h1>
      </motion.div>
      <p className="text-sm mb-8 ml-[52px]" style={{ color: 'var(--text-muted)' }}>
        Parašyk žinutę — administratorius ją gaus pranešimų lange ir susisieks su tavimi.
      </p>

      <AnimatePresence mode="wait">
        {success ? (
          <motion.div
            key="success"
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0 }}
            className="glass text-center py-14 px-8"
          >
            <CheckCircle2 size={48} className="mx-auto mb-4" style={{ color: '#16a34a' }} />
            <p className="font-semibold text-lg mb-1" style={{ color: 'var(--text-primary)' }}>
              Žinutė išsiųsta!
            </p>
            <p className="text-sm mb-6" style={{ color: 'var(--text-muted)' }}>
              Administratorius netrukus ją perskaitys.
            </p>
            <motion.button
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.97 }}
              onClick={() => setSuccess(false)}
              className="btn-gradient px-8 py-2.5"
            >
              Siųsti dar vieną
            </motion.button>
          </motion.div>
        ) : (
          <motion.div
            key="form"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            className="glass p-6"
          >
            <textarea
              className="glass-input mb-3 resize-none"
              rows={6}
              placeholder="Aprašyk savo klausimą ar pageidavimą..."
              value={text}
              onChange={(e) => setText(e.target.value)}
              style={{ paddingTop: '12px' }}
            />
            {error && (
              <motion.p
                initial={{ opacity: 0, x: -8 }}
                animate={{ opacity: 1, x: 0 }}
                className="text-sm mb-3"
                style={{ color: '#be185d' }}
              >
                {error}
              </motion.p>
            )}
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.97 }}
              onClick={handleSend}
              disabled={loading}
              className="btn-gradient w-full py-3"
            >
              {loading ? 'Siunčiama...' : 'Siųsti žinutę'}
            </motion.button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
