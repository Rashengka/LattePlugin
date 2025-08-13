# Projektové guidelines – Latte Plugin pro JetBrains IDEs

Poslední aktualizace: 2025-08-11 09:02

Tento dokument shrnuje vše podstatné o projektu, o Latte a Nette a především jak tento projekt vyvíjet a testovat. Cílem je mít vše důležité na jednom místě pro rychlý start i každodenní práci.

- Repo: LattePlugin (JetBrains/IntelliJ plugin pro Latte šablony)
- Podporované IDE: IntelliJ IDEA, PhpStorm, WebStorm aj.
- Podporované Latte verze: 2.x, 3.0+ a 4.0+ (automatická detekce i ruční přepínání)

Odkazy na detailní dokumentaci v repozitáři:
- Hlavní README: README.md
- Build & Test: docs/setup/BUILD_AND_TEST.md
- Testy v IntelliJ: docs/testing/RUNNING_TESTS_IN_INTELLIJ.md
- Test konfigurace a potlačení font chyb: docs/testing/TEST_CONFIGURATION.md
- Logování (kam se co zapisuje): docs/logging/README.md
- Uživatelské testování pluginu po instalaci: docs/user/TESTING.md


## 1) Co je Latte a Nette

- Latte je bezpečný a výkonný šablonovací jazyk (původně z Nette), používaný v PHP projektech. Nabízí:
  - Makra: {if}, {foreach}, {block}, {include}, {var}, {capture}, atd.
  - n:atributy pro HTML tagy: n:if, n:foreach, n:class, n:attr, n:syntax, …
  - Filtry: |escape, |date, |number, |capitalize atd.
  - Verze 3+ a 4+ přináší typové anotace ({varType}, {templateType}), vylepšení maker a atributů.
- Nette je PHP framework, se kterým Latte úzce souvisí. V kontextu šablon:
  - Presenter/Action/Signal konvence (např. odkazy ve tvaru Product:detail, signály delete!)
  - Komponenty (createComponentXyz), které lze používat v šablonách přes {control xyz}
  - n:href a další integrace pro generování odkazů a práci s UI komponentami


## 2) Co dělá tento plugin

Plugin přidává do JetBrains IDEs plnohodnotnou podporu pro Latte šablony:
- Syntax highlighting pro Latte makra, n:atributy a filtry
- Code completion pro makra, n:atributy a filtry (včetně verzí Latte 2/3/4)
- Quick documentation (Ctrl+Q / F1 na Macu) pro makra/atributy/filtry
- HTML integrace (Latte rozšíření pro HTML editor)
- Validace chyb (neplatná makra, nevhodná syntaxe, neuzavřené značky)
- Podpora Nette konvencí: doplňování a navigace na Presentery/akce/signály/komponenty, n:href apod.
- Výkonové optimalizace (caching šablon, inkrementální parsování, memory optimalizace)
- Detekce verze Latte: composer.json, komentáře v souboru, rozpoznání verzi-specifické syntaxe; možnost ručního přepnutí v Tools menu


## 3) Požadavky na prostředí

- Java: JDK 17 (doporučeno). Kompatibilní JDK 8–19. JDK 20+ není podporováno.
- Gradle: 7.6 (důležité – JetBrains IntelliJ Gradle plugin není kompatibilní s Gradle 8.x)
- IDE pro vývoj pluginu: IntelliJ IDEA (2023.1.5 nebo kompatibilní)

Tipy:
- Kontrola verzí: ./check_versions.sh (zajistí, že JDK/Gradle odpovídá požadavkům)
- Doporučeno mít Gradle wrapper; pokud chybí, viz docs/setup/BUILD_AND_TEST.md (sekce „Adding Gradle Wrapper“)


## 4) Struktura repozitáře (zkráceně)

