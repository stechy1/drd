package cz.stechy.drd.db.base;

import com.google.inject.Inject;
import java.util.Map;

public class TableWrapperFactory implements ITableWrapperFactory {

    private final Map<Class, OfflineOnlineTableWrapper> tableWrapper;

    @Inject
    public TableWrapperFactory(Map<Class, OfflineOnlineTableWrapper> tableWrapper) {
        this.tableWrapper = tableWrapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends OnlineRecord> OfflineOnlineTableWrapper<T> getTableWrapper(Class clazz) {
        return tableWrapper.get(clazz);
    }

}
