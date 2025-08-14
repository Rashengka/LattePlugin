# Developer notes – 2025-08-13

Tento zápis shrnuje dnešní změny v oblasti logování testů a zavádí procesní zásadu pro psaní denních poznámek.

## 1) Per‑metodové zachytávání konzole – implementace a doporučení
- Stav: Aktivováno pro všechny JUnit 4 testy přes `@Rule TestOutputCaptureRule` a záložní `@Before/@After` v `LattePluginTestBase`, takže i při netypickém běhu jsou per‑metodové logy vždy k dispozici.
- Co se ukládá:
  - Kompletní výstup každé testovací metody (stdout i stderr) do souboru:
    - `log/test_YYYY-MM-DD_HH-MM-SS/<Třída>/<metoda>.console.log`
  - Per‑test plugin logy (LatteLogger – debug/validation) zůstávají v:
    - `log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log`
  - Souhrnný log běhu testů (čas, paměť, výsledky):
    - `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
- Proč je to důležité: U pluginových testů IntelliJ se část výstupu (varování, známé font/VFS hlášky apod.) nemusí objevit v HTML reportu. Per‑metodové `.console.log` poskytují kompletní obraz (stdout+stderr s hlavičkou/patičkou a prefixy `[STDOUT]`/`[STDERR]`).
- Postup analýzy (doporučený):
  1) Otevři souhrn: `log/test_<ts>/test_<ts>.log` a HTML report `build/reports/tests/test/index.html`.
  2) Pro konkrétní fail/timeout otevři `log/test_<ts>/<Class>/<method>.console.log` – plná konzole včetně varování a stack‑traces.
  3) Doplň do obrazu i per‑test plugin logy v `log/test_<ts>/<TestName_method>/latte_plugin_<ts>_*.log`.
- Tip: Konzole vždy vypíše „Command output logged to: …“ s absolutní cestou na souhrnný log běhu, což urychlí nalezení aktuální složky běhu testů.

Poznámka: Tato sekce byla omylem zapsána do starších poznámek (2025‑08‑11). Nyní je přesunuta a platí jako dnešní stav.

## 2) Procesní zásada: Denní developer notes
- Po KAŽDÉ úpravě v repozitáři (kód, testy, dokumentace) zapiš stručné poznámky do denního souboru:
  - `docs/notes/YYYY-MM-DD_developer-notes.md`
  - Pokud soubor pro aktuální den neexistuje, vytvoř jej.
  - Nikdy nevkládej nové poznámky do starších datovaných souborů (historii udržuj chronologicky).
  - Obsah minimálně: co se změnilo, proč, jak ověřeno (testy, logy), případné TODO/next steps.
- Smysl: Sdílená stopa změn zrychluje analýzu problémů (spojení kódu, testů a logů v konkrétním dni/iteraci).

## 3) Dnešní změny (shrnutí)
- Přidány/aktivovány komponenty pro logování testů:
  - `TestOutputCaptureRule` – per‑metodové zachytávání konzole do `<Class>/<method>.console.log`.
  - `LattePluginTestBase` – záložní zachytávání přes `@Before/@After` pro robustnost.
  - `TestTimingRule` a `TestMemoryRule` – zapisují do souhrnu `test_<ts>.log` a per‑test debug logů.
- Dokumentace:
  - Aktualizovány README a `docs/logging/README.md` (sekce „Analyzing test failures and timeouts“).
  - ITC (`docs/testing/ITERACNI_TESTOVACI_CYKLUS.md`) doplněn o per‑metodové .console.log a povinné čtení těchto logů při selhání/timeoutu.

## 4) Validace
- Rychlé běhy vybraných testů proběhly bez chyb; ve složce `log/test_<ts>/` se tvoří:
  - souhrnný `test_<ts>.log`,
  - per‑metodové `<Class>/<method>.console.log`,
  - per‑test plugin logy `<TestName_method>/latte_plugin_<ts>_*.log`.
- Další běhy ITC: viz `docs/testing/ITERACNI_TESTOVACI_CYKLUS.md` (kroky 0–8).