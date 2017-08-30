package cz.stechy.drd.util;

import cz.stechy.drd.model.MaxActValue;
import java.util.function.UnaryOperator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.NumberStringConverter;

/**
 * Pomocná třída pro validování vstupu
 */
public final class FormUtils {

    /**
     * Inicializuje číselný vstup
     *
     * @param textField {@link TextField}
     * @param maxActValue {@link MaxActValue}
     */
    public static void initTextFormater(TextField textField, MaxActValue maxActValue) {
        textField.setTextFormatter(new TextFormatter<>(new NumberStringConverter(),
            null, new MinMaxUnaryOperator(maxActValue)));
        textField.textProperty()
            .bindBidirectional(maxActValue.actValueProperty(), new NumberStringConverter());
    }

    public static final class MinMaxUnaryOperator implements UnaryOperator<Change> {

        private final ObjectProperty<Number> min = new SimpleObjectProperty<>();
        private final ObjectProperty<Number> max = new SimpleObjectProperty<>();

        private MinMaxUnaryOperator(MaxActValue maxActValue) {
            this.min.bind(maxActValue.minValueProperty());
            this.max.bind(maxActValue.maxValueProperty());
        }

        @Override
        public Change apply(Change change) {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*|0)?")) {
                if (newText.trim().isEmpty()) {
                    return change;
                }

                // Nedovolím napsat mínus, pokud minimální hodnota je nezáporná
                if ("-".equals(newText)) {
                    if (min.get().doubleValue() >= 0) {
                        return null;
                    }
                }

                double value = Double.parseDouble(newText);
                if (value < min.get().doubleValue() || value > max.get().doubleValue()) {
                    return null;
                }

                return change;
            } else if ("-".equals(change.getText()) ) {
                // Odebere mínus
                if (change.getControlText().startsWith("-")) {
                    if (max.get().doubleValue() < 0) {
                        return null;
                    }

                    double value = Double.parseDouble(change.getControlText());
                    // Pokud je hodnota bez mínusu větší než je max, tak zakážu odebrání mínusu
                    if (Math.abs(value) > max.get().doubleValue()) {
                        return null;
                    }

                    change.setText("");
                    change.setRange(0, 1);
                    change.setCaretPosition(change.getCaretPosition()-2);
                    change.setAnchor(change.getAnchor()-2);
                    return change;
                } else {
                    if (min.get().doubleValue() >= 0) {
                        return null;
                    }

                    double value = Double.parseDouble("-" + change.getControlText());
                    // Pokud je hodnota bez mínusu větší než je max, tak zakážu odebrání mínusu
                    if (value < min.get().doubleValue()) {
                        return null;
                    }

                    change.setRange(0, 0);
                    return change;
                }
            }
            return null;
        }
    }

}
