import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Sidebar from './components/Sidebar'

import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import InboxPage from './pages/InboxPage'
import SendPage from './pages/SendPage'
import FriendsPage from './pages/FriendsPage'
import FavoritesPage from './pages/FavoritesPage'
import ProfilePage from './pages/ProfilePage'
import NotificationsPage from './pages/NotificationsPage'
import LimitsPage from './pages/LimitsPage'
import AdminPage from './pages/AdminPage'

function PrivateLayout({ children }) {
  const { token } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 bg-slate-50 overflow-y-auto">
        {children}
      </main>
    </div>
  )
}

function AdminRoute({ children }) {
  const { token, isAdmin } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (!isAdmin()) return <Navigate to="/inbox" replace />
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 bg-slate-50 overflow-y-auto">
        {children}
      </main>
    </div>
  )
}

function AppRoutes() {
  const { token } = useAuth()
  return (
    <Routes>
      <Route path="/login" element={token ? <Navigate to="/inbox" replace /> : <LoginPage />} />
      <Route path="/register" element={token ? <Navigate to="/inbox" replace /> : <RegisterPage />} />
      <Route path="/inbox" element={<PrivateLayout><InboxPage /></PrivateLayout>} />
      <Route path="/send" element={<PrivateLayout><SendPage /></PrivateLayout>} />
      <Route path="/friends" element={<PrivateLayout><FriendsPage /></PrivateLayout>} />
      <Route path="/favorites" element={<PrivateLayout><FavoritesPage /></PrivateLayout>} />
      <Route path="/profile" element={<PrivateLayout><ProfilePage /></PrivateLayout>} />
      <Route path="/notifications" element={<PrivateLayout><NotificationsPage /></PrivateLayout>} />
      <Route path="/limits" element={<PrivateLayout><LimitsPage /></PrivateLayout>} />
      <Route path="/admin" element={<AdminRoute><AdminPage /></AdminRoute>} />
      <Route path="*" element={<Navigate to={token ? '/inbox' : '/login'} replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
