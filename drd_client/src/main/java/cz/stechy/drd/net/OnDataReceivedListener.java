package cz.stechy.drd.net;

@FunctionalInterface
public interface OnDataReceivedListener {

    /**
     * Metoda je zavolána vždy, když dorazí nějaká data
     *
     * @param data
     */
    void onDataReceived(Object data);
}
