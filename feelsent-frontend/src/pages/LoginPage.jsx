import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { login } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { saveAuth } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(form)
      saveAuth(res.data.token, { id: res.data.id, role: res.data.role, firstName: res.data.firstName, lastName: res.data.lastName })
      navigate('/inbox')
    } catch (err) {
      const msg = err.response?.data?.message || ''
      if (msg.includes('nepatvirtintas')) {
        setError('El. paštas nepatvirtintas. Patikrinkite savo paštą.')
      } else {
        setError(msg || 'Neteisingi duomenys')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      className="min-h-screen flex items-center justify-center p-4"
      style={{
        background: 'linear-gradient(rgba(255,255,255,0.38), rgba(255,255,255,0.38)), url(/flower-login.jpg) center/cover fixed',
      }}
    >
      <motion.div
        initial={{ opacity: 0, y: 36 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
        className="glass w-full max-w-sm p-8"
      >
        <div className="text-center mb-8">
          <h1 className="text-3xl font-extrabold text-gradient mb-1">FeelSent</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Prisijunkite prie savo paskyros</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            className="glass-input"
            type="email"
            placeholder="El. paštas"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />

          <div className="relative">
            <input
              className="glass-input"
              type={showPassword ? 'text' : 'password'}
              placeholder="Slaptažodis"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
              style={{ paddingRight: '2.75rem' }}
            />
            <button
              type="button"
              onMouseDown={() => setShowPassword(true)}
              onMouseUp={() => setShowPassword(false)}
              onMouseLeave={() => setShowPassword(false)}
              className="absolute right-3 top-1/2 -translate-y-1/2"
              style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}
            >
              {showPassword ? '🙈' : '👁'}
            </button>
          </div>

          {error && (
            <motion.p
              initial={{ opacity: 0, x: -8 }}
              animate={{ opacity: 1, x: 0 }}
              className="text-sm"
              style={{ color: '#be185d' }}
            >
              {error}
            </motion.p>
          )}

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="btn-gradient py-3 mt-1"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Jungiamasi...' : 'Prisijungti'}
          </motion.button>
        </form>

        <p className="text-center text-sm mt-6" style={{ color: 'var(--text-muted)' }}>
          Neturi paskyros?{' '}
          <Link to="/register" className="font-semibold" style={{ color: 'var(--accent-from)' }}>
            Registruotis
          </Link>
        </p>
      </motion.div>
    </div>
  )
}
