# Pomocné skripty
Skripty, které slouží pro automatické generování kódu.
Pro použití skriptů je potřeba mít nainstalovaný Python

## Item builder
Skript, pomocí kterého se generují nové třídy předmětů do celé aplikace.
### Použití
Pro vygenerování nové třídy předmětu je potřeba upravit soubor *build_item.py*.
V tomto souboru se nachází předpřipravená kostra pro vygenerování nového předmětu.

Výpis nutných parametrů:
 - `builder.item_name()` - Jako parametr funkce se přijímá název třídy předmětu.
 - `builder.table()` - Jako parametr funkce se přijímá název tabulky v databízi,
  do které se budou ukládat jednotlivé předměty.
 - `builder.set_firebase()` - Funkce očekává boolean parametr. Pokud se nastaví na tru,
 tak se očekává, že předmět bude moct zpřístupnit i do online databáze.
 - `builder.set_firebase_child_path()` - Jako parametr funkce se přijímá
cesta ve firebase databázi, do které se budou ukládat sdílené předměty.
 - `builder.columns()` - Jako parametr funkce prijímá pole mapy s definovanými hodnotami.
  - _name_ - Název vlastnosti (textový řetězec)
  - _typ_ - Datový typ vlastnosti ([int, boolean, String, blob])
  - _specialized_type_ - Pouze, pokud je datový typ typu blob (např.: pro obrázek byte[])

Když jsou všechny parametry vyplněny, zavolá se příkaz: `./build_item.py`,
kterým se vygenerují potřebné třídy přímo do zdrojového kódu.

## Resource generator
Pomocný skript, který automaticky při každé kompilaci vygeneruje třídu __R__,
která obsahuje konstanty s názvy všech FXML souborů a všechny konstanty
pro překlad. Skript je využívaný za pomoci IDE.