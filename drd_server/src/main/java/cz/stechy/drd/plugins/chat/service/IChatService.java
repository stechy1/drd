package cz.stechy.drd.plugins.chat.service;

import com.google.inject.ImplementedBy;
import cz.stechy.drd.core.connection.IClient;
import java.util.Optional;

@ImplementedBy(ChatService.class)
public interface IChatService {

    /**
     * Přidá klienta na seznam
     *
     * @param client {@link IClient} Klient, který se právě připojil
     * @param id Id uživatele
     * @param name Jméno uživatele
     */
    void addClient(IClient client, String id, String name);

    /**
     * Odstraní klienta ze seznamu
     *
     * @param id String Id klienta, který se má odebrat
     */
    void removeClient(String id);

    /**
     * Odešle zprávu vybranému příjemci
     *
     * @param destinationClientId Id cílového klienta
     * @param sourceClientId Id klienta, který zprávu poslal
     * @param rawMessage Zpráva, která se má odeslat
     */
    void sendMessage(String destinationClientId, String sourceClientId, byte[] rawMessage);

    /**
     * Najde Id klienta podle instance
     *
     * @param client {@link IClient} Instance hledaného klienta
     * @return Id klienta
     */
    Optional<String> findIdByClient(IClient client);

    /**
     * Informuje uživatele, že druhý kontakt začal/přestal psát
     *
     * @param destinationClientId Id cílového uživatele
     * @param sourceClientId Id zdrojového uživatele
     * @param typing True, pokud zdrojový uživatel začal psát, Falce, pokud přestal psát
     */
    void informClientIsTyping(String destinationClientId, String sourceClientId,
        boolean typing);

}
