package cz.stechy.drd.db.table.ranged_weapon;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.RangedWeapon;

@Table(clazz = RangedWeapon.class, type = Type.WRAPPER)
public class RangedWeaponOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<RangedWeapon> {

    // region Constructors

    @Inject
    public RangedWeaponOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(RangedWeapon.class), tableFactory.getOnlineTable(RangedWeapon.class));
    }

    // endregion

}
