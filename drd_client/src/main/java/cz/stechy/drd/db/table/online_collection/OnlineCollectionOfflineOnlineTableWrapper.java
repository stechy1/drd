package cz.stechy.drd.db.table.online_collection;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.OnlineCollection;

@Table(clazz = OnlineCollection.class, type = Type.WRAPPER)
public class OnlineCollectionOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<OnlineCollection> {

    // region Constructors

    @Inject
    public OnlineCollectionOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(OnlineCollection.class), tableFactory.getOnlineTable(OnlineCollection.class));
    }

    // endregion

}
