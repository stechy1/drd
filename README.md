#  Správce postav pro dračí doupě
![Úvodní obrazovka](screenshots/main_screen.png)

[![Release Version](https://img.shields.io/github/release/stechy1/drd.svg)](https://github.com/stechy1/drd/releases)

## Instalace
### Prerekvizity
Aplikace vyžaduje Javu verze 8.
Pro sestavení projektu je vyžadován Gradle.

K sestavení projektu je potřeba otevřít příkazovou řádku v adresáři s projektem.
Pomocí příkazu `./gradlew jfxJar` pro Linux, případně `gradlew.bat jfxJar` pro Windows
se vytvoří spustitelný Jar soubor.

## Spuštění aplikace
Aplikaci lze spustit dvojitým poklepáním na Jar soubor. Z příkazové řádky
je lze aplikaci spustit příkazem `java -jar drd.jar`.

S aplikací se dodává složka lib, která obsahuje veškeré knihovny, na kterých je aplikace závislá. 
Změnou obsahu složky může dojít k nestabilitě aplikace či nemožnosti spustit ji.

## Server
Pro hru více hráčů je třeba spustit server. Server využívá Firebase jako perzistentní úložiště.
Tím je zajištěno, že více serverů může sdílet stejné uživatele, předměty, kouzla a nestvůry.
### Parametry pro server
 - clients - maximální počet klientů, kteří můžou v jednu chvíli být připojeni k serveru
 - credentials - cesta k přístupovým údajům pro Firebase
 - max_waiting_queue - maximální počet klientů, kteří můžou čekat ve frontě, než se jim uvolní místo na serveru
 - port - číslo portu, na kterém bude server dostupný

## Ovládání aplikace
Celé ovládání je popsané v sekci [Wiki](https://github.com/stechy1/drd/wiki).

## Připomínky, nápady
Své připomínky a nápady sdílejte prostřednictví issue.

## Autor
- Petr Štechmüller - kompletní práce

## Licence
Tento projekt je pod licencí Apache License - více informací v
license.txt.
