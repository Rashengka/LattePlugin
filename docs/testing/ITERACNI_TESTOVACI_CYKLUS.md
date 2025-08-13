# Iterační testovací cyklus (ITC)

Krátký, opakovatelný a přísný postup pro spouštění a opravování testů v projektu Latte Plugin. Tento dokument je závazný pro vývoj i CI a slouží jako centrální odkaz. Pokud v komunikaci zazní „proveď testování dle ITC“, znamená to vykonat přesně tento postup.

Alias pro zkrácenou zmínku: ITC (Iterační testovací cyklus).

## Cíle ITC
- Rychle detekovat chyby a anomálie v testech i kódu pluginu.
- Považovat „0 testů spuštěno“ za chybu konfigurace nebo prostředí.
- Zajistit, že žádný test nevyprší kvůli časovému limitu (timeout = chyba v kódu nebo konfiguraci).
- Iterativně opravovat příčiny a opakovaně spouštět testy, dokud nejsou stabilní a srozumitelné.

## Předpoklady
- JDK 17 (podporováno 8–19), nepoužívat JDK 20+.
- Gradle 7.6 (plugin není kompatibilní s Gradle 8.x).
- Doporučeno: Gradle Wrapper.
- Doporučené VM options pro běh v IDE viz docs/testing/RUNNING_TESTS_IN_INTELLIJ.md.

## ITC – Kroky
0. Clear (spusť úklid před během)
   - Vždy před spuštěním testů vyčisti artefakty předchozího běhu, aby výsledky nebyly zkreslené a per‑test logy se tvořily od nuly.
   - Doporučený příkaz: ./gradlew clearTestArtifacts
   - Co se smaže: log/test_YYYY-MM-DD_HH-MM-SS/ (podsložky per test) a build/reports/tests/test (HTML report), případně zbloudilé test_*.log soubory.
   - Pokud per‑test složky/soubory po běhu testu nevzniknou, NEPOKRAČUJ v dalším běhu – nejprve oprav kód/logging (viz TestPerTestLogWriter, TestTimingRule, TestMemoryRule), teprve poté opakuj test po provedení "Clear".

1. Spusť všechny testy
   - CLI (doporučeno):
     - ./gradlew test
     - Selektivně během iterací: ./gradlew test --tests "*Vzor*"
   - IDE: Gradle Run Configuration s taskem test nebo :test.
   - Poznámka: Alias LattePlugin:test nepoužívejte s --tests (není typu Test).

2. Ověř minimální validitu běhu
   - Pokud se spustilo 0 testů → okamžitě považuj za chybu.
     - Zkontroluj konfiguraci Gradlu a JUnit (Vintage/Platform v IDE, pokud relevantní).
     - Zkontroluj JDK/Gradle verze (viz docs/setup/BUILD_AND_TEST.md).
     - Ověř, že testovací třídy v src/test/java mají správné názvy a anotace.

3. Analyzuj výsledky a logy
   - HTML report: build/reports/tests/test/index.html.
   - Souhrnný log běhu: log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log.
   - Per‑test logy: log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log.
   - Per‑metoda (kompletní konzolový výstup): log/test_YYYY-MM-DD_HH-MM-SS/<Třída>/<metoda>.console.log.
   - Konzole vždy vypíše „Command output logged to: …“ s cestou k souhrnnému logu.

4. Zhodnoť smysluplnost testů
   - Ověř, že testy testují reálné chování pluginu (lexer, parser, completion, highlighting, integrace s HTML/Nette) a nejsou závislé na nahodilém stavu.
   - Zvaž, zda selhání odpovídá očekávané validaci (např. chybná makra, n:atributy, verze Latte).
   - U testů s přílišnou fluktuací nebo implicitními závislostmi přidej stabilizační kroky (fix data, deterministic setup, jasné pre‑conditions).

5. Identifikuj příčinu selhání nebo timeoutu
   - Timeout testu nebo celé sady je chyba (obvykle zacyklení, nekonečná rekurze, blokující I/O, nevhodná synchronizace, chybějící optimalizace).
   - Pro pomalé části zvaž profilaci anebo dodatečné logování (viz docs/logging/README.md) pro odhalení úzkého hrdla.

6. Proveď minimální opravy
   - Implementuj nejmenší nutnou změnu v produkčním kódu nebo testech k odstranění příčiny.
   - Dávej pozor na globální stav v IntelliJ testech (případně spouštěj sekvenčně: -PdisableTestParallel, nebo forkuj per test: -PforkEveryOne).
   - Aktualizuj testy, pokud jsou špatně navržené nebo nesouladí s cílovým chováním (ale dbej, aby to nebyl „golden master“ na chybné implementaci).

7. Opakovaně spusť testy
   - Během ladění spouštěj menší výřezy: ./gradlew test --tests "*NAttributeSyntaxTest*".
   - Pro full‑run pravidelně ověř, že se nespouští „0 testů“ a nic nevyprší.

8. Kritéria úspěchu
   - Nespustí se „0 testů“ – vždy nenulový počet.
   - Žádný test nevyprší (timeouts jsou považované za chybu, dokud se neodstraní příčina).
   - HTML report a logy jsou bez neočekávaných výjimek a výsledky dávají smysl vůči očekáváním.

## Poznámky a tipy
- V IDE používej JUnit konfiguraci s doporučenými VM options: viz docs/testing/RUNNING_TESTS_IN_INTELLIJ.md.
- Známé font/VFS warningy lze potlačit; nejsou důvodem k obcházení ITC.
- Pro rychlejší cyklus pracuj v malých iteracích a nezapomeň na logy.

## Rychlá reference (cheatsheet)
- Full run: ./gradlew test
- Selektivní běh: ./gradlew test --tests "*LatteHtmlHighlightingTest*"
- Report: build/reports/tests/test/index.html
- Logy: log/test_YYYY-MM-DD_HH-MM-SS/

