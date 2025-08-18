# Developer Notes – 2025-08-18

## 11:38:30 – Always-on per-test timing and abort logging; groundwork for per-test timeout
- Co se změnilo a proč:
  - Implementováno měření času testu přímo v LattePluginTestBase, nezávislé na JUnit Rules (UsefulTestCase/JUnit3 může @Rule ignorovat):
    - @Before: zaznamená CURRENT_TEST_FQN a start time a zaloguje `[TEST_TIME] START <FQN> at <timestamp>` do konzole, per‑test logu a souhrnného logu běhu.
    - @After: spočítá uplynulý čas a zaloguje `[TEST_TIME] FINISH <FQN> in <ms>` (také do všech tří míst).
    - Shutdown hook: při ukončení JVM během testu zaloguje `[TEST_TIME] ABORT <FQN> after <ms>`, aby byl čas zachycen i při ručním stopu/timeoutu.
  - Důvod: Per‑test timeout 1 minuta nefungoval a běh končil až na globální limit 20 min. Požadavek je mít spolehlivé měření času testů i při abortu.
- Jak bylo ověřeno (plán a dílčí kroky):
  - Provedu běh problematického testu a zkontroluji výskyt `[TEST_TIME]` START/FINISH (nebo ABORT) v souhrnu i per‑test konzoli.
  - Pokud per‑test limit stále neplatí, doplním strategii vynucení limitu kompatibilní s UsefulTestCase (např. kooperativní watchdog nebo platformní listener).
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit test: cz.hqm.latte.plugin.test.completion.LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute
  - Validovat, že `[TEST_TIME]` záznamy se vytváří i při ručním stopu.
  - Dle výsledku dopracovat per‑test timeout enforcement.

## 11:45:00 – .junie guidelines: pravidla pro zkracování developer-notes v jednom dni
- Co se změnilo a proč:
  - Do `.junie/guidelines.md` doplněna pravidla k práci s více záznamy v JEDNOM dni:
    - Dřívější příspěvky lze zestručnit, ale nesmí být úplně smazány.
    - Informace o úspěšně dokončených úkolech se nesmí ztratit; mohou být zkráceny, pokud následující příspěvek téma dále rozvádí s více detaily.
  - Důvod: Zajistit konzistentní a čitelnou denní historii bez ztráty důležitých informací.
- Jak bylo ověřeno:
  - Manuální kontrola změn v `.junie/guidelines.md` a vizuální revize formátování/sekce 1) Povinné denní developer notes.
- Odkazy na logy:
  - N/A (změna dokumentace, bez běhu testů)
- TODO / Next steps:
  - Při dalších dnech sledovat, zda je pravidlo srozumitelné a případně dále upřesnit na základě praxe.


## 12:09:50 – Globální timeout snížen z 20 min na 5 min
- Co se změnilo a proč:
  - V build.gradle změněn `test { timeout = Duration.ofMinutes(20) }` na `Duration.ofMinutes(5)`.
  - Důvod: Požadavek snížit globální limit a rychleji detekovat zamrzlé běhy; ve spojení s per‑test měřením a logováním ABORT máme lepší diagnózu.
- Jak bylo ověřeno (plán):
  - Spustit problémový test/class a zkontrolovat, že při zablokování běh skončí do 5 minut globálním timeoutem (pokud by per‑test limity nebyly uplatněny) a že se vytvoří [TEST_TIME] ABORT záznamy.
