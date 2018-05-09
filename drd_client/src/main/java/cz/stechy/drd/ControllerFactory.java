package cz.stechy.drd;

import cz.stechy.drd.di.IDependencyManager;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tovární třída na výrobu kontrolerů
 */
public class ControllerFactory implements Callback<Class<?>, Object> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerFactory.class);

    // endregion

    // region Variables

    private final IDependencyManager container;

    // endregion

    // region Constructors

    public ControllerFactory(IDependencyManager container) {
        this.container = container;
    }

    // endregion

    @Override
    public Object call(Class<?> clazz) {
        LOGGER.trace("Konstruuji kontroler třídy {}", clazz.getSimpleName());
        return container.getInstance(clazz);
    }
}