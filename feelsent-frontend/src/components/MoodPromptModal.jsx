import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function MoodPromptModal() {
  const { showMoodPrompt, setShowMoodPrompt } = useAuth()
  const navigate = useNavigate()

  if (!showMoodPrompt) return null

  const handleUpdate = () => {
    setShowMoodPrompt(false)
    navigate('/profile')
  }

  const handleSkip = () => {
    localStorage.setItem('moodSetAt', Date.now().toString())
    setShowMoodPrompt(false)
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full mx-4 text-center">
        <div className="text-4xl mb-4">💭</div>
        <h2 className="text-lg font-bold mb-2">Kaip šiandien jautiesi?</h2>
        <p className="text-slate-500 text-sm mb-6">
          Praėjo daugiau nei 24 val. nuo paskutinio nuotaikos atnaujinimo. Draugai galės siųsti
          tinkamesnius palinkėjimus.
        </p>
        <div className="flex flex-col gap-2">
          <button
            onClick={handleUpdate}
            className="bg-indigo-600 text-white rounded-xl py-2.5 font-medium hover:bg-indigo-700"
          >
            Atnaujinti nuotaiką
          </button>
          <button
            onClick={handleSkip}
            className="text-slate-400 text-sm hover:text-slate-600 py-1"
          >
            Palikti kaip yra
          </button>
        </div>
      </div>
    </div>
  )
}
