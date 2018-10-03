package cz.stechy.drd;

import java.beans.PropertyChangeListener;

public interface IAppSettings {

    /**
     * Uloží konfiguraci do souboru
     */
    void save();

    void addListener(String propertyName, PropertyChangeListener listener);

    void removeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Vrátí záznam na základě klíče. Použijte pouze v případě, že jste si jistí, že záznam opravdu
     * existuje
     *
     * @param key Klič záznamu
     * @return Hodnotu záznamu
     */
    String getProperty(String key);

    /**
     * Získá záznam z konfiguračního souboru na základě klíče. Pokud záznam neexistuje, vytvoří se
     * nový s výchozí hodnotou.
     *
     * @param key Klíč záznamu
     * @param defaultValue Výchozí hodnota, která se má vrátit, kdy záznam neexistuje
     * @return {@link String}
     */
    String getProperty(String key, String defaultValue);

    /**
     * Uloží záznam
     *
     * @param key Klíč
     * @param value Hodnota
     */
    void setProperty(String key, String value);
}
