import { useState, useRef } from 'react'
import { useNavigate, Link } from 'react-router-dom'
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
        setError('El. paštas nepatvirtintas. Patikrinkite savo paštą ir spauskite patvirtinimo nuorodą.')
      } else {
        setError(msg || 'Neteisingi duomenys')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50">
      <div className="bg-white rounded-xl shadow p-8 w-full max-w-sm">
        <h1 className="text-2xl font-bold text-center mb-6">FeelSent</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            className="border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
            type="email"
            placeholder="El. paštas"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
          <div className="relative">
            <input
              className="w-full border rounded-lg px-4 py-2 pr-10 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              type={showPassword ? 'text' : 'password'}
              placeholder="Slaptažodis"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
            <button
              type="button"
              onMouseDown={() => setShowPassword(true)}
              onMouseUp={() => setShowPassword(false)}
              onMouseLeave={() => setShowPassword(false)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
            >
              {showPassword ? '🙈' : '👁'}
            </button>
          </div>
          {error && <p className="text-red-500 text-sm">{error}</p>}
          <button
            className="bg-indigo-600 text-white rounded-lg py-2 font-medium hover:bg-indigo-700 disabled:opacity-50"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Jungiamasi...' : 'Prisijungti'}
          </button>
        </form>
        <p className="text-center text-sm mt-4 text-slate-500">
          Neturi paskyros?{' '}
          <Link to="/register" className="text-indigo-600 hover:underline">Registruotis</Link>
        </p>
      </div>
    </div>
  )
}