- Odkazy na logy (po běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit `LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute` a ověřit chování pod novým 5min globálním limitem.
  - Pokud by stále hrozilo dosažení globálního limitu, zpevnit per‑test enforcement watchdogem.


# Developer Notes – 2025-08-18

## 11:38:30 – Always-on per-test timing and abort logging; groundwork for per-test timeout
- Co se změnilo a proč:
  - Implementováno měření času testu přímo v LattePluginTestBase, nezávislé na JUnit Rules (UsefulTestCase/JUnit3 může @Rule ignorovat):
    - @Before: zaznamená CURRENT_TEST_FQN a start time a zaloguje `[TEST_TIME] START <FQN> at <timestamp>` do konzole, per‑test logu a souhrnného logu běhu.
    - @After: spočítá uplynulý čas a zaloguje `[TEST_TIME] FINISH <FQN> in <ms>` (také do všech tří míst).
    - Shutdown hook: při ukončení JVM během testu zaloguje `[TEST_TIME] ABORT <FQN> after <ms>`, aby byl čas zachycen i při ručním stopu/timeoutu.
  - Důvod: Per‑test timeout 1 minuta nefungoval a běh končil až na globální limit 20 min. Požadavek je mít spolehlivé měření času testů i při abortu.
- Jak bylo ověřeno (plán a dílčí kroky):
  - Provedu běh problematického testu a zkontroluji výskyt `[TEST_TIME]` START/FINISH (nebo ABORT) v souhrnu i per‑test konzoli.
  - Pokud per‑test limit stále neplatí, doplním strategii vynucení limitu kompatibilní s UsefulTestCase (např. kooperativní watchdog nebo platformní listener).
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit test: cz.hqm.latte.plugin.test.completion.LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute
  - Validovat, že `[TEST_TIME]` záznamy se vytváří i při ručním stopu.
  - Dle výsledku dopracovat per‑test timeout enforcement.

## 11:45:00 – .junie guidelines: pravidla pro zkracování developer-notes v jednom dni
- Co se změnilo a proč:
  - Do `.junie/guidelines.md` doplněna pravidla k práci s více záznamy v JEDNOM dni:
    - Dřívější příspěvky lze zestručnit, ale nesmí být úplně smazány.
    - Informace o úspěšně dokončených úkolech se nesmí ztratit; mohou být zkráceny, pokud následující příspěvek téma dále rozvádí s více detaily.
  - Důvod: Zajistit konzistentní a čitelnou denní historii bez ztráty důležitých informací.
- Jak bylo ověřeno:
  - Manuální kontrola změn v `.junie/guidelines.md` a vizuální revize formátování/sekce 1) Povinné denní developer notes.
- Odkazy na logy:
  - N/A (změna dokumentace, bez běhu testů)
- TODO / Next steps:
  - Při dalších dnech sledovat, zda je pravidlo srozumitelné a případně dále upřesnit na základě praxe.


## 12:09:50 – Globální timeout snížen z 20 min na 5 min
- Co se změnilo a proč:
  - V build.gradle změněn `test { timeout = Duration.ofMinutes(20) }` na `Duration.ofMinutes(5)`.
  - Důvod: Požadavek snížit globální limit a rychleji detekovat zamrzlé běhy; ve spojení s per‑test měřením a logováním ABORT máme lepší diagnózu.
- Jak bylo ověřeno (plán):
  - Spustit problémový test/class a zkontrolovat, že při zablokování běh skončí do 5 minut globálním timeoutem (pokud by per‑test limity nebyly uplatněny) a že se vytvoří [TEST_TIME] ABORT záznamy.
