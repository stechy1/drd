package cz.stechy.drd.model.item;

import java.util.Map;

/**
 * Rozhraní pro popis předmětu
 */
public interface IDescriptable {

    /**
     * Vrátí {@link Map}, kde klíč bude název atributu
     * a hodnota bude hodnota atributu předmětu
     *
     * @return {@link Map}
     */
    Map<String, String> getMapDescription();

}
