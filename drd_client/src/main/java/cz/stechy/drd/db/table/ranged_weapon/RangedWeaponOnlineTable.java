package cz.stechy.drd.db.table.ranged_weapon;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.RangedWeapon;
import java.util.Map;

@Table(clazz = RangedWeapon.class, type = Type.ONLINE)
public class RangedWeaponOnlineTable extends BaseOnlineTable<RangedWeapon> {

    // region Constructors

    @Inject
    public RangedWeaponOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(RangedWeapon.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Weaponranged.FIREBASE_CHILD;
    }

    @Override
    public RangedWeapon fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

    // endregion

}
