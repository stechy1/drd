package cz.stechy.drd.widget;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.NamedArg;
import javafx.scene.control.ComboBox;

/**
 * Vylepšený {@link ComboBox}, který automaticky zobrazuje hodnoty z výčtového typu
 *
 * @param <T> Vyčtový typ, který se zobrazuje
 */
public class EnumComboBox<T extends Enum<T>> extends JFXComboBox<T> {

    public EnumComboBox(@NamedArg("enumType") String enumType) throws Exception {
        @SuppressWarnings("unchecked") Class<T> enumClass = (Class<T>) Class.forName(enumType);
        getItems().setAll(enumClass.getEnumConstants());
    }

}
