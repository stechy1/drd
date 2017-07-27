package cz.stechy.drd.util;

import cz.stechy.drd.model.MaxActValue;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.NumberStringConverter;

/**
 * Pomocná třída pro validování vstupu
 */
public final class FormUtils {

    private static final String INTEGER_PATTERN = "[^0-9]";

    /**
     * Číselný filter s intervalem
     *
     * @param min Spodní hranice intervalu
     * @param max Horní hranice intervalu
     * @return {@link UnaryOperator}
     */
    private static UnaryOperator<Change> integerFilter(final int min, final int max) {
        return t -> {
            if (t.isAdded()) {
                if (t.getText().matches(INTEGER_PATTERN)) {
                    t.setText(String.valueOf(min));
                }
            }

            if (t.isReplaced()) {
                if (t.getText().matches(INTEGER_PATTERN)) {
                    t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));
                }
            }

            if (t.isDeleted()) {
                if (t.getControlNewText().isEmpty()) {
                    t.setText(String.valueOf(0));
                }
            }

            long value = Long.parseLong(t.getControlNewText().trim());
            if (value > max || value < min) {
                return null;
            }

            if (value == 0) {
                t.setCaretPosition(1);
            }

            return t;
        };
    }

    public static void initTextFormater(TextField textField, MaxActValue maxActValue) {
//        textField.setTextFormatter(new TextFormatter<>(new NumberStringConverter(), null,
//            FormUtils.integerFilter(
//                maxActValue.getMinValue().intValue(),
//                maxActValue.getMaxValue().intValue()
//            )));
        textField.textProperty()
            .bindBidirectional(maxActValue.actValueProperty(), new NumberStringConverter());
    }

    public static void disposeTextFormater(TextField textField, MaxActValue maxActValue) {
        textField.setTextFormatter(null);
        textField.textProperty().unbindBidirectional(maxActValue.actValueProperty());

    }

}
