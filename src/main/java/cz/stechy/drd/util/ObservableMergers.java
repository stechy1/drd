package cz.stechy.drd.util;

import java.util.Map.Entry;
import java.util.function.Function;
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

    @SafeVarargs
    public static <T> void mergeList(ObservableList<T> into, ObservableList<T>... lists) {
        final ObservableList<T> list = into;
        for (ObservableList<T> l : lists) {
            list.addAll(l);
            l.addListener((ListChangeListener<T>) c -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        list.addAll(c.getAddedSubList());
                    }
                    if (c.wasRemoved()) {
                        list.removeAll(c.getRemoved());
                    }
                }
            });
        }
    }

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
                    if (c.wasAdded()) {
                        for (E item : c.getAddedSubList()) {
                            list.add(mapper.apply(item));
                        }
                    }

                    if (c.wasRemoved()) {
                        for (E item : c.getRemoved()) {
                            list.remove(mapper.apply(item));
                        }
                    }
                }
            });
        }
    }

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
