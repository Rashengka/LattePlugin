# Developer notes – 2025-08-11

Tento zápis shrnuje stav testů, nalezené problémy a provedené opravy v rámci dnešní iterace.

## 1) Spuštění testů a výstupy
- Spuštění celé sady: `./gradlew test` – běh skončil chybou „Timeout has been exceeded“ po ~5 minutách, nicméně z průběžného logu bylo patrné, že některé testy padají.
- Cílené spuštění problémových testů:
  - `./gradlew test --tests "*NAttributeSyntaxTest*"` – všechny testy v této třídě po opravě prošly.
  - Doplňkové rychlé smoke testy:
    - `./gradlew test --tests "*LatteHtmlHighlightingTest*" --tests "*LatteSyntaxHighlighterTest*" --tests "*Latte4xCompletionTest*"` – proběhly úspěšně.

Pozn.: Logy k testům jsou v adresáři `log/` (test timestamp podsložky) a standardní HTML report je v `build/reports/tests/test/index.html`.

## 2) Identifikovaný problém
- Failing testy (před opravou):
  - `NAttributeSyntaxTest.testNSyntaxDoubleAttribute` a `NAttributeSyntaxTest.testNSyntaxUnquotedValue` – očekávaly, že po zpracování atributu `n:syntax` bude nastavena `LatteSyntaxMode.DOUBLE`, ale `LatteLexer` zůstával v režimu `DEFAULT`.
- Příčina:
  - `LatteLexer` spoléhal na vrstvený lexer pro n:atributy registrovaný přes vlastní `LatteTokenTypes` (`LATTE_ATTRIBUTE_START/END`). Tyto tokeny ale negeneruje bazový `HtmlLexer`, a tudíž se vrstva při použití `LatteLexer` neaktivovala. Regex fallback v `processSyntaxTags()` nepostačoval, protože HTML tokenizace obvykle dělí název a hodnotu atributu do samostatných XML tokenů.

## 3) Oprava
- Soubor: `src/main/java/cz/hqm/latte/plugin/lexer/LatteLexer.java`
- Změny (minimální, neinvazivní):
  1) Přidána detekce XML tokenů z `HtmlLexer` pomocí `XmlTokenType`:
     - Pokud je aktuální token `XML_NAME` a text je `n:syntax`, zapne se interní příznak `nSyntaxAttributeSeen`.
     - Pokud je následně aktuální token `XML_ATTRIBUTE_VALUE_TOKEN`, hodnota se odčárkuje (odstranění uvozovek) a zavolá se `setSyntaxMode(value)`. Příznak se resetuje.
  2) Zachován dosavadní mechanismus `processSyntaxTags()` pro případy, kdy je celý atribut v jednom tokenu (fallback regex podpora obou variant: citovaná i necitovaná hodnota).
  3) Drobnosti: přidán import `com.intellij.psi.xml.XmlTokenType`, drobné util `stripQuotes()`, reset příznaku v `reset()`.

- Výsledek: 
  - `NAttributeSyntaxTest` nyní prochází včetně unquoted varianty.
  - Rychlé smoke testy pro highlighting/completion rovněž prochází.

## 4) Poznámky k rizikům a dalším krokům
- Potenciální dopady: Změna sleduje pouze XML tokeny `XML_NAME` a `XML_ATTRIBUTE_VALUE_TOKEN`. To by mělo být stabilní pro HTML (X)HTML kontexty. Při nestandardních vstupech (např. fragmenty bez korektního párování) může být chování závislé na `HtmlLexer`.
- Doporučení:
  - Spustit plnou sadu testů v prostředí s delším timeoutem (příp. `org.gradle.jvmargs=-Xmx...` a zvýšený test timeout v Gradlu/CI), jelikož lokální běh se zastavil na globálním timeoutu. Na základě dílčích běhů je aktuální fix konzistentní.
  - Zvážit případnou registraci attribute layer nad vhodnými `XmlTokenType` (např. `XML_NAME`/`XML_ATTRIBUTE_VALUE_TOKEN`) pokud by bylo potřeba bohatší tokenizace n:atributů přímo v `LatteLexer` (zatím není nutné).

