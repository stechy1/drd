package cz.stechy.drd.db.table.mele_weapon;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.MeleWeapon;
import java.util.Map;

@Table(clazz = MeleWeapon.class, type = Type.ONLINE)
public class MeleWeaponOnlineTable extends BaseOnlineTable<MeleWeapon> {

    // region Constructors

    @Inject
    public MeleWeaponOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(MeleWeapon.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Weaponmele.FIREBASE_CHILD;
    }

    @Override
    public MeleWeapon fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

    // endregion

}
