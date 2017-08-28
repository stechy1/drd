package cz.stechy.drd.controller.user;

import cz.stechy.drd.R;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.persistent.UserService.UserException;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import cz.stechy.screens.Notification;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro přihlášení
 */
public class LoginController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private static final int ACTION_REGISTRATION = 1;
    private static final int ACTION_LOST_PASSWORD = 2;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Button btnLogin;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtPassword;

    // endregion

    private final LoginModel loginModel = new LoginModel();
    private final UserService userService;

    private String title;
    private String loginFail;
    private String registerSuccess;

    // endregion

    // region Constructors

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.USER_LOGIN_TITLE);
        loginFail = resources.getString(R.Translate.NOTIFY_LOGIN_FAIL);
        registerSuccess = resources.getString(R.Translate.NOTIFY_REGISTER_SUCCESS);

        txtLogin.textProperty().bindBidirectional(loginModel.login);
        txtPassword.textProperty().bindBidirectional(loginModel.password);
        btnLogin.disableProperty().bind(loginModel.valid.not());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(500, 260);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_REGISTRATION:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }
                showNotification(new Notification(registerSuccess));

                break;
            case ACTION_LOST_PASSWORD:
                break;
            default:

        }
    }

    // region Button handlers

    @FXML
    private void handleLogin(ActionEvent actionEvent) {
        try {
            userService.login(loginModel.login.getValue(), loginModel.password.getValue());
            setResult(RESULT_SUCCESS);
            finish();
        } catch (UserException e) {
            LOGGER.info("Přihlášení se nezdařilo", e);
            showNotification(new Notification(loginFail));
            loginModel.valid.set(false);
        }
    }

    @FXML
    private void handleRegistration(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.REGISTER, ACTION_REGISTRATION);
    }

    @FXML
    private void handleLostPassword(ActionEvent actionEvent) {
        showNotification(new Notification("Funkce není implementována"));
    }

    // endregion

}