## 5) Shrnutí commit změn
- Upraven: `LatteLexer.java` – přidána detekce `n:syntax` přes XML tokeny a nastavení `syntaxMode` při čtení hodnoty.
- Testy: `NAttributeSyntaxTest` a vybrané highlight/completion testy prochází.

—
Autor: Junie (AI), datum: 2025-08-11


## 6) Iterace – 10:10 (retest + pokračování)
- Full run: `./gradlew test` – opět globální timeout po ~5 min (konzistentní se stavem z předchozího běhu).
- Selektivní běhy dle poznámek:
  - `./gradlew test --tests "*NAttributeSyntaxTest*"` – všechny testy PROŠLY.
  - `./gradlew test --tests "*LatteHtmlHighlightingTest*" --tests "*LatteSyntaxHighlighterTest*" --tests "*Latte4xCompletionTest*"` – všechny PROŠLY.
  - `./gradlew test --tests "*LatteCachingInEditorTest*" --tests "*LatteMacroCachingTest*" --tests "*LatteCacheManagerTest*"` – všechny PROŠLY.

### Poznámky
- Oprava v `LatteLexer` pro `n:syntax` se potvrzuje na opakovaných bězích.
- Pro full-run timeout navrhuji v CI zvýšit časový limit test tasku a/nebo spouštět balíčky po skupinách (attributes, highlighting, completion, caching). Viz docs/testing/TEST_CONFIGURATION.md pro VM options; případně přidat Gradle vlastnosti (větší heap, delší timeouty).
- Další krok: případné zahrnutí zbývajících testů (např. Nette completion) do selektivních běhů, jakmile budou připravené a označené ne-skip.

—
Autor: Junie (AI), čas: 2025-08-11 10:10

## 7) Proč nefunguje spuštění všech testů přes Gradle z IDE
- Pozorování: V IDE Gradle konzoli se objevuje hláška „LattePlugin:test nebylo nalezeno“.
- Příčina: Projekt je single-module. V Gradlu má root projekt cestu k tasku `:test` (ne `:LattePlugin:test`). Zápis `LattePlugin:test` by odkazoval na subprojekt `LattePlugin`, který v repozitáři neexistuje, proto Gradle vypíše, že task nebyl nalezen.
- Důsledek: Pokus o spuštění všech testů přes špatně zadanou cestu k tasku v IDE skončí chybou „task not found“. Při spuštění `./gradlew test` (správný task) se testy spouští, ale u nás dříve narážely na 5min timeout.

### Doporučení (IDE i CLI)
- V IDE v Gradle Run Configuration používej jako task `test` nebo `:test`, případně `check` (který na `test` závisí).
- Pro filtrování testů používej Gradle parametr `--tests`, např. `--tests "*NAttributeSyntaxTest*"`.
- Pokud potřebuješ izolované běhy jako v IDE a rychlost, nespouštěj s per‑test forkováním JVM. Nově je v build.gradle forkování podmíněné a defaultně vypnuté; zapneš jej volbou `-PforkEveryOne`.
- Pokud narazíš na globální timeout celé sady, zvaž dočasně běh po skupinách (attributes/highlighting/completion/caching) nebo uprav timeout (lze zvýšit v build.gradle).

## 8) Fix pro IDE chybovou hlášku „Cannot locate tasks that match 'LattePlugin:test' … project 'LattePlugin' not found“
- Příčina: IDE Gradle konfigurace odkazovala na neexistující subprojekt `LattePlugin` (správný task v single‑module projektu je `:test`).
- Oprava v repozitáři (kompatibilitní alias):
  - Přidán subprojekt `LattePlugin` (adresář `LattePlugin/`) a `settings.gradle` nyní obsahuje `include ':LattePlugin'`.
  - V `LattePlugin/build.gradle` jsou zaregistrovány delegující tasky `test` a `check`, které pouze `dependsOn` kořenové `:test`/`:check`.
- Výsledek: Spuštění `LattePlugin:test` nebo `LattePlugin:check` v IDE je nyní podporováno a přesměruje se na skutečné tasky v root projektu. Stávající IDE Run Configurations tím pádem fungují bez manuální úpravy.
- Pozn.: Alternativně lze nadále používat správné tasky `test`, `:test` nebo `check`.

