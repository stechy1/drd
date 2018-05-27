package cz.stechy.drd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace pro indikaci, že bude použito připojení k Firebase
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface FirebaseTable {

    /**
     * Cesta k "tabulce" ve firebase
     */
    String childPath();

}
