package cz.stechy.drd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace pro indikaci, že z dané třídy se bude generovat manažer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Table {

    /**
     * Název tabulky
     */
    String name();

    /**
     * Třída, kterou bude generovaný manažer dědit
     */
    Class<?> parent();

}
