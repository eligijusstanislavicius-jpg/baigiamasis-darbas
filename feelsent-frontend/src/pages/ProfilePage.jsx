import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { User, AlertTriangle } from 'lucide-react'
import { getMe, updateMood, updateWant, clearMood, clearWant, getPoints, deleteAccount } from '../api/users'
import { useAuth } from '../context/AuthContext'

const MOODS = [
  { value: 'HAPPY',       label: '😊 Laimingas' },
  { value: 'SAD',         label: '😢 Liūdnas' },
  { value: 'TIRED',       label: '😴 Pavargęs' },
  { value: 'ENERGETIC',   label: '⚡ Energingas' },
  { value: 'ANXIOUS',     label: '😰 Nerimastingas' },
  { value: 'SICK',        label: '🤒 Sergantis' },
  { value: 'STRESSED',    label: '😤 Stresas' },
  { value: 'CALM',        label: '🧘 Ramus' },
  { value: 'LAZY',        label: '🛋️ Tingus' },
  { value: 'HARD_TIME',   label: '💪 Sunkus laikotarpis' },
  { value: 'ON_VACATION', label: '🏖️ Atostogauju' },
  { value: 'NOSTALGIC',   label: '🌅 Nostalgiškas' },
]

const WANTS = [
  { value: 'CHEER_ME_UP',  label: '😄 Pralinksmink mane' },
  { value: 'SUPPORT_ME',   label: '🤝 Palaikyk mane' },
  { value: 'INSPIRE_ME',   label: '✨ Įkvėpk mane' },
  { value: 'GOOD_DAY',     label: '🌞 Geros dienos' },
  { value: 'SWEET_DREAMS', label: '🌙 Saldžių sapnų' },
  { value: 'SURPRISE_ME',  label: '🎁 Nustebink mane' },
  { value: 'CALM_ME_DOWN', label: '🕊️ Nuramink mane' },
  { value: 'JUST_BE_THERE',label: '💙 Tyliai šalia' },
]

const stampMoodTime = () => localStorage.setItem('moodSetAt', Date.now().toString())

const fadeUp = { hidden: { opacity: 0, y: 20 }, show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: [0.22,1,0.36,1] } } }

