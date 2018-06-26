package cz.stechy.drd.util;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Pomocná knihovní třida sloužící k bindování růžných pozorovatelných kolekcí
 */
@SuppressWarnings("unused")
public final class ObservableMergers {

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *
     * @param <T> Typ, který používají jak zdrojové tak cílové kolekce
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param lists Pole zdrojových kolekcí
     */
    @SafeVarargs
    public static <T> ListChangeListener<T> mergeList(ObservableList<T> into, ObservableList<T>... lists) {
        final ObservableList<T> list = into;
        final ListChangeListener<T> listener = c -> {
            while (c.next()) {
                list.addAll(c.getAddedSubList());
                list.removeAll(c.getRemoved());
            }
        };

        for (ObservableList<T> l : lists) {
            list.addAll(l);
            l.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *  @param <T> Typ, který používá výsledná kolekce
     * @param <E> Typ, který používají zdrojové kolekce
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param lists Pole zdrojových kolekcí
     */
    @SafeVarargs
    public static <T, E> ListChangeListener<E> mergeList(Function<? super E, ? extends T> mapper,
        ObservableList<T> into, ObservableList<E>... lists) {
        final ObservableList<T> list = into;
        final ListChangeListener<E> listener = c -> {
            while (c.next()) {
                list.addAll(
                    c.getAddedSubList().stream().map(mapper).collect(Collectors.toList()));
                list.removeAll(
                    c.getRemoved().stream().map(mapper).collect(Collectors.toList()));
            }
        };

        for (ObservableList<E> l : lists) {
            for (E item : l) {
                list.add(mapper.apply(item));
            }
            l.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *
     * @param <T> Typ, který používají jak zdrojové tak cílové kolekce
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param sets Pole zdrojových kolekcí
     */
    @SafeVarargs
    public static <T> SetChangeListener<T> mergeSet(ObservableSet<T> into, ObservableSet<T>... sets) {
        final ObservableSet<T> set = into;
        final SetChangeListener<T> listener = c -> {
            if (c.wasAdded()) {
                set.add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                set.remove(c.getElementRemoved());
            }
        };

        for (ObservableSet<T> s : sets) {
            set.addAll(s);
            s.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *  @param <T> Typ, který používá výsledná kolekce
     * @param <E> Typ, který používají zdrojové kolekce
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param sets Pole zdrojových kolekcí
     */
    @SafeVarargs
    public static <T, E> SetChangeListener<E> mergeSet(Function<? super E, ? extends T> mapper,
        ObservableSet<T> into, ObservableSet<E>... sets) {
        final ObservableSet<T> set = into;
        final SetChangeListener<E> listener = c -> {
            if (c.wasAdded()) {
                set.add(mapper.apply(c.getElementAdded()));
            }
            if (c.wasRemoved()) {
                set.remove(mapper.apply(c.getElementRemoved()));
            }
        };

        for (ObservableSet<E> s : sets) {
            for (E item : s) {
                set.add(mapper.apply(item));
            }
            s.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné mapy, která pozoruje více
     * zdrojových map.
     *  @param <K> Typ klíče, který používají zdrojové i cílové mapy
     * @param <V> Typ hodnoty, který používají jak zdrojové tak cílové mapy
     * @param into Výsledná mapa, která obsahuje prvky ze zdrojových map
     * @param maps Pole zdrojových map
     */
    @SafeVarargs
    public static <K, V> MapChangeListener<K, V> mergeMap(ObservableMap<K, V> into,
        ObservableMap<K, V>... maps) {
        final ObservableMap<K, V> map = into;
        final MapChangeListener<K, V> listener = c -> {
            if (c.wasAdded()) {
                map.put(c.getKey(), c.getValueAdded());
            }
            if (c.wasRemoved()) {
                map.remove(c.getKey());
            }
        };

        for (ObservableMap<K, V> m : maps) {
            map.putAll(m);
            m.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné mapy, která pozoruje více
     * zdrojových map.
     *  @param <K> Typ klíče, který používají zdrojové i cílové mapy
     * @param <V> Typ hodnoty, který používá cílová mapa
     * @param <W> Typ hodnoty, který používají zdrojové mapy
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná mapa, která obsahuje prvky ze zdrojových map
     * @param maps Pole zdrojových map
     */
    @SafeVarargs
    public static <K, V, W> MapChangeListener<K, W> mergeMap(Function<? super W, ? extends V> mapper,
        ObservableMap<K, V> into, ObservableMap<K, W>... maps) {
        final ObservableMap<K, V> map = into;
        final MapChangeListener<K, W> listener = c -> {
            if (c.wasAdded()) {
                map.put(c.getKey(), mapper.apply(c.getValueAdded()));
            }
            if (c.wasRemoved()) {
                map.remove(c.getKey());
            }
        };

        for (ObservableMap<K, W> m : maps) {
            for (Entry<K, W> entry : m.entrySet()) {
                map.put(entry.getKey(), mapper.apply(entry.getValue()));
            }
            m.addListener(listener);
        }

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro mergování pozorovatelného setu do pozorovatelného listu
     *
     * @param <T> Typ, který používají jak zdrojový set, tak cílový list
     * @param set Zdrojový set, který se pozoruje
     * @param list Výsledný list, který pozoruje set
     */
    public static <T> SetChangeListener<T> listObserveSet(ObservableSet<T> set, ObservableList<T> list) {
        list.setAll(set);
        final SetChangeListener<T> listener = c -> {
            if (c.wasAdded()) {
                list.add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                list.remove(c.getElementRemoved());
            }
        };

        set.addListener(listener);

        return listener;
    }

    /**
     * Pomocná knihovní funkce pro mergování pozorovatelné mapy do pozorovatelného listu
     *
     * @param <K> Typ klíče v mapě
     * @param <V> Typ, který pouívají jak zdrojová mapa, tak cílový list
     * @param map Zdrojová mapa
     * @param list Cílový list
     */
    public static <K, V> MapChangeListener<K, V> listObserveMap(ObservableMap<K, V> map, ObservableList<V> list) {
        list.setAll(map.values());
        final MapChangeListener<K, V> listener = c -> {
            if (c.wasAdded()) {
                list.add(c.getValueAdded());
            }
            if (c.wasRemoved()) {
                list.remove(c.getValueRemoved());
            }
        };

        map.addListener(listener);

        return listener;
    }
}
