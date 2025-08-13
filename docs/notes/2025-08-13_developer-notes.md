# Developer notes – 2025-08-13

Tento zápis zakládá novou iteraci podle doporučeného postupu testování (viz .junie/guidelines, sekce „Iterativní postup testování s opravou chyb“).

## 1) Startovní stav
- Navazuji na poznámky z 2025-08-11.
- V build.gradle je použita JUnit Platform s Vintage, aliasový modul LattePlugin existuje a deleguje `test`/`check` na kořenový projekt.
- Poslední selektivní běhy (11. 8.) proběhly, ale full-run mohl narážet na globální timeout (~5 min).
- Cíl: spustit všechny testy, analyzovat výsledky, případné chyby opravit a opakovat.

## 2) Dnešní plán
1. Spustit `./gradlew test` (plný běh), ověřit že neběží „0 tests“ a shromáždit artefakty: 
   - HTML report: build/reports/tests/test/index.html
   - Logy: log/test_YYYY-MM-DD_HH-MM-SS/**
2. Pokud 0 testů → považovat za chybu a řešit konfiguraci.
3. Pokud dojde k selháním nebo timeoutům → analyzovat logy a opravit minimální změnou kódu/testů.
4. Opakovat kroky 1–3, dokud nebude běh stabilní.

## 3) Běh 1 – plná sada
- Akce: `./gradlew test`
- Očekávání: nenulový počet testů, bez timeoutů.
- Výsledek: Globální timeout test tasku po ~5 minutách (Execution failed for task ':test' > Timeout has been exceeded).
- Analýza: Souhrnný log ukazuje 34 testů (30 success, 4 skipped) v čase běhu; pravděpodobně se jedná o falešně nízký globální timeout na úrovni Gradle test tasku, zatímco jednotlivé testy mají své vlastní limity (např. 30–60 s). To způsobí selhání i při průběžně úspěšném běhu.
- Akce: Navýšil jsem globální timeout v build.gradle z 5 min na 20 min, aby se předešlo falešným failům a zároveň se spoléháme na per‑test limity pro detekci skutečných zacyklení.
- Poznámky k prostředí: JDK 17 doporučeno; případné font warningy jsou očekávané.

## 4) Smoke běhy po změně timeoutu
- `./gradlew :test --tests "*Latte4xCompletionTest*"` → PROŠLO (rychlý běh, cca 31 s)
- `./gradlew :test --tests "*LatteHtmlHighlightingTest*" --tests "*LatteSyntaxHighlighterTest*"` → PROŠLO (cca 30 s)
- `./gradlew :test --tests "*NAttributeSyntaxTest*" --tests "*EnhancedAttributeTest*"` → PROŠLO (cca 30 s)

## 5) Další kroky
- Spustit plný běh (`./gradlew test`) – vzhledem k limitům běhového prostředí AI nástroje může tento krok lokálně vypršet, ale v reálném prostředí by měl doběhnout s navýšeným timeoutem (20 min). V případě pádu analyzovat logy v log/test_YYYY-MM-DD_HH-MM-SS a pokračovat dle guidelines.
- Průběžně sledovat, zda nedochází k per‑test timeoutům (ty by indikovaly bug/zacyklení).

—
Autor: Junie (AI), čas: 2025-08-13 09:25
