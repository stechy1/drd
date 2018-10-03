package cz.stechy.drd.db.base;

import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.BaseOnlineTable;

public interface ITableFactory {

    @SuppressWarnings("unchecked")
    <T extends Row> BaseOfflineTable<T> getOfflineTable(Class clazz);

    @SuppressWarnings("unchecked")
    <T extends OnlineRecord> BaseOnlineTable<T> getOnlineTable(Class clazz);

//    @SuppressWarnings("unchecked")
//    <T> BaseTableDefinitions<T> getTableDefinitions(Class clazz);

//    @SuppressWarnings("unchecked")
//    <T extends OnlineRecord> OfflineOnlineTableWrapper<T> getTableWrapper(Class clazz);
}
