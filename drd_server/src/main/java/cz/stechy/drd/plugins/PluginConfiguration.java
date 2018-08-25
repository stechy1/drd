package cz.stechy.drd.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PluginConfiguration {

    int DEFAULT_PRIORITY = 0;

    int priority() default DEFAULT_PRIORITY;

}
