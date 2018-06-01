package cz.stechy.drd.net.message;

import java.io.Serializable;
import java.util.Map;

public class DatabaseMessage implements IMessage {

    private static final long serialVersionUID = 524340027525212916L;

    private final IDatabaseMessageData data;
    private final MessageSource source;

    public DatabaseMessage(IDatabaseMessageData data, MessageSource source) {
        this.data = data;
        this.source = source;
    }

    @Override
    public MessageType getType() {
        return MessageType.DATABASE;
    }

    @Override
    public MessageSource getSource() {
        return source;
    }

    @Override
    public Object getData() {
        return data;
    }

    public interface IDatabaseMessageData extends Serializable {

        DatabaseMessageDataType getDataType();

        DatabaseMessageItemType getItemType();

        Object getData();
    }

    public enum DatabaseMessageDataType {
        DATA_ADMINISTRATION, DATA_MANIPULATION
    }

    public enum DatabaseMessageItemType {
        ITEM, SPELL, BEASTIARY, COLLECTION
    }

    public static final class DatabaseMessageCRUD implements IDatabaseMessageData {

        private static final long serialVersionUID = -6367186706204237621L;

        private final Map<String, Object> itemMap;
        private final DatabaseMessageItemType itemType;
        private final DatabaseAction action;

        public DatabaseMessageCRUD(DatabaseAction action, Map<String, Object> itemMap,
            DatabaseMessageItemType itemType) {
            this.action = action;
            this.itemMap = itemMap;
            this.itemType = itemType;
        }

        @Override
        public DatabaseMessageDataType getDataType() {
            return DatabaseMessageDataType.DATA_MANIPULATION;
        }

        @Override
        public DatabaseMessageItemType getItemType() {
            return itemType;
        }

        public DatabaseAction getAction() {
            return action;
        }

        @Override
        public Object getData() {
            return itemMap;
        }

        public enum DatabaseAction {
            CREATE, UPDATE, DELETE
        }
    }

    public static final class DatabaseMessageAdministration implements IDatabaseMessageData {

        private static final long serialVersionUID = -4557049744249570971L;

        private final String tableName;
        private final DatabaseMessageItemType itemType;
        private final DatabaseAction action;

        public DatabaseMessageAdministration(String tableName, DatabaseMessageItemType itemType,
            DatabaseAction action) {
            this.tableName = tableName;
            this.itemType = itemType;
            this.action = action;
        }

        @Override
        public DatabaseMessageDataType getDataType() {
            return DatabaseMessageDataType.DATA_ADMINISTRATION;
        }

        @Override
        public DatabaseMessageItemType getItemType() {
            return itemType;
        }

        public DatabaseAction getAction() {
            return action;
        }

        @Override
        public Object getData() {
            return tableName;
        }

        public enum DatabaseAction {
            REGISTER, UNGERISTER
        }
    }
}
