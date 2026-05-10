# FeelSent

Socialinė platforma, kurioje vartotojai siunčia vienas kitam personalizuotus palinkėjimus pagal nuotaiką ir ryšio tipą.

🔗 **Live demo:** http://168.231.126.172:8080/

---

## Technologijos

**Backend**
- Java 17, Spring Boot 3.4
- Spring Security + JWT autentifikacija
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

**Frontend**
- React + Vite
- Tailwind CSS v4

**Deploy**
- VPS (Hostinger) — Spring Boot + PostgreSQL
- Brevo SMTP — el. laiškų siuntimas

---

## Pagrindinės funkcijos

- Registracija, prisijungimas su JWT tokenu
- Draugų sistema (pakvietimas, priėmimas, ryšio tipai)
- Palinkėjimų siuntimas pagal gavėjo nuotaiką
- GUESS režimas — gavėjas spėja palinkėjimo toną
- Reakcijos į žinutes su taškų sistema
- Rangų sistema (Naujokas → Širdies žmogus)
- Pranešimų sistema (real-time notifikacijos)
- Dienos žinučių limitai tarp draugų
- Automatinis re-engagement el. laiškas neaktyviems vartotojams
- Admin panelė — vartotojų, palinkėjimų valdymas

---

## Architektūra

```
React (Vite) → bundled į Spring Boot static/
Spring Boot REST API (11 kontrolerių)
PostgreSQL duomenų bazė
JWT stateless autentifikacija
4 automatizuotos Scheduler užduotys
```
