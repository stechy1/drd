package cz.stechy.drd.cmd;

/**
 * Rozhraní poskytující metody pro parametry programu
 */
public interface IParameterProvider {

    String DEFAULT_STRING = "";
    int DEFAULT_INTEGER = -1;

    /**
     * Vrátí textovou hodnotu podle klíče
     *
     * @param key Klíč
     * @return Textová hodnota podle klíče, nebo prázdná hodnota
     */
    default String getString(String key) {
        return getString(key, DEFAULT_STRING);
    }

    /**
     * Vrátí textovou hodnotu podle klíče
     *
     * @param key Klíč
     * @param def Výchozí hodnota, pokud klíč není nalezen
     * @return Textovoá hodnota podle klíče, nebo výchozí hodnota
     */
    String getString(String key, String def);

    /**
     * Vrátí číselnou hodnotu podle klíče
     *
     * @param key Klíč
     * @return Číselnou hodnotu podle klíče, nebo -1
     */
    default int getInteger(String key) {
        return getInteger(key, DEFAULT_INTEGER);
    }

    /**
     * Vrátí číselnou hodnotu podle klíče
     *
     * @param key Klíč
     * @param def Výchozí hodnota, pokud klíč není nalezen
     * @return Číselnou hodnotu podle klíče, nebo výchozí hodnotu
     */
    int getInteger(String key, int def);

}
