package cz.stechy.drd.db.table.hero;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.OfflineOnlineTableWrapper;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.entity.hero.Hero;

@Table(clazz = Hero.class, type = Type.WRAPPER)
public class HeroOfflineOnlineTableWrapper extends OfflineOnlineTableWrapper<Hero> {

    // region Constructors

    @Inject
    public HeroOfflineOnlineTableWrapper(ITableFactory tableFactory) {
        super(tableFactory.getOfflineTable(Hero.class), tableFactory.getOnlineTable(Hero.class));
    }

    // endregion

}
