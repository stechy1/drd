#  Správce postav pro dračí doupě
![Úvodní obrazovka](screenshots/main_screen.png)

[![Release Version](https://img.shields.io/github/release/stechy1/drd.svg)](https://github.com/stechy1/drd/releases)

## Instalace
### Prerekvizity
Aplikace vyžaduje Javu verze 8.
Pro sestavení projektu je vyžadován Gradle.

#### Firebase
K provozování vlastní online databáze je třeba inicializovat Firebase
databázi. Inicializace databáze se provádí v nastavení aplikace.
V nastavení přepnete přepínač pro zpřístupnění online databáze.
Tím se aktivuje tlačítko pro výběr souboru s přístupovými údaji k Firebase.
Po výběru správného souboru se zobrazí notifikace o (ne)úspěšném navázání spojené.
Pokud bylo spojení úspěšné, lze začít využívat veškeré výhody online databáze.

K sestavení projektu je potřeba otevřít příkazovou řádku v adresáři s projektem.
Pomocí příkazu `./gradlew jfxJar` pro Linux, případně `gradlew.bat jfxJar` pro Windows
se vytvoří spustitelný Jar soubor.

## Spuštění aplikace
Aplikaci lze spustit dvojitým poklepáním na Jar soubor. Z příkazové řádky
je lze aplikaci spustit příkazem `java -jar drd.jar`.

## Ovládání aplikace
Celé ovládání je popsané v sekci Wiki.

## Připomínky, nápady
Své připomínky a nápady sdílejte prostřednictví issue.

## Autor
- Petr Štechmüller - kompletní práce

## Licence
Tento projekt je pod licencí Apache License - více informací v
license.txt.
