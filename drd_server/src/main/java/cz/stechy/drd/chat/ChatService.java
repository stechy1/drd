package cz.stechy.drd.chat;

import cz.stechy.drd.Client;
import cz.stechy.drd.crypto.CryptoService;
import cz.stechy.drd.net.message.ChatMessage;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatAction;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClient;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChatService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    // endregion

    // region Variables

    private final Object lock = new Object();
    private final Map<UUID, ChatClient> clients = new HashMap<>();

    private final CryptoService cryptoService;

    // endregion

    // region Constructors

    public ChatService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // endregion

    // region Private methods

    /**
     * Odešle zprávu všem klientům, kteří jsou přihlášení k chatu
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat všem
     */
    private void broadcastMessage(IMessage message) {
        clients.values().forEach(chatClient -> chatClient.getClient().sendMessage(message));
    }

    // endregion

    // region Public methods

    /**
     * Přidá klienta na seznam
     *
     * @param client {@link Client} Klient, který se právě připojil
     * @param name Jméno uživatele
     */
    public void addClient(Client client, String name) {
        LOGGER.info("Přidávám nového klienta {} na seznam v chatu.", name);
        final ChatClient chatClient = new ChatClient(client, name);
        synchronized (lock) {
            // Přidám záznam o klientovi na seznam
            clients.put(client.getId(), chatClient);
            // Rozešlu broadcast všem připojeným klientům
            // Ano, odešlu ho i klientovi, který se právě připojil
            // Tím získám jistou výhodu a můžu snadno přidat sám sebe na seznam kontaktů
            broadcastMessage(new ChatMessage(MessageSource.SERVER,
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClient(
                        ChatAction.CLIENT_CONNECTED, client.getId(), name,
                        cryptoService.getClientPublicKey(client)))));
        }
    }

    /**
     * Odstraní klienta ze seznamu
     *
     * @param client {@link Client} Klient, který se právě odpjil
     */
    public void removeClient(Client client) {
        LOGGER.info("Odebírám klienta ze seznamu chatu.");
        synchronized (lock) {
            // Rozešlu broadcast všem připojeným klientům, že se klient odpojuje
            broadcastMessage(new ChatMessage(MessageSource.SERVER,
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClient(
                        ChatAction.CLIENT_DISCONNECTED, client.getId()))));
            // Nakonec odeberu lokální záznam
            clients.remove(client.getId());
        }
    }

    // endregion

}
