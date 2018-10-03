package cz.stechy.drd.db.base;

public interface ITableDefinitionsFactory {

    @SuppressWarnings("unchecked")
    <T> BaseTableDefinitions<T> getTableDefinitions(Class clazz);

}
