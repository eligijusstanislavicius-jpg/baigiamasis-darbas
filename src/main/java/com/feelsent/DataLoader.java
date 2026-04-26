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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // ── Admino kūrimas (tik jei dar nėra) ───────────────────────────────────
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFirstName("Eligijus");
            admin.setLastName("Stanislavičius");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setPoints(0);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("Adminas sukurtas: {}", adminEmail);
        }

        if (wishRepository.count() > 0) {
            log.info("Palinkėjimai jau įkelti - praleidžiama.");
            return;
        }

        List<Wish> wishes = List.of(

                // ── FUNNY + FRIEND ───────────────────────────────────────────────────
                wish("Laikas sėdint bare eina daug greičiau nei sėdint darbe. Siūlau eksperimentą.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Kadaise ir aš turėjau svajonę - susipažinti su savim. Bet vis neišdrįsau. O jei nepatiks? Geriau atokiau.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Draugystė – tai kai kitas žmogus žino visas tavo klaidas ir vis tiek skambina.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Koks žmogus, tokia ir jo kompanija. Tavo – tikrai nevargina.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Kartais geriausia dienos pradžia – kai pamiršti, kad reikia keltis, ir atsikeli pats.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Kiekvienas, kas labai protingas, turi vieną sykį išeiti iš proto. Tu jau spėjai - ir puikiai.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Negalima juoktis iš kitų, nesijuokiant iš savęs tuo pat metu. Mes su tuo susitvarkom puikiai.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Gyvenimo nuoboduliu serga tik nuobodūs žmonės. Tu - garantuotai ne toks.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Jei duoda - imk, jei muša - bėk. Gyvenimo išmintis.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Sako, kad lietuviai nejudrūs. Bet tu – tikra išimtis. Judri ir kartais net linksma.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Vasara trumpa, žiema ilga, o draugas – tai tas, kuris ateina ir vasarą, ir žiemą.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Optimistas – tai tas, kuris dar neperskaitė ryto naujienų. Būk optimistu kiek įmanoma ilgiau.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Tu ne atsilikęs. Tu tiesiog esi savo laiko juostoje.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Draugas – tai tas, kuris žino tavo WiFi slaptažodį ir dar grįžta.", WishTone.FUNNY, RelationshipType.FRIEND),
                wish("Kai visi eina miegoti – mes dar tik pradedame. Tai ir yra draugystė.", WishTone.FUNNY, RelationshipType.FRIEND),

// ── FUNNY + PARTNER ──────────────────────────────────────────────────
                wish("Keliaudamas iš miegamojo į virtuvę nuolat susiduriu su žmona. Būtų galima ir aplinkkelį per balkoną, bet... Ten gėlių vazonas. Briuselis neleis.", WishTone.FUNNY, RelationshipType.PARTNER),
                wish("Retai pamatysi vyrą vaikštant su žmona. Su šunimi - taip. Mat šuniui viskas įdomu. Su tavimi - irgi.", WishTone.FUNNY, RelationshipType.PARTNER),
                wish("Kodėl blogos dienos ilgos, o geros trumpos? Kad žmogus nepriprastų prie gero. Tu - išimtis.", WishTone.FUNNY, RelationshipType.PARTNER),
                wish("Žmogus galvoja, kad jis namuose šeimininkas. Moteris jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.PARTNER),
                wish("Moterys viską žino. Tik nutyli. Nes jei pasakytų - vyrams nebeliktų ką atrasti.", WishTone.FUNNY, RelationshipType.PARTNER),
                wish("Gyvenimas kartu – tai kai abu žinot tą pačią istoriją, bet pasakojat skirtingai. Ir abu teisūs.", WishTone.FUNNY, RelationshipType.PARTNER),

// ── FUNNY + HUSBAND ──────────────────────────────────────────────────
                wish("Keliaudamas iš miegamojo į virtuvę nuolat susiduriu su žmona. Būtų galima ir aplinkkelį per balkoną, bet... Ten gėlių vazonas. Briuselis neleis.", WishTone.FUNNY, RelationshipType.HUSBAND),
                wish("Retai pamatysi vyrą vaikštant su žmona. Su šunimi - taip. Mat šuniui viskas įdomu. Su tavimi - irgi.", WishTone.FUNNY, RelationshipType.HUSBAND),
                wish("Kodėl blogos dienos ilgos, o geros trumpos? Kad žmogus nepriprastų prie gero. Tu - išimtis.", WishTone.FUNNY, RelationshipType.HUSBAND),
                wish("Žmogus galvoja, kad jis namuose šeimininkas. Moteris jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.HUSBAND),
                wish("Moterys viską žino. Tik nutyli. Nes jei pasakytų - vyrams nebeliktų ką atrasti.", WishTone.FUNNY, RelationshipType.HUSBAND),
                wish("Gyvenimas kartu – tai kai abu žinot tą pačią istoriją, bet pasakojat skirtingai. Ir abu teisūs.", WishTone.FUNNY, RelationshipType.HUSBAND),

// ── FUNNY + WIFE ─────────────────────────────────────────────────────
                wish("Keliaudamas iš miegamojo į virtuvę nuolat susiduriu su žmona. Būtų galima ir aplinkkelį per balkoną, bet... Ten gėlių vazonas. Briuselis neleis.", WishTone.FUNNY, RelationshipType.WIFE),
                wish("Retai pamatysi vyrą vaikštant su žmona. Su šunimi - taip. Mat šuniui viskas įdomu. Su tavimi - irgi.", WishTone.FUNNY, RelationshipType.WIFE),
                wish("Kodėl blogos dienos ilgos, o geros trumpos? Kad žmogus nepriprastų prie gero. Tu - išimtis.", WishTone.FUNNY, RelationshipType.WIFE),
                wish("Žmogus galvoja, kad jis namuose šeimininkas. Moteris jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.WIFE),
                wish("Moterys viską žino. Tik nutyli. Nes jei pasakytų - vyrams nebeliktų ką atrasti.", WishTone.FUNNY, RelationshipType.WIFE),
                wish("Gyvenimas kartu – tai kai abu žinot tą pačią istoriją, bet pasakojat skirtingai. Ir abu teisūs.", WishTone.FUNNY, RelationshipType.WIFE),

// ── FUNNY + BROTHER ──────────────────────────────────────────────────
                wish("Brolis – vienintelis žmogus, kuriam gali paskambinti be priežasties. Ir jis supras.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Gerai, kad ir šiandien nesiprausiau. Atrodau žymiai rūstesnis.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Jeigu dirbate daug, o uždirbate mažai - pabandykite dirbti mažai ir gal uždirbsite daug. Jeigu ne - darbas ne jūsų sritis.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Optimistas – tai tas, kuris dar neperskaitė ryto naujienų. Būk optimistu kiek įmanoma ilgiau.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Kol kas niekam nepasakojau, kad tu mano brolis. Manau, pats papasakosi.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Brolis – tai tas, kuris pirmą kartą išmokė ką nors, ko mama nežino.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Su broliu net kivirčas baigiasi tuo, kad abu pamirštat dėl ko.", WishTone.FUNNY, RelationshipType.BROTHER),
                wish("Brolis – vienintelis žmogus, kuriam gali skolintis ir negrąžinti. Kol kas.", WishTone.FUNNY, RelationshipType.BROTHER),

// ── FUNNY + SISTER ───────────────────────────────────────────────────
                wish("Sesuo – tai tas žmogus, kuriam gali pasakyti viską. Ir ji vis tiek mylės. Nors ir pakritikuos.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Vyras kaip vyras - tik reikia mokėti jį auginti nuo mažens. Užaugęs jau per vėlu.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Sesuo yra tas žmogus, kuris žino viską apie tave ir mandagiai apsimeta, kad nežino.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Sesuo – vienintelis žmogus, kuris tave supranta iš pusės žodžio. Kartais net iš tono.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Šeima – tai kai visi kalba vienu metu ir vis tiek visi supranta.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Sesuo – tai žmogus, kuris žino visas tavo paslaptis ir vis tiek yra tavo pusėje.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Su seserimi net tylėjimas turi prasmę. Ji supranta viską be žodžių.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Sesuo – tai draugė, kurios nepasirinkau, bet visada pasirinkčiau.", WishTone.FUNNY, RelationshipType.SISTER),
                wish("Brolis gali pamiršti. Sesuo – niekada. Ir tai kartais gąsdina.", WishTone.FUNNY, RelationshipType.SISTER),

// ── FUNNY + MOTHER ───────────────────────────────────────────────────
                wish("Mama - tai žmogus, kuris žino atsakymą į klausimą, kurio dar nepaklausi.", WishTone.FUNNY, RelationshipType.MOTHER),
                wish("Mama žino, kada verksi, dar prieš tau žinant. Tai ne magija – tai mama.", WishTone.FUNNY, RelationshipType.MOTHER),
                wish("Žmogus galvoja, kad jis namuose šeimininkas. Mama jam to nedraudžia galvoti.", WishTone.FUNNY, RelationshipType.MOTHER),
                wish("Mama nutyli tada, kai reikia. Ir pasako tada, kai būtina. Tai irgi menas.", WishTone.FUNNY, RelationshipType.MOTHER),

// ── FUNNY + FATHER ───────────────────────────────────────────────────
                wish("Tėtis - tai žmogus, kuris visada turi atsakymą. Net kai klausimo nėra.", WishTone.FUNNY, RelationshipType.FATHER),
                wish("Tėvas – tai tas, kuris niekad nepasako, kad pavargo. Tik atsisėda.", WishTone.FUNNY, RelationshipType.FATHER),
                wish("Tėtis – tai tas, kuris sako 'pažiūrėsim' ir visada žiūri.", WishTone.FUNNY, RelationshipType.FATHER),
                wish("Tėčio 'tuoj' – tai laiko vienetas, kuriam mokslas dar nerado paaiškinimo.", WishTone.FUNNY, RelationshipType.FATHER),

// ── FUNNY + GRANDFATHER ──────────────────────────────────────────────
                wish("Senelis - tai tas, kuris leidžia tai, ko mama neleidžia. Tai irgi išmintis.", WishTone.FUNNY, RelationshipType.GRANDFATHER),
                wish("Senelis – tai enciklopedija, kuri dar ir juokauja.", WishTone.FUNNY, RelationshipType.GRANDFATHER),
                wish("Senelis turi atsakymą į viską. Tik kartais leidžia pačiam surasti.", WishTone.FUNNY, RelationshipType.GRANDFATHER),

// ── FUNNY + GRANDMOTHER ──────────────────────────────────────────────
                wish("Močiutė visada turi skanaus. Net kai sako, kad nieko nėra.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),
                wish("Močiutė neprašo paaiškinti. Ji tiesiog supranta.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),
                wish("Močiutė – vienintelis žmogus, kuris džiaugiasi tavimi be jokios priežasties.", WishTone.FUNNY, RelationshipType.GRANDMOTHER),

// ── FUNNY + SON ──────────────────────────────────────────────────────
                wish("Kartais geriausia diena – ta, kurios neplanovai. Mėgaukis.", WishTone.FUNNY, RelationshipType.SON),
                wish("Net kompiuteriai reikalauja perkrovimo. Tu irgi esi sistema. Perkrauk.", WishTone.FUNNY, RelationshipType.SON),
                wish("Pasaulis nesustos, jei šiandien nieko nepadarysi. Pažadu.", WishTone.FUNNY, RelationshipType.SON),
                wish("Tu ne lazy. Tu energy-saving mode.", WishTone.FUNNY, RelationshipType.SON),
                wish("Šiandien tu padarei viską teisingai. Net jei niekas nepastebėjo.", WishTone.FUNNY, RelationshipType.SON),

// ── FUNNY + DAUGHTER ─────────────────────────────────────────────────
                wish("Kartais geriausia diena – ta, kurios neplanovai. Mėgaukis.", WishTone.FUNNY, RelationshipType.DAUGHTER),
                wish("Net kompiuteriai reikalauja perkrovimo. Tu irgi esi sistema. Perkrauk.", WishTone.FUNNY, RelationshipType.DAUGHTER),
                wish("Pasaulis nesustos, jei šiandien nieko nepadarysi. Pažadu.", WishTone.FUNNY, RelationshipType.DAUGHTER),
                wish("Tu ne lazy. Tu energy-saving mode.", WishTone.FUNNY, RelationshipType.DAUGHTER),
                wish("Šiandien tu padarei viską teisingai. Net jei niekas nepastebėjo.", WishTone.FUNNY, RelationshipType.DAUGHTER),

// ── SUPPORTIVE + FRIEND ──────────────────────────────────────────────
                wish("Socialiniai tinklai rodo tik viršūnes. Niekas nerodo slėnių. Tu nesi vienintelis slėnyje.", WishTone.SUPPORTIVE, RelationshipType.FRIEND),
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
                wish("Tu – vienintelis žmogus, su kuriuo net eilė parduotuvėje tampa įdomesnė.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
                wish("Su tavimi net blogiausias filmas tampa geru vakaru.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
                wish("Tu – tai tas žmogus, dėl kurio verta keltis iš lovos. Kartais net anksti.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
                wish("Meilė – tai kai abu žinote, kad kitas paims paskutinį sausainį. Ir vis tiek palieka.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
                wish("Su tavimi net stresas atrodo valdomas. Tai arba meilė, arba magija.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),
                wish("Tu – vienintelis žmogus, kuriam galiu paskambinti be priežasties. Ir tu atsiliepsi.", WishTone.SUPPORTIVE, RelationshipType.PARTNER),

// ── SUPPORTIVE + HUSBAND ─────────────────────────────────────────────
                wish("Kai blogai - nereikia žodžių. Pakanka, kad esi šalia.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Tavo ramybė man yra saugiausia vieta pasaulyje.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Net ir tada, kai sunku - tu lieki. Ir tai reiškia viską.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Tu esi tas žmogus, su kuriuo net tyla yra patogi. Tai reta.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Tu – vienintelis žmogus, su kuriuo net eilė parduotuvėje tampa įdomesnė.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Su tavimi net blogiausias filmas tampa geru vakaru.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Tu – tai tas žmogus, dėl kurio verta keltis iš lovos. Kartais net anksti.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Meilė – tai kai abu žinote, kad kitas paims paskutinį sausainį. Ir vis tiek palieka.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Su tavimi net stresas atrodo valdomas. Tai arba meilė, arba magija.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),
                wish("Tu – vienintelis žmogus, kuriam galiu paskambinti be priežasties. Ir tu atsiliepsi.", WishTone.SUPPORTIVE, RelationshipType.HUSBAND),

// ── SUPPORTIVE + WIFE ────────────────────────────────────────────────
                wish("Kai blogai - nereikia žodžių. Pakanka, kad esi šalia.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Tavo ramybė man yra saugiausia vieta pasaulyje.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Net ir tada, kai sunku - tu lieki. Ir tai reiškia viską.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Tu esi tas žmogus, su kuriuo net tyla yra patogi. Tai reta.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Tu – vienintelis žmogus, su kuriuo net eilė parduotuvėje tampa įdomesnė.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Su tavimi net blogiausias filmas tampa geru vakaru.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Tu – tai tas žmogus, dėl kurio verta keltis iš lovos. Kartais net anksti.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Meilė – tai kai abu žinote, kad kitas paims paskutinį sausainį. Ir vis tiek palieka.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Su tavimi net stresas atrodo valdomas. Tai arba meilė, arba magija.", WishTone.SUPPORTIVE, RelationshipType.WIFE),
                wish("Tu – vienintelis žmogus, kuriam galiu paskambinti be priežasties. Ir tu atsiliepsi.", WishTone.SUPPORTIVE, RelationshipType.WIFE),

// ── SUPPORTIVE + SISTER ──────────────────────────────────────────────
                wish("Sesuo – tai ta, kuri pasakys tiesą. Net kai nenori jos girdėti. Ir bus teisi.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
                wish("Tu gali skambinti man bet kuriuo metu. Bet po vidurnakčio – tik jei tikrai įdomu.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
                wish("Sesuo – vienintelis žmogus, kuris žino visas tavo klaidas ir vis tiek siunčia gimtadienio tortą.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
                wish("Sesuo – tai ta, kuri pirmoji pasakys, kad suknelė netinka. Ir bus teisi.", WishTone.SUPPORTIVE, RelationshipType.SISTER),
                wish("Su tavimi net blogiausia diena turi bent vieną gerą momentą.", WishTone.SUPPORTIVE, RelationshipType.SISTER),

// ── SUPPORTIVE + BROTHER ─────────────────────────────────────────────
                wish("Vienu metu galima išspręsti tik vieną problemą. Likusios tegul palaukia eilėje.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
                wish("Brolis – tai tas, kuris ateis padėti kraustytis. Niurnėdamas. Bet ateis.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
                wish("Tu skambini tik tada, kai reikia pagalbos. Aš atsiliepiu tik tada, kai noriu. Draugystė.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
                wish("Brolis – vienintelis žmogus, kuriam gali pasakyti 'blogai' ir jis supras be paaiškinimų.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
                wish("Su broliu net nieko neveikimas tampa kokybišku laiko leidimu.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),
                wish("Tu mano brolis. Tai reiškia – visada turiu ką kaltinti. Ir visada turiu ką ginti.", WishTone.SUPPORTIVE, RelationshipType.BROTHER),

// ── SUPPORTIVE + MOTHER ──────────────────────────────────────────────
                wish("Mama visada žino tiesą. Net kai meluoji labai įtikinamai.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
                wish("Niekas taip nemoka pasakyti 'na ir kas' kaip mama. Ir visada pagelbsti.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
                wish("Mama – tai GPS, kuris veikia net be interneto.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
                wish("Pas mamą visada pilnas šaldytuvas. Net kai ji sako, kad nieko nėra.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
                wish("Mama – vienintelis žmogus, kuris tave myli net tada, kai tu pats savęs nemėgsti.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),
                wish("Mama nemoko teorijos. Ji tiesiog parodo. Ir tai visada veikia.", WishTone.SUPPORTIVE, RelationshipType.MOTHER),

// ── SUPPORTIVE + FATHER ──────────────────────────────────────────────
                wish("Tėtis – tai tas, kuris sako 'pats išsiaiškink', bet vis tiek ateina padėti.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
                wish("Tėtis visada turi įrankių. Net kai problema visiškai ne apie įrankius.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
                wish("Tėtis – vienintelis žmogus, kuris gali nieko nesakyti ir vis tiek pasakyti viską.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
                wish("Su tėčiu net tyla yra produktyvi. Jis jau viską apgalvojo.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
                wish("Tėtis – tai tas, kuris niekada nepasako 'nežinau'. Jis tiesiog eina ir patikrina.", WishTone.SUPPORTIVE, RelationshipType.FATHER),
                wish("Tėtis moko ne žodžiais. Jis tiesiog daro. Ir tu žiūri. Ir supranti.", WishTone.SUPPORTIVE, RelationshipType.FATHER),

// ── SUPPORTIVE + SON ─────────────────────────────────────────────────
                wish("Tu dar tik pradedi. O pradžia – visada geriausia dalis.", WishTone.SUPPORTIVE, RelationshipType.SON),
                wish("Klaidos – tai ne nesėkmės. Tai medžiaga geroms istorijoms vėliau.", WishTone.SUPPORTIVE, RelationshipType.SON),
                wish("Tu dar nežinai, koks esi. Bet mes jau matome. Ir tai – įspūdinga.", WishTone.SUPPORTIVE, RelationshipType.SON),
                wish("Niekas nežinojo ko nori sulaukus dvidešimt. Tie, kurie sakė žinoją – melagiai.", WishTone.SUPPORTIVE, RelationshipType.SON),
                wish("Tu – tai žmogus, dėl kurio verta anksti keltis. Net savaitgalį.", WishTone.SUPPORTIVE, RelationshipType.SON),
                wish("Gyvenimas – ne lenktynės. Bet jei ir lenktynės – tu dar tik įsibėgėji.", WishTone.SUPPORTIVE, RelationshipType.SON),

// ── SUPPORTIVE + DAUGHTER ────────────────────────────────────────────
                wish("Tu dar tik pradedi. O pradžia – visada geriausia dalis.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),
                wish("Klaidos – tai ne nesėkmės. Tai medžiaga geroms istorijoms vėliau.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),
                wish("Tu dar nežinai, kokia esi. Bet mes jau matome. Ir tai – įspūdinga.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),
                wish("Niekas nežinojo ko nori sulaukus dvidešimt. Tie, kurie sakė žinoją – melagiai.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),
                wish("Tu – tai žmogus, dėl kurio verta anksti keltis. Net savaitgalį.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),
                wish("Gyvenimas – ne lenktynės. Bet jei ir lenktynės – tu dar tik įsibėgėji.", WishTone.SUPPORTIVE, RelationshipType.DAUGHTER),

// ── SUPPORTIVE + GRANDMOTHER ─────────────────────────────────────────
                wish("Pas močiutę visada šilta. Net kai lauke minus dvidešimt.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
                wish("Močiutė – vienintelis žmogus, kuris tave myli ir bara tuo pačiu įkvėpimu.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
                wish("Močiutės patarimai veikia. Net tie, kurių nesupratai būdamas mažas.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
                wish("Močiutė – tai Google, bet su meile ir šiltu pyragu.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),
                wish("Su močiute net nieko neveikimas tampa geriausiu laiko leidimu.", WishTone.SUPPORTIVE, RelationshipType.GRANDMOTHER),

// ── SUPPORTIVE + GRANDFATHER ─────────────────────────────────────────
                wish("Senelis – tai tas, kuris visada turi laiko. Net kai visi kiti skuba.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
                wish("Senelis žino visas gyvenimo gudrybes. Ir pasakoja tik tada, kai tikrai reikia.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
                wish("Su seneliu net senas anekdotas skamba kaip naujas. Tai menas.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
                wish("Senelis – tai tas, kuris leidžia tai, ko tėvai neleidžia. Ir visada randa pateisinimą.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
                wish("Senelio patirtis – tai enciklopedija, kurios niekas kitas neturi.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),
                wish("Su seneliu net ilgiausia kelionė praeina greitai. Nes jis visada turi ką papasakoti.", WishTone.SUPPORTIVE, RelationshipType.GRANDFATHER),

// ── ROMANTIC + PARTNER ───────────────────────────────────────────────
                wish("Tu – tai žmogus, dėl kurio verta išjungti telefoną. Bent valandai.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Su tavimi net tyla skamba gražiai.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Meilė – tai kai kitas žmogus tampa geriausia dienos dalimi.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Tu – tai žmogus, dėl kurio net blogas oras atrodo gerai.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Su tavimi net lietus atrodo romantiškai. Na, beveik.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Meilė – tai kai nereikia aiškinti. Jis tiesiog žino.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Tu – vienintelis žmogus, su kuriuo norisi ir keliauti, ir tiesiog sėdėti namuose.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Su tavimi net eilinė diena tampa istorija, kurią norisi papasakoti.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Meilė – tai kai žinai visus trūkumus ir vis tiek pasirenkate vienas kitą.", WishTone.ROMANTIC, RelationshipType.PARTNER),
                wish("Tu – tai mano geriausia dienos dalis. Kiekvieną dieną.", WishTone.ROMANTIC, RelationshipType.PARTNER),

// ── ROMANTIC + HUSBAND ───────────────────────────────────────────────
                wish("Tu – tai žmogus, dėl kurio verta išjungti telefoną. Bent valandai.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Su tavimi net tyla skamba gražiai.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Meilė – tai kai kitas žmogus tampa geriausia dienos dalimi.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Tu – tai žmogus, dėl kurio net blogas oras atrodo gerai.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Su tavimi net lietus atrodo romantiškai. Na, beveik.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Meilė – tai kai nereikia aiškinti. Jis tiesiog žino.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Tu – vienintelis žmogus, su kuriuo norisi ir keliauti, ir tiesiog sėdėti namuose.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Su tavimi net eilinė diena tampa istorija, kurią norisi papasakoti.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Meilė – tai kai žinai visus trūkumus ir vis tiek pasirenkate vienas kitą.", WishTone.ROMANTIC, RelationshipType.HUSBAND),
                wish("Tu – tai mano geriausia dienos dalis. Kiekvieną dieną.", WishTone.ROMANTIC, RelationshipType.HUSBAND),

// ── ROMANTIC + WIFE ──────────────────────────────────────────────────
                wish("Tu – tai žmogus, dėl kurio verta išjungti telefoną. Bent valandai.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Su tavimi net tyla skamba gražiai.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Meilė – tai kai kitas žmogus tampa geriausia dienos dalimi.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Tu – tai žmogus, dėl kurio net blogas oras atrodo gerai.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Su tavimi net lietus atrodo romantiškai. Na, beveik.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Meilė – tai kai nereikia aiškinti. Ji tiesiog žino.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Tu – vienintelis žmogus, su kuriuo norisi ir keliauti, ir tiesiog sėdėti namuose.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Su tavimi net eilinė diena tampa istorija, kurią norisi papasakoti.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Meilė – tai kai žinai visus trūkumus ir vis tiek pasirenkate vienas kitą.", WishTone.ROMANTIC, RelationshipType.WIFE),
                wish("Tu – tai mano geriausia dienos dalis. Kiekvieną dieną.", WishTone.ROMANTIC, RelationshipType.WIFE),

// ── BIRTHDAY + FRIEND ────────────────────────────────────────────────
                wish("Šiandien tavo diena. Naudokis tol, kol kas nors neprimins, kad rytoj pirmadienis.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
                wish("Gimtadienis – vienintelė diena, kai tortas pusryčiams yra visiškai priimtinas sprendimas.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
                wish("Šiandien oficialiai leista nieko nedaryti. Ir dar gauti dovanų. Puiki sistema.", WishTone.BIRTHDAY, RelationshipType.FRIEND),
                wish("Tu senstai kaip geras vynas. Kiek brangsti – dar nežinome.", WishTone.BIRTHDAY, RelationshipType.FRIEND),

// ── BIRTHDAY + PARTNER ───────────────────────────────────────────────
                wish("Šiandien tavo diena. Aš pasirūpinsiu tortu. Tu pasirūpink nuotaika.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Dar vieni metai kartu. Ir vis dar norisi daugiau. Tai jau kažką reiškia.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Dar vieni metai su tavimi. Ir dar kartą patvirtinu – geras sprendimas.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Šiandien tavo gimtadienis. Vadinasi – tavo pasirinkimas, kur valgome.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Metai bėga. Tu – vis toks pat. Aš – vis labiau įsitikinęs, kad pasirinkau teisingai.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Šiandien taisyklė viena – kaip tu sakai, taip ir bus. Rytoj grįžtam prie demokratijos.", WishTone.BIRTHDAY, RelationshipType.PARTNER),
                wish("Tu – mano mėgstamiausias žmogus. Net kai erzini. Ypač kai erzini.", WishTone.BIRTHDAY, RelationshipType.PARTNER),

// ── BIRTHDAY + HUSBAND ───────────────────────────────────────────────
                wish("Šiandien tavo diena. Aš pasirūpinsiu tortu. Tu pasirūpink nuotaika.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Dar vieni metai kartu. Ir vis dar norisi daugiau. Tai jau kažką reiškia.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Dar vieni metai su tavimi. Ir dar kartą patvirtinu – geras sprendimas.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Šiandien tavo gimtadienis. Vadinasi – tavo pasirinkimas, kur valgome.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Metai bėga. Tu – vis toks pat. Aš – vis labiau įsitikinęs, kad pasirinkau teisingai.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Šiandien taisyklė viena – kaip tu sakai, taip ir bus. Rytoj grįžtam prie demokratijos.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),
                wish("Tu – mano mėgstamiausias žmogus. Net kai erzini. Ypač kai erzini.", WishTone.BIRTHDAY, RelationshipType.HUSBAND),

// ── BIRTHDAY + WIFE ──────────────────────────────────────────────────
                wish("Šiandien tavo diena. Aš pasirūpinsiu tortu. Tu pasirūpink nuotaika.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Dar vieni metai kartu. Ir vis dar norisi daugiau. Tai jau kažką reiškia.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Dar vieni metai su tavimi. Ir dar kartą patvirtinu – geras sprendimas.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Šiandien tavo gimtadienis. Vadinasi – tavo pasirinkimas, kur valgome.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Metai bėga. Tu – vis tokia pati. Aš – vis labiau įsitikinęs, kad pasirinkau teisingai.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Šiandien taisyklė viena – kaip tu sakai, taip ir bus. Rytoj grįžtam prie demokratijos.", WishTone.BIRTHDAY, RelationshipType.WIFE),
                wish("Tu – mano mėgstamiausias žmogus. Net kai erzini. Ypač kai erzini.", WishTone.BIRTHDAY, RelationshipType.WIFE),

// ── BIRTHDAY + MOTHER ────────────────────────────────────────────────
                wish("Mama gimtadienį švenčia taip pat kaip visada – rūpindamasi visais kitais. Šiandien – tik tu.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
                wish("Tu pagimdei mane. Aš atsinešiau tortą. Manau, esame lygiateisiai.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
                wish("Šiandien tavo diena. Jokių patarimų, jokių receptų, jokių klausimų. Tik tortas.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
                wish("Mama – vienintelis žmogus, kurio gimtadienį prisimenu geriau nei savą.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
                wish("Tu augini vaikus, tvarkai namus ir dar spėji atrodyti gerai. Šiandien – pailsėk.", WishTone.BIRTHDAY, RelationshipType.MOTHER),
                wish("Šiandien ne tu rūpiniesi mumis. Mes rūpinamės tavimi. Bent jau bandome.", WishTone.BIRTHDAY, RelationshipType.MOTHER),

// ── BIRTHDAY + FATHER ────────────────────────────────────────────────
                wish("Tėtis gimtadienį švenčia kaip visada – sako, kad nereikia jokios šventės. Bet torto neatsisako.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Tu visada žinojai atsakymą. Šiandien atsakymas paprastas – laimingo gimtadienio.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Tėtis – vienintelis žmogus, kuris dovanų nenori, bet vis tiek džiaugiasi.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Šiandien tavo diena. Jokių darbų, jokių planų, jokių remontų. Bent jau bandome.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Tu – tai žmogus, kuris visada turi laiko. Net kai sako, kad neturi.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Šiandien mes rūpinamės tavimi. Kaip tu visada rūpinaisi mumis.", WishTone.BIRTHDAY, RelationshipType.FATHER),
                wish("Tu sakai, kad niekada nepavargsti. Šiandien – oficialiai leista.", WishTone.BIRTHDAY, RelationshipType.FATHER),

// ── BIRTHDAY + GRANDMOTHER ───────────────────────────────────────────
                wish("Močiutės gimtadienis – vienintelė diena, kai ji leidžia kitiems gaminti. Na, beveik.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
                wish("Tu žinai viską apie visus. Šiandien mes žinome viena – laimingo gimtadienio.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
                wish("Močiutė neskaičiuoja metų. Ji skaičiuoja receptus, anūkus ir geras akimirkas.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
                wish("Šiandien tavo diena. Jokių darbų, jokių rūpesčių. Tik tortas ir mes.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),
                wish("Močiutės gimtadienis – puiki proga prisiminti, kodėl pas ją visada norisi grįžti.", WishTone.BIRTHDAY, RelationshipType.GRANDMOTHER),

// ── BIRTHDAY + GRANDFATHER ───────────────────────────────────────────
                wish("Senelio gimtadienis – puiki proga išgirsti gerą istoriją. Ar kelias.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
                wish("Tu žinai daugiau nei Google. Šiandien – laimingo gimtadienio.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
                wish("Senelis neskaičiuoja metų. Jis skaičiuoja, kiek dar yra nepapasakotų istorijų.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
                wish("Šiandien tavo diena. Jokių darbų, jokių patarimų. Tik tortas ir mes.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
                wish("Tu – tai žmogus, kurio istorijos visada baigiasi geriau nei tikėjaisi.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),
                wish("Senelio gimtadienis – vienintelė diena, kai jis leidžia sau nieko nedaryti. Na, beveik.", WishTone.BIRTHDAY, RelationshipType.GRANDFATHER),

// ── BIRTHDAY + SISTER ────────────────────────────────────────────────
                wish("Sesuo gimtadienį švenčia kaip visada – ji žino, kad šiandien gali reikalauti ko nori. Ir reikalauja.", WishTone.BIRTHDAY, RelationshipType.SISTER),
                wish("Gimtadienis – puiki proga priminti, kad tu – mano mėgstamiausia sesuo. Vienintelė, bet vis tiek.", WishTone.BIRTHDAY, RelationshipType.SISTER),
                wish("Šiandien tavo diena. Jokių ginčų, jokių debatų. Bent jau iki vakaro.", WishTone.BIRTHDAY, RelationshipType.SISTER),
                wish("Tu – tai žmogus, su kuriuo net kivirčas baigiasi juoku. Šiandien – tik juokas.", WishTone.BIRTHDAY, RelationshipType.SISTER),
                wish("Sesuo – vienintelis žmogus, kuris žino visas tavo paslaptis ir vis tiek ateina į gimtadienį.", WishTone.BIRTHDAY, RelationshipType.SISTER),
                wish("Sesuo – tai ta, kuri visada atsimena tavo gimtadienį. Net kai tu pats pamiršti.", WishTone.BIRTHDAY, RelationshipType.SISTER),

// ── BIRTHDAY + BROTHER ───────────────────────────────────────────────
                wish("Brolis gimtadienį švenčia kaip visada – sako, kad nereikia nieko. Bet laukia.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
                wish("Tu – mano brolis. Tai reiškia – amžinas visų mano kvailysčių liudytojas. Laimingo gimtadienio.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
                wish("Gimtadienis – puiki proga priminti, kad tu – mano mėgstamiausias brolis. Vienintelis, bet vis tiek.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
                wish("Šiandien tavo diena. Jokių ginčų, jokių debatų. Bent jau bandome.", WishTone.BIRTHDAY, RelationshipType.BROTHER),
                wish("Brolis – vienintelis žmogus, kuriam galiu paskambinti bet kuriuo metu. Ir jis atsilieps. Po trijų skambučių.", WishTone.BIRTHDAY, RelationshipType.BROTHER),

// ── BIRTHDAY + SON ───────────────────────────────────────────────────
                wish("Tu gimei. Mūsų gyvenimas tapo įdomesnis. Tai – komplimentas.", WishTone.BIRTHDAY, RelationshipType.SON),
                wish("Kiekvieni metai tu nustebini. Šiandien – laukiame, kuo nustebinsi toliau.", WishTone.BIRTHDAY, RelationshipType.SON),
                wish("Tu – tai žmogus, kuris visada nustebina. Šiandien – švęsk.", WishTone.BIRTHDAY, RelationshipType.SON),
                wish("Šiandien tavo diena. Jokių pareigų, jokių planų. Tik tortas ir tu.", WishTone.BIRTHDAY, RelationshipType.SON),
                wish("Tu dar tik įsibėgėji. O mes jau matome, kur link.", WishTone.BIRTHDAY, RelationshipType.SON),

// ── BIRTHDAY + DAUGHTER ──────────────────────────────────────────────
                wish("Tu gimei. Mūsų gyvenimas tapo įdomesnis. Tai – komplimentas.", WishTone.BIRTHDAY, RelationshipType.DAUGHTER),
                wish("Kiekvieni metai tu nustebini. Šiandien – laukiame, kuo nustebinsi toliau.", WishTone.BIRTHDAY, RelationshipType.DAUGHTER),
                wish("Tu – tai žmogus, kuris visada nustebina. Šiandien – švęsk.", WishTone.BIRTHDAY, RelationshipType.DAUGHTER),
                wish("Šiandien tavo diena. Jokių pareigų, jokių planų. Tik tortas ir tu.", WishTone.BIRTHDAY, RelationshipType.DAUGHTER),
                wish("Tu dar tik įsibėgėji. O mes jau matome, kur link.", WishTone.BIRTHDAY, RelationshipType.DAUGHTER)

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
