package cz.stechy.drd.annotation.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace reprezentující tabulku v databázi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Table {

    /**
     * Název tabulky
     */
    String tableName();

    String itemType();

    /**
     * Nastaví, zda-li se má generovat kód upravující obsah mapy, která konvertuje objekt do mapy
     */
    boolean customizeMapEntries() default false;
}

