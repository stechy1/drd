package cz.stechy.drd.annotation.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace reprezentující jeden sloupeček v databázi
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Column {

    /**
     * Název sloupečku
     */
    String columnName() default "";

    /**
     * Datový typ sloupečku
     */
    String clazz();

    /**
     * Specifikace sloupečku
     */
    Specification[] specifications() default {};

    /**
     * True, pokud se nemá generovat záznam v metodě fromStringItemMap()
     */
    boolean ignoreFromStringItemMap() default false;

    /**
     * True, pokud se nemá generovat záznam v metodě parseResultSet()
     */
    boolean ignoreParseResultSet() default false;

    /**
     * True, pokud se nemá generovat záznam v metodě itemToParams()
     */
    boolean ignoreItemToParams() default false;

    /**
     * Výčet specifikací sloupečku
     */
    enum Specification {
        NOT_NULL, PRIMARY_KEY, UNIQUE;

        @Override
        public String toString() {
            return name();
        }
    }

}

