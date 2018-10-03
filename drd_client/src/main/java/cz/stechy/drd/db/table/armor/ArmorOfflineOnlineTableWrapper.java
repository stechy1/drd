package cz.stechy.drd.db.table.armor;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.Armor;

@Table(clazz = Armor.class, type = Type.WRAPPER)
public class ArmorOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<Armor> {

    // region Constructors

    @Inject
    public ArmorOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(Armor.class), tableFactory.getOnlineTable(Armor.class));
    }

    // endregion

}
