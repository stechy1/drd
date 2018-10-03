package cz.stechy.drd.annotations;


import com.google.inject.BindingAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Anotace označující proměnnou, do které se má vložit instance třídy {@link java.io.File}, která bude obsahovat cestu ke konfiguračnímu souboru
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BindingAnnotation
public @interface ConfigFile {}
