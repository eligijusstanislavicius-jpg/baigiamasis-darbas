# GLEBIS – Projekto santrauka (baigiamasis darbas 2026)

## Technologijos
- Java 17, Spring Boot 3.4.4, Hibernate/JPA, PostgreSQL
- Spring Security + JWT (jjwt 0.12.6)
- El. paštas: Brevo SMTP
- Talpinimas: Railway (backend + DB), Netlify (frontend)
- Frontend: HTML + Tailwind CSS

## Architektūra
```
Controller → Service → Repository → PostgreSQL
Paketas: com.glebis
```

## Projekto esmė
Aplikacija leidžia artimais žmonėmis keistis emociniais palinkėjimais. Siuntėjas pasirenka palinkėjimą iš DB, gavėjas atidaro "voką", atspėja ką gavo ir reaguoja. Nėra laisvo teksto – visas turinys iš DB. Taškai skiriami TIK už gavėjo reakciją – ne už siuntimą.

---

## Entitetai (model/)

| Entitetas | Svarbiausi laukai |
|---|---|
| User | id, username, email, passwordHash, moodStatus, moodWant, points, createdAt |
| Friendship | id, sender, receiver, relationshipType, status, createdAt |
| Wish | id, text, tone, relationshipType, active |
| FavoriteWish | id, user, text, createdAt (max 10 vnt.) |
| Message | id, sender, receiver, wish, favoriteWish, sendMode, status, guessResult, reaction, sentAt |
| PointTransaction | id, user, message, points, reason, createdAt |
| MessageLimit | id, receiver, sender, dailyLimit |

> Wish.id = paveikslėlio pavadinimas → `/static/images/wishes/{id}.png`

---

## Enum klasės (enums/)

- **MoodStatus:** LAIMINGAS, LIUDNAS, PAVARGES, ENERGINGAS, NERIMAS, SERGANTIS, STRESAS, RAMUS, TINGUS, SUNKUS_LAIKOTARPIS, ATOSTOGAUJU, NOSTALGISKAS
- **MoodWant:** PRALINKSMINK, PALAIKYK, IKVEIPK, GEROS_DIENOS, SALDZIU_SAPNU, NUSTEBINK, PARODYK_MEILE, NURAMINK, TYLIAI_SALIA
- **RelationshipType:** MAMA, TETIS, VAIKAS, PARTNERIS, DRAUGAS, BROLIS, SESUO, SENELIS, MOCIUTE
- **FriendshipStatus:** PENDING, ACCEPTED, DECLINED
- **WishTone:** SUPPORTIVE, FUNNY, ROMANTIC, BIRTHDAY
- **SendMode:** SIMPLE, GUESS, PASSIVE
- **MessageStatus:** SENT, OPENED, GUESSED, REACTED
- **PointReason:** GUESS_CORRECT (5t), REACTION_RECEIVED (10t)

---

## Repository (repository/)

- **UserRepository:** findByEmail, findByUsername, existsByEmail
- **FriendshipRepository:** findBySenderAndReceiver, findAllByReceiverAndStatus, findAllBySenderAndStatus, existsBySenderAndReceiver
- **WishRepository:** findByToneAndRelationshipTypeAndActiveTrue, findByRelationshipTypeAndActiveTrue, findAllByActiveTrue
- **FavoriteWishRepository:** findAllByUser, countByUser, findByIdAndUser
- **MessageRepository:** findAllByReceiverOrderBySentAtDesc, findAllBySenderAndReceiverAndSentAtAfter, countBySenderAndReceiverAndSentAtAfter, findAllBySenderAndReceiver
- **PointTransactionRepository:** findAllByUser, @Query sumPointsByUser
- **MessageLimitRepository:** findByReceiverAndSender, existsByReceiverAndSender

---

## Service (service/)

| Servisas | Metodai |
|---|---|
| UserService | register, login, updateMoodStatus, updateMoodWant, getProfile, getPointsProgress |
| FriendshipService | sendInvite, acceptFriendship, declineFriendship, getFriendsWithMood, areFriends |
| WishService | getFilteredWish, getRandomWish, getGuessOptions |
| FavoriteWishService | getAll, add, delete |
| MessageService | sendFromDb, sendFromFavorite, openMessage, submitGuess, submitReaction, getInbox |
| PointService | addPoints, getTotalPoints, getProgress (kiekvienas lygis = 100t) |
| EmailService | sendInviteEmail (Brevo SMTP) |
| MessageLimitService | setLimit, removeLimit, checkLimit (per 24val) |

---

## Controller – REST API (controller/)

