package cz.stechy.drd.processor.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableEntry {

    // region Variables

    private final String tableName;
    private final String firebaseName;
    private final List<ColumnEntry> columns = new ArrayList<>();

    // endregion

    // region Constructors

    TableEntry(String row) {
        String[] definition = row.split(": ");
        String[] tableNames = definition[0].split(", ");
        this.tableName = tableNames[0];
        this.firebaseName = tableNames.length > 1 ? tableNames[1] : "";
        String[] columns = definition[1].split(", ");
        for (String column : columns) {
            this.columns.add(new ColumnEntry(column, tableName));
        }
    }

    // endregion

    // region Getters & Setters

    public String getTableName() {
        return tableName;
    }

    public String getFirebaseName() {
        return firebaseName;
    }

    public List<ColumnEntry> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    // endregion

    public static final class ColumnEntry {

        // region Variables

        private final String key;
        private final String value;

        // endregion

        // region Constructors

        ColumnEntry(String column, String tableName) {
            this.key = "COLUMN_" + column.toUpperCase();
            this.value = tableName + "_" + column;
        }

        // endregion

        // region Getters & Setters

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        // endregion

        @Override
        public String toString() {
            return "Column: " + key + " = " + value;
        }
    }

    @Override
    public String toString() {
        return "Table: " + tableName + "; firebase: " + firebaseName + "; columns: " + columns.toString();
    }
}