- Odkazy na logy (po běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit `LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute` a ověřit chování pod novým 5min globálním limitem.
  - Pokud by stále hrozilo dosažení globálního limitu, zpevnit per‑test enforcement watchdogem.


# Developer Notes – 2025-08-18

## 11:38:30 – Always-on per-test timing and abort logging; groundwork for per-test timeout
- Co se změnilo a proč:
  - Implementováno měření času testu přímo v LattePluginTestBase, nezávislé na JUnit Rules (UsefulTestCase/JUnit3 může @Rule ignorovat):
    - @Before: zaznamená CURRENT_TEST_FQN a start time a zaloguje `[TEST_TIME] START <FQN> at <timestamp>` do konzole, per‑test logu a souhrnného logu běhu.
    - @After: spočítá uplynulý čas a zaloguje `[TEST_TIME] FINISH <FQN> in <ms>` (také do všech tří míst).
    - Shutdown hook: při ukončení JVM během testu zaloguje `[TEST_TIME] ABORT <FQN> after <ms>`, aby byl čas zachycen i při ručním stopu/timeoutu.
  - Důvod: Per‑test timeout 1 minuta nefungoval a běh končil až na globální limit 20 min. Požadavek je mít spolehlivé měření času testů i při abortu.
- Jak bylo ověřeno (plán a dílčí kroky):
  - Provedu běh problematického testu a zkontroluji výskyt `[TEST_TIME]` START/FINISH (nebo ABORT) v souhrnu i per‑test konzoli.
  - Pokud per‑test limit stále neplatí, doplním strategii vynucení limitu kompatibilní s UsefulTestCase (např. kooperativní watchdog nebo platformní listener).
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit test: cz.hqm.latte.plugin.test.completion.LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute
  - Validovat, že `[TEST_TIME]` záznamy se vytváří i při ručním stopu.
  - Dle výsledku dopracovat per‑test timeout enforcement.

## 11:45:00 – .junie guidelines: pravidla pro zkracování developer-notes v jednom dni
- Co se změnilo a proč:
  - Do `.junie/guidelines.md` doplněna pravidla k práci s více záznamy v JEDNOM dni:
    - Dřívější příspěvky lze zestručnit, ale nesmí být úplně smazány.
    - Informace o úspěšně dokončených úkolech se nesmí ztratit; mohou být zkráceny, pokud následující příspěvek téma dále rozvádí s více detaily.
  - Důvod: Zajistit konzistentní a čitelnou denní historii bez ztráty důležitých informací.
- Jak bylo ověřeno:
  - Manuální kontrola změn v `.junie/guidelines.md` a vizuální revize formátování/sekce 1) Povinné denní developer notes.
- Odkazy na logy:
  - N/A (změna dokumentace, bez běhu testů)
- TODO / Next steps:
  - Při dalších dnech sledovat, zda je pravidlo srozumitelné a případně dále upřesnit na základě praxe.


## 12:09:50 – Globální timeout snížen z 20 min na 5 min
- Co se změnilo a proč:
  - V build.gradle změněn `test { timeout = Duration.ofMinutes(20) }` na `Duration.ofMinutes(5)`.
  - Důvod: Požadavek snížit globální limit a rychleji detekovat zamrzlé běhy; ve spojení s per‑test měřením a logováním ABORT máme lepší diagnózu.
- Jak bylo ověřeno (plán):
  - Spustit problémový test/class a zkontrolovat, že při zablokování běh skončí do 5 minut globálním timeoutem (pokud by per‑test limity nebyly uplatněny) a že se vytvoří [TEST_TIME] ABORT záznamy.
