package cz.stechy.drd.db.table.general_item;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.item.GeneralItem;
import java.util.Map;

@Table(clazz = GeneralItem.class, type = Type.ONLINE)
public class GeneralItemOnlineTable extends BaseOnlineTable<GeneralItem> {

    // region Constructors

    @Inject
    public GeneralItemOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(GeneralItem.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Generalitems.FIREBASE_CHILD;
    }

    @Override
    public GeneralItem fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

    // endregion

}
