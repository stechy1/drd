package cz.stechy.drd.db.table.bestiary;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.entity.mob.Mob;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table(clazz = Mob.class, type = Type.ONLINE)
public class BestiaryOnlineTable extends BaseOnlineTable<Mob> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BestiaryOnlineTable.class);

    @Inject
    public BestiaryOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(Mob.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Bestiary.FIREBASE_CHILD;
    }

    @Override
    public Mob fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

}
