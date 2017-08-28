package cz.stechy.drd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentuje nastavení aplikace
 */
public final class AppSettings {
    
    // region Constants
    
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSettings.class);
    
    // endregion
    
    // region Variables
    
    private final Properties properties = new Properties();
    private final File propertiesFile;

    // endregion

    // region Constructors
    
    /**
     * Vytvoří novou instanci nastavení aplikace
     * 
     * @param propertiesFile {@link File} Soubor s konfigurací aplikace
     */
    public AppSettings(File propertiesFile) {
        this.propertiesFile = propertiesFile;
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            LOGGER.warn("Nepodařilo se načíst nastavení aplikace. Používám výchozí hodnoty");
        }
    }
    
    // endregion
    
    // region Public methods

    /**
     * Uloží konfiguraci do souboru
     */
    public void save() {
        try {
            properties.store(new FileOutputStream(propertiesFile), "");
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se uložit konfiguraci", e);
        }
    }
    
    // endregion
    
    // region Getters & Setters

    /**
     * Vrátí záznam na základě klíče. Použijte pouze v případě, že jste si jistí, že záznam opravdu
     * existuje
     *
     * @param key Klič záznamu
     * @return Hodnotu záznamu
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Získá záznam z konfiguračního souboru na základě klíče. Pokud záznam neexistuje, vytvoří se
     * nový s výchozí hodnotou.
     *
     * @param key Klíč záznamu
     * @param defaultValue Výchozí hodnota, která se má vrátit, kdy záznam neexistuje
     * @return {@link String}
     */
    public String getProperty(String key, String defaultValue) {
        final String property = properties.getProperty(key, defaultValue);
        properties.setProperty(key, property);
        return property;
    }

    /**
     * Uloží záznam
     *
     * @param key Klíč
     * @param value Hodnota
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    // endregion
}