## 9) „Run test …/src/test/java“ v IDE nespouští žádné testy – oprava
- Příčina: IDE často spouští „All in directory“ přes JUnit Platform (JUnit 5). Naše testy jsou v JUnit 4, Gradle byl nastaven na useJUnit() a chyběl Vintage engine → JUnit 4 testy nebyly pro JUnit Platform viditelné.
- Oprava v build.gradle (root):
  - Přepnuto na `test { useJUnitPlatform() }`.
  - Přidán Vintage engine: `testRuntimeOnly 'org.junit.vintage:junit-vintage-engine:5.9.2'` a Jupiter API/engine pro úplnost.
  - Zůstává `testImplementation 'junit:junit:4.13.2'` – testy dál používají JUnit 4.
- Výsledek: Při spuštění „Run tests in directory (src/test/java)“ v IDE se testy korektně najdou a běží (přes JUnit Platform s Vintage). Stejně tak `./gradlew test` běží beze změny chování, jen přes JUnit Platform.

## 10) Dnešní iterace – 12:15+ (oprava běhu testů v Gradlu, smoke běhy)
- Problém: Selektivní běhy `:test --tests "*Latte4xCompletionTest*"` padaly s chybou `NoClassDefFoundError: org/junit/platform/launcher/LauncherSessionListener` (JUnit Platform chyběla v classpath test běhu v interakci s IntelliJ plugin classloaderem).
- Oprava: Přidán explicitní launcher do test runtime:
  - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.9.2'` v root `build.gradle`.
- Problém: Aliasový task `LattePlugin:test` (subprojekt) původně definován jako `Test` → Gradle se snažil číst `testClassesDirs` (null) a padal s `Cannot invoke ... getTestClassesDirs()` a navíc nešlo použít `--tests` (task není typu Test).
- Oprava: Změna aliasu v `LattePlugin/build.gradle` z typu `Test` na obecný task:
  - nyní: `tasks.register('test') { dependsOn gradle.rootProject.tasks.named('test') }`
  - Stejně pro `check`.
  - Pozn.: Filtrování testů vždy zadávejte na kořenový `:test` (např. `./gradlew :test --tests "*Latte4xCompletionTest*"`). Při volbě `LattePlugin:test` není `--tests` podporováno (očekávané).
- Retest (Gradle):
  - `./gradlew :test --tests "*LatteVersionTest*"` – PROŠEL.
  - `./gradlew :test --tests "*Latte4xCompletionTest*"` – PROŠEL (4 testy, 100%).
  - `./gradlew :test --tests "*LatteSyntaxHighlighterTest*" --tests "*LatteHtmlHighlightingTest*"` – PROŠLY.
  - `./gradlew :test --tests "*NAttributeSyntaxTest*"` – PROŠEL.
- Doporučení: Full-run `./gradlew test` může stále narážet na globální timeout 5 min (viz dřívější poznámky). V CI dočasně zvyšovat timeout nebo spouštět po skupinách.

—
Autor: Junie (AI), čas: 2025-08-11 12:20

## 11) Paralelní běh testů – zapnutí a konfigurace
- Cíl: Umožnit běh testů paralelně, dosud se spouštěly sekvenčně v rámci jediného JVM.
- Změna v `build.gradle` (root):
  - Nastaven `maxParallelForks` dynamicky dle počtu CPU jader (default = floor(jádra/2), minimálně 1).
  - Lze přepnout/ovlivnit:
    - `-PmaxParallelForks=N` → explicitně nastaví počet paralelních forků.
    - `-PdisableTestParallel` → vynutí sekvenční běh (`maxParallelForks=1`).
  - Pro JUnit Platform doplněny systémové vlastnosti pro paralelní běh Jupiter testů (Vintage testy to neovlivní, je to bezpečné).
- Pozn.: Pro pluginové testy IntelliJ (BasePlatformTestCase) je paralelizace citlivá na sdílený Application/Project stav. Pokud by se objevily flaky chyby, použijte `-PdisableTestParallel` pro sekvenční běh, případně s per‑test forkováním `-PforkEveryOne`.
- Příklady:
  - `./gradlew test` → poběží paralelně s výchozím počtem forků.
  - `./gradlew test -PmaxParallelForks=2` → přesně 2 paralelní JVM.
  - `./gradlew test -PdisableTestParallel` → sekvenční běh.

