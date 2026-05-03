import { motion } from 'framer-motion'
import { HelpCircle, Users, Send, Inbox, User, Heart, Bell, Settings, Mail } from 'lucide-react'

const sections = [
  {
    Icon: Users,
    title: 'Draugai — pradėk čia',
    text: 'Tai pirmas žingsnis — be draugų negalėsi siųsti ar gauti žinučių. Įvesk draugo el. paštą, pasirink koks jis tau žmogus (draugas, brolis, partneris ir t.t.) ir išsiųsk pakvietimą. Draugas gaus pranešimą ir turės patvirtinti — tik tada tampate draugais programoje. Tavo draugų sąrašas yra asmeninis — kiti vartotojai jo nemato.',
  },
  {
    Icon: Send,
    title: 'Siųsti',
    text: 'Pasirink draugą — programa pasiūlys žinutę pagal jūsų santykį ir draugo nuotaiką. Jei pasiūlyta žinutė nepatinka, spausk „Kita" ir programa pasiūlys kitą. Prie kiekvienos siūlomos žinutės dešinėje matosi širdutė — ją nuspaudus žinutė išsaugoma tavo mėgstamiausių sąraše. Pasirinkęs žinutę nusprendžia kaip ją siųsti: iš karto (gavėjas matys tekstą) arba atspėjimo režimu (gavėjas turės atspėti stilių iš 4 variantų — abu gauna taškų už teisingą atsakymą).',
  },
  {
    Icon: Inbox,
    title: 'Gautos žinutės',
    text: 'Čia matosi visos tau atsiųstos žinutės. Dalis žinučių rodomos iš karto, kitos — paslėptos ir reikia atspėti teisingą stilių iš 4 variantų (už teisingą atsakymą gauni taškų, taškų gauna ir siuntėjas). Kiekviena žinutė yra vienkartinė — perskaityta išlieka tam tikrą laiką, vėliau išnyksta.',
  },
  {
    Icon: User,
    title: 'Profilis',
    text: 'Nustatyk savo šiandienos nuotaiką ir ko norėtum gauti iš draugų — programa tai naudoja rinkdama tinkamesnes žinutes siuntėjams. Čia taip pat matai surinktus taškus ir savo rangą. Nuotaiką rekomenduojame atnaujinti kasdien — taip draugai gaus tiksliau pritaikytas žinutes.',
  },
  {
    Icon: Heart,
    title: 'Mano sąrašas',
    text: 'Žinutės kurios tau patiko — jas gali išsaugoti čia. Tai tavo asmeninis mėgstamiausių žinučių archyvas, kurį matai tik tu. Išsaugotas žinutes taip pat gali siųsti draugams tiesiai iš šio sąrašo. Sąraše yra baigtinis vietų skaičius — jei vietos nebelieka, ištrink senesnes žinutes kad atsirastų vietos naujoms.',
  },
  {
    Icon: Bell,
    title: 'Pranešimai',
    text: 'Čia gausi sistemos pranešimus — kai kas nors siunčia pakvietimą draugauti, kai gauni žinutę, kai draugas patvirtina pakvietimą ir kt. Neperskaitytus pranešimus matysi kaip raudoną tašką šoniniame meniu.',
  },
  {
    Icon: Settings,
    title: 'Žinučių limitai',
    text: 'Gali nustatyti kiek žinučių per dieną nori gauti iš kiekvieno draugo. Tai naudinga jei nenorite būti užversti žinutėmis — čia galima tai valdyti pagal kiekvieną draugą atskirai.',
  },
  {
    Icon: Mail,
    title: 'Susisiekti su Admin',
    text: 'Turi asmeninę žinutę kurią norėtum siųsti draugams? Parašyk administratoriui — jis sukurs ir priskirs tą žinutę tik tau, niekas kitas jos negaus. Arba — jei turi gerą palinkėjimą kurį norėtum pasidalinti su visais, pateik tekstą ir adminas įkels į bendrą duomenų bazę.',
  },
]

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.07 } },
}

const item = {
  hidden: { opacity: 0, y: 18 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.38, ease: [0.22, 1, 0.36, 1] } },
}

export default function HelpPage() {
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
          <HelpCircle size={20} color="white" strokeWidth={2} />
        </div>
        <div>
          <h1 className="text-2xl font-extrabold" style={{ color: 'var(--text-primary)' }}>Pagalba</h1>
          <p className="text-sm" style={{ color: 'var(--text-muted)' }}>Kaip naudotis programa</p>
        </div>
      </motion.div>

      {/* Kortelės */}
      <motion.div
        className="flex flex-col gap-4"
        variants={stagger}
        initial="hidden"
        animate="show"
      >
        {sections.map(({ Icon, title, text }) => (
          <motion.div key={title} variants={item} className="glass p-5">
            <div className="flex items-center gap-3 mb-2">
              <div
                className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0"
                style={{ background: 'linear-gradient(135deg, var(--accent-from), var(--accent-to))' }}
              >
                <Icon size={16} color="white" strokeWidth={2} />
              </div>
              <h2 className="font-bold text-sm" style={{ color: 'var(--text-primary)' }}>{title}</h2>
            </div>
            <p className="text-sm leading-relaxed" style={{ color: 'var(--text-primary)', paddingLeft: '44px' }}>
              {text}
            </p>
          </motion.div>
        ))}
      </motion.div>
    </div>
  )
}
