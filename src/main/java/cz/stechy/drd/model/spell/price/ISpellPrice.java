package cz.stechy.drd.model.spell.price;

import java.util.HashMap;
import java.util.Map;

/**
 * Rozhraní definující metodu pro výpočet ceny kouzla
 */
public interface ISpellPrice {

    /**
     * Pomocná metoda výhradně pro testovací účely
     * Spočíta cenu kouzla bez žádných vstupních parametrů
     *
     * @return Cena kouzla
     */
    default int calculateMainPrice() {
        return calculateMainPrice(new HashMap<>());
    }

    /**
     * Spočítá cenu kouzla
     *
     * @return Cena kouzla
     * @param parameters Případné parametry pro konkrétní výpočet
     */
    int calculateMainPrice(Map<String, Integer> parameters);

    /**
     * Vypočítá cenu za prodloužení kouzla
     *
     * @return Cenu za prodloužení kouzla
     */
    int calculateExtention();

    /**
     * Převede cenu do uložitelného stavu
     *
     * @return Uložitelný stav ceny
     */
    String pack();
}
