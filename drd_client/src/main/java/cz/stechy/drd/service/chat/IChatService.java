package cz.stechy.drd.service.chat;

import cz.stechy.drd.model.chat.ChatContact;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public interface IChatService {

    /**
     * Odešle zprávu
     *
     * @param id ID cílového klienta
     * @param message Obsah zprávy
     */
    void sendMessage(String id, String message);

    /**
     * Odešle zprávu na server, že jsem začal psát
     *
     * @param id ID cílového klienta, se kterým mluvím
     * @param typing True, pokud něco píšu, false, pokud jsem přestal psát
     */
    void notifyTyping(String id, boolean typing);

    /**
     * Vrátí seznam všech připojených klientů
     *
     * @return {@link ObservableList}
     */
    ObservableMap<String, ChatContact> getClients();

    /**
     * Vrátí seznam všech místností vlastněných klientem
     *
     * @return {@link ObservableMap}
     */
    ObservableMap<String, ObservableList<String>> getRooms();

}
