package cz.stechy.drd.old.firebase;

/**
 * Rozhraní definující metody pro posluchače událostí na firebase
 */
@FunctionalInterface
public interface ItemEventListener {

    /**
     * Metoda je zavolána vždy, když nastane nějaká událost ve firebase
     * @param event {@link ItemEvent}
     */
    void onEvent(ItemEvent event);

}