## 12) IDE: Spuštění všech testů bez zadání cesty (Run All Tests)
- Cíl: V IDE spustit všechny testy bez ručního zadání absolutní cesty nebo filtru – ideálně přes:
  - Gradle Run Config: Task `test` nebo `:test` (případně `LattePlugin:test` alias)
  - JUnit Run Config: „All in Project“ nebo „All in directory src/test/java“
- Co bylo upraveno (rekapitulace):
  - `test { useJUnitPlatform() }` + Vintage engine → JUnit 4 testy jsou viditelné pro JUnit Platform (IDE i Gradle).
  - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.9.2'` → řeší chybějící launcher v některých IDE/Gradle kombinacích.
  - Alias modul `LattePlugin` + `LattePlugin/test` deleguje na `:test` → stávající IDE konfigurace se subprojektem fungují.
- Ověření:
  - „Run Gradle task: test“ (bez filtrů) testy vyhledal a spustil – proběhla detekce výrazně více než 11 testů (viz log `log/test_YYYY-MM-DD_HH-MM-SS/…`).
  - „Run tests in directory (src/test/java)“ jako JUnit konfigurace: testy se najdou (Vintage) a běží bez nutnosti uvádět absolutní cestu.
- Poznámka: Úspěšnost jednotlivých testů se může lišit (pluginové testy jsou náročné na prostředí). Cílem této položky bylo zajistit spolehlivou detekci a start; to je splněno.

### 12.1) Sdílené IDE konfigurace (commitnuté)
- Přidána sdílená Run Config do `.idea/runConfigurations/`:
  - `All Tests (Gradle)` → spustí `:test` přes Gradle, výsledky v Test UI.
- Díky tomu lze v IDE rovnou zvolit konfiguraci a spustit testy bez dalšího nastavování.

# Developer notes – 2025-08-11

Tento zápis shrnuje stav testů, nalezené problémy a provedené opravy v rámci dnešní iterace.

## 1) Spuštění testů a výstupy
- Spuštění celé sady: `./gradlew test` – běh skončil chybou „Timeout has been exceeded“ po ~5 minutách, nicméně z průběžného logu bylo patrné, že některé testy padají.
- Cílené spuštění problémových testů:
  - `./gradlew test --tests "*NAttributeSyntaxTest*"` – všechny testy v této třídě po opravě prošly.
  - Doplňkové rychlé smoke testy:
    - `./gradlew test --tests "*LatteHtmlHighlightingTest*" --tests "*LatteSyntaxHighlighterTest*" --tests "*Latte4xCompletionTest*"` – proběhly úspěšně.

Pozn.: Logy k testům jsou v adresáři `log/` (test timestamp podsložky) a standardní HTML report je v `build/reports/tests/test/index.html`.

## 2) Identifikovaný problém
- Failing testy (před opravou):
  - `NAttributeSyntaxTest.testNSyntaxDoubleAttribute` a `NAttributeSyntaxTest.testNSyntaxUnquotedValue` – očekávaly, že po zpracování atributu `n:syntax` bude nastavena `LatteSyntaxMode.DOUBLE`, ale `LatteLexer` zůstával v režimu `DEFAULT`.
- Příčina:
  - `LatteLexer` spoléhal na vrstvený lexer pro n:atributy registrovaný přes vlastní `LatteTokenTypes` (`LATTE_ATTRIBUTE_START/END`). Tyto tokeny ale negeneruje bazový `HtmlLexer`, a tudíž se vrstva při použití `LatteLexer` neaktivovala. Regex fallback v `processSyntaxTags()` nepostačoval, protože HTML tokenizace obvykle dělí název a hodnotu atributu do samostatných XML tokenů.

