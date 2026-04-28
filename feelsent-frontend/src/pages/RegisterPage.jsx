import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { register } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '' })
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
      const res = await register(form)
      saveAuth(res.data.token, { id: res.data.id, role: res.data.role, firstName: res.data.firstName, lastName: res.data.lastName })
      navigate('/inbox')
    } catch (err) {
      setError(err.response?.data?.message || 'Registracija nepavyko')
    } finally {
      setLoading(false)
    }
  }

  const field = (key, placeholder, type = 'text') => (
    <input
      className="glass-input"
      type={type}
      placeholder={placeholder}
      value={form[key]}
      onChange={(e) => setForm({ ...form, [key]: e.target.value })}
      required
    />
  )

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
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Sukurkite naują paskyrą</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div className="flex gap-3">
            {field('firstName', 'Vardas')}
            {field('lastName', 'Pavardė')}
          </div>
          {field('email', 'El. paštas', 'email')}

          <div className="relative">
            <input
              className="glass-input"
              type={showPassword ? 'text' : 'password'}
              placeholder="Slaptažodis (min. 6)"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
              minLength={6}
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
            {loading ? 'Kuriama paskyra...' : 'Registruotis'}
          </motion.button>
        </form>

        <p className="text-center text-sm mt-6" style={{ color: 'var(--text-muted)' }}>
          Jau turi paskyrą?{' '}
          <Link to="/login" className="font-semibold" style={{ color: 'var(--accent-from)' }}>
            Prisijungti
          </Link>
        </p>
      </motion.div>
    </div>
  )
}
