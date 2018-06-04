package cz.stechy.drd.firebase;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

public interface ItemEvent {

    String getTableName();

    DatabaseAction getAction();

    Map<String, Object> getItem();
}
