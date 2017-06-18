package cz.stechy.drd.widget;

import javafx.beans.NamedArg;
import javafx.scene.control.ComboBox;

/**
 * Vylepšený {@link ComboBox}, který automaticky zobrazuje hodnoty z výčtového typu
 *
 * @param <T> Vyčtový typ, který se zobrazuje
 */
public class EnumComboBox<T extends Enum<T>> extends ComboBox<T> {

    public EnumComboBox(@NamedArg("enumType") String enumType) throws Exception {
        Class<T> enumClass = (Class<T>) Class.forName(enumType);
        getItems().setAll(enumClass.getEnumConstants());
    }

}
