package cz.stechy.drd.net.message;

import java.io.Serializable;
import java.util.Map;

public class DatabaseMessage implements IMessage {

    private static final long serialVersionUID = 524340027525212916L;

    public static final String MESSAGE_TYPE = "database";

    private final MessageSource source;
    private final boolean success;
    private final IDatabaseMessageData data;

    public DatabaseMessage(MessageSource source, IDatabaseMessageData data) {
        this(source, true, data);
    }

    public DatabaseMessage(MessageSource source, boolean success, IDatabaseMessageData data) {
        this.success = success;
        this.data = data;
        this.source = source;
    }

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public MessageSource getSource() {
        return source;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return getType() + " - " + data.toString();
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

        @Override
        public String toString() {
            return getAction().toString();
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

        @Override
        public String toString() {
            return getAction() + " table: " + getData().toString();
        }

        public enum DatabaseAction {
            REGISTER, UNGERISTER
        }
    }
}
