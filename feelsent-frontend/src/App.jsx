import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import { NotificationProvider } from './context/NotificationContext'
import Sidebar from './components/Sidebar'
import MoodPromptModal from './components/MoodPromptModal'

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
import AdminUsersPage from './pages/AdminUsersPage'
import ContactPage from './pages/ContactPage'
import HelpPage from './pages/HelpPage'

/* Sidebar mount'inamas VIENĄ kartą čia — state išlieka naviguojant */
function AuthenticatedLayout() {
  const { isAdmin } = useAuth()
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 overflow-y-auto pb-16 md:pb-0" style={{ paddingLeft: '0' }}>
        <MoodPromptModal />
        <Routes>
          <Route path="/inbox"         element={<InboxPage />} />
          <Route path="/send"          element={<SendPage />} />
          <Route path="/friends"       element={<FriendsPage />} />
          <Route path="/favorites"     element={<FavoritesPage />} />
          <Route path="/profile"       element={<ProfilePage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/limits"        element={<LimitsPage />} />
          <Route path="/contact"       element={<ContactPage />} />
          <Route path="/help"          element={<HelpPage />} />
          <Route path="/admin"         element={isAdmin() ? <AdminPage defaultTab="notify" />  : <Navigate to="/inbox" replace />} />
          <Route path="/admin/wishes"  element={isAdmin() ? <AdminPage defaultTab="wishes" />  : <Navigate to="/inbox" replace />} />
          <Route path="/admin/users"   element={isAdmin() ? <AdminUsersPage />                 : <Navigate to="/inbox" replace />} />
          <Route path="*"              element={<Navigate to="/inbox" replace />} />
        </Routes>
      </main>
    </div>
  )
}

function AppRoutes() {
  const { token } = useAuth()
  return (
    <Routes>
      <Route path="/login"    element={token ? <Navigate to="/inbox" replace /> : <LoginPage />} />
      <Route path="/register" element={token ? <Navigate to="/inbox" replace /> : <RegisterPage />} />
      <Route path="/*"        element={token ? <AuthenticatedLayout /> : <Navigate to="/login" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <NotificationProvider>
          <AppRoutes />
        </NotificationProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