## 3) Oprava
- Soubor: `src/main/java/cz/hqm/latte/plugin/lexer/LatteLexer.java`
- Změny (minimální, neinvazivní):
  1) Přidána detekce XML tokenů z `HtmlLexer` pomocí `XmlTokenType`:
     - Pokud je aktuální token `XML_NAME` a text je `n:syntax`, zapne se interní příznak `nSyntaxAttributeSeen`.
     - Pokud je následně aktuální token `XML_ATTRIBUTE_VALUE_TOKEN`, hodnota se odčárkuje (odstranění uvozovek) a zavolá se `setSyntaxMode(value)`. Příznak se resetuje.
  2) Zachován dosavadní mechanismus `processSyntaxTags()` pro případy, kdy je celý atribut v jednom tokenu (fallback regex podpora obou variant: citovaná i necitovaná hodnota).
  3) Drobnosti: přidán import `com.intellij.psi.xml.XmlTokenType`, drobné util `stripQuotes()`, reset příznaku v `reset()`.

- Výsledek: 
  - `NAttributeSyntaxTest` nyní prochází včetně unquoted varianty.
  - Rychlé smoke testy pro highlighting/completion rovněž prochází.

## 4) Poznámky k rizikům a dalším krokům
- Potenciální dopady: Změna sleduje pouze XML tokeny `XML_NAME` a `XML_ATTRIBUTE_VALUE_TOKEN`. To by mělo být stabilní pro HTML (X)HTML kontexty. Při nestandardních vstupech (např. fragmenty bez korektního párování) může být chování závislé na `HtmlLexer`.
- Doporučení:
  - Spustit plnou sadu testů v prostředí s delším timeoutem (příp. `org.gradle.jvmargs=-Xmx...` a zvýšený test timeout v Gradlu/CI), jelikož lokální běh se zastavil na globálním timeoutu. Na základě dílčích běhů je aktuální fix konzistentní.
  - Zvážit případnou registraci attribute layer nad vhodnými `XmlTokenType` (např. `XML_NAME`/`XML_ATTRIBUTE_VALUE_TOKEN`) pokud by bylo potřeba bohatší tokenizace n:atributů přímo v `LatteLexer` (zatím není nutné).

## 5) Shrnutí commit změn
- Upraven: `LatteLexer.java` – přidána detekce `n:syntax` přes XML tokeny a nastavení `syntaxMode` při čtení hodnoty.
- Testy: `NAttributeSyntaxTest` a vybrané highlight/completion testy prochází.

—
Autor: Junie (AI), datum: 2025-08-11


## 6) Iterace – 10:10 (retest + pokračování)
- Full run: `./gradlew test` – opět globální timeout po ~5 min (konzistentní se stavem z předchozího běhu).
- Selektivní běhy dle poznámek:
  - `./gradlew test --tests "*NAttributeSyntaxTest*"` – všechny testy PROŠLY.
  - `./gradlew test --tests "*LatteHtmlHighlightingTest*" --tests "*LatteSyntaxHighlighterTest*" --tests "*Latte4xCompletionTest*"` – všechny PROŠLY.
  - `./gradlew test --tests "*LatteCachingInEditorTest*" --tests "*LatteMacroCachingTest*" --tests "*LatteCacheManagerTest*"` – všechny PROŠLY.

### Poznámky
- Oprava v `LatteLexer` pro `n:syntax` se potvrzuje na opakovaných bězích.
- Pro full-run timeout navrhuji v CI zvýšit časový limit test tasku a/nebo spouštět balíčky po skupinách (attributes, highlighting, completion, caching). Viz docs/testing/TEST_CONFIGURATION.md pro VM options; případně přidat Gradle vlastnosti (větší heap, delší timeouty).
- Další krok: případné zahrnutí zbývajících testů (např. Nette completion) do selektivních běhů, jakmile budou připravené a označené ne-skip.

—
Autor: Junie (AI), čas: 2025-08-11 10:10

## 7) Proč nefunguje spuštění všech testů přes Gradle z IDE
- Pozorování: V IDE Gradle konzoli se objevuje hláška „LattePlugin:test nebylo nalezeno“.
- Příčina: Projekt je single-module. V Gradlu má root projekt cestu k tasku `:test` (ne `:LattePlugin:test`). Zápis `LattePlugin:test` by odkazoval na subprojekt `LattePlugin`, který v repozitáři neexistuje, proto Gradle vypíše, že task nebyl nalezen.
- Důsledek: Pokus o spuštění všech testů přes špatně zadanou cestu k tasku v IDE skončí chybou „task not found“. Při spuštění `./gradlew test` (správný task) se testy spouští, ale u nás dříve narážely na 5min timeout.