```
POST   /api/auth/register
POST   /api/auth/login

GET    /api/users/me
PATCH  /api/users/me/mood
PATCH  /api/users/me/want
GET    /api/users/me/points

POST   /api/friendships/invite
POST   /api/friendships/{id}/accept
POST   /api/friendships/{id}/decline
GET    /api/friendships/friends

GET    /api/wishes/filtered     ?tone=ROMANTIC&relationshipType=PARTNERIS
GET    /api/wishes/random       ?relationshipType=DRAUGAS

GET    /api/favorites
POST   /api/favorites
DELETE /api/favorites/{id}

POST   /api/messages/send
GET    /api/messages/inbox
POST   /api/messages/{id}/open
POST   /api/messages/{id}/guess
POST   /api/messages/{id}/react

POST   /api/limits/{friendId}
DELETE /api/limits/{friendId}
```

> `/api/auth/**` – laisvi. Visi kiti – reikalauja JWT.

---

## DTO (dto/)

- **RegisterRequest:** username, email, password
- **LoginRequest:** email, password
- **AuthResponse:** token, username
- **UserProfileResponse:** id, username, moodStatus, moodWant, points
- **SendMessageRequest:** receiverId, wishId (arba favoriteWishId), sendMode
- **GuessRequest:** guessedTone
- **ReactionRequest:** reaction
- **MessageResponse:** id, senderUsername, sendMode, status, imageUrl, wishText, reaction, sentAt

---

## Config (config/)

- **SecurityConfig** – `/api/auth/**` laisvi, kiti su JWT
- **JwtConfig** – generuoja ir tikrina token, galioja 24val
- **JwtFilter** – OncePerRequestFilter, tikrina `Authorization: Bearer <token>`, 401 jei negalioja
- **DataLoader** – užkrauna 200+ palinkėjimų į DB (tikrina ar jau užpildyta)
- **MailConfig** – JavaMailSender konfigūracija

---

## Exception (exception/)

- **GlobalExceptionHandler** (@ControllerAdvice) → `{ code, message, timestamp }`
- UserNotFoundException (404), FriendshipNotFoundException (404)
- NotFriendsException (403), MessageLimitExceededException (429)
- FavoriteWishLimitException (400)

---

## application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/glebis_db
spring.datasource.username=glebis_user
spring.datasource.password=glebis123
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=tavo@gmail.com
spring.mail.password=brevo_api_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

jwt.secret=glebis_super_secret_key_2026
jwt.expiration=86400000
```

---

## Verslo taisyklės (svarbiausia)

1. Taškai TIK už reakciją (10t) ir teisingą atspėjimą (5t) – NE už siuntimą
2. Palinkėjimai nesikartoja – tikrinama siuntėjo+gavėjo istorija
3. Siuntimas tik tarp ACCEPTED draugų – visada tikrinama
4. FavoriteWish max 10 vnt. vienam vartotojui
5. MessageLimit – gavėjas riboja siuntėją per 24val
6. Paveikslėliai – `/static/images/wishes/{id}.png` (atskiro DB lauko nereikia)
7. GUESS mode – tik paveikslėlis + 3 tonai; tekstas atsiskleidžia po atspėjimo
8. Reaction → siūloma siųsti atgal (grandinės efektas)

---

## Realizavimo tvarka

| # | Žingsnis | Statusas |
|---|---|---|
| 1 | Spring Boot projektas + pom.xml | ✅ Atlikta |
| 2 | application.properties | ⬜ |
| 3 | Enum klasės (8 vnt.) | ⬜ |
| 4 | Entity klasės (7 vnt.) | ⬜ |
| 5 | Repository (7 vnt.) | ⬜ |
| 6 | Exception klasės + GlobalExceptionHandler | ⬜ |
| 7 | Config: SecurityConfig, JwtConfig, JwtFilter | ⬜ |
| 8 | UserService + UserController | ⬜ |
| 9 | FriendshipService + FriendshipController | ⬜ |
| 10 | WishService + DataLoader (200+ palinkėjimų) | ⬜ |
| 11 | FavoriteWishService + FavoriteWishController | ⬜ |
| 12 | MessageService + MessageController | ⬜ |
| 13 | PointService | ⬜ |
| 14 | MessageLimitService + MessageLimitController | ⬜ |
| 15 | EmailService + MailConfig | ⬜ |
| 16 | DTO klasės | ⬜ |
| 17 | Postman testavimas | ⬜ |
| 18 | Deploy Railway | ⬜ |
| 19 | Frontend HTML + Tailwind | ⬜ |
| 20 | Deploy Netlify | ⬜ |