package cz.stechy.drd.net;

import cz.stechy.drd.net.message.IMessage;

@FunctionalInterface
public interface OnDataReceivedListener {

    /**
     * Metoda je zavolána vždy, když dorazí nějaká data
     *
     * @param message {@link IMessage} Přijatá zpráva
     */
    void onDataReceived(IMessage message);
}
