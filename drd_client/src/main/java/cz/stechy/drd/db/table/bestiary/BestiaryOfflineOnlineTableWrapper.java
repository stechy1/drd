package cz.stechy.drd.db.table.bestiary;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.entity.mob.Mob;

@Table(clazz = Mob.class, type = Type.WRAPPER)
public class BestiaryOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<Mob> {

    @Inject
    public BestiaryOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(Mob.class), tableFactory.getOnlineTable(Mob.class));
    }
}
