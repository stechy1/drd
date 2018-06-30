package cz.stechy.drd.service;

import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.chat.ChatContact;
import cz.stechy.drd.model.chat.OnChatMessageReceived;
import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.ChatMessage;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClient;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationClientRoom;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.ChatMessageAdministrationRoom;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageAdministrationData.IChatMessageAdministrationData;
import cz.stechy.drd.net.message.ChatMessage.ChatMessageCommunicationData;
import cz.stechy.drd.net.message.ChatMessage.IChatMessageData;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.net.message.MessageType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída poskytující chatovací služby
 */
@Singleton
public final class ChatService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    // endregion

    // region Variables

    // Kolekce připojených klientů
    private final ObservableMap<UUID, ChatContact> clients = FXCollections.observableHashMap();
    // Kolekce vytvořených místností
    private final ObservableMap<String, ObservableList<UUID>> rooms = FXCollections.observableHashMap();
    // Register posluchačů na příjem zprávy
    private final List<OnChatMessageReceived> messageListeners = new ArrayList<>();
    // Komunikátor se serverem
    private final ClientCommunicator communicator;

    private CryptoService cryptoService;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou chatovací službu
     *
     * @param communicator {@link ClientCommunicator} Služba poskytující komunikaci se serverem
     * @param cryptoService {@link CryptoService} Služba poskytující šifrovací funkce
     */
    public ChatService(ClientCommunicator communicator, CryptoService cryptoService) {
        this.communicator = communicator;
        this.cryptoService = cryptoService;
        this.communicator.connectionStateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case CONNECTED:
                    this.communicator.registerMessageObserver(MessageType.AUTH, this.chatMessageListener);
                    break;
                case CONNECTING:
                    break;
                case DISCONNECTED:
                    this.communicator.unregisterMessageObserver(MessageType.AUTH, this.chatMessageListener);
                    break;
            }

        });
    }

    // endregion

    // region Public methods

    /**
     * Odešle zprávu
     *
     * @param message Obsah zprávy
     * @param destination ID cílového klienta
     */
    public void sendMessage(String message, UUID destination) {
        final ChatContact chatContact = clients.get(destination);
        if (chatContact == null) {
            throw new RuntimeException("Klient nebyl nalezen.");
        }

        byte[] messageData = chatContact.encrypt(message.getBytes());
        communicator.sendMessage(new ChatMessage(MessageSource.CLIENT,
            new ChatMessageCommunicationData(destination, messageData)));

    }

    public void addChatMessageReceivedListener(OnChatMessageReceived listener) {
        messageListeners.add(listener);
    }

    public void removeChatMessageReceivedListener(OnChatMessageReceived listener) {
        messageListeners.remove(listener);
    }

    // endregion

    // region Getters & Setters

    /**
     * Vrátí seznam všech připojených klientů
     *
     * @return {@link ObservableList}
     */
    public ObservableMap<UUID, ChatContact> getClients() {
        return FXCollections.unmodifiableObservableMap(clients);
    }

    /**
     * Vrátí seznam všech místností vlastněných klientem
     *
     * @return {@link ObservableMap}
     */
    public ObservableMap<String, ObservableList<UUID>> getRooms() {
        return FXCollections.unmodifiableObservableMap(rooms);
    }

    // endregion

    private final OnDataReceivedListener chatMessageListener = message -> {
        final ChatMessage chatMessage = (ChatMessage) message;
        final IChatMessageData messageData = (IChatMessageData) chatMessage.getData();
        switch (messageData.getDataType()) {
            case DATA_ADMINISTRATION:
                final ChatMessageAdministrationData administrationData = (ChatMessageAdministrationData) messageData;
                IChatMessageAdministrationData data = (IChatMessageAdministrationData) administrationData.getData();
                switch (data.getAction()) {
                    case CLIENT_CONNECTED:
                        final ChatMessageAdministrationClient messageAdministrationClientConnected = (ChatMessageAdministrationClient) data;
                        final UUID connectedClientID = messageAdministrationClientConnected.getClientID();
                        final String connectedClientName = messageAdministrationClientConnected.getName();
                        final CypherKey connectedClientKey = messageAdministrationClientConnected.getKey();
                        clients.putIfAbsent(connectedClientID, new ChatContact(connectedClientName, cryptoService.makeCypher(connectedClientKey)));
                        break;
                    case CLIENT_DISCONNECTED:
                        final ChatMessageAdministrationClient messageAdministrationClientDiconnected = (ChatMessageAdministrationClient) data;
                        final UUID disconnectedClientID = messageAdministrationClientDiconnected.getClientID();
                        clients.remove(disconnectedClientID);
                        break;
                    case ROOM_CREATED:
                        final ChatMessageAdministrationRoom messageAdministrationRoomCreated = (ChatMessageAdministrationRoom) data;
                        final String createdRoomName = messageAdministrationRoomCreated.roomName();
                        rooms.put(createdRoomName, FXCollections.observableArrayList());
                        break;
                    case ROOM_DELETED:
                        final ChatMessageAdministrationRoom messageAdministrationRoomDeleted = (ChatMessageAdministrationRoom) data;
                        final String deletedRoomName = messageAdministrationRoomDeleted.roomName();
                        rooms.remove(deletedRoomName);
                        break;
                    case CLIENT_JOINED_ROOM:
                        final ChatMessageAdministrationClientRoom messageAdministrationClientJoinedRoom = (ChatMessageAdministrationClientRoom) data;
                        final UUID joinedRoomClientID = messageAdministrationClientJoinedRoom.getClient();
                        final String joinedRoomName = messageAdministrationClientJoinedRoom.getRoom();
                        rooms.get(joinedRoomName).add(joinedRoomClientID);
                        break;
                    case CLIENT_LEAVE_ROOM:
                        final ChatMessageAdministrationClientRoom messageAdministrationClientLeavedRoom = (ChatMessageAdministrationClientRoom) data;
                        final UUID leavedRoomClientID = messageAdministrationClientLeavedRoom.getClient();
                        final String leavedRoomName = messageAdministrationClientLeavedRoom.getRoom();
                        rooms.get(leavedRoomName).remove(leavedRoomClientID);
                        break;
                    default:
                        throw new IllegalArgumentException("Neplatny argument.");
                }
                break;
            case DATA_COMMUNICATION:
                final ChatMessageCommunicationData communicationData = (ChatMessageCommunicationData) messageData;
                final UUID destination = communicationData.getDestination();
                final byte[] messageRaw = (byte[]) communicationData.getData();
                final String messageContent = new String(cryptoService.decrypt(messageRaw),
                    StandardCharsets.UTF_8);
                messageListeners.forEach(
                    listener -> listener.onChatMessageReceived(messageContent, destination));
                break;
            default:
                throw new IllegalArgumentException("Neplatny parametr.");
        }
    };
}
