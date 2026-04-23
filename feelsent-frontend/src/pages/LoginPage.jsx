import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
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
      saveAuth(res.data.token, { username: res.data.username })
      navigate('/inbox')
    } catch (err) {
      setError(err.response?.data?.message || 'Neteisingi duomenys')
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
          <input
            className="border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
            type="password"
            placeholder="Slaptažodis"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
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
