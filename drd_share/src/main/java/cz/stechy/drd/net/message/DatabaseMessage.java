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

        Object getData();
    }

    public enum DatabaseMessageDataType {
        DATA_ADMINISTRATION, DATA_MANIPULATION
    }

    public static final class DatabaseMessageCRUD implements IDatabaseMessageData {

        private static final long serialVersionUID = -6367186706204237621L;

        private final Map<String, Object> itemMap;
        private final String tableName;
        private final String itemId;
        private final DatabaseAction action;

        public DatabaseMessageCRUD(Map<String, Object> itemMap, String tableName,
            DatabaseAction action) {
            this(itemMap, tableName, action, "");
        }

        public DatabaseMessageCRUD(Map<String, Object> itemMap, String tableName,
            DatabaseAction action,
            String itemId) {
            this.action = action;
            this.tableName = tableName;
            this.itemMap = itemMap;
            this.itemId = itemId;
        }

        @Override
        public DatabaseMessageDataType getDataType() {
            return DatabaseMessageDataType.DATA_MANIPULATION;
        }

        public DatabaseAction getAction() {
            return action;
        }

        public String getTableName() {
            return tableName;
        }

        public String getItemId() {
            return itemId;
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
        private final DatabaseAction action;

        public DatabaseMessageAdministration(String tableName, DatabaseAction action) {
            this.tableName = tableName;
            this.action = action;
        }

        @Override
        public DatabaseMessageDataType getDataType() {
            return DatabaseMessageDataType.DATA_ADMINISTRATION;
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
