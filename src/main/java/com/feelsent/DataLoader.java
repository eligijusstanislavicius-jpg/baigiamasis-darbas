package com.feelsent;

import com.feelsent.enums.RelationshipType;
import com.feelsent.enums.Role;
import com.feelsent.enums.WishTone;
import com.feelsent.model.User;
import com.feelsent.model.Wish;
import com.feelsent.repository.UserRepository;
import com.feelsent.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ── Admino kūrimas (tik jei dar nėra) ───────────────────────────────────
        if (!userRepository.existsByEmail("eligijusstanislavicius@gmail.com")) {
            User admin = new User();
            admin.setUsername("ElisWelis");
            admin.setFirstName("Eligijus");
            admin.setLastName("Stanislavičius");
            admin.setEmail("eligijusstanislavicius@gmail.com");
            admin.setPasswordHash(passwordEncoder.encode("1.ManoProjektas.1"));
            admin.setRole(Role.ADMIN);
            admin.setPoints(0);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("Adminas sukurtas: ElisWelis");
        }

        if (wishRepository.count() > 0) {
            log.info("Palinkėjimai jau įkelti - praleidžiama.");
            return;
        }

        List<Wish> wishes = List.of(

            // ── FUNNY + FRIEND ───────────────────────────────────────────────────
            wish("Laikas sėdint bare eina daug greičiau nei sėdint darbe. Siūlau eksperimentą.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Kadaise ir aš turėjau svajonę - susipažinti su savim. Bet vis neišdrįsau. O jei nepatiks? Geriau atokiau.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Mokslas dar nedavė aiškaus atsakymo, kuris būtent galas yra pirmas. Mes vis tiek draugaujam.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Ką bedarytum, mūsiškio nepradžiuginsi. O va pietiečiai kitokie - pietiečiui parodai mandariną ir juokiasi.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Kai pamiršti, kad esi - kartais net gyvent imi. Siūlau pabandyti.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Kiekvienas, kas labai protingas, turi vieną sykį išeiti iš proto. Tu jau spėjai - ir puikiai.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Negalima juoktis iš kitų, nesijuokiant iš savęs tuo pat metu. Mes su tuo susitvarkom puikiai.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Koks žmogus, toks ir jo televizorius. Tavo - tikrai įdomus.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Gyvenimo nuoboduliu serga tik nuobodūs žmonės. Tu - garantuotai ne toks.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Jei duoda - imk, jei muša - bėk. Gyvenimo išmintis.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Išjudinti provincijos žmogų sunkiau nei traukinį. Tu - tikra išimtis.", WishTone.FUNNY, RelationshipType.FRIEND),
            wish("Adrenalinas ilgina gyvenimą. Kas nuo tilto šoka su virve - adrenalino pagamina tik sau. Kas be virvės - visiems.", WishTone.FUNNY, RelationshipType.FRIEND),

            // ── FUNNY + PARTNER ──────────────────────────────────────────────────
            wish("Keliaudamas iš miegamojo į virtuvę nuolat susiduriu su žmona. Būtų galima ir aplinkkelį per balkoną, bet... Ten gėlių vazonas. Briuselis neleis.", WishTone.FUNNY, RelationshipType.PARTNER),
            wish("Retai pamatysi vyrą vaikštant su žmona. Su šunimi - taip. Mat šuniui viskas įdomu. Su tavimi - irgi.", WishTone.FUNNY, RelationshipType.PARTNER),
            wish("Kodėl blogos dienos ilgos, o geros trumpos? Kad žmogus nepriprastų prie gero. Tu - išimtis.", WishTone.FUNNY, RelationshipType.PARTNER),
            wish("Žmogus galvoja, kad jis namuose šeimininkas. Moteris jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.PARTNER),
            wish("Moterys viską žino. Tik nutyli. Nes jei pasakytų - vyrams nebeliktų ką atrasti.", WishTone.FUNNY, RelationshipType.PARTNER),
            wish("Iš pradžių gėrėm alų, paskui irgi alų, dar vėliau - alų. Apie trečią valandą supratau, kad jau nebežinau - ar aš Juozas, ar Erlickas, ar jau tiesiog pati gamta.", WishTone.FUNNY, RelationshipType.PARTNER),

            // ── FUNNY + BROTHER ──────────────────────────────────────────────────
            wish("Susidūriau su Petru prie cecho vartų - ir galvojom, ką daryti, kad nereiktų krauti plytų.", WishTone.FUNNY, RelationshipType.BROTHER),
            wish("Gerai, kad ir šiandien nesiprausiau. Atrodau žymiai rūstesnis.", WishTone.FUNNY, RelationshipType.BROTHER),
            wish("Jeigu dirbate daug, o uždirbate mažai - pabandykite dirbti mažai ir gal uždirbsite daug. Jeigu ne - darbas ne jūsų sritis.", WishTone.FUNNY, RelationshipType.BROTHER),
            wish("Kam yra valdžia, žmogau? Ogi tam, kad gyvenimas negerėtų!", WishTone.FUNNY, RelationshipType.BROTHER),
            wish("Kol kas niekam nepasakojau, kad tu mano brolis. Manau, pats papasakosi.", WishTone.FUNNY, RelationshipType.BROTHER),

            // ── FUNNY + SISTER ───────────────────────────────────────────────────
            wish("Lietuvis į dangų pakliūtų - ir ten rastų kuo pasipiktinti. Tu - tikrai ne tokia.", WishTone.FUNNY, RelationshipType.SISTER),
            wish("Vyras kaip vyras - tik reikia mokėti jį auginti nuo mažens. Užaugęs jau per vėlu.", WishTone.FUNNY, RelationshipType.SISTER),
            wish("Sesuo yra tas žmogus, kuris žino viską apie tave ir vis tiek daro, kad nežino.", WishTone.FUNNY, RelationshipType.SISTER),
            wish("Optimistas - tai informuotas pesimistas. Tik informacija jo kol kas nepasiekė. Tu - tikra optimistė.", WishTone.FUNNY, RelationshipType.SISTER),
            wish("Susirinkime visi sutarė. Skirtingas buvo tik supratimas, dėl ko susitarė. Kaip mūsų šeimoje.", WishTone.FUNNY, RelationshipType.SISTER),

            // ── FUNNY + MOTHER ───────────────────────────────────────────────────
            wish("Mama - tai žmogus, kuris žino atsakymą į klausimą, kurio dar nepaklausi.", WishTone.FUNNY, RelationshipType.MOTHER),
            wish("Lietuvis į dangų pakliūtų - ir ten rastų kuo pasipiktinti. Mama - tikrai ne.", WishTone.FUNNY, RelationshipType.MOTHER),
            wish("Žmogus galvoja, kad jis namuose šeimininkas. Mama jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.MOTHER),
            wish("Moterys viską žino. Tik nutyli. Mama - žino ir pasako. Bent jau man.", WishTone.FUNNY, RelationshipType.MOTHER),

            // ── FUNNY + FATHER ───────────────────────────────────────────────────
            wish("Tėtis - tai žmogus, kuris visada turi atsakymą. Net kai klausimo nėra.", WishTone.FUNNY, RelationshipType.FATHER),
            wish("Mokslas dar nedavė aiškaus atsakymo, kuris būtent galas yra pirmas. Tėtis žino.", WishTone.FUNNY, RelationshipType.FATHER),
            wish("Lietuvis į dangų pakliūtų - ir ten rastų kuo pasipiktinti. Tėtis - tik kieme.", WishTone.FUNNY, RelationshipType.FATHER),
            wish("Seniausias vyriškių paradoksas: dingsta žemyn iki parduotuvės ir grįžta po dviejų valandų. Niekas nežino, kur buvo.", WishTone.FUNNY, RelationshipType.FATHER),

            // ── FUNNY + GRANDFATHER ──────────────────────────────────────────────
            wish("Senelis - tai tas, kuris leidžia tai, ko mama neleidžia. Tai irgi išmintis.", WishTone.FUNNY, RelationshipType.GRANDFATHER),
            wish("Geras humoras - geriau nei bet kokia medicina. Tu tai žinai geriau nei bet kuris gydytojas.", WishTone.FUNNY, RelationshipType.GRANDFATHER),
            wish("Kiekvienas, kas labai protingas, turi vieną sykį išeiti iš proto. Senelis tai jau seniai padarė - ir puikiai jaučiasi.", WishTone.FUNNY, RelationshipType.GRANDFATHER),

            // ── FUNNY + GRANDMOTHER ──────────────────────────────────────────────
            wish("Močiutė visada turi skanaus. Net kai sako, kad nieko nėra.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),
            wish("Močiutės receptai neturi gramų, laiko ir proporcijų. Bet visada pavyksta. Magija.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),
            wish("Močiutė - tai Google prieš Google laikotarpį. Viską žino, visada teisingai.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),

            // ── FUNNY + CHILD ────────────────────────────────────────────────────
            wish("Produktyvumas - tai ne tavo žodis. Šiandien produktyvumas = egzistencija. Ir tai skaitosi.", WishTone.FUNNY, RelationshipType.CHILD),
            wish("Net kompiuteriai reikalauja perkrovimo. Tu irgi esi sistema. Perkrauk.", WishTone.FUNNY, RelationshipType.CHILD),
            wish("Pasaulis nesustos, jei šiandien nieko nepadarysi. Pažadu.", WishTone.FUNNY, RelationshipType.CHILD),

            // ── SUPPORTIVE + FRIEND ──────────────────────────────────────────────
            wish("Visi rodo gyvenimo hilaitus. Niekas nerodo 14:00 antradienį, kai tiesiog guli ant grindų. Tu nesi atsilikęs - tu tiesiog gyvi.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Algoritmas nesupranta tavo vertės. Žmonės, kurie tave pažįsta asmeniškai - taip.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Kažkieno tobulas gyvenimas internete yra 3 nuotraukos iš 300 dienų. Nepamirški likusių 297.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Kai kažkas išeina iš tavo gyvenimo - tai ne tavo nesėkmė. Tai vieta, kuri atsilaisvino kažkam tikram.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Kiekvienas, kuris dabar atrodo, kad turi viską susidėliojęs - kažkada buvo ten, kur tu dabar.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Sunki diena nėra tavo charakteristika. Tai tik sunki diena.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Pagirk save kartais. Laukti, kol kiti padarys tai - per ilgas laukimas.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Šiandien tu padarei kažką, ko vakarykštis tu negalėjo. Tai jau yra kas nors.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Švęsk tai. Ir nebūk tas žmogus, kuris sako 'aaa, bet tai smulkmena'. Smulkmenos sudaro gyvenimą.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
            wish("Tas jausmas kai pasiekei tai, kas dar prieš metus atrodė neįmanoma - tai ir yra augimas. Pastebėk jį.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),

            // ── SUPPORTIVE + PARTNER ─────────────────────────────────────────────
            wish("Kai blogai - nereikia žodžių. Pakanka, kad esi šalia.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
            wish("Tavo ramybė man yra saugiausia vieta pasaulyje.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
            wish("Net ir tada, kai sunku - tu lieki. Ir tai reiškia viską.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
            wish("Tu esi tas žmogus, su kuriuo net tyla yra patogi. Tai reta.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
            wish("Nerimas meluoja. Jis sako 'viskas bus blogai' - nors dar nieko nežino. Aš esu šalia.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
            wish("Pauzė. Įkvėpimas. Iškvėpimas. Mes čia. Dar niekas neatsitiko.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),

            // ── SUPPORTIVE + SISTER ──────────────────────────────────────────────
            wish("Šiandien tu tiesiog spinduliuoji ir tai matosi net per ekraną.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
            wish("Tas jausmas, kai viskas klojasi - tai ne atsitiktinumas. Tai tu.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
            wish("Laimingas žmogus neieško patvirtinimo. Jis tiesiog yra laimingas. Tu šiandien - tas žmogus.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
            wish("Giliausios baimės retai išsipildo taip, kaip įsivaizduoji. Dažniausiai viskas baigiasi kitaip - dažnai geriau.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
            wish("Tau nereikia visko turėti susidėliojus. Pakanka tokios, kokia esi.", WishTone.SUPPORTIVE, RelationshipType.SISTER),

            // ── SUPPORTIVE + BROTHER ─────────────────────────────────────────────
            wish("Sunkus laikotarpis nėra tavo istorijos pabaiga. Tai tik vienas skyrius - ir tu rašai toliau.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
            wish("Vienu metu galima išspręsti tik vieną problemą. Likusios tegul palaukia eilėje.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
            wish("Jei viskas lyg ir dega - pažiūrėk, gal tik vienas dalykas dega, o kiti tik atrodo.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
            wish("Pasaulis yra pilnas žmonių, kurie rado kelią pavėluotai. Tu dar nesi vėlu.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
            wish("Nereikia matyti viso kelio. Pakanka matyti kitą žingsnį. Ir tu jį matai.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),

            // ── SUPPORTIVE + MOTHER ──────────────────────────────────────────────
            wish("Tavo stiprybė - tylūs veiksmai. Tavo meilė - kasdieniai maži dalykai. Ačiū.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
            wish("Net ir tada, kai pati vargsti - tu visada sugebi padovanoti šiltą žodį. Tai reta dovana.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
            wish("Tegul šie metai atneša tau tiek džiaugsmo, kiek tu jo suteikei kitiems.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
            wish("Tu niekada neišėjai iš savo vietos - ir mes tai žinome. Ačiū.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),

            // ── SUPPORTIVE + FATHER ──────────────────────────────────────────────
            wish("Ne žodžiais, o veiksmais matėm tavo meilę. Ir ją jautėm kiekvieną dieną.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
            wish("Tavo ramybė ir tavo pavyzdys - tai vertingiausia, ką gali duoti.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
            wish("Stipriausi žmonės - tie, kurie atsikėlė. Tu atsikėlei ne kartą. Mes tai matėm.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
            wish("CV neatskleidžia, koks tu žmogus. Tu - žymiai daugiau nei bet koks popierius.", WishTone.SUPPORTIVE, RelationshipType.FATHER),

            // ── SUPPORTIVE + CHILD ───────────────────────────────────────────────
            wish("Vienas egzaminas nesprendžia tavo ateities. Tai tik popierius su data. Tu esi daugiau nei pažymys.", WishTone.SUPPORTIVE, RelationshipType.CHILD),
            wish("Nežinoti ko nori sulaukus 20-ies yra ne trūkumas - tai statistiškai normalu. Spausk toliau.", WishTone.SUPPORTIVE, RelationshipType.CHILD),
            wish("Mokykla moko daug ko. Kaip gyventi - to mokaisi pats. Ir tai svarbesnis egzaminas.", WishTone.SUPPORTIVE, RelationshipType.CHILD),
            wish("Šiandien tu padarei kažką, ko vakarykštis tu negalėjo. Tai jau yra augimas.", WishTone.SUPPORTIVE, RelationshipType.CHILD),
            wish("Tu nesi atsilikęs. Tu tiesiog eini savo tempu. Ir tai - teisingas tempas.", WishTone.SUPPORTIVE, RelationshipType.CHILD),

            // ── SUPPORTIVE + GRANDMOTHER ─────────────────────────────────────────
            wish("Tavo išmintis - tai metų sukauptas lobis, kurį tu dosniai dalijies. Ačiū.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
            wish("Laimė ir džiaugsmas - tai tavo namai, į kuriuos mes visada norim grįžti. Ačiū.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
            wish("Net ir toli - tu visada šalia. Kaip tik močiutė gali.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),

            // ── SUPPORTIVE + GRANDFATHER ─────────────────────────────────────────
            wish("Tavo tyliai pasakyti žodžiai liko ilgiau nei bet kas kitas.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
            wish("Gyvenimas susideda iš atradimų ir praradimų. Tavo atradimai - neįkainojami.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
            wish("Visi mes išskrendame iš vaikystės. O ji pasiveja mus - su tavo pasakomis, su tavo tyliu juoku.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),

            // ── ROMANTIC + PARTNER ───────────────────────────────────────────────
            wish("Meilė - tai du sujungti delnai. Ir gurkšnis vandens juose: gyvenimas.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Diemedžiu žydėsiu, diemedžiu kvepiančiu - prie tavo lango.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Tau pirmą pavasario dainą aukoju - su jaunu kvapu sprogstančiųjų alėjų.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Tiktai paliesk mane. Tik būki prie šalies, kad tavo šviesoje aš augčiau.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Tas, kurs myli - nieko nenori, nieko negeidžia. Jis visko turi.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Man reikia mylėt - bent meilėj nenoriu pakeistas būt.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Yra turbūt ir vakarinis džiaugsmas - tylus, ramus ir pilnas išminties.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Per sutemusį sodą eina mėnuo ir obuolius liečia - girdėt, kaip jie krinta laimingi.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Vakaras toksai, kad nėr kur dėtis - ir toksai neišreiškiamas pilnumas, mėlynas kaip dūmai.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Kur gėlynai žydi - ten ir aš. Kur drugeliai žaidžia - ten ir aš.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Mes nė vienas nemokam būti vienas - ir kaip gerai, kad yra žmonių, kurių nenorime atsisakyti.", WishTone.ROMANTIC, RelationshipType.PARTNER),
            wish("Ir ką tu girdi, ir ko jau negirdi - yra apie meilę.", WishTone.ROMANTIC, RelationshipType.PARTNER),

            // ── BIRTHDAY + FRIEND ────────────────────────────────────────────────
            wish("Gerai, pripažink - šiandien esi nenusakomai geros nuotaikos ir tai šiek tiek įtartina.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
            wish("Kiekvienais metais tu tampi tokiu žmogumi, kuriuo norėtų tapti. Ir tai matosi.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
            wish("Šiandien oficialus leidimas daryti viską, ko paprastai negali. Pasinaudok.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
            wish("Gyvenimas yra nuostabumų kupinas - jau kuone galėtai sakyti, kad jis pats yra stebuklas. Ypač šiandien.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
            wish("Sako: pilnas dienų. Nepilnas, šaukiu - dar tiek daug gražių dienų priekyje.", WishTone.BIRTHDAY, RelationshipType.FRIEND),

            // ── BIRTHDAY + PARTNER ───────────────────────────────────────────────
            wish("Šiandien - tavo diena. O aš džiaugiuos, kad galiu ją švęsti šalia tavęs.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
            wish("Koks džiaugsmas pašaukti tave savo. Ačiū - už dar vienerius metus šalia.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
            wish("Meilė - tai du sujungti delnai. Tegul šie metai atneša dar daugiau tokių akimirkų.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
            wish("Spindulys esti begalinės šviesos - tu esi vienas iš tų spindulių, kurių pasigendu, kai nėra šalia.", WishTone.BIRTHDAY, RelationshipType.PARTNER),

            // ── BIRTHDAY + MOTHER ────────────────────────────────────────────────
            wish("Tegul šie metai atneša tau tiek džiaugsmo, kiek tu jo suteikei kitiems.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
            wish("Gyvenimas gražus, kai savo vidaus šiluma jį sušildai. Ačiū, kad taip sugebai visada.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
            wish("Reikia mylėti žmones, kad ir kokie jie bebūtų. Tu tai visada mokėjai. Šiandien mokai mus.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
            wish("Ryškiausias prisiminimas - sunkios rankos, raikančios duoną prie stalo. Jame visa gyvenimo prasmė. Tai tavo prasmė.", WishTone.BIRTHDAY, RelationshipType.MOTHER),

            // ── BIRTHDAY + FATHER ────────────────────────────────────────────────
            wish("Atrandam, norim, trokštam, siekiam - tu mus išmokei to. Šiandien - ačiū.", WishTone.BIRTHDAY, RelationshipType.FATHER),
            wish("Stipriausi žmonės - tie, kurie atsikėlė. Tu atsikėlei ne kartą. Mes tai matėm.", WishTone.BIRTHDAY, RelationshipType.FATHER),
            wish("Tegul šios dienos šventė būna tokia didelė, kokia yra tavo širdis.", WishTone.BIRTHDAY, RelationshipType.FATHER),

            // ── BIRTHDAY + GRANDMOTHER ───────────────────────────────────────────
            wish("Gyvenimas yra nuostabumų kupinas - ir tu, gyvenusi tiek daug, vis dar stebiesi. Tai stebuklas.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
            wish("Tavo metai - tai sukauptos meilės, išminties ir šilumos krovinys. Ačiū, kad dalinies.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
            wish("Tegul šventė šiandien būna tokia šilta, kaip tavo virtuvė, kai visi susirenkam.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),

            // ── BIRTHDAY + GRANDFATHER ───────────────────────────────────────────
            wish("Gyvenimas susideda iš atradimų ir praradimų. Tavo atradimai - neįkainojami.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
            wish("Visi mes išskrendame iš vaikystės. O ji pasiveja mus - su tavo pasakomis, su tavo tyliu juoku.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
            wish("Tavo tyliai pasakyti žodžiai liko ilgiau nei bet kas kitas. Ačiū.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),

            // ── BIRTHDAY + SISTER ────────────────────────────────────────────────
            wish("Sveika, sesuo! Šiandien tavo diena - ir šviesa ant stalo tavo.", WishTone.BIRTHDAY, RelationshipType.SISTER),
            wish("Tau pirmą pavasario dainą aukoju - nes tu esi tas pavasaris mano gyvenime.", WishTone.BIRTHDAY, RelationshipType.SISTER),
            wish("Tegul šie metai skleistųsi kaip gėlė po lietaus - lėtai, gražiai ir tikrai.", WishTone.BIRTHDAY, RelationshipType.SISTER),

            // ── BIRTHDAY + BROTHER ───────────────────────────────────────────────
            wish("Atrandam, trokštam, siekiam - einame ne milimetrais, o sieksniais. Tegul šie metai - sieksniais.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
            wish("Sunkus semestras, sunkūs metai - bet tu atsikeli. Ir tai matosi. Sveikinu.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
            wish("Laimingo gimtadienio. Tegul šiandien viskas pavyksta - kaip tada, kai tikrai stengeisi.", WishTone.BIRTHDAY, RelationshipType.BROTHER),

            // ── BIRTHDAY + CHILD ─────────────────────────────────────────────────
            wish("Tu esi mano didžiausias stebuklas - nepaprastas paprastumu. Laimingo gimtadienio.", WishTone.BIRTHDAY, RelationshipType.CHILD),
            wish("Kiekvieni tavo metai - tai naujas skyrius. Tegul šis - pats įdomiausias.", WishTone.BIRTHDAY, RelationshipType.CHILD),
            wish("Niekuomet žmogus nėra toks gražus, koks jis būna kurdamas. Kurk, augk, būk.", WishTone.BIRTHDAY, RelationshipType.CHILD),
            wish("Gyvenimas gražus, kai savo vidaus spinduliais jį nušvieti. Tu jau šiandien spindi.", WishTone.BIRTHDAY, RelationshipType.CHILD)
        );

        wishRepository.saveAll(wishes);
        log.info("Įkelta {} palinkėjimų.", wishes.size());
    }

    private Wish wish(String text, WishTone tone, RelationshipType relationshipType) {
        Wish w = new Wish();
        w.setText(text);
        w.setTone(tone);
        w.setRelationshipType(relationshipType.name());
        w.setActive(true);
        return w;
    }
}