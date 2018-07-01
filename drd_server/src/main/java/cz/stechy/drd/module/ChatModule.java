package cz.stechy.drd.module;

import cz.stechy.drd.Client;
import cz.stechy.drd.chat.ChatService;
import cz.stechy.drd.net.message.ChatMessage;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientRequestConnect;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.IChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.IChatMessageData;
import cz.stechy.drd.net.message.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatModule implements IModule {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatModule.class);

    // endregion

    // region Variables

    private final ChatService chatService;

    // endregion

    // region Constructors

    public ChatModule(ChatService chatService) {
        this.chatService = chatService;
    }

    // endregion

    @Override
    public void handleMessage(IMessage message, Client client) {
        final ChatMessage chatMessage = (ChatMessage) message;
        final IChatMessageData chatMessageData = (IChatMessageData) chatMessage.getData();
        switch (chatMessageData.getDataType()) {
            case DATA_ADMINISTRATION:
                IChatMessageAdministrationData administrationData = (IChatMessageAdministrationData) chatMessageData.getData();
                switch (administrationData.getAction()) {
                    case CLIENT_REQUEST_CONNECT:
                        final ChatMessageAdministrationClientRequestConnect clientRequestConnect = (ChatMessageAdministrationClientRequestConnect) administrationData;
                        final String clientName = clientRequestConnect.getName();
                        chatService.addClient(client, clientName);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný argument.");
                }
                break;
            case DATA_COMMUNICATION:
                break;
            default:
                throw new IllegalArgumentException("Neplatný argument.");
        }
    }

    @Override
    public void onClientDisconnect(Client client) {
        chatService.removeClient(client);
    }
}
