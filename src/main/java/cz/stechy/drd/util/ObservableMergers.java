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
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param lists Pole zdrojových kolekcí
     * @param <T> Typ, který používají jak zdrojové tak cílové kolekce
     */
    @SafeVarargs
    public static <T> void mergeList(ObservableList<T> into, ObservableList<T>... lists) {
        final ObservableList<T> list = into;
        for (ObservableList<T> l : lists) {
            list.addAll(l);
            l.addListener((ListChangeListener<T>) c -> {
                while (c.next()) {
                    list.addAll(c.getAddedSubList());
                    list.removeAll(c.getRemoved());
                }
            });
        }
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param lists Pole zdrojových kolekcí
     * @param <T> Typ, který používá výsledná kolekce
     * @param <E> Typ, který používají zdrojové kolekce
     */
    @SafeVarargs
    public static <T, E> void mergeList(Function<? super E, ? extends T> mapper,
        ObservableList<T> into, ObservableList<E>... lists) {
        final ObservableList<T> list = into;
        for (ObservableList<E> l : lists) {
            for (E item : l) {
                list.add(mapper.apply(item));
            }
            l.addListener((ListChangeListener<E>) c -> {
                while (c.next()) {
                    list.addAll(c.getAddedSubList().stream().map(mapper).collect(Collectors.toList()));
                    list.removeAll(c.getRemoved().stream().map(mapper).collect(Collectors.toList()));
                }
            });
        }
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param sets Pole zdrojových kolekcí
     * @param <T> Typ, který používají jak zdrojové tak cílové kolekce
     */
    @SafeVarargs
    public static <T> void mergeSet(ObservableSet<T> into, ObservableSet<T>... sets) {
        final ObservableSet<T> set = into;
        for (ObservableSet<T> s : sets) {
            set.addAll(s);
            s.addListener((SetChangeListener<T>) c -> {
                if (c.wasAdded()) {
                    set.add(c.getElementAdded());
                }
                if (c.wasRemoved()) {
                    set.remove(c.getElementRemoved());
                }
            });
        }
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné kolekce, která pozoruje více
     * zdrojových kolekcí.
     *
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná kolekce, která obsahuje prvky ze zdrojových kolekcí
     * @param sets Pole zdrojových kolekcí
     * @param <T> Typ, který používá výsledná kolekce
     * @param <E> Typ, který používají zdrojové kolekce
     */
    @SafeVarargs
    public static <T, E> void mergeSet(Function<? super E, ? extends T> mapper,
        ObservableSet<T> into, ObservableSet<E>... sets) {
        final ObservableSet<T> set = into;
        for (ObservableSet<E> s : sets) {
            for (E item : s) {
                set.add(mapper.apply(item));
            }
            s.addListener((SetChangeListener<E>) c -> {
                if (c.wasAdded()) {
                    set.add(mapper.apply(c.getElementAdded()));
                }
                if (c.wasRemoved()) {
                    set.remove(mapper.apply(c.getElementRemoved()));
                }
            });
        }
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné mapy, která pozoruje více
     * zdrojových map.
     *
     * @param into Výsledná mapa, která obsahuje prvky ze zdrojových map
     * @param maps Pole zdrojových map
     * @param <K> Typ klíče, který používají zdrojové i cílové mapy
     * @param <V> Typ hodnoty, který používají jak zdrojové tak cílové mapy
     */
    @SafeVarargs
    public static <K, V> void mergeMap(ObservableMap<K, V> into,
        ObservableMap<K, V>... maps) {
        final ObservableMap<K, V> map = into;
        for (ObservableMap<K, V> m : maps) {
            map.putAll(m);
            m.addListener((MapChangeListener<K, V>) c -> {
                if (c.wasAdded()) {
                    map.put(c.getKey(), c.getValueAdded());
                }
                if (c.wasRemoved()) {
                    map.remove(c.getKey());
                }
            });
        }
    }

    /**
     * Pomocná knihovní funkce pro automatickou úpravu výsledné mapy, která pozoruje více
     * zdrojových map.
     *
     * @param mapper Funkce, která dokáže přemapovat jeden datový typ na druhý
     * @param into Výsledná mapa, která obsahuje prvky ze zdrojových map
     * @param maps Pole zdrojových map
     * @param <K> Typ klíče, který používají zdrojové i cílové mapy
     * @param <V> Typ hodnoty, který používá cílová mapa
     * @param <W> Typ hodnoty, který používají zdrojové mapy
     */
    @SafeVarargs
    public static <K, V, W> void mergeMap(Function<? super W, ? extends V> mapper,
        ObservableMap<K, V> into, ObservableMap<K, W>... maps) {
        final ObservableMap<K, V> map = into;
        for (ObservableMap<K, W> m : maps) {
            for (Entry<K, W> entry : m.entrySet()) {
                map.put(entry.getKey(), mapper.apply(entry.getValue()));
            }
            m.addListener((MapChangeListener<K, W>) c -> {
                if (c.wasAdded()) {
                    map.put(c.getKey(), mapper.apply(c.getValueAdded()));
                }
                if (c.wasRemoved()) {
                    map.remove(c.getKey());
                }
            });
        }
    }
}