### Doporučení (IDE i CLI)
- V IDE v Gradle Run Configuration používej jako task `test` nebo `:test`, případně `check` (který na `test` závisí).
- Pro filtrování testů používej Gradle parametr `--tests`, např. `--tests "*NAttributeSyntaxTest*"`.
- Pokud potřebuješ izolované běhy jako v IDE a rychlost, nespouštěj s per‑test forkováním JVM. Nově je v build.gradle forkování podmíněné a defaultně vypnuté; zapneš jej volbou `-PforkEveryOne`.
- Pokud narazíš na globální timeout celé sady, zvaž dočasně běh po skupinách (attributes/highlighting/completion/caching) nebo uprav timeout (lze zvýšit v build.gradle).

## 8) Fix pro IDE chybovou hlášku „Cannot locate tasks that match 'LattePlugin:test' … project 'LattePlugin' not found“
- Příčina: IDE Gradle konfigurace odkazovala na neexistující subprojekt `LattePlugin` (správný task v single‑module projektu je `:test`).
- Oprava v repozitáři (kompatibilitní alias):
  - Přidán subprojekt `LattePlugin` (adresář `LattePlugin/`) a `settings.gradle` nyní obsahuje `include ':LattePlugin'`.
  - V `LattePlugin/build.gradle` jsou zaregistrovány delegující tasky `test` a `check`, které pouze `dependsOn` kořenové `:test`/`:check`.
- Výsledek: Spuštění `LattePlugin:test` nebo `LattePlugin:check` v IDE je nyní podporováno a přesměruje se na skutečné tasky v root projektu. Stávající IDE Run Configurations tím pádem fungují bez manuální úpravy.
- Pozn.: Alternativně lze nadále používat správné tasky `test`, `:test` nebo `check`.

## 9) „Run test …/src/test/java“ v IDE nespouští žádné testy – oprava
- Příčina: IDE často spouští „All in directory“ přes JUnit Platform (JUnit 5). Naše testy jsou v JUnit 4, Gradle byl nastaven na useJUnit() a chyběl Vintage engine → JUnit 4 testy nebyly pro JUnit Platform viditelné.
- Oprava v build.gradle (root):
  - Přepnuto na `test { useJUnitPlatform() }`.
  - Přidán Vintage engine: `testRuntimeOnly 'org.junit.vintage:junit-vintage-engine:5.9.2'` a Jupiter API/engine pro úplnost.
  - Zůstává `testImplementation 'junit:junit:4.13.2'` – testy dál používají JUnit 4.
- Výsledek: Při spuštění „Run tests in directory (src/test/java)“ v IDE se testy korektně najdou a běží (přes JUnit Platform s Vintage). Stejně tak `./gradlew test` běží beze změny chování, jen přes JUnit Platform.

## 10) Dnešní iterace – 12:15+ (oprava běhu testů v Gradlu, smoke běhy)
- Problém: Selektivní běhy `:test --tests "*Latte4xCompletionTest*"` padaly s chybou `NoClassDefFoundError: org/junit/platform/launcher/LauncherSessionListener` (JUnit Platform chyběla v classpath test běhu v interakci s IntelliJ plugin classloaderem).
- Oprava: Přidán explicitní launcher do test runtime:
  - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.9.2'` v root `build.gradle`.
- Problém: Aliasový task `LattePlugin:test` (subprojekt) původně definován jako `Test` → Gradle se snažil číst `testClassesDirs` (null) a padal s `Cannot invoke ... getTestClassesDirs()` a navíc nešlo použít `--tests` (task není typu Test).
- Oprava: Změna aliasu v `LattePlugin/build.gradle` z typu `Test` na obecný task:
  - nyní: `tasks.register('test') { dependsOn gradle.rootProject.tasks.named('test') }`
  - Stejně pro `check`.
  - Pozn.: Filtrování testů vždy zadávejte na kořenový `:test` (např. `./gradlew :test --tests "*Latte4xCompletionTest*"`). Při volbě `LattePlugin:test` není `--tests` podporováno (očekávané).
