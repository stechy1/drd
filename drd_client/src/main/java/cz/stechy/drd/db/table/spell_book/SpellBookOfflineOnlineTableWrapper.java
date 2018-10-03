package cz.stechy.drd.db.table.spell_book;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.spell.Spell;

@Table(clazz = Spell.class, type = Type.WRAPPER)
public class SpellBookOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<Spell> {

    @Inject
    public SpellBookOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(Spell.class), tableFactory.getOnlineTable(Spell.class));
    }
}
