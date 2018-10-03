package cz.stechy.drd.db.table.backpack;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.Backpack;
import java.util.Map;

@Table(type = Type.ONLINE, clazz = Backpack.class)
public class BackpackOnlineTable extends BaseOnlineTable<Backpack> {

    // region Constructors

    @Inject
    public BackpackOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(Backpack.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Backpack.FIREBASE_CHILD;
    }

    @Override
    public Backpack fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

    // endregion
}
