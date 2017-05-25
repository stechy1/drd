package cz.stechy.drd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace představující jeden sloupeček v tabulce
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Column {

    /**
     * Název sloupce
     */
    String name();

    /**
     * Pořadí zpracování
     */
    int order();
}
