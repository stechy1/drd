package cz.stechy.drd.db.table.armor;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.Armor;
import java.util.Map;

@Table(clazz = Armor.class, type = Type.ONLINE)
public class ArmorOnlineTable extends BaseOnlineTable<Armor> {

    // region Constructors

    @Inject
    public ArmorOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(Armor.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Armor.FIREBASE_CHILD;
    }

    @Override
    public Armor fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
