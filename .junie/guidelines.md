# Project Guidelines for Junie – Operational Rules

Last update: 2025-08-18 11:45

This file defines a few operational rules for working on the LattePlugin repository with the Junie assistant.

## 1) Povinné denní developer notes
- Po KAŽDÉ změně v repozitáři (kód, testy, dokumentace) zapiš stručné poznámky do souboru pro aktuální den:
  - Cesta: `docs/notes/YYYY-MM-DD_developer-notes.md`
  - Pokud soubor pro daný den neexistuje, vytvoř jej.
  - Nikdy nevkládej nové poznámky do minulých (starších) souborů – historie musí být chronologická.
  - Minimální obsah zápisu:
    - Co se změnilo a proč
    - Jak bylo ověřeno (testy, logy, manuální runIde)
    - Odkazy na logy (konkrétní běh – timestamp složka)
    - Případné TODO/next steps
  - Doporučení k formátu času: u KAŽDÉHO zápisu uveď přesný čas zápisu (např. 09:05:00); nepoužívej zástupné štítky typu "(Later)".
  - Při více záznamech o opravách/testech v JEDNOM dni je možné dřívější příspěvky zestručnit, ale ne je úplně smazat.
  - Nesmí se ztratit informace o tom, co bylo úspěšně dokončeno; tyto informace lze zkrátit, pokud následující příspěvek dané téma dále rozvádí a obsahuje podrobnější informace.

## 2) Kde číst logy při analýze testů
Při selháních/timeoutu testů čti vždy tyto zdroje (v tomto pořadí):
1. Souhrn: `log/test_YYYY-MM-DD_HH-MM-SS/test_YYYY-MM-DD_HH-MM-SS.log`
2. Plná konzole metody: `log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<method>.console.log`
3. Per‑test plugin logy: `log/test_YYYY-MM-DD_HH-MM-SS/<TestName_method>/latte_plugin_TIMESTAMP_*.log`

Pozn.: Konzole vždy vypíše `Command output logged to: ...` s absolutní cestou k souhrnnému logu běhu.

## 3) Respekt k ITC (Iterační testovací cyklus)
- Při práci na opravách vždy postupuj podle `docs/testing/ITERACNI_TESTOVACI_CYKLUS.md`.
- "0 tests" je chyba konfigurace/prostředí a musí být řešena.
- Timeouts jsou bug – neignoruj, najdi příčinu a oprav.

## 4) Kompatibilita prostředí
- JDK 17 doporučeno (8–19 podporováno), JDK 20+ není podporováno.
- Gradle 7.6 je povinný (plugin pro IntelliJ není kompatibilní s 8.x).

## 5) Odkazy
- Hlavní README: `README.md`
- Logging: `docs/logging/README.md`
- ITC: `docs/testing/ITERACNI_TESTOVACI_CYKLUS.md`
- Uživatelské testování: `docs/user/TESTING.md`

—
Tento soubor je zdroj pravdy pro operativní pravidla Junie v rámci projektu. V případě změny procesu (např. nové povinnosti při zápisech poznámek) tento dokument aktualizuj spolu s denními developer notes.