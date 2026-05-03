import { useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { HelpCircle, X } from 'lucide-react'

export default function NewUserWelcomeModal({ onClose }) {
  const navigate = useNavigate()

  const handleGoToHelp = () => {
    onClose()
    navigate('/help')
  }

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 z-50 flex items-center justify-center p-4"
        style={{ background: 'rgba(0,0,0,0.5)', backdropFilter: 'blur(8px)', WebkitBackdropFilter: 'blur(8px)' }}
        onClick={onClose}
      >
        <motion.div
          initial={{ scale: 0.88, y: 24, opacity: 0 }}
          animate={{ scale: 1, y: 0, opacity: 1 }}
          exit={{ scale: 0.88, y: 24, opacity: 0 }}
          transition={{ type: 'spring', stiffness: 320, damping: 28 }}
          className="relative rounded-2xl p-6 max-w-sm w-full"
          style={{ background: 'var(--card-bg)', border: '1px solid var(--card-border)' }}
          onClick={e => e.stopPropagation()}
        >
          <button
            onClick={onClose}
            className="absolute top-3 right-3 p-1 rounded-lg transition-colors"
            style={{ color: 'var(--text-muted)' }}
          >
            <X size={16} />
          </button>

          <div className="flex flex-col items-center text-center gap-4 pt-2">
            <div
              className="w-16 h-16 rounded-full flex items-center justify-center"
              style={{ background: 'linear-gradient(135deg, rgba(190,24,93,0.15), rgba(124,58,237,0.15))' }}
            >
              <HelpCircle size={32} style={{ color: 'var(--accent-from)' }} />
            </div>

            <div>
              <h2 className="text-lg font-bold mb-2" style={{ color: 'var(--text-primary)' }}>
                Sveiki atvykę i FeelSent!
              </h2>
              <p className="text-sm leading-relaxed" style={{ color: 'var(--text-muted)' }}>
                Atrodo, kad dar neturite draugų sąraše. Susipažinkite su programa —
                pagalbos puslapyje rasite viską, ko reikia sėkmingai pradžiai.
              </p>
            </div>

            <button
              onClick={handleGoToHelp}
              className="w-full py-2.5 rounded-xl text-sm font-semibold text-white transition-opacity"
              style={{ background: 'linear-gradient(135deg, #be185d, #7c3aed)', paddingLeft: '5px', paddingRight: '5px' }}
              onMouseEnter={e => e.currentTarget.style.opacity = '0.9'}
              onMouseLeave={e => e.currentTarget.style.opacity = '1'}
            >
              Peržiureti pagalba
            </button>

            <button
              onClick={onClose}
              className="text-xs transition-colors"
              style={{ color: 'var(--text-muted)' }}
            >
              Uzdaryti
            </button>
          </div>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  )
}