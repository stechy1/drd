package cz.stechy.drd.model;

/**
 * Pomocné značkovací rozhraní pro třídy, které sdílí stejné vlastnosti
 * id, název a obrázek
 */
public interface WithSameProperties extends WithImage {

    String getId();

    String getName();

}
