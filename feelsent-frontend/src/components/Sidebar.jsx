import { NavLink, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import {
  Inbox, Send, Users, Heart, User, Bell,
  Settings, Mail, LogOut, Shield, HelpCircle,
  ChevronRight,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useNotifications } from '../context/NotificationContext'
import { getInbox } from '../api/messages'

const navLinks = [
  { to: '/inbox',         label: 'Gautos žinutės', Icon: Inbox },
  { to: '/send',          label: 'Siųsti',          Icon: Send },
  { to: '/friends',       label: 'Draugai',         Icon: Users },
  { to: '/favorites',     label: 'Mano sąrašas',    Icon: Heart },
  { to: '/profile',       label: 'Profilis',        Icon: User },
  { to: '/notifications', label: 'Pranešimai',      Icon: Bell },
  { to: '/limits',        label: 'Žinučių limitai', Icon: Settings },
  { to: '/help',          label: 'Pagalba',          Icon: HelpCircle },
]

const mobileLinks = [
  { to: '/inbox',         Icon: Inbox },
  { to: '/send',          Icon: Send },
  { to: '/friends',       Icon: Users },
  { to: '/notifications', Icon: Bell },
  { to: '/profile',       Icon: User },
]

const adminLinks = [
  { to: '/admin',       label: 'Valdymas',        Icon: Shield, end: true },
  { to: '/admin/users', label: 'Visi vartotojai', Icon: Users,  end: false },
]

function NavItem({ to, label, Icon, badge, expanded, end = false, blink = false }) {
  return (
    <NavLink
      to={to}
      end={end}
      className="flex items-center px-2 py-2.5 rounded-xl transition-colors duration-150"
      style={({ isActive }) => ({
        background: isActive
          ? 'linear-gradient(135deg, rgba(190,24,93,0.15), rgba(124,58,237,0.15))'
          : 'transparent',
      })}
    >
      {({ isActive }) => (
        <>
          <div className="relative shrink-0 w-8 flex items-center justify-center">
            <Icon
              size={20}
              strokeWidth={isActive ? 2.2 : 1.8}
              className={isActive ? '' : blink ? 'help-blink' : badge > 0 ? 'icon-pulse' : ''}
              style={{ color: isActive ? 'var(--accent-from)' : 'var(--text-muted)' }}
            />
          </div>
          <AnimatePresence>
            {expanded && (
              <motion.span
                initial={{ opacity: 0, x: -6 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -6 }}
                transition={{ duration: 0.15 }}
                className="ml-3 text-sm font-medium whitespace-nowrap"
                style={{ color: isActive ? 'var(--accent-from)' : 'var(--text-primary)' }}
              >
                {label}
              </motion.span>
            )}
          </AnimatePresence>
        </>
      )}
    </NavLink>
  )
}

export default function Sidebar({ noFriends = false }) {
  const { user, logout, isAdmin } = useAuth()
  const { unreadNotifs } = useNotifications()
  const navigate = useNavigate()
  const [expanded, setExpanded] = useState(false)
  const [unreadMessages, setUnreadMessages] = useState(0)

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const msgRes = await getInbox()
        setUnreadMessages(msgRes.data.length)
      } catch {}
    }
    fetchMessages()
    const interval = setInterval(fetchMessages, 30000)
    return () => clearInterval(interval)
  }, [])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const getBadge = (to) => {
    if (to === '/inbox') return unreadMessages
    if (to === '/notifications') return unreadNotifs
    return 0
  }

  return (
    <>
      {/* ── Desktop sidebar ───────────────────────── */}
      <motion.div
        className="hidden md:flex flex-col min-h-screen shrink-0"
        animate={{ width: expanded ? 220 : 64 }}
        transition={{ duration: 0.28, ease: [0.22, 1, 0.36, 1] }}
        style={{
          background: 'var(--sidebar-bg)',
          backdropFilter: 'blur(24px)',
          WebkitBackdropFilter: 'blur(24px)',
          borderRight: '1px solid var(--sidebar-border)',
          overflow: 'hidden',
        }}
      >
        {/* Logo + toggle mygtukas */}
        <div
          className="flex items-center justify-between px-2 py-5"
          style={{ height: '72px', borderBottom: '1px solid rgba(255,255,255,0.5)' }}
        >
          <div className="flex items-center overflow-hidden">
            <div className="w-8 h-8 shrink-0" />
            <AnimatePresence>
              {expanded && (
                <motion.div
                  initial={{ opacity: 0, x: -8 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -8 }}
                  transition={{ duration: 0.18 }}
                  className="ml-3 overflow-hidden"
                >
                  <p className="text-sm font-extrabold text-gradient whitespace-nowrap">FeelSent</p>
                  {user && (
                    <p className="text-xs whitespace-nowrap" style={{ color: 'var(--text-muted)' }}>
                      {user.firstName} {user.lastName}
                    </p>
                  )}
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Toggle mygtukas */}
          <motion.button
            onClick={() => setExpanded(e => !e)}
            animate={{ rotate: expanded ? 180 : 0 }}
            transition={{ duration: 0.25 }}
            className="shrink-0 w-7 h-7 rounded-lg flex items-center justify-center transition-colors"
            style={{ color: 'var(--text-muted)', background: 'rgba(255,255,255,0.4)' }}
            onMouseEnter={e => e.currentTarget.style.color = 'var(--accent-from)'}
            onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
          >
            <ChevronRight size={14} strokeWidth={2} />
          </motion.button>
        </div>

        {/* Navigacija */}
        <nav className="flex-1 py-4 px-2 flex flex-col gap-0.5 overflow-hidden">
          {navLinks.map(({ to, label, Icon }) => (
            <NavItem
              key={to}
              to={to}
              label={label}
              Icon={Icon}
              badge={getBadge(to)}
              expanded={expanded}
              blink={to === '/help' && noFriends}
            />
          ))}

          {!isAdmin() && (
            <NavItem to="/contact" label="Susisiekti su Admin" Icon={Mail} expanded={expanded} />
          )}

          {isAdmin() && (
            <>
              <div className="mx-2 my-2" style={{ height: '1px', background: 'rgba(255,255,255,0.5)' }} />
              {adminLinks.map(({ to, label, Icon, end }) => (
                <NavItem key={to} to={to} label={label} Icon={Icon} expanded={expanded} end={end} />
              ))}
            </>
          )}
        </nav>

        {/* Atsijungti */}
        <div className="px-2 py-4" style={{ borderTop: '1px solid rgba(255,255,255,0.5)' }}>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.97 }}
            onClick={handleLogout}
            className="w-full flex items-center px-2 py-2.5 rounded-xl transition-colors"
            style={{ color: 'var(--text-muted)' }}
            onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
            onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
          >
            <div className="shrink-0 w-8 flex items-center justify-center">
              <LogOut size={18} strokeWidth={1.8} />
            </div>
            <AnimatePresence>
              {expanded && (
                <motion.span
                  initial={{ opacity: 0, x: -6 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.15 }}
                  className="ml-3 text-sm font-medium whitespace-nowrap"
                >
                  Atsijungti
                </motion.span>
              )}
            </AnimatePresence>
          </motion.button>
        </div>
      </motion.div>

      {/* ── Mobile bottom bar ─────────────────────── */}
      <div
        className="md:hidden fixed bottom-0 left-0 right-0 z-50 flex items-center justify-around px-1 py-1"
        style={{
          background: 'var(--sidebar-bg)',
          backdropFilter: 'blur(24px)',
          WebkitBackdropFilter: 'blur(24px)',
          borderTop: '1px solid var(--sidebar-border)',
        }}
      >
        {mobileLinks.map(({ to, Icon }) => {
          const badge = getBadge(to)
          return (
            <NavLink
              key={to}
              to={to}
              className="flex flex-col items-center p-2 rounded-xl"
              style={({ isActive }) => ({
                color: isActive ? 'var(--accent-from)' : 'var(--text-muted)',
                background: isActive ? 'rgba(190,24,93,0.1)' : 'transparent',
              })}
            >
              {({ isActive }) => (
                <div className="relative">
                  <Icon
                    size={22}
                    strokeWidth={isActive ? 2.2 : 1.8}
                    className={badge > 0 && !isActive ? 'icon-pulse' : ''}
                  />
                </div>
              )}
            </NavLink>
          )
        })}

        {isAdmin() ? (
          <NavLink
            to="/admin"
            className="flex flex-col items-center p-2 rounded-xl"
            style={({ isActive }) => ({
              color: isActive ? 'var(--accent-from)' : 'var(--text-muted)',
              background: isActive ? 'rgba(190,24,93,0.1)' : 'transparent',
            })}
          >
            {({ isActive }) => <Shield size={22} strokeWidth={isActive ? 2.2 : 1.8} />}
          </NavLink>
        ) : (
          <NavLink
            to="/contact"
            className="flex flex-col items-center p-2 rounded-xl"
            style={({ isActive }) => ({
              color: isActive ? 'var(--accent-from)' : 'var(--text-muted)',
              background: isActive ? 'rgba(190,24,93,0.1)' : 'transparent',
            })}
          >
            {({ isActive }) => <Mail size={22} strokeWidth={isActive ? 2.2 : 1.8} />}
          </NavLink>
        )}
      </div>
    </>
  )
}
