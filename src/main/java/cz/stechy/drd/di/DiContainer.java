package cz.stechy.drd.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentující DI kontejner se službami
 */
public final class DiContainer {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(DiContainer.class);

    // endregion

    // region Variables

    // Mapa instancí
    private final Map<Class<?>, Object> instances = new HashMap<>();

    // endregion

    // region Private methods

    /**
     * Pokusí se zkonstruovat instanci z předaného konstruktoru a parametrů
     *
     * @param constructor Konstruktor
     * @param params Parametry konstruktoru
     * @param <T> Datový typ instance
     * @return Novou instanci požadovaného typu, nebo null
     */
    private <T> T getInstance(Constructor<T> constructor, final Object[] params) {
        T instance = null;
        try {
            instance = constructor.newInstance(params);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private <T> void insertDependencies(Class klass, T instance) throws IllegalAccessException {
        final Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                field.set(instance, getInstance(field.getType()));
            }
        }

    }

    // endregion

    // region Public methods

    /**
     * Přidá službu do mapy instancí
     *
     * @param klass Třída, která službu reprezentuje
     * @param instance Instance služby
     */
    public void addService(Class klass, Object instance) {
        instances.put(klass, instance);
    }

    /**
     * Vytvoří a vrátí požadovanou instanci
     *
     * @param klass Třída, která se ma instancovat
     * @param <T> Datový typ, který se vrátí
     * @return Instance třídy
     */
    public <T> T getInstance(Class klass) {
        if (!instances.containsKey(klass)) {
            final Constructor<T>[] constructors = klass.getConstructors();
            for (Constructor<T> constructor : constructors) {
                final Class[] parameterTypes = constructor.getParameterTypes();
                final Object[] instancedParameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    instancedParameters[i] = getInstance(parameterTypes[i]);
                }

                T instance = getInstance(constructor, instancedParameters);
                // Pokud se nepodaří instancovat, zkusím další konstruktor
                if (instance == null) {
                    continue;
                }

                try {
                    insertDependencies(klass, instance);
                } catch (IllegalAccessException e) {
                    continue;
                }

                if (klass.isAnnotationPresent(Singleton.class)) {
                    instances.put(klass, instance);
                } else {
                    return instance;
                }
            }
        }

        return (T) instances.get(klass);
    }

    // endregion
}
