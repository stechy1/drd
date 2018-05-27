package cz.stechy.drd.app.user;

import cz.stechy.drd.model.User;
import cz.stechy.drd.util.BitUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

class LoginModel {

    private static final int LOGIN_FLAG = 1 << 0; // 1
    private static final int PASSWORD_FLAG = 1 << 1; // 2

    final StringProperty login = new SimpleStringProperty();
    final StringProperty password = new SimpleStringProperty();
    final BooleanProperty valid = new SimpleBooleanProperty(false);
    private final IntegerProperty flags = new SimpleIntegerProperty(LOGIN_FLAG + PASSWORD_FLAG);

    LoginModel() {
        login.addListener(loginChangeListener);
        password.addListener(passwordChangeListener);
        flags.addListener(
            (observable, oldValue, newValue) -> valid.setValue(newValue.intValue() == 0));
    }

    private final ChangeListener<String> loginChangeListener = (observable, oldValue, newValue) -> {
        final int oldFlags = flags.get();
        final boolean isLoginValid = User.isNameValid(newValue);
        final int result = BitUtils.setBit(oldFlags, LOGIN_FLAG, !isLoginValid);
        if (oldFlags == result) {
            return;
        }

        flags.setValue(result);
    };

    private final ChangeListener<String> passwordChangeListener = (observable, oldValue, newValue) -> {
        final int oldFlags = flags.get();
        final boolean isPasswordValid = !newValue.isEmpty();
        final int result = BitUtils.setBit(oldFlags, PASSWORD_FLAG, !isPasswordValid);
        if (oldFlags == result) {
            return;
        }

        flags.setValue(result);
    };
}