package cz.stechy.drd.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Table {

    /**
     * Třída, které patří definice tabulky
     */
    Class clazz();

    Type type();

    enum Type {
        DEFINITION, OFFLINE, ONLINE, WRAPPER
    }
}
