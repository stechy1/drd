package cz.stechy.drd;

import com.google.inject.Inject;
import cz.stechy.drd.annotations.ConfigFile;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentuje nastavení aplikace
 */
public final class AppSettings implements IAppSettings {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSettings.class);

    // endregion

    // region Variables

    private final Properties properties = new Properties();
    private final File propertiesFile;
    private final Map<String, List<PropertyChangeListener>> propertyListeners = new HashMap<>();

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci nastavení aplikace
     *
     * @param propertiesFile {@link File} Soubor s konfigurací aplikace
     */
    @Inject
    public AppSettings(@ConfigFile File propertiesFile) {
        this.propertiesFile = propertiesFile;
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            LOGGER.warn("Nepodařilo se načíst nastavení aplikace. Používám výchozí hodnoty");
        }
    }

    // endregion

    // region Public methods

    @Override
    public void save() {
        try {
            properties.store(new FileOutputStream(propertiesFile), "");
        } catch (IOException e) {
            LOGGER.error("Nepodařilo se uložit konfiguraci", e);
        }
    }

    @Override
    public void addListener(String propertyName, PropertyChangeListener listener) {
        List<PropertyChangeListener> changeListeners = propertyListeners.computeIfAbsent(propertyName, k -> new ArrayList<>());

        changeListeners.add(listener);
    }

    @Override
    public void removeListener(String propertyName, PropertyChangeListener listener) {
        final List<PropertyChangeListener> changeListeners = propertyListeners.get(propertyName);

        if (changeListeners == null) {
            return;
        }

        changeListeners.remove(listener);
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
    @Override
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
    @Override
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
    @Override
    public void setProperty(String key, String value) {
        final String oldValue = properties.getProperty(key);
        properties.setProperty(key, value);
        final List<PropertyChangeListener> changeListeners = propertyListeners.get(key);
        if (changeListeners == null) {
            return;
        }

        for (PropertyChangeListener listener : changeListeners) {
            listener.propertyChange(new PropertyChangeEvent(this, key, oldValue, value));
        }
    }

    // endregion
}
