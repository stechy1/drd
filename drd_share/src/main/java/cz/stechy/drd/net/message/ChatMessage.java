package cz.stechy.drd.net.message;

import cz.stechy.drd.crypto.RSA.CypherKey;
import java.io.Serializable;

public class ChatMessage implements IMessage {

    private static final long serialVersionUID = -7817515518938131863L;

    private final MessageSource source;
    private final IChatMessageData data;

    public ChatMessage(MessageSource source, IChatMessageData data) {
        this.source = source;
        this.data = data;
    }

    @Override
    public MessageType getType() {
        return MessageType.CHAT;
    }

    @Override
    public MessageSource getSource() {
        return source;
    }

    @Override
    public Object getData() {
        return data;
    }

    public interface IChatMessageData extends Serializable {

        ChatMessageDataType getDataType();

        Object getData();

    }

    public enum ChatMessageDataType {
        DATA_ADMINISTRATION, DATA_COMMUNICATION
    }

    public static final class ChatMessageAdministrationData implements IChatMessageData {

        private static final long serialVersionUID = 8237826895694688852L;

        private final IChatMessageAdministrationData data;

        public ChatMessageAdministrationData(IChatMessageAdministrationData data) {
            this.data = data;
        }

        @Override
        public ChatMessageDataType getDataType() {
            return ChatMessageDataType.DATA_ADMINISTRATION;
        }

        @Override
        public Object getData() {
            return data;
        }

        public enum ChatAction {
            CLIENT_REQUEST_CONNECT, // Požadavek na připojení k chatovací službě
            CLIENT_CONNECTED, CLIENT_DISCONNECTED, // Akce klientů
            CLIENT_TYPING, CLIENT_NOT_TYPING, // Informace o tom, zda-li někdo píše
            ROOM_CREATED, ROOM_DELETED, // Akce místnosti
            CLIENT_JOINED_ROOM, CLIENT_LEAVE_ROOM // Akce klientů v místnostech
        }

        public interface IChatMessageAdministrationData extends Serializable {

            ChatAction getAction();

        }

        public static final class ChatMessageAdministrationClientRequestConnect implements IChatMessageAdministrationData {

            private static final long serialVersionUID = 642524654412490721L;

            private final String id;
            private final String name;

            public ChatMessageAdministrationClientRequestConnect(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public ChatAction getAction() {
                return ChatAction.CLIENT_REQUEST_CONNECT;
            }
        }

        public static final class ChatMessageAdministrationClient implements IChatMessageAdministrationData {

            private static final long serialVersionUID = -6101992378764622660L;

            private final ChatAction action;
            private final String clientID;
            private final String name;
            private final CypherKey key;

            public ChatMessageAdministrationClient(ChatAction action, String clientID) {
                this(action, clientID, "", CypherKey.EMPTY);
            }

            public ChatMessageAdministrationClient(ChatAction action, String clientID,
                String name, CypherKey key) {
                this.clientID = clientID;
                this.name = name;
                this.key = key;
                assert action == ChatAction.CLIENT_CONNECTED || action == ChatAction.CLIENT_DISCONNECTED;
                this.action = action;
            }

            public String getClientID() {
                return clientID;
            }

            public String getName() {
                return name;
            }

            public CypherKey getKey() {
                return key;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }

        public static final class ChatMessageAdministrationClientTyping implements IChatMessageAdministrationData {

            private static final long serialVersionUID = 630432882631419944L;

            private final ChatAction action;
            private final String clientID;

            public ChatMessageAdministrationClientTyping(ChatAction action, String clientID) {
                assert action == ChatAction.CLIENT_TYPING || action == ChatAction.CLIENT_NOT_TYPING;
                this.action = action;
                this.clientID = clientID;
            }

            public String getClientID() {
                return clientID;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }

        public static final class ChatMessageAdministrationRoom implements IChatMessageAdministrationData {

            private static final long serialVersionUID = 1423709615397074472L;

            private final ChatAction action;
            private final String roomName;

            public ChatMessageAdministrationRoom(ChatAction action, String roomName) {
                assert action == ChatAction.ROOM_CREATED || action == ChatAction.ROOM_DELETED;
                this.action = action;
                this.roomName = roomName;
            }

            public String roomName() {
                return roomName;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }

        public static final class ChatMessageAdministrationClientRoom implements IChatMessageAdministrationData {

            private static final long serialVersionUID = -5297448492415747259L;

            private final ChatAction action;
            private final String client;
            private final String room;

            public ChatMessageAdministrationClientRoom(ChatAction action, String client,
                String room) {
                this.client = client;
                this.room = room;
                assert action == ChatAction.CLIENT_JOINED_ROOM || action == ChatAction.CLIENT_LEAVE_ROOM;
                this.action = action;
            }

            public String getClient() {
                return client;
            }

            public String getRoom() {
                return room;
            }

            @Override
            public ChatAction getAction() {
                return action;
            }
        }
    }

    public static final class ChatMessageCommunicationData implements IChatMessageData {

        private static final long serialVersionUID = -2426630119019364058L;

        private final ChatMessageCommunicationDataContent data;

        public ChatMessageCommunicationData(String id, byte[] data) {
            this.data = new ChatMessageCommunicationDataContent(id, data);
        }

        @Override
        public ChatMessageDataType getDataType() {
            return ChatMessageDataType.DATA_COMMUNICATION;
        }

        @Override
        public Object getData() {
            return data;
        }

        public static final class ChatMessageCommunicationDataContent implements Serializable {

            private static final long serialVersionUID = -905319575968060192L;

            private final String destination;
            private final byte[] data;

            ChatMessageCommunicationDataContent(String destination, byte[] data) {
                this.destination = destination;
                this.data = data;
            }

            public String getDestination() {
                return destination;
            }

            public byte[] getData() {
                return data;
            }
        }
    }
}
