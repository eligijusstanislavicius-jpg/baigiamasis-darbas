import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMe, updateMood, updateWant, clearMood, clearWant, getPoints, deleteAccount } from '../api/users'
import { useAuth } from '../context/AuthContext'

const MOODS = [
  { value: 'HAPPY', label: '😊 Laimingas' },
  { value: 'SAD', label: '😢 Liūdnas' },
  { value: 'TIRED', label: '😴 Pavargęs' },
  { value: 'ENERGETIC', label: '⚡ Energingas' },
  { value: 'ANXIOUS', label: '😰 Nerimastingas' },
  { value: 'SICK', label: '🤒 Sergantis' },
  { value: 'STRESSED', label: '😤 Stresas' },
  { value: 'CALM', label: '🧘 Ramus' },
  { value: 'LAZY', label: '🛋️ Tingus' },
  { value: 'HARD_TIME', label: '💪 Sunkus laikotarpis' },
  { value: 'ON_VACATION', label: '🏖️ Atostogauju' },
  { value: 'NOSTALGIC', label: '🌅 Nostalgiškas' },
]

const WANTS = [
  { value: 'CHEER_ME_UP', label: '😄 Pralinksmink mane' },
  { value: 'SUPPORT_ME', label: '🤝 Palaikyk mane' },
  { value: 'INSPIRE_ME', label: '✨ Įkvėpk mane' },
  { value: 'GOOD_DAY', label: '🌞 Geros dienos' },
  { value: 'SWEET_DREAMS', label: '🌙 Saldžių sapnų' },
  { value: 'SURPRISE_ME', label: '🎁 Nustebink mane' },
  { value: 'CALM_ME_DOWN', label: '🕊️ Nuramink mane' },
  { value: 'JUST_BE_THERE', label: '💙 Tyliai šalia' },
]

const stampMoodTime = () => localStorage.setItem('moodSetAt', Date.now().toString())

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

  if (!profile) return <div className="p-8 text-slate-400">Kraunama...</div>

  return (
    <div className="p-8 max-w-2xl">
      <h2 className="text-xl font-bold mb-6">👤 Profilis</h2>

      <div className="bg-white border rounded-xl p-6 mb-6">
        <div className="flex items-center gap-3">
          <p className="text-2xl font-bold">{profile.firstName} {profile.lastName}</p>
          {profile.role === 'ADMIN' && (
            <span className="px-2 py-0.5 bg-violet-100 text-violet-700 text-xs font-semibold rounded-full">
              Administratorius
            </span>
          )}
        </div>
        {points && (
          <>
            <span className="inline-block mt-2 px-3 py-1 bg-indigo-100 text-indigo-700 text-sm font-semibold rounded-full">
              Lygis: {points.rank}
            </span>
            <div className="mt-3">
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">{points.points} taškai</span>
                <span className="text-slate-400">+{points.pointsToNextLevel} iki kito lygio</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-indigo-500 rounded-full transition-all"
                  style={{ width: `${points.percent}%` }}
                />
              </div>
            </div>
          </>
        )}
      </div>

      <div className="bg-white border rounded-xl p-6 mb-6">
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-semibold">Kaip jaučiuosi {saving === 'mood' ? '(saugoma...)' : ''}</h3>
          {profile.moodStatus && (
            <button
              onClick={handleClearMood}
              className="text-xs text-slate-400 hover:text-red-500 transition-colors"
            >
              ✕ Išvalyti
            </button>
          )}
        </div>
        <div className="grid grid-cols-2 gap-2">
          {MOODS.map((m) => (
            <button
              key={m.value}
              onClick={() => handleMood(m.value)}
              className={`px-3 py-2 rounded-lg text-sm text-left transition-colors ${
                profile.moodStatus === m.value
                  ? 'bg-indigo-600 text-white font-medium'
                  : 'bg-slate-50 hover:bg-slate-100 text-slate-700'
              }`}
            >
              {m.label}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-white border rounded-xl p-6 mb-6">
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-semibold">Ko norėčiau gauti {saving === 'want' ? '(saugoma...)' : ''}</h3>
          {profile.moodWant && (
            <button
              onClick={handleClearWant}
              className="text-xs text-slate-400 hover:text-red-500 transition-colors"
            >
              ✕ Išvalyti
            </button>
          )}
        </div>
        <div className="grid grid-cols-2 gap-2">
          {WANTS.map((w) => (
            <button
              key={w.value}
              onClick={() => handleWant(w.value)}
              className={`px-3 py-2 rounded-lg text-sm text-left transition-colors ${
                profile.moodWant === w.value
                  ? 'bg-indigo-600 text-white font-medium'
                  : 'bg-slate-50 hover:bg-slate-100 text-slate-700'
              }`}
            >
              {w.label}
            </button>
          ))}
        </div>
      </div>
      {profile.role !== 'ADMIN' && <div className="bg-white border border-red-200 rounded-xl p-6">
        <h3 className="font-semibold text-red-600 mb-3">Pavojinga zona</h3>
        <p className="text-sm text-slate-500 mb-4">Ištrynus paskyrą visi duomenys bus prarasti ir šio veiksmo negalima atšaukti.</p>
        <button
          onClick={handleDeleteAccount}
          className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
        >
          Ištrinti paskyrą
        </button>
      </div>}
    </div>
  )
}