export default function ProfilePage() {
  const [profile, setProfile] = useState(null)
  const [points, setPoints] = useState(null)
  const [saving, setSaving] = useState('')
  const { logout } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    Promise.all([getMe(), getPoints()]).then(([pRes, ptRes]) => {
      setProfile(pRes.data)
      setPoints(ptRes.data)
    })
  }, [])

  const handleMood = async (value) => {
    setSaving('mood')
    try {
      await updateMood(value)
      setProfile((p) => ({ ...p, moodStatus: value }))
      stampMoodTime()
    } finally {
      setSaving('')
    }
  }

  const handleClearMood = async () => {
    setSaving('mood')
    try {
      await clearMood()
      setProfile((p) => ({ ...p, moodStatus: null }))
      localStorage.removeItem('moodSetAt')
    } finally {
      setSaving('')
    }
  }

  const handleWant = async (value) => {
    setSaving('want')
    try {
      await updateWant(value)
      setProfile((p) => ({ ...p, moodWant: value }))
      stampMoodTime()
    } finally {
      setSaving('')
    }
  }

  const handleClearWant = async () => {
    setSaving('want')
    try {
      await clearWant()
      setProfile((p) => ({ ...p, moodWant: null }))
    } finally {
      setSaving('')
    }
  }

  const handleDeleteAccount = async () => {
    if (!window.confirm('Ar tikrai norite ištrinti paskyrą? Visi duomenys bus prarasti ir šio veiksmo negalima atšaukti.')) return
    await deleteAccount()
    logout()
    navigate('/login')
  }

  if (!profile) return (
    <div className="p-8 flex items-center gap-3" style={{ color: 'var(--text-muted)' }}>
      <div className="w-5 h-5 rounded-full border-2 animate-spin"
        style={{ borderColor: 'var(--accent-from)', borderTopColor: 'transparent' }} />
      Kraunama...
    </div>
  )

  return (
    <div className="p-8 max-w-2xl" style={{ paddingLeft: '2.5rem' }}>
      {/* Antraštė */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-3 mb-8"
      >
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
        >
          <User size={20} color="white" strokeWidth={2} />
        </div>
        <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Profilis</h1>
      </motion.div>

      {/* Profilio kortelė */}
      <motion.div variants={fadeUp} initial="hidden" animate="show" className="glass p-6 mb-5" style={{ paddingLeft: '28px' }}>
        <div className="flex items-center gap-4">
          <div
            className="w-14 h-14 rounded-2xl flex items-center justify-center text-2xl font-bold text-white"
            style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
          >
            {profile.firstName?.[0]?.toUpperCase()}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <p className="text-xl font-extrabold" style={{ color: 'var(--text-primary)' }}>
                {profile.firstName} {profile.lastName}
              </p>
              {profile.role === 'ADMIN' && (
                <span
                  className="px-2 py-0.5 text-xs font-bold rounded-full"
                  style={{
                    background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
                    color: 'white',
                  }}
                >
                  Administratorius
                </span>
              )}
            </div>
            {points && (
              <span
                className="inline-block mt-1 py-0.5 text-xs font-semibold rounded-full"
                style={{ background: 'rgba(190,24,93,0.12)', color: 'var(--accent-from)', paddingLeft: '3px', paddingRight: '3px' }}
              >
                {points.rank}
              </span>
            )}
          </div>
        </div>

        {points && (
          <div className="mt-4" style={{ paddingLeft: '5px' }}>
            <div className="flex justify-between text-sm mb-1.5">
              <span style={{ color: 'var(--text-primary)' }}>{points.points} taškų</span>
              <span style={{ color: 'var(--text-muted)' }}>+{points.pointsToNextLevel} iki kito lygio</span>
            </div>
            <div className="h-2 rounded-full overflow-hidden" style={{ background: 'rgba(255,255,255,0.4)' }}>
              <motion.div
                className="h-full rounded-full"
                style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
                initial={{ width: 0 }}
                animate={{ width: `${points.percent}%` }}
                transition={{ duration: 0.8, ease: [0.22, 1, 0.36, 1] }}
              />
            </div>
          </div>
        )}
      </motion.div>

      {/* Nuotaika */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="glass p-6 mb-5"
      >
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-sm" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
            Kaip jaučiuosi {saving === 'mood' ? '(saugoma...)' : ''}
          </h3>
          {profile.moodStatus && (
            <button
              onClick={handleClearMood}
              className="text-xs transition-colors"
              style={{ color: 'var(--text-muted)', paddingRight: '5px' }}
              onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
              onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
            >
              ✕ Išvalyti
            </button>
          )}
        </div>
        <div className="grid grid-cols-2 gap-2">
          {MOODS.map((m) => {
            const active = profile.moodStatus === m.value
            return (
              <motion.button
                key={m.value}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.97 }}
                onClick={() => handleMood(m.value)}
                className="px-3 py-2 rounded-xl text-sm text-left font-medium transition-all"
                style={active ? {
                  background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
                  color: 'white',
                } : {
                  background: 'rgba(255,255,255,0.45)',
                  color: 'var(--text-primary)',
                  border: '1px solid rgba(255,255,255,0.7)',
                }}
              >
                {m.label}
              </motion.button>
            )
          })}
        </div>
      </motion.div>

      {/* Ko norėčiau gauti */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.15 }}
        className="glass p-6 mb-5"
      >
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-sm" style={{ color: 'var(--text-primary)', paddingLeft: '5px' }}>
            Ko norėčiau gauti {saving === 'want' ? '(saugoma...)' : ''}
          </h3>
          {profile.moodWant && (
            <button
              onClick={handleClearWant}
              className="text-xs transition-colors"
              style={{ color: 'var(--text-muted)', paddingRight: '5px' }}
              onMouseEnter={e => e.currentTarget.style.color = '#be185d'}
              onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
            >
              ✕ Išvalyti
            </button>
          )}
        </div>
        <div className="grid grid-cols-2 gap-2">
          {WANTS.map((w) => {
            const active = profile.moodWant === w.value
            return (
              <motion.button
                key={w.value}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.97 }}
                onClick={() => handleWant(w.value)}
                className="px-3 py-2 rounded-xl text-sm text-left font-medium transition-all"
                style={active ? {
                  background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))',
                  color: 'white',
                } : {
                  background: 'rgba(255,255,255,0.45)',
                  color: 'var(--text-primary)',
                  border: '1px solid rgba(255,255,255,0.7)',
                }}
              >
                {w.label}
              </motion.button>
            )
          })}
        </div>
      </motion.div>

      {/* Pavojinga zona */}
      {profile.role !== 'ADMIN' && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="glass p-6"
          style={{ border: '2px solid #be185d', paddingLeft: '29px', marginTop: '4px' }}
        >
          <div className="flex items-center gap-2 mb-2">
            <AlertTriangle size={16} style={{ color: '#be185d' }} />
            <h3 className="font-semibold text-sm" style={{ color: '#be185d' }}>Pavojinga zona</h3>
          </div>
          <p className="text-xs mb-4" style={{ color: 'var(--text-muted)' }}>
            Ištrynus paskyrą visi duomenys bus prarasti ir šio veiksmo negalima atšaukti.
          </p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.97 }}
            onClick={handleDeleteAccount}
            className="py-2 rounded-xl text-sm font-medium text-white"
            style={{ background: '#be185d', paddingLeft: '5px', paddingRight: '5px' }}
          >
            Ištrinti paskyrą
          </motion.button>
        </motion.div>
      )}
    </div>
  )
}
