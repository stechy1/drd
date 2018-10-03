package cz.stechy.drd.db.table.online_collection;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.OnlineCollection;
import java.util.Map;

@Table(clazz = OnlineCollection.class, type = Type.ONLINE)
public class OnlineCollectionOnlineTable extends BaseOnlineTable<OnlineCollection> {

    // region Constructors

    @Inject
    public OnlineCollectionOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(OnlineCollection.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return null;
    }

    @Override
    public OnlineCollection fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