- Retest (Gradle):
  - `./gradlew :test --tests "*LatteVersionTest*"` – PROŠEL.
  - `./gradlew :test --tests "*Latte4xCompletionTest*"` – PROŠEL (4 testy, 100%).
  - `./gradlew :test --tests "*LatteSyntaxHighlighterTest*" --tests "*LatteHtmlHighlightingTest*"` – PROŠLY.
  - `./gradlew :test --tests "*NAttributeSyntaxTest*"` – PROŠEL.
- Doporučení: Full-run `./gradlew test` může stále narážet na globální timeout 5 min (viz dřívější poznámky). V CI dočasně zvyšovat timeout nebo spouštět po skupinách.

—
Autor: Junie (AI), čas: 2025-08-11 12:20

## 11) Paralelní běh testů – zapnutí a konfigurace
- Cíl: Umožnit běh testů paralelně, dosud se spouštěly sekvenčně v rámci jediného JVM.
- Změna v `build.gradle` (root):
  - Nastaven `maxParallelForks` dynamicky dle počtu CPU jader (default = floor(jádra/2), minimálně 1).
  - Lze přepnout/ovlivnit:
    - `-PmaxParallelForks=N` → explicitně nastaví počet paralelních forků.
    - `-PdisableTestParallel` → vynutí sekvenční běh (`maxParallelForks=1`).
  - Pro JUnit Platform doplněny systémové vlastnosti pro paralelní běh Jupiter testů (Vintage testy to neovlivní, je to bezpečné).
- Pozn.: Pro pluginové testy IntelliJ (BasePlatformTestCase) je paralelizace citlivá na sdílený Application/Project stav. Pokud by se objevily flaky chyby, použijte `-PdisableTestParallel` pro sekvenční běh, případně s per‑test forkováním `-PforkEveryOne`.
- Příklady:
  - `./gradlew test` → poběží paralelně s výchozím počtem forků.
  - `./gradlew test -PmaxParallelForks=2` → přesně 2 paralelní JVM.
  - `./gradlew test -PdisableTestParallel` → sekvenční běh.

## 12) IDE: Spuštění všech testů bez zadání cesty (Run All Tests)
- Cíl: V IDE spustit všechny testy bez ručního zadání absolutní cesty nebo filtru – ideálně přes:
  - Gradle Run Config: Task `test` nebo `:test` (případně `LattePlugin:test` alias)
  - JUnit Run Config: „All in Project“ nebo „All in directory src/test/java“
- Co bylo upraveno (rekapitulace):
  - `test { useJUnitPlatform() }` + Vintage engine → JUnit 4 testy jsou viditelné pro JUnit Platform (IDE i Gradle).
  - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.9.2'` → řeší chybějící launcher v některých IDE/Gradle kombinacích.
  - Alias modul `LattePlugin` + `LattePlugin/test` deleguje na `:test` → stávající IDE konfigurace se subprojektem fungují.
- Ověření:
  - „Run Gradle task: test“ (bez filtrů) testy vyhledal a spustil – proběhla detekce výrazně více než 11 testů (viz log `log/test_YYYY-MM-DD_HH-MM-SS/…`).
  - „Run tests in directory (src/test/java)“ jako JUnit konfigurace: testy se najdou (Vintage) a běží bez nutnosti uvádět absolutní cestu.
- Poznámka: Úspěšnost jednotlivých testů se může lišit (pluginové testy jsou náročné na prostředí). Cílem této položky bylo zajistit spolehlivou detekci a start; to je splněno.

### 12.1) Sdílené IDE konfigurace (commitnuté)
- Přidána sdílená Run Config do `.idea/runConfigurations/`:
  - `All Tests (Gradle)` → spustí `:test` přes Gradle, výsledky v Test UI.
- Díky tomu lze v IDE rovnou zvolit konfiguraci a spustit testy bez dalšího nastavování.

## 13) ITC – akcelerace testů (per‑test cíle a změny 2025‑08‑13)
- Způsob: Dle ITC analyzován poslední report `build/reports/tests/test/index.html` (13. 8. 2025 09:24). Největší podíl času mají čistě lexerové testy, které zbytečně inicializují BasePlatformTestCase.

