package cz.stechy.drd.plugins.firebase;

/**
 * Rozhraní definující metody pro posluchače událostí na firebase
 */
@FunctionalInterface
public interface FirebaseEntryEventListener {

    /**
     * Metoda je zavolána vždy, když nastane nějaká událost ve firebase
     * @param event {@link FirebaseEntryEvent}
     */
    void onEvent(FirebaseEntryEvent event);

}
