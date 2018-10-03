package cz.stechy.drd.processor.table;

public class Entry {

    private final String tablePackage;
    private final String tableName;
    private final String entityPackage;
    private final String entityName;

    public Entry(String tablePackage, String tableName, String entityPackage, String entityName) {
        this.tablePackage = tablePackage;
        this.tableName = tableName;
        this.entityPackage = entityPackage;
        this.entityName = entityName;
    }

    public String getTablePackage() {
        return tablePackage;
    }

    public String getTableName() {
        return tableName;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public String getEntityName() {
        return entityName;
    }
}
