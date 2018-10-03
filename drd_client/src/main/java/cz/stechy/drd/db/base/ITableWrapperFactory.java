package cz.stechy.drd.db.base;

public interface ITableWrapperFactory {

    @SuppressWarnings("unchecked")
    <T extends OnlineRecord> OfflineOnlineTableWrapper<T> getTableWrapper(Class clazz);
}