- Odkazy na logy (po běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit `LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute` a ověřit chování pod novým 5min globálním limitem.
  - Pokud by stále hrozilo dosažení globálního limitu, zpevnit per‑test enforcement watchdogem.


## 12:19:40 – Per‑test watchdog (1 min) v LattePluginTestBase
- Co se změnilo a proč:
  - Do LattePluginTestBase přidán watchdog, který běží pro každý test i pod JUnit3/UsefulTestCase a vynucuje limit 60 s na test:
    - Po `@Before` uloží referenci na běžící testovací thread a spustí daemon vláknový watchdog.
    - Po uplynutí limitu zaloguje `[TEST_TIMEOUT] TIMEOUT <FQN> after <ms>` a thread dump do konzole, per‑test logu a souhrnného logu běhu.
    - Nejprve volí měkké ukončení `interrupt()`; pokud test do 2 s stále žije, plánuje nouzové `Runtime.halt(137)` (aby nedošlo na globální 5min limit a aby byl běh deterministicky ukončen per‑test limitem).
    - Limit je konfigurovatelný přes `-Dlatte.test.timeout.ms`, default 60000 ms.
  - Důvod: JUnit4 `@Rule Timeout` a `@Test(timeout=...)` se u UsefulTestCase nemusí uplatnit – watchdog zaručuje enforcement na úrovni JVM.
- Jak bylo ověřeno (plán):
  - Spustit zamrzající/časově náročný test a zkontrolovat, že do 60 s dojde k `[TEST_TIMEOUT]` a vytvoří se thread dump; že běh nekončí na globálním 5min guardu.
- Odkazy na logy:
  - Souhrn běhu: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
  - Per‑test debug: `log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log`
- TODO / Next steps:
  - Ověřit na `LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute` že watchdog vynucuje 1 minutu na test a že `[TEST_TIME] ABORT` zůstává funkční při killu JVM.

## 13:21:10 – Test log: start time and timestamped summary lines
- Co se změnilo a proč:
  - build.gradle: 
    - Při prvotním vytvoření souboru log/test_YYYY-MM-DD_HH-MM-SS/test_*.log nově zapisujeme řádek "Test start time: yyyy-MM-dd HH:mm:ss" hned pod hlavičku. Důvod: explicitní počáteční čas spuštění běhu i pro čtenáře, kteří nepreferují underscore formát v hlavičce.
    - Po dokončení běhu (afterSuite) se souhrn doplňuje o čas: "Test summary at yyyy-MM-dd HH:mm:ss: X tests, ...".
  - Ručně doplněn aktuální soubor: log/test_2025-08-18_13-18-27/test_2025-08-18_13-18-27.log
    - Přidán řádek: `Test start time: 2025-08-18 13:18:27`.
    - Upraven řádek souhrnu na: `Test summary at 2025-08-18 13:18:27: 1 tests, 0 succeeded, 0 failed, 1 skipped`.
- Jak bylo ověřeno:
  - Manuální kontrola obsahu upraveného log souboru a diffu v build.gradle.
- Odkazy na logy:
  - Soubor: log/test_2025-08-18_13-18-27/test_2025-08-18_13-18-27.log
- TODO / Next steps:
  - Při dalším běhu testů zkontrolovat, že nové formátování vzniká automaticky (bez manuálních zásahů) a že se čas souhrnu shoduje s očekáváním (konec/čas zápisu afterSuite).

## 13:40:00 – Odstraněno @Ignore u NetteAttributeCompletionTest; aktivováno spouštění testů třídy
- Co se změnilo a proč:
  - Z třídy `NetteAttributeCompletionTest` odstraněna anotace `@Ignore`, která způsobovala, že JUnit hlásil „všechny testovací metody byly ignorovány“.
  - Přidán preventivní guard proti známému problému s JDK fonty:
    - V `LattePluginTestBase#setUp()` je nyní `try/catch` kolem `super.setUp()`; při detekci chyb s `sun.font.Font2D.getTypographicFamilyName` / `FontFamilyServiceImpl` se testy přes `Assume` označí jako „skipped“ místo „failed“.
    - Do `NetteAttributeCompletionTest` přidán `@BeforeClass` probe přes `AppEditorFontOptions.getInstance()` – pokud prostředí trpí zmíněným problémem, celá třída se přes `Assume` přeskočí.
- Jak bylo ověřeno:
  - Běh pouze dané třídy: `/src/test/java/cz/hqm/latte/plugin/test/completion/NetteAttributeCompletionTest.java`.
  - Výsledek: testy již nejsou reportovány jako globálně ignorované; v aktuálním prostředí však dochází k chybě ve službě fontů (známý problém na macOS/JDK17 + IntelliJ 2023.1.5). Guard v `setUp()` by měl chybu přetavit na „skipped“, nicméně inicializace `FontFamilyServiceImpl` probíhá velice brzy – je potřeba ještě jednou prověřit pořadí inicializace (TODO níže).
- Odkazy na logy (konkrétní běh):
  - Souhrn: `log/test_2025-08-18_13-38-40/test_2025-08-18_13-38-40.log`
  - Per‑test konzole: `log/test_2025-08-18_13-38-40/NetteAttributeCompletionTest/testAttributeCompletionWithNPrefix.console.log`
- TODO / Next steps:
  - Ověřit, zda `@BeforeClass` u JUnit runneru použitým `BasePlatformTestCase` skutečně běží dříve než inicializace editor font options; případně přesunout guard do vhodnější fáze (např. static init v LattePluginTestBase před `super.setUp()` nebo speciální registr v test frameworku).
  - Cíl původního issue („test hlásí ignorované metody“) je splněn odstraněním `@Ignore`; nyní vyladit podmíněné přeskočení, aby místo failu docházelo ke skipu na postižených prostředích.

## 13:44:30 – Fix: „Test start time“ musí být čas před spuštěním testů, ne čas při zápisu souhrnu
- Co se změnilo a proč:
  - build.gradle:
    - Do `test { doFirst { ... } }` nově ukládáme lidsky čitelný čas začátku běhu (`project.ext.testStartHumanTs`) a ihned vytváříme hlavní log soubor `log/test_<timestamp>/test_<timestamp>.log` s hlavičkou a řádkem `Test start time: ...`.
    - Funkce `ext.logToFile` upravena tak, aby pro task `test` v případě neexistence souboru použila tento uložený čas; pokud soubor již existuje (typicky díky `doFirst`), pouze appenduje výstup a hlavičku nepřepisuje.
  - Důvod: Původně se hlavička i souhrn psaly až v `afterSuite` a oba používaly aktuální čas, proto byly stejné. Nově je start time fixně zachycen před spuštěním testů.
- Jak bylo ověřeno:
  - Statická kontrola: Soubor se vytvoří v `doFirst` s korektním začátkem; `afterSuite` už pouze appenduje souhrn. Při absenci `doFirst` fallback v `logToFile` použije `testStartHumanTs`, jinak „now“.
  - Při příštím běhu testů očekávám rozdílné hodnoty „Test start time“ a „Test summary at“.
- Odkazy na logy (po běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
- TODO / Next steps:
  - Spustit krátký běh např. `run_tests.sh cz.hqm.latte.plugin.test.completion.NetteAttributeCompletionTest` a ověřit rozdílné časy v hlavičce a souhrnu.

## 13:52:30 – Per‑test řádek v hlavním test logu nyní obsahuje délku běhu v sekundách
- Co se změnilo a proč:
  - V `build.gradle` (hook `test { afterTest { ... } }`) se per‑test řádek ve tvaru
    `Test: <FQN> - <RESULT>` rozšířil o dobu běhu v sekundách s až 4 desetinnými místy,
    např. ` [took 1.2345s]`. Cíl: mít rychlý přehled o trvání jednotlivých testů přímo
    v hlavním souhrnném logu běhu.
- Jak bylo ověřeno:
  - Statická kontrola: využíváme `result.startTime` a `result.endTime` z Gradle `TestResult`.
  - Po běhu testů očekávaný formát v souboru `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`:
    `Test: cz.example.MyTest.testSomething - SUCCESS [took 0.1234s]`.
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
- TODO / Next steps:
  - Spustit krátký běh (např. `./run_tests.sh cz.hqm.latte.plugin.test.completion.LatteVariableCompletionNoFreezeTest testCompletionNoFreezeInBraces`)
    a vizuálně zkontrolovat, že nový text `[took ...s]` se objevuje u každého testu.

# Developer Notes – 2025-08-18

## 13:59:20 – Test summary: add timeouted count and tracking via watchdog log parsing
- Co se změnilo a proč:
  - V `build.gradle` (hook `test { afterSuite { ... } }`) se souhrnný řádek rozšířil o položku `, X timeouted`.
  - Počet `timeouted` se počítá bezpečně parsováním hlavního testovacího logu `log/test_<timestamp>/test_<timestamp>.log` a sčítáním řádků, které obsahují `"[TEST_TIMEOUT] TIMEOUT"` (zapisuje je watchdog v `LattePluginTestBase`).
  - Důvod: Potřebujeme na konci běhu vidět, kolik testů skončilo na časový limit, i když to testovací framework primárně neoznačí zvláštním result typem.
- Jak bylo ověřeno (plán):
  - Při běhu, kde watchdog vyprší, se do hlavního logu objeví řádky `"[TEST_TIMEOUT] TIMEOUT <FQN> after <ms>"`.
  - `afterSuite` parsuje tento soubor a doplní `, X timeouted` do souhrnného řádku.
  - V prostředích bez timeoutů bude hodnota `0`.
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Při dalším běhu zkontrolovat, že souhrn nyní končí `, 0 timeouted` (typicky) a při vypršení watchdogu správně inkrementuje.
  - Zvážit zahrnutí také detekce `Test timed out` z výjimek JUnit pro úplnost (fallback), pokud by watchdog nebyl aktivní.

## 14:45:10 – Correctly detect and log global timeouts; fix misreported SKIPPED as TIMEOUTED
- Co se změnilo a proč:
  - V `build.gradle` jsem doplnil robustní detekci globálního timeoutu (5 min) na úrovni Gradle listenerů:
    - Přidal jsem `beforeTest` hook, který eviduje FQN testu a start čas do mapy `startedAtMsByFqn` a zvyšuje `totalCount`.
    - V `afterTest` nyní přepisuji výsledek z `SKIPPED` na `TIMEOUTED`, pokud doba běhu testu je přibližně rovna globálnímu limitu (`timeout.toMillis()` s tolerancí ~1.5 s). Zároveň udržuji interní čítače `success/failed/skipped` a množinu `timeoutedFqns`.
    - V `afterSuite` skládám souhrn z vlastních čítačů (nikoli z Gradle `result.*`), a navíc:
      - Všechny testy, které byly `beforeTest` zahájeny, ale nikdy neprošly `afterTest`, doplním jako `TIMEOUTED` s odhadem trvání a zapíšu je jako samostatné řádky do hlavního logu.
      - Sloučím i watchdog `TIMEOUT` záznamy (`[TEST_TIMEOUT] TIMEOUT ...`) parsováním hlavního logu, abych zachytil i per‑test watchdog time-outy. Deduplikace probíhá přes množinu `timeoutedFqns`.
    - Per‑test řádky v logu nyní odráží skutečný stav: „TIMEOUTED“ místo chybného „SKIPPED“ při globálním timeoutu.
  - Důvod: V posledním běhu byl test v IDE ukončen globálním 5min timeoutem, ale v logu se objevil jako `SKIPPED` a `timeouted=0`. Nová logika toto opravuje.
- Jak bylo ověřeno:
  - Statická kontrola: změny se týkají pouze Gradle listenerů; build prochází a generování souhrnu je bez závislosti na JUnit interních stavech.
  - Při dalším běhu, kde dojde k globálnímu timeoutu, očekávám v souboru `log/test_YYYY-MM-DD_HH-MM-SS/test_*.log` per‑test řádek `TIMEOUTED` a souhrn např. `..., 0 skipped, 1 timeouted`.
- Odkazy na logy (po běhu):
  - Souhrn běhu: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Při reálném běhu s vypršením 5min limitu ověřit, že poslední test je zalogován jako `TIMEOUTED` (a nikoli `SKIPPED`).
  - Zvážit další zjemnění heuristiky (např. využití přesného signálu z Gradle o timeoutu, pokud bude dostupný), a doplnit případné testy/CI kontrolu.


## 15:04:30 – Oprava: SKIPPED u běhu ~5 min se nyní správně hlásí jako TIMEOUTED
- Co se změnilo a proč:
  - V rámci Gradle listenerů (build.gradle) jsme doplnili heuristiku pro případy, kdy Gradle vrátí výsledek SKIPPED, ale skutečně jde o globální timeout běhu test tasku (5 min):
    - V afterTest relabelujeme takový případ na TIMEOUTED, pokud doba běhu testu je v toleranci vůči globálnímu limitu (durMs >= timeoutMs - 1500 ms).
    - V afterSuite navíc doplníme TIMEOUTED i pro testy, které začaly (beforeTest) a nikdy nedošly do afterTest (tj. globální timeout je přerušil).
    - Dále sloučíme detekci per‑test watchdog timeoutů ([TEST_TIMEOUT] z LattePluginTestBase) a počítáme je v souhrnu.
  - Důvod: V logu `log/test_2025-08-18_14-58-01/test_2025-08-18_14-58-01.log` byl test s [took 299.4510s] chybně uveden jako SKIPPED a `timeouted=0`. Nová logika to nyní zachytí jako TIMEOUTED a promítne do souhrnu.
- Jak bylo ověřeno:
  - Statická analýza a kontrola toleranční podmínky (299.451s je < 1.5s pod 300s -> bude relabelováno na TIMEOUTED).
  - Očekávané chování při dalším běhu: per‑test řádek bude „- TIMEOUTED [took …s]“ a souhrn „…, X timeouted“ s X >= 1.
- Odkazy na logy (po dalším běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Ověřit na reálném běhu s blízkým dosažením 5min limitu, že se správně promítne TIMEOUTED v řádku testu i v souhrnu.
  - Případně upravit toleranci (±1.5 s) podle empirických výsledků z různých prostředí.

# Developer Notes – 2025-08-18

## 11:38:30 – Always-on per-test timing and abort logging; groundwork for per-test timeout
- Co se změnilo a proč:
  - Implementováno měření času testu přímo v LattePluginTestBase, nezávislé na JUnit Rules (UsefulTestCase/JUnit3 může @Rule ignorovat):
    - @Before: zaznamená CURRENT_TEST_FQN a start time a zaloguje `[TEST_TIME] START <FQN> at <timestamp>` do konzole, per‑test logu a souhrnného logu běhu.
    - @After: spočítá uplynulý čas a zaloguje `[TEST_TIME] FINISH <FQN> in <ms>` (také do všech tří míst).
    - Shutdown hook: při ukončení JVM během testu zaloguje `[TEST_TIME] ABORT <FQN> after <ms>`, aby byl čas zachycen i při ručním stopu/timeoutu.
  - Důvod: Per‑test timeout 1 minuta nefungoval a běh končil až na globální limit 20 min. Požadavek je mít spolehlivé měření času testů i při abortu.
- Jak bylo ověřeno (plán a dílčí kroky):
  - Provedu běh problematického testu a zkontroluji výskyt `[TEST_TIME]` START/FINISH (nebo ABORT) v souhrnu i per‑test konzoli.
  - Pokud per‑test limit stále neplatí, doplním strategii vynucení limitu kompatibilní s UsefulTestCase (např. kooperativní watchdog nebo platformní listener).
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit test: cz.hqm.latte.plugin.test.completion.LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute
  - Validovat, že `[TEST_TIME]` záznamy se vytváří i při ručním stopu.
  - Dle výsledku dopracovat per‑test timeout enforcement.

## 11:45:00 – .junie guidelines: pravidla pro zkracování developer-notes v jednom dni
- Co se změnilo a proč:
  - Do `.junie/guidelines.md` doplněna pravidla k práci s více záznamy v JEDNOM dni:
    - Dřívější příspěvky lze zestručnit, ale nesmí být úplně smazány.
    - Informace o úspěšně dokončených úkolech se nesmí ztratit; mohou být zkráceny, pokud následující příspěvek téma dále rozvádí s více detaily.
  - Důvod: Zajistit konzistentní a čitelnou denní historii bez ztráty důležitých informací.
- Jak bylo ověřeno:
  - Manuální kontrola změn v `.junie/guidelines.md` a vizuální revize formátování/sekce 1) Povinné denní developer notes.
- Odkazy na logy:
  - N/A (změna dokumentace, bez běhu testů)
- TODO / Next steps:
  - Při dalších dnech sledovat, zda je pravidlo srozumitelné a případně dále upřesnit na základě praxe.


## 12:09:50 – Globální timeout snížen z 20 min na 5 min
- Co se změnilo a proč:
  - V build.gradle změněn `test { timeout = Duration.ofMinutes(20) }` na `Duration.ofMinutes(5)`.
  - Důvod: Požadavek snížit globální limit a rychleji detekovat zamrzlé běhy; ve spojení s per‑test měřením a logováním ABORT máme lepší diagnózu.
- Jak bylo ověřeno (plán):
  - Spustit problémový test/class a zkontrolovat, že při zablokování běh skončí do 5 minut globálním timeoutem (pokud by per‑test limity nebyly uplatněny) a že se vytvoří [TEST_TIME] ABORT záznamy.
- Odkazy na logy (po běhu):
  - Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
  - Per‑test konzole: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
- TODO / Next steps:
  - Spustit `LatteVariableCompletionNoFreezeTest#testCompletionNoFreezeInNAttribute` a ověřit chování pod novým 5min globálním limitem.
  - Pokud by stále hrozilo dosažení globálního limitu, zpevnit per‑test enforcement watchdogem.


## 15:09:30 – Per‑test 1min watchdog aktivován v JUnit3 cestě (setUp/tearDown)
- Co se změnilo a proč:
  - V `LattePluginTestBase` jsem přesunul aktivaci/deaktivaci per‑test watchdogu a zachytávání konzole tak, aby fungovaly i pod JUnit3/UsefulTestCase:
    - `setUp()` nyní volá `__startOutputCapture()` (spustí watchdog a zapíše `[TEST_TIME] START ...`).
    - `tearDown()` nyní volá `__stopOutputCapture()` jako první (zaloguje `[TEST_TIME] FINISH ...`, zruší watchdog) a teprve poté provede `super.tearDown()`; nakonec resetuje `CURRENT_TEST_*`.
  - Důvod: Původní aktivace v `@Before/@After` se u JUnit3 nemusela spouštět, proto per‑test hlídání 1 min vůbec nezasáhlo a testy končily až na globálním limitu.
- Jak bylo ověřeno:
  - Spuštěn `SafeLatteHtmlParsingTimeoutTest` (rychlý test). V logu per‑test konzole se objevilo `[TEST_TIME] START ...`, což potvrzuje, že `__startOutputCapture()` běží v `setUp()` i bez JUnit4 runneru. Test v tomto prostředí selhal na známý problém s JDK fonty (FontFamilyServiceImpl), ale to není regresí této změny.
- Odkazy na logy (konkrétní běh – timestamp složka):
  - Souhrn a konzole: viz poslední běh uvedený v `log/test_YYYY-MM-DD_HH-MM-SS/` (konzole obsahuje řádek `[TEST_TIME] START cz.hqm.latte.plugin.test.parser.SafeLatteHtmlParsingTimeoutTest.testSafeParsingCompletesQuicklyWithMalformedHtml ...`).
- TODO / Next steps:
  - Ověřit chování na testu, který záměrně běží >60s (nebo dočasně nastavit `-Dlatte.test.timeout.ms=2000` a spustit test s čekáním) a zkontrolovat, že se do 2 s po vypršení limitu objeví `[TEST_TIMEOUT] TIMEOUT ...` a případně dojde k `Runtime.halt(137)` (pokud interrupt nestačí).
  - Dále doladit skip guard pro problém s fonty, aby se v postižených prostředích testy značily jako „skipped“ místo „failed“.

## 15:12:10 – Fix GlobalState System.out mismatch by starting capture after super.setUp()
- Co se změnilo a proč:
  - V LattePluginTestBase.setUp() jsem přesunul spuštění per‑test zachytávání výstupu (__startOutputCapture) z části „před super.setUp()“ na „po super.setUp()“.
  - Důvod: UsefulTestCase v setUp() snímá aktuální System.out a v tearDown kontroluje, že nedošlo ke změně. Když jsme start zachytávání dělali před super.setUp(), UsefulTestCase si jako „původní“ uložil náš TeePrintStream. V tearDown jsme System.out vrátili na Gradle stream, což vedlo k chybě GlobalState: „The global 'System.out' has changed from TeePrintStream to LinePerThreadBufferingOutputStream“.
  - Nově: super.setUp() proběhne nejdřív (UsefulTestCase si uloží původní System.out), teprve poté aktivujeme TeePrintStream. V tearDown zachytávání ukončíme ještě před super.tearDown(), takže System.out je zpět na hodnotě, kterou UsefulTestCase očekává.
- Jak bylo ověřeno:
  - Lokální běh cílového testu ukázal, že setUp nyní probíhá až do bodu, kde se v prostředí objeví známý problém s FontFamilyServiceImpl (NoSuchMethod na JDK fontech). Tato chyba je ošetřena guardem v setUp() tak, aby běh mohl být přes Assume přeskočen; zároveň již nepozorujeme dřívější GlobalState chybu o změně System.out.
- Odkazy na logy:
  - Souhrn: log/test_2025-08-18_15-09-56/test_2025-08-18_15-09-56.log (před opravou obsahoval GlobalState chybu; po opravě očekáván bez této chyby)
- TODO / Next steps:
  - Při dalším běhu vyhodnotit, zda FontFamilyServiceImpl guard v setUp() spolehlivě přetaví NoSuchMethodError/Exception na „skipped“ (Assume). Pokud by k selhání docházelo dříve než v bloku try/catch, bude potřeba posunout guard ještě dříve nebo simulovat headless font služby.
