package cz.stechy.drd.plugins.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.core.connection.MessageReceivedEvent;
import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.net.message.ChatMessage;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClient;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientRequestConnect;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientTyping;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.IChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageCommunicationData.ChatMessageCommunicationDataContent;
import cz.stechy.drd.net.message.ChatMessage.IChatMessageData;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.chat.service.IChatService;

@Singleton
public class ChatPlugin implements IPlugin {

    // region Constants

    public static final String PLUGIN_NAME = "chat";

    // endregion

    // region Variables

    private final IChatService chatService;

    // endregion

    // region Constructors

    @Inject
    public ChatPlugin(IChatService chatService) {
        this.chatService = chatService;
    }

    // endregion

    // region Private methods

    private void chatMessageHandler(IEvent event) {
        assert event instanceof MessageReceivedEvent;
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final IClient client = messageReceivedEvent.getClient();
        final ChatMessage chatMessage = (ChatMessage) messageReceivedEvent.getReceivedMessage();

        final IChatMessageData chatMessageData = (IChatMessageData) chatMessage.getData();
        switch (chatMessageData.getDataType()) {
            case DATA_ADMINISTRATION:
                IChatMessageAdministrationData administrationData = (IChatMessageAdministrationData) chatMessageData.getData();
                switch (administrationData.getAction()) {
                    case CLIENT_REQUEST_CONNECT:
                        final ChatMessageAdministrationClientRequestConnect clientRequestConnect = (ChatMessageAdministrationClientRequestConnect) administrationData;
                        final String clientId = clientRequestConnect.getId();
                        final String clientName = clientRequestConnect.getName();
                        chatService.addClient(client, clientId, clientName);
                        break;
                    case CLIENT_DISCONNECTED:
                        final ChatMessageAdministrationClient clientDisconnected = (ChatMessageAdministrationClient) administrationData;
                        final String disconnectedClientId = clientDisconnected.getClientID();
                        chatService.removeClient(disconnectedClientId);
                        break;
                    case CLIENT_TYPING:
                        final ChatMessageAdministrationClientTyping clientIsTyping = (ChatMessageAdministrationClientTyping) administrationData;
                        final String typingClientId = clientIsTyping.getClientID();
                        chatService.informClientIsTyping(typingClientId, chatService.findIdByClient(client).orElse(""), true);
                        break;
                    case CLIENT_NOT_TYPING:
                        final ChatMessageAdministrationClientTyping clientIsNotTyping = (ChatMessageAdministrationClientTyping) administrationData;
                        final String notTypingClientId = clientIsNotTyping.getClientID();
                        chatService.informClientIsTyping(notTypingClientId, chatService.findIdByClient(client).orElse(""), false);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatný argument. " + administrationData.getAction());
                }
                break;
            case DATA_COMMUNICATION:
                final ChatMessageCommunicationDataContent communicationDataContent = (ChatMessageCommunicationDataContent) chatMessageData.getData();
                final String destinationClientId = communicationDataContent.getDestination();
                final String sourceClientId = chatService.findIdByClient(client).orElse("");
                final byte[] rawMessage = communicationDataContent.getData();
                chatService.sendMessage(destinationClientId, sourceClientId, rawMessage);
                break;
            default:
                throw new IllegalArgumentException("Neplatný argument." + chatMessageData.getDataType());
        }
    }

    // endregion

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {

    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(ChatMessage.MESSAGE_TYPE, this::chatMessageHandler);
    }
}