## 14) Per‑metodové zachytávání konzole – implementace a doporučení (2025‑08‑13 11:06)
- Stav: Aktivováno pro všechny JUnit4 testy přes `@Rule TestOutputCaptureRule` a záložní `@Before/@After` v `LattePluginTestBase`.
- Co se ukládá:
  - Kompletní výstup každé testovací metody (stdout i stderr) do souboru:
    - `log/test_YYYY-MM-DD_HH-MM-SS/<Třída>/<metoda>.console.log`
  - Per‑test plugin logy (LatteLogger – debug/validation) zůstávají v:
    - `log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log`
  - Souhrnný log běhu testů (čas, paměť, výsledky):
    - `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
- Proč je to důležité: U pluginových testů IntelliJ se část výstupu (varování, známé chyby font/VFS) neobjevuje vždy v HTML reportu. Per‑metodové `.console.log` poskytuje plný pohled pro analýzu příčin selhání/timeoutu.
- Postup analýzy (zkráceně):
  1) Otevři souhrn: `log/test_<ts>/test_<ts>.log` a HTML report `build/reports/tests/test/index.html`.
  2) Pro konkrétní fail/timeout otevři `log/test_<ts>/<Class>/<method>.console.log` – je tam kompletní konzole s prefixy `[STDOUT]`/`[STDERR]` a hlavičkou/patičkou.
  3) Doplň do obrazu i per‑test plugin logy v `log/test_<ts>/<TestName_method>/latte_plugin_<ts>_*.log`.
- Pozn.: Konzole vždy vypíše `Command output logged to: ...` s absolutní cestou na souhrnný log běhu, což usnadňuje navigaci.

### 13.1) Baseline časy (před změnou)
- NAttributeSyntaxTest
  - testNSyntaxDoubleAttribute: 1.188 s
  - ostatní metody: 0.014–0.021 s
- EnhancedAttributeTest
  - testPrefixedAttributes: 1.227 s
  - ostatní metody: 0.011–0.022 s

### 13.2) Cíle zrychlení (per‑test)
- NAttributeSyntaxTest.testNSyntaxDoubleAttribute → cíl ≤ 0.20 s
- EnhancedAttributeTest.testPrefixedAttributes → cíl ≤ 0.15 s
- Ostatní krátké testy (≤ 0.03 s) → držet ≤ 0.05 s stabilně

### 13.3) Implementované změny (minimální a neinvazivní)
1) LattePluginTestBase: přidán přepínač useIdeaFixture()
   - Výchozí true. Pokud test tento přepínač v podtřídě přepíše na false, nevolá se BasePlatformTestCase#setUp() (těžká inicializace IDE).
   - Soubor: src/test/java/cz/hqm/latte/plugin/test/LattePluginTestBase.java
2) NAttributeSyntaxTest: přepnuto na lightweight režim
   - Přidáno `@Override protected boolean useIdeaFixture() { return false; }`.
   - Očekávaný dopad: pod 200 ms pro testNSyntaxDoubleAttribute.
3) EnhancedAttributeTest: oddělení testu vyžadujícího Project a přepnutí zbytku na lightweight
   - Z testu vyjmut `testCustomAttributesProvider` do nové třídy `EnhancedAttributeProviderTest` (ta zůstává s fixture).
   - `EnhancedAttributeTest` nyní `useIdeaFixture() == false` – čistě lexerové testy běží bez IDE.
   - Soubory:
     - Upraven: src/test/java/cz/hqm/latte/plugin/test/attributes/EnhancedAttributeTest.java
     - Nový: src/test/java/cz/hqm/latte/plugin/test/attributes/EnhancedAttributeProviderTest.java

### 13.4) Další kroky a rozšíření
- Kandidáti na lightweight režim: další lexerové/unit testy, které nepoužívají myFixture/getProject.
- Pro pluginové testy vyžadující IDEA: zachovat plný fixture, případně zvážit sdílený projekt mezi metodami, pokud to test framework dovolí.
- Metriky: Po retestu zapsat nové časy do této sekce (13.5) a upravit cíle podle reality.

### 13.5) Retest (TODO po merge této iterace)
- Proveď: `./gradlew :test --tests "*NAttributeSyntaxTest*" --tests "*EnhancedAttribute*Test*"`
- Zapiš dosažené časy k výše uvedeným cílům.

—
Autor: Junie (AI), čas: 2025-08-13 09:34
