package cz.stechy.drd.annotation;

/**
 * Přepravka obsahující informace o daném sloupečku
 */
public class ColumnInfo {

    final String name;
    final Class clazz;

    public ColumnInfo(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }
}
