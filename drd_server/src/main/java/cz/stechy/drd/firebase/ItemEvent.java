package cz.stechy.drd.firebase;

import cz.stechy.drd.net.message.DatabaseMessage.DatabaseMessageCRUD.DatabaseAction;
import java.util.Map;

public interface ItemEvent {

    DatabaseAction getAction();

    Map<String, Object> getItem();
}
