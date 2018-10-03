package cz.stechy.drd.db.table.mele_weapon;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.item.MeleWeapon;

@Table(clazz = MeleWeapon.class, type = Type.WRAPPER)
public class MeleWeaponOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<MeleWeapon> {

    // region Constructors

    @Inject
    public MeleWeaponOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(MeleWeapon.class), tableFactory.getOnlineTable(MeleWeapon.class));
    }

    // endregion

}
