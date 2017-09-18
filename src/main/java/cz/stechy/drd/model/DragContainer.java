package cz.stechy.drd.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.input.DataFormat;

/**
 * Pomocný kontejner do kterého se ukládají proměnné během drag&drop operace
 */
public class DragContainer implements Serializable {

    // region Constants

    private static final long serialVersionUID = -2032311960583540299L;

    public static final DataFormat PRICE_NODE_ADD = new DataFormat(
        "cz.stechy.drd.controller.spellBook.priceEditor.draggableSpellNode.add");
    public static final DataFormat PRICE_NODE_LINK_ADD = new DataFormat(
        "cz.stechy.drd.controller.spellBook.priceEditor.draggableSpellLink.add");

    // endregion

    // region Variables

    private final Map<String, Object> data = new HashMap<>();

    // endregion

    // region Public methods

    /**
     * Přidá data do kontejneru
     *
     * @param key Klíč, pod kterým se dají data najít
     * @param value Hodnota dat
     */
    public void addData(String key, Object value) {
        System.out.println("Put to: " + key + " value: " + value.toString());
        data.put(key, value);
    }

    /**
     * Vrátí {@link Optional} wrapper. Pokud data obsahují klíč, vrátí hodnotu, jinak {@link Optional#empty()}
     *
     * @param key Klíč, pod kterým by měla být uložena hodnota
     * @param <T> Typ návratového typu hodnoty
     * @return {@link Optional<T>}
     */
    public <T> Optional<T> getValue(String key) {
        if (data.containsKey(key)) {
            return Optional.of((T) data.get(key));
        }

        return Optional.empty();
    }

    // endregion

}
