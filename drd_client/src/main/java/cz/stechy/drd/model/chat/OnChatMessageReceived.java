package cz.stechy.drd.model.chat;

import java.util.UUID;

/**
 * Rozhraní definující metodu pro příjem zprávy
 */
@FunctionalInterface
public interface OnChatMessageReceived {

    /**
     * Zavolá se vždy, když přijde nová zpráva
     *
     * @param message Obsah zprávy
     * @param source ID klienta, který zprávu odeslal
     */
    void onChatMessageReceived(String message, UUID source);
}
