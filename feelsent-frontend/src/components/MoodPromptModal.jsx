import { useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuth } from '../context/AuthContext'

export default function MoodPromptModal() {
  const { showMoodPrompt, setShowMoodPrompt } = useAuth()
  const navigate = useNavigate()

  const handleUpdate = () => {
    setShowMoodPrompt(false)
    navigate('/profile')
  }

  const handleSkip = () => {
    localStorage.setItem('moodSetAt', Date.now().toString())
    setShowMoodPrompt(false)
  }

  return (
    <AnimatePresence>
      {showMoodPrompt && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 flex items-center justify-center p-4"
          style={{ background: 'rgba(30,27,75,0.35)', backdropFilter: 'blur(6px)' }}
          onClick={handleSkip}
        >
          <motion.div
            initial={{ opacity: 0, scale: 0.92, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.92, y: 20 }}
            transition={{ duration: 0.35, ease: [0.22, 1, 0.36, 1] }}
            className="glass max-w-sm w-full p-8 text-center"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="text-5xl mb-4">💭</div>
            <h2 className="text-lg font-extrabold mb-2" style={{ color: 'var(--text-primary)' }}>
              Kaip šiandien jautiesi?
            </h2>
            <p className="text-sm mb-6" style={{ color: 'var(--text-muted)' }}>
              Praėjo daugiau nei 24 val. nuo paskutinio nuotaikos atnaujinimo. Draugai galės siųsti
              tinkamesnius palinkėjimus.
            </p>
            <div className="flex flex-col gap-2">
              <motion.button
                whileHover={{ scale: 1.03 }}
                whileTap={{ scale: 0.97 }}
                onClick={handleUpdate}
                className="btn-gradient py-2.5 font-semibold"
              >
                Atnaujinti nuotaiką
              </motion.button>
              <button
                onClick={handleSkip}
                className="text-sm py-1 transition-colors"
                style={{ color: 'var(--text-muted)' }}
                onMouseEnter={e => e.currentTarget.style.color = 'var(--text-primary)'}
                onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
              >
                Palikti kaip yra
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}