- src/main/java/cz/hqm/latte/plugin/… – hlavní kód (lexer, parser, completion, highlighting, …)
- src/main/resources/META-INF/plugin.xml – registrace pluginu
- src/test/java – testy (JUnit 4)
- docs/ – dokumentace (setup, testing, logging, user)
- samples/ – ukázky Latte souborů
- log/ – výstupy logování (běžný režim i testovací režim s podsložkami)

Detailnější rozpis viz README.md (sekce Project Structure).


## 5) Build, spuštění a ladění pluginu

Rychlý start (viz i README.md):
1) buildPlugin
   - gradle buildPlugin
   - artefakt: build/distributions/LattePlugin-1.0-SNAPSHOT.zip
2) spuštění v IDE
   - Otevřít projekt v IntelliJ IDEA
   - Gradle tool window → Tasks > intellij > runIde
   - Spustit runIde (spustí sandbox IDE s nainstalovaným pluginem)
3) ladění
   - V IntelliJ IDEA: pravé tlačítko na runIde → Debug
   - Nastavit breakpoints a ladit běžící sandbox

Poznámka: detailní návod a troubleshooting (včetně instalace JDK/Gradle) viz docs/setup/BUILD_AND_TEST.md.


## 6) Testování tohoto projektu (vývojářské)

Základní spuštění testů (JUnit 4):
- gradle test
- nebo (pokud používáte wrapper) ./gradlew test
- případně ./gradlew check (doporučeno pro CI; obsahuje extra konfiguraci potlačující fontové chyby)

Jak správně filtrovat testy přes Gradle (důležité):
- Používejte pouze kořenový test task: ./gradlew test --tests "*Pattern*"
  - Příklady:
    - ./gradlew test --tests "*NAttributeSyntaxTest*" ✓
    - ./gradlew test --tests "*LatteHtmlHighlightingTest*" --tests "*Latte4xCompletionTest*" ✓
- Nevolejte aliasový task LattePlugin:test s parametrem --tests:
  - gradle LattePlugin:test --tests "..." → chyba „Unknown command-line option '--tests'“ (aliasový task není typu Test)
  - Pokud IDE má uloženou konfiguraci s LattePlugin:test, ponechte ji; alias existuje kvůli kompatibilitě, ale filtrace se musí zadat na kořenový :test.
- Alternativy v IDE:
  - V Gradle Run Configuration nastavte Task na test nebo :test.
  - Filtrování testů nastavte v rovině Gradle parametrů (Arguments): --tests "Vzor".

Kam se zapisují logy z testů a co se vypisuje do terminálu:
- Logy (souhrn běhu): log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log (soubor je nyní uvnitř stejnojmenné timestamp složky)
- Detailní logy testů: log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log (a případné další pomocné logy)
- HTML report: build/reports/tests/test/index.html
- I při vypnutých výstupech do terminálu se vždy vypíše cesta k vytvořenému log souboru (Command output logged to: ...).

Jak hledat a analyzovat logy testů:
- Najděte poslední timestamp složku v log/: např. log/test_2025-08-11_11-52-54
- Otevřete souhrnný log v této složce: test_2025-08-11_11-52-54.log (obsahuje přehled testů a summary)
- Pro detailní analýzu projděte REKURZIVNĚ celou složku a její podsložky (např. CodeInsightTestFixtureImpl_.../latte_plugin_...log)
  - Teprve kombinace souhrnu + per-test logů dává kompletní obraz průběhu
- Pokud je výstup do terminálu vypnutý, konzole vždy vypíše alespoň: Command output logged to: <absolutní_cesta_k_souhrnnému_logu>

Spuštění testů z IntelliJ IDEA:
- Použijte JUnit konfiguraci s doporučenými VM options (viz docs/testing/RUNNING_TESTS_IN_INTELLIJ.md):
  --add-exports=java.desktop/sun.font=ALL-UNNAMED
  --add-opens=java.desktop/sun.font=ALL-UNNAMED
  --add-opens=java.base/java.lang=ALL-UNNAMED
  -Djava.awt.headless=true
  -Didea.use.headless.ui=true
  -Didea.force.use.core.fonts=true
  -Didea.font.system.disable=true
  -Didea.use.mock.ui=true
  -Didea.use.minimal.fonts=true
  (a další volby viz dokument) – cílem je potlačit známé „font“ chyby

