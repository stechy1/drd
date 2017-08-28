package cz.stechy.drd.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída reprezentující DI kontejner se službami
 */
public final class DiContainer {

    // Mapa instancí
    private final Map<Class<?>, Object> instances = new HashMap<>();

    /**
     * Přidá službu do mapy instancí
     *
     * @param klass Třída, která službu reprezentuje
     * @param instance Instance služby
     */
    public void addService(Class klass, Object instance) {
        instances.put(klass, instance);
    }

    public <T> T getInstance(Class klass) {
        System.out.println("Hledám instance pro třídu: " + klass.getName());
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
            System.out.println("Vkládám závislost do fieldu: " + field.getName());
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                field.set(instance, getInstance(field.getType()));
            }
        }

    }
}
