package cz.stechy.drd.db.table.hero;

import com.google.inject.Inject;
import cz.stechy.drd.annotation.Table;
import cz.stechy.drd.annotation.Table.Type;
import cz.stechy.drd.db.BaseOnlineTable;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.model.entity.hero.Hero;
import java.util.Map;

@Table(clazz = Hero.class, type = Type.ONLINE)
public class HeroOnlineTable extends BaseOnlineTable<Hero> {

    // region Constructors

    @Inject
    public HeroOnlineTable(ITableDefinitionsFactory tableDefinitionsFactory) {
        super(tableDefinitionsFactory.getTableDefinitions(Hero.class));
    }

    // endregion

    // region Public methods

    @Override
    public String getFirebaseChildName() {
        return null;
    }

    @Override
    public Hero fromStringMap(Map<String, Object> map) {
        return null;
    }

    // endregion

}