Známé „font“ a VFS warningy při testech:
- Jsou očekávané a testy mohou i tak proběhnout úspěšně.
- Více v docs/testing/TEST_CONFIGURATION.md (vysvětlení vlastností a proč jsou bezpečné ignorovat).

Výstupy testů a logy:
- Test report: build/reports/tests/test/index.html
- Test logy: log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log
- Běžné logy: log/latte_plugin_TIMESTAMP_*.log
- Detail v docs/logging/README.md

Skript v kořeni repozitáře:
- run_tests.sh – může sloužit jako zkratka pro běh testů (pokud je přizpůsoben vašemu prostředí)


## 7) Testování pluginu po instalaci (uživatelské)

- Návod a checklist je v docs/user/TESTING.md (vytvoření .latte souboru, zvýraznění, completion, dokumentace, navigace na Presentery/komponenty/signály, dědičnost šablon, typová makra, verze Latte 2/3/4 atd.).

Rychlá ukázka (vložit do .latte a pozorovat zvýraznění/kompletaci):
```
{* This is a Latte comment *}
<!DOCTYPE html>
<html>
<head>
    <title>{$title}</title>
</head>
<body>
    {if $user->isLoggedIn()}
        <h1>Welcome, {$user->name|capitalize}</h1>
        <p n:if="$user->isAdmin">You are an administrator.</p>
    {else}
        <h1>Please log in</h1>
    {/if}

    <ul n:if="$items">
        {foreach $items as $item}
            <li>{$item}</li>
        {/foreach}
    </ul>

    {include 'footer.latte'}
</body>
</html>
```


## 8) Integrace s Nette – praktické poznámky

- Navigace a completion pro Presentery/akce/signály:
  - "Product:detail" → navigace na ProductPresenter::actionDetail()
  - "{link delete!}" → navigace na handleDelete()
- Komponenty:
  - createComponentProductList() → {control productList}
  - completion názvů komponent, navigace na tovární metody
- n:href, n:if, n:class apod. – zvýraznění a kontrola syntaxe v HTML


## 9) Tipy pro vývoj

- Pracujte v menších iteracích, často spouštějte runIde a manuálně si vyzkoušejte nové chování.
- Pro diagnostiku používejte logy (viz docs/logging/README.md) – snadno dohledáte, co se děje při parsování/validaci.
- Při větších změnách v lexeru/parseru pište doprovodné testy v src/test/java a sledujte reporty v build/reports/tests.
- Přidávejte odkazy do dokumentace přímo v kódu (javadoc/KDoc), ať je Quick Documentation informativní.


## 10) Známá omezení a kompatibilita

- Gradle 8.x není kompatibilní s JetBrains Gradle pluginem používaným v tomto projektu – používejte 7.6.
- JDK 20+ není podporováno (doporučeno JDK 17, funguje 8–19).
- Při běhu testů v IntelliJ bez doporučených VM options se mohou objevovat chyby ohledně fontů – viz výše.


## 11) Troubleshooting (časté problémy)

- buildPlugin selže s chybou kolem IntelliJ pluginu / Gradlu:
  - Ověřte, že používáte Gradle 7.6 (nikoliv 8.x). Viz docs/setup/BUILD_AND_TEST.md.
- „class org.jetbrains.intellij.MemoizedProvider overrides final method …“:
  - Důsledek Gradle 8.x – vraťte se na 7.6.
- Testy padají kvůli NoSuchMethodError v sun.font.*:
  - Spouštějte ./gradlew check nebo přidejte VM options dle docs/testing/RUNNING_TESTS_IN_INTELLIJ.md.
