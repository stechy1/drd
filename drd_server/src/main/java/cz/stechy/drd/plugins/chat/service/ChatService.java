package cz.stechy.drd.plugins.chat.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.net.message.ChatMessage;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatAction;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClient;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientTyping;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageCommunicationData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.plugins.chat.ChatClient;
import cz.stechy.drd.plugins.crypto.service.ICryptoService;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class ChatService implements IChatService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    // endregion

    // region Variables

    private final Object lock = new Object();
    private final Map<String, ChatClient> clients = new HashMap<>();

    private final ICryptoService cryptoService;

    // endregion

    // region Constructors

    @Inject
    public ChatService(ICryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // region Private methods

    /**
     * Odešle zprávu všem klientům, kteří jsou přihlášení k chatu
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat všem
     */
    private void broadcastMessage(IMessage message) {
        clients.values().forEach(chatClient -> chatClient.getClient().sendMessageAsync(message));
    }

    // endregion

    // endregion

    @Override
    public void addClient(IClient client, String id, String name) {
        LOGGER.info("Přidávám nového klienta {} na seznam v chatu.", name);
        final ChatClient chatClient = new ChatClient(client, name);
        synchronized (lock) {
            // Odešlu klientovi aktuální seznam všech klientů
            clients.forEach((clientId, entry) ->
                client.sendMessageAsync(new ChatMessage(MessageSource.SERVER,
                    new ChatMessageAdministrationData(
                        new ChatMessageAdministrationClient(
                            ChatAction.CLIENT_CONNECTED, clientId, entry.getName(),
                            cryptoService.getClientPublicKey(entry.getClient()))))));
            // Přidám záznam o klientovi na seznam
            clients.put(id, chatClient);
            // Rozešlu broadcast všem připojeným klientům
            // Ano, odešlu ho i klientovi, který se právě připojil
            // Tím získám jistou výhodu a můžu snadno přidat sám sebe na seznam kontaktů
            broadcastMessage(new ChatMessage(MessageSource.SERVER,
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClient(
                        ChatAction.CLIENT_CONNECTED, id, name,
                        cryptoService.getClientPublicKey(client)))));
        }
    }

    @Override
    public void removeClient(String id) {
        LOGGER.info("Odebírám klienta ze seznamu chatu.");
        synchronized (lock) {
            // Odeberu záznam ze seznamu klientů
            clients.remove(id);
            // A rozešlu broadcast všem připojeným klientům, že se klient odpojuje
            broadcastMessage(new ChatMessage(MessageSource.SERVER,
                new ChatMessageAdministrationData(
                    new ChatMessageAdministrationClient(
                        ChatAction.CLIENT_DISCONNECTED, id))));
        }
    }

    @Override
    public void sendMessage(String destinationClientId, String sourceClientId, byte[] rawMessage) {
        clients.get(destinationClientId)
            .getClient()
            .sendMessageAsync(new ChatMessage(MessageSource.SERVER,
                new ChatMessageCommunicationData(sourceClientId, rawMessage)));
    }

    @Override
    public Optional<String> findIdByClient(IClient client) {
        final Optional<Entry<String, ChatClient>> entryOptional = clients.entrySet()
            .stream()
            .filter(entry -> entry.getValue().getClient() == client)
            .findFirst();

        return entryOptional.map(Entry::getKey);
    }

    @Override
    public void informClientIsTyping(String destinationClientId, String sourceClientId,
        boolean typing) {
        final ChatClient chatClient = clients.get(destinationClientId);
        if (chatClient == null) {
            return;
        }

        chatClient.getClient().sendMessageAsync(new ChatMessage(MessageSource.SERVER,
            new ChatMessageAdministrationData(
                new ChatMessageAdministrationClientTyping(
                    typing ? ChatAction.CLIENT_TYPING : ChatAction.CLIENT_NOT_TYPING, sourceClientId
                ))));
    }

}
