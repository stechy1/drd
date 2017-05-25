package cz.stechy.drd.annotation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

/**
 * Pomocná knihovní třída pro zpracování anotací procesorem
 */
final class ProcessorUtils {

    public static void error(Messager messager, Element e, String msg, Object... args) {
        messager.printMessage(Kind.ERROR, String.format(msg, args), e);
    }

    public static void note(Messager messager, Element e, String msg, Object... args) {
        messager.printMessage(Kind.NOTE, String.format(msg, args), e);
    }

    public static void other(Messager messager, Element e, String msg, Object... args) {
        messager.printMessage(Kind.OTHER, String.format(msg, args), e);
    }

    public static void warning(Messager messager, Element e, String msg, Object... args) {
        messager.printMessage(Kind.WARNING, String.format(msg, args), e);
    }

}
