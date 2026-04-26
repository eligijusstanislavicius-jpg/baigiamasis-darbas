import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/inbox', label: '📬 Žinutės' },
  { to: '/send', label: '✉️ Siųsti' },
  { to: '/friends', label: '👥 Draugai' },
  { to: '/favorites', label: '❤️ Mano sąrašas' },
  { to: '/profile', label: '👤 Profilis' },
  { to: '/notifications', label: '🔔 Pranešimai' },
  { to: '/limits', label: '⚙️ Limitai' },
  { to: '/contact', label: '📩 Susisiekti' },
]

export default function Sidebar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="w-56 min-h-screen bg-white border-r flex flex-col">
      <div className="px-6 py-5 border-b">
        <p className="font-bold text-indigo-600 text-lg">FeelSent</p>
        {user && (
          <div className="mt-1">
            <p className="text-sm font-medium text-slate-700">{user.firstName} {user.lastName}</p>
            {isAdmin() && (
              <span className="inline-block mt-0.5 px-2 py-0.5 bg-violet-100 text-violet-700 text-xs font-semibold rounded-full">
                Administratorius
              </span>
            )}
          </div>
        )}
      </div>
      <nav className="flex-1 py-4">
        {links.map((l) => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) =>
              `block px-6 py-2.5 text-sm transition-colors ${
                isActive
                  ? 'bg-indigo-50 text-indigo-700 font-medium border-r-2 border-indigo-600'
                  : 'text-slate-600 hover:bg-slate-50'
              }`
            }
          >
            {l.label}
          </NavLink>
        ))}
        {isAdmin() && (
          <>
            <NavLink
              to="/admin"
              end
              className={({ isActive }) =>
                `block px-6 py-2.5 text-sm transition-colors ${
                  isActive
                    ? 'bg-red-50 text-red-700 font-medium border-r-2 border-red-600'
                    : 'text-slate-600 hover:bg-slate-50'
                }`
              }
            >
              🛡️ Valdymas
            </NavLink>
            <NavLink
              to="/admin/wishes"
              className={({ isActive }) =>
                `block px-6 py-2.5 text-sm transition-colors ${
                  isActive
                    ? 'bg-red-50 text-red-700 font-medium border-r-2 border-red-600'
                    : 'text-slate-600 hover:bg-slate-50'
                }`
              }
            >
              📋 Palinkėjimai
            </NavLink>
            <NavLink
              to="/admin/users"
              className={({ isActive }) =>
                `block px-6 py-2.5 text-sm transition-colors ${
                  isActive
                    ? 'bg-red-50 text-red-700 font-medium border-r-2 border-red-600'
                    : 'text-slate-600 hover:bg-slate-50'
                }`
              }
            >
              👥 Visi vartotojai
            </NavLink>
          </>
        )}
      </nav>
      <div className="px-6 py-4 border-t">
        <button
          onClick={handleLogout}
          className="w-full text-sm text-slate-500 hover:text-red-600 text-left"
        >
          Atsijungti
        </button>
      </div>
    </div>
  )
}
