package cz.stechy.drd.db.base;

import com.google.inject.Inject;
import java.util.Map;

public class TableDefinitionsFactory implements ITableDefinitionsFactory {

    private final Map<Class, BaseTableDefinitions> tableDefinitionsBinder;

    @Inject
    public TableDefinitionsFactory(Map<Class, BaseTableDefinitions> tableDefinitionsBinder) {
        this.tableDefinitionsBinder = tableDefinitionsBinder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BaseTableDefinitions<T> getTableDefinitions(Class clazz) {
        return tableDefinitionsBinder.get(clazz);
    }

}
