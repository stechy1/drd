package cz.stechy.drd.di;

/**
 * Třída obsahující metody pro správu instancí pomocí DI
 */
public interface IDependencyManager {

    /**
     * Přidá službu do seznamu instancí
     *
     * @param klass Třída, která službu reprezentuje
     * @param instance Instance služby
     */
    void addService(Class klass, Object instance);

    /**
     * Vytvoří a vrátí požadovanou instanci
     *
     * @param klass Třída, která se ma instancovat
     * @param <T> Datový typ, který se vrátí
     * @return Instance třídy
     */
    <T> T getInstance(Class klass);

}
