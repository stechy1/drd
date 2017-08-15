package cz.stechy.drd;

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

    private final Context context;

    // endregion

    // region Constructors

    public ControllerFactory(Context context) {
        this.context = context;
    }

    // endregion

    @Override
    public Object call(Class<?> clazz) {
        LOGGER.trace("Konstruuji kontroler třídy {}", clazz.getSimpleName());
        try {
            return clazz.getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            try {
                return clazz.newInstance();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
