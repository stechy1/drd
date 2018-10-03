package cz.stechy.drd.db;

import com.google.inject.Inject;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.db.base.OnlineRecord;
import cz.stechy.drd.db.base.Row;
import java.util.Map;

public class TableFactory implements ITableFactory {

    private final Map<Class, BaseOfflineTable> offlineTableBinder;
    private final Map<Class, BaseOnlineTable> onlineTableBinder;
//    private final Map<Class, OfflineOnlineTableWrapper> tableWrapper;

    @Inject
    public TableFactory(Map<Class, BaseOfflineTable> offlineTableBinder, Map<Class, BaseOnlineTable> onlineTableBinder) {
        this.offlineTableBinder = offlineTableBinder;
        this.onlineTableBinder = onlineTableBinder;
//        this.tableWrapper = tableWrapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Row> BaseOfflineTable<T> getOfflineTable(Class clazz) {
        return offlineTableBinder.get(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends OnlineRecord> BaseOnlineTable<T> getOnlineTable(Class clazz) {
        return onlineTableBinder.get(clazz);
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> BaseTableDefinitions<T> getTableDefinitions(Class clazz) {
//        return tableDefinitionsBinder.get(clazz);
//    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public <T extends OnlineRecord> OfflineOnlineTableWrapper<T> getTableWrapper(Class clazz) {
//        return tableWrapper.get(clazz);
//    }
}
