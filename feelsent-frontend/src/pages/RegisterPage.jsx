import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: ''
  })
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

  const f = (field) => ({
    value: form[field],
    onChange: (e) => setForm({ ...form, [field]: e.target.value }),
    required: true,
    className: 'border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400'
  })

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50">
      <div className="bg-white rounded-xl shadow p-8 w-full max-w-sm">
        <h1 className="text-2xl font-bold text-center mb-6">Registracija</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input {...f('firstName')} placeholder="Vardas" />
          <input {...f('lastName')} placeholder="Pavardė" />
          <input {...f('email')} type="email" placeholder="El. paštas" />
          <div className="relative">
            <input
              {...f('password')}
              type={showPassword ? 'text' : 'password'}
              placeholder="Slaptažodis (min. 6)"
              minLength={6}
              className="w-full border rounded-lg px-4 py-2 pr-10 focus:outline-none focus:ring-2 focus:ring-indigo-400"
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
            {loading ? 'Kuriama paskyra...' : 'Registruotis'}
          </button>
        </form>
        <p className="text-center text-sm mt-4 text-slate-500">
          Jau turi paskyrą?{' '}
          <Link to="/login" className="text-indigo-600 hover:underline">Prisijungti</Link>
        </p>
      </div>
    </div>
  )
}
