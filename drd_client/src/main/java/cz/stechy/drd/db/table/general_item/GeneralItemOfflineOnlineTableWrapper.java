package cz.stechy.drd.db.table.general_item;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.GeneralItem;

@Table(clazz = GeneralItem.class, type = Type.WRAPPER)
public class GeneralItemOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<GeneralItem> {

    // region Constructors

    @Inject
    public GeneralItemOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(GeneralItem.class), tableFactory.getOnlineTable(GeneralItem.class));
    }

    // endregion

}