- Plugin se v sandboxu nenačte:
  - Zkontrolujte verzi IntelliJ v build.gradle (blok intellij) a závislosti na HTML pluginu.


## 12) Užitečné zdroje

- README.md (rychlý přehled, build a běh)
- docs/setup/BUILD_AND_TEST.md (komplexní průvodce)
- docs/testing/RUNNING_TESTS_IN_INTELLIJ.md (Run Config šablona s VM options)
- docs/testing/TEST_CONFIGURATION.md (popis systémových vlastností pro testy)
- docs/logging/README.md (kde jsou logy a jak se jmenují)
- docs/user/TESTING.md (kontrolní seznam po instalaci pluginu)

## 13) Iterativní postup testování s opravou chyb (doporučený)

Tento postup je závazný pro vývoj i CI. Cílem je rychle detekovat chyby, průběžně je opravovat a zajistit stabilní běh testů.

1. Spusťte všechny testy
   - CLI: `./gradlew test` (nebo `:test`)
   - IDE: Gradle Run Configuration s taskem `test`/`:test` (případně alias `LattePlugin:test`)
   - Pokud běžíte v hlavě IDE jako JUnit konfiguraci, ujistěte se, že je povolen JUnit Platform s Vintage (viz docs/testing/RUNNING_TESTS_IN_INTELLIJ.md).
2. Pokud se spustilo 0 testů → považujte to za chybu
   - Zkontrolujte konfiguraci: `test { useJUnitPlatform() }`, přítomnost Vintage engine, správný task (`:test` vs. neexistující `LattePlugin:test`), JDK/Gradle verze.
   - Ověřte, že třídy v `src/test/java` mají správné názvosloví a anotace JUnit 4/5.
3. Pokud některý test selže nebo časově vyprší
   - Časový limit testu nebo celé sady znamená problém v kódu (bug, zacyklení nebo chybějící optimalizace). Nespokojte se s „jen to občas doběhne“. Opravte příčinu.
   - Proveďte analýzu logů:
     - HTML report: `build/reports/tests/test/index.html`
     - Souhrnný log: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
     - Per-test logy: `log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log`
4. Opravte chyby a optimalizujte
   - Implementujte minimální změny v produkčním kódu nebo testech, které řeší příčinu (nekonečné smyčky, zbytečná složitost, chybné regexy, špatná konfigurace lexeru/parseru, nevhodná závislost na globálním stavu apod.).
   - Při podezření na sdílený stav v IntelliJ testech zvažte sekvenční běh (`-PdisableTestParallel`) nebo per‑test fork (`-PforkEveryOne`).
5. Spusťte znovu všechny testy a opakujte
   - Opakujte kroky 1–4, dokud testy stabilně neprocházejí.
   - Pro urychlení používejte selektivní běhy: `./gradlew :test --tests "*Pattern*"`.
6. Kritéria úspěchu
   - Neproběhne „0 tests“ – vždy se musí spustit nenulový počet testů.
   - Žádný test nevyprší na čas – časové limity jsou považovány za chybu v kódu nebo konfiguraci.
   - HTML report i logy jsou čisté od neočekávaných výjimek.

Poznámky:
- Pokud full-run naráží na globální timeout celé sady, rozdělte běh po skupinách (attributes/highlighting/completion/caching) a zároveň hledejte a odstraňujte příčinu (optimalizace/paralelizace). V CI je možné dočasně navýšit timeout, ale finálním cílem je běh v rozumném čase i lokálně.
- Další kontext a nedávné závěry jsou v `docs/notes/` (developer notes), zejména poslední záznam.

---
Pokud vám v dokumentu něco chybí, doplňte prosím příslušnou sekci nebo přidejte odkaz do výše uvedených souborů. Cílem je udržovat tyto guidelines jako centrální a stručný rozcestník pro celý projekt.