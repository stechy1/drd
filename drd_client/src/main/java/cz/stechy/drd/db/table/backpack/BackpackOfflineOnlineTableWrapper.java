package cz.stechy.drd.db.table.backpack;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.Backpack;

@Table(type = Type.WRAPPER, clazz = Backpack.class)
public class BackpackOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<Backpack> {

    @Inject
    public BackpackOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(Backpack.class), tableFactory.getOnlineTable(Backpack.class));
    }

}
