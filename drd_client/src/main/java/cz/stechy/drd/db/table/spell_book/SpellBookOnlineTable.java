package cz.stechy.drd.db.table.spell_book;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.spell.Spell;
import java.util.Map;

@Table(clazz = Spell.class, type = Type.ONLINE)
public class SpellBookOnlineTable extends BaseOnlineTable<Spell> {

    // region Constructors

    @Inject
    public SpellBookOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(Spell.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return R.Database.Spells.FIREBASE_CHILD;
    }

    @Override
    public Spell fromStringMap(Map<String, Object> map) {
        return tableDefinitions.fromStringMap(map);
    }

    // endregion
}
